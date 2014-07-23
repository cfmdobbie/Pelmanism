package com.maycontainsoftware.pelmanism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

class GameAreaComponent extends Component {

	private static final String TAG = GameAreaComponent.class.getName();

	private Texture mTilesetTexture;
	private TextureRegion[] mCardTextures;

	// TODO: Board size comes from prefs, options or defaults - how does this work?
	private static final int BOARD_WIDTH = 4;
	private static final int BOARD_HEIGHT = 5;
	private static final int NUMBER_OF_CELLS = BOARD_WIDTH * BOARD_HEIGHT;
	// TODO: Better place for board representation
	private final Rectangle[] mBoardCells = new Rectangle[NUMBER_OF_CELLS];

	// TODO: Better place for data describing state of game
	private final boolean[] mCellUncovered = new boolean[NUMBER_OF_CELLS];
	private final boolean[] mCellAnimating = new boolean[NUMBER_OF_CELLS];
	private final int[] mCellContents = new int[NUMBER_OF_CELLS];
	private int mFirstCell;
	private int mSecondCell;
	private float animFirstAlpha;
	private float animSecondAlpha;
	private float showTimer;

	// Game State Engine
	private enum GameState {
		AwaitingFirstSelection, AnimFirstSelection, AwaitingSecondSelection, AnimSecondSelection, ShowingPair, Complete,
	}

	// The game state
	private GameState mGameState = GameState.AwaitingFirstSelection;

	public GameAreaComponent(PelmanismGame game) {
		super(game);
	}

	@Override
	protected void resize(int screenWidth, int screenHeight) {
		// Position board cells on screen
		updateBoardArea();
		positionBoardCells(rect);
	}

	@Override
	protected void onAssetsLoaded(AssetManager assetManager) {
		// Acquire Texture references
		mTilesetTexture = assetManager.get(PelmanismGame.HARD_TILESET_TEXTURE);

		// Chop textures into TextureRegions as required
		mCardTextures = chopTextureIntoRegions(mTilesetTexture, 4, 4);

		// Either start new game or load old game?
		// TODO: Start new game or load game from preferences logic
		// Randomly generate board
		// Chose a set of (paired, shuffled) cards
		List<Integer> chosenCards = generateCardSet();
		// Position cards on board
		for (int i = 0; i < chosenCards.size(); i++) {
			mCellContents[i] = chosenCards.get(i);
		}
	}

	@Override
	protected void render(SpriteBatch batch) {
		switch (game.mUiState) {
		case Loading:
			// Assets not loaded, nothing more to do
			break;
		case LoadingToGame:
		case GameToOptions_Game:
		case OptionsToGame_Game:
			drawBoard(batch, game.mUiAlpha);
			break;
		case Game:
			drawBoard(batch, 1.0f);
			break;
		case GameToOptions_Options:
		case Options:
		case OptionsToGame_Options:
			// TODO: Draw options screen
			break;
		}
	}

	@Override
	protected void input(Vector2 touch) {
		switch (game.mUiState) {
		case Game:
			// Determine whether a cell was touched
			int cell = getTouchedCell(touch);

			// Gdx.app.log(TAG, "mGameState = " + mGameState);
			// Gdx.app.log(TAG, "touched cell = " + cell);
			// Gdx.app.log(TAG, "mFirstCell = " + mFirstCell);
			// Gdx.app.log(TAG, "animFirstAlpha = " + animFirstAlpha);
			// Gdx.app.log(TAG, "mSecondCell = " + mSecondCell);
			// Gdx.app.log(TAG, "animSecondAlpha = " + animSecondAlpha);

			// Was a valid cell touched?
			if (cell != -1) {
				switch (mGameState) {
				case AwaitingFirstSelection:
					if (!mCellUncovered[cell]) {
						// Remember this cell
						mFirstCell = cell;
						// Set it to start animating in
						mGameState = GameState.AnimFirstSelection;
						animFirstAlpha = 0.0f;
						mCellAnimating[cell] = true;
					}
					break;
				case AnimFirstSelection:
				case AwaitingSecondSelection:
					if (!mCellUncovered[cell] && !mCellAnimating[cell]) {
						// Remember this cell
						mSecondCell = cell;
						// Start animating in
						mGameState = GameState.AnimSecondSelection;
						animSecondAlpha = 0.0f;
						mCellAnimating[cell] = true;
					}
					break;
				case AnimSecondSelection:
				case ShowingPair:
					if (cell != mFirstCell && cell != mSecondCell && !mCellUncovered[cell]) {
						// Clear the previously-selected cells
						// TODO: Pair was a match?
						mCellAnimating[mFirstCell] = false;
						mCellAnimating[mSecondCell] = false;
						mCellUncovered[mFirstCell] = false;
						mCellUncovered[mSecondCell] = false;
						mFirstCell = -1;
						mSecondCell = -1;
						// Remember this cell
						mFirstCell = cell;
						// Set it to start animating in
						mGameState = GameState.AnimFirstSelection;
						animFirstAlpha = 0.0f;
						mCellAnimating[cell] = true;
					}
					break;
				case Complete:
					// Board is complete, ignore touch
					break;
				default:
					Gdx.app.log(TAG, "Invalid game state! [" + mGameState + "]");
					break;
				}
			}
			break;
		default:
			// Ignore input
			break;
		}
	}

	private static final TextureRegion[] chopTextureIntoRegions(Texture texture, int horizontalRegions, int verticalRegions) {
		int numberOfRegions = horizontalRegions * verticalRegions;
		TextureRegion[] regions = new TextureRegion[numberOfRegions];
		for (int i = 0; i < numberOfRegions; i++) {
			int x = i % horizontalRegions;
			int y = i / horizontalRegions;
			float u1 = (x) * (1.0f / horizontalRegions);
			float v1 = (y) * (1.0f / verticalRegions);
			float u2 = (x + 1) * (1.0f / horizontalRegions);
			float v2 = (y + 1) * (1.0f / verticalRegions);
			regions[i] = new TextureRegion(texture, u1, v1, u2, v2);
		}
		return regions;
	}

	private final List<Integer> generateCardSet() {
		// Generate a list of all cards
		List<Integer> availableCards = new ArrayList<Integer>(mCardTextures.length - 1);
		for (int i = 0; i < (mCardTextures.length - 1); i++) {
			availableCards.add(i);
		}
		// Chose a random set of cards, and collect together in pairs
		List<Integer> chosenCards = new ArrayList<Integer>(NUMBER_OF_CELLS);
		for (int i = 0; i < (NUMBER_OF_CELLS / 2); i++) {
			Integer card = availableCards.remove(game.mRandom.nextInt(availableCards.size()));
			chosenCards.add(card);
			chosenCards.add(card);
		}
		// Shuffle cards and position on board
		Collections.shuffle(chosenCards, game.mRandom);

		return chosenCards;
	}

	private final void positionBoardCells(Rectangle gameArea) {
		// Padding between cells
		final int cellPadding = 8;
		// Size of each cell
		final float cellWidth = ((gameArea.width + cellPadding) / BOARD_WIDTH) - cellPadding;
		final float cellHeight = ((gameArea.height + cellPadding) / BOARD_HEIGHT) - cellPadding;
		for (int y = 0; y < BOARD_HEIGHT; y++) {
			final float yCoord = gameArea.y + y * (cellHeight + cellPadding);
			for (int x = 0; x < BOARD_WIDTH; x++) {
				final float xCoord = gameArea.x + x * (cellWidth + cellPadding);
				mBoardCells[y * BOARD_WIDTH + x] = new Rectangle(xCoord, yCoord, cellWidth, cellHeight);
			}
		}
	}

	private final int getTouchedCell(Vector2 touch) {
		for (int i = 0; i < mBoardCells.length; i++) {
			if (mBoardCells[i].contains(touch)) {
				return i;
			}
		}
		return -1;
	}

	private final void drawBoard(SpriteBatch batch, float alpha) {
		// TODO: Draw board using alpha
		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		// Draw cells
		for (int i = 0; i < mBoardCells.length; i++) {
			final Rectangle r = mBoardCells[i];
			if (mCellAnimating[i]) {
				float cellAlpha = (i == mFirstCell) ? animFirstAlpha : animSecondAlpha;
				batch.draw(mCardTextures[mCellContents[i]], r.x, r.y, r.width, r.height);
				batch.setColor(1.0f, 1.0f, 1.0f, 1.0f - cellAlpha);
				batch.draw(mCardTextures[mCardTextures.length - 1], r.x, r.y, r.width, r.height);
				batch.setColor(1.0f, 1.0f, 1.0f, alpha);
			} else if (mCellUncovered[i]) {
				batch.draw(mCardTextures[mCellContents[i]], r.x, r.y, r.width, r.height);
			} else {
				batch.draw(mCardTextures[mCardTextures.length - 1], r.x, r.y, r.width, r.height);
			}
		}
	}

	private final void updateBoardArea() {
		Rectangle emptyArea = game.calculateEmptyArea();
		Gdx.app.log(TAG, "updateBoardArea: emptyArea = " + emptyArea.toString());

		final float availableAspect = emptyArea.getAspectRatio(); // w/h
		Gdx.app.log(TAG, "updateBoardArea: availableAspect = " + availableAspect);

		final float desiredAspect = BOARD_WIDTH / (float) BOARD_HEIGHT;
		Gdx.app.log(TAG, "updateBoardArea: desiredAspect = " + desiredAspect);

		if (availableAspect < desiredAspect) {
			// Fill width, calculate height
			float desiredHeight = emptyArea.width / desiredAspect;
			float desiredY = (emptyArea.height - desiredHeight) / 2 + emptyArea.y;
			rect.setPosition(emptyArea.x, desiredY);
			rect.setSize(emptyArea.width, desiredHeight);
		} else {
			// Fill height, calculate width
			float desiredWidth = emptyArea.height * desiredAspect;
			float desiredX = (emptyArea.width - desiredWidth) / 2 + emptyArea.x;
			rect.setPosition(desiredX, emptyArea.y);
			rect.setSize(desiredWidth, emptyArea.height);
		}

		Gdx.app.log(TAG, "updateBoardArea: rect = " + rect.toString());
	}

	@Override
	protected void update(float delta) {
		// Perform game update logic
		switch (mGameState) {
		case AnimFirstSelection:
			// Update animation alpha
			animFirstAlpha += delta * 2;
			if (animFirstAlpha >= 1.0f) {
				animFirstAlpha = 1.0f;
				// Mark cell as uncovered
				mCellUncovered[mFirstCell] = true;
				// And no longer animating
				mCellAnimating[mFirstCell] = false;
				// Wait for second click
				if (mGameState == GameState.AnimFirstSelection) {
					mGameState = GameState.AwaitingSecondSelection;
				}
			}
			break;
		case AnimSecondSelection:
			// Update animation alpha
			animFirstAlpha += delta * 2;
			if (animFirstAlpha >= 1.0f) {
				animFirstAlpha = 1.0f;
				// Mark cell as uncovered
				mCellUncovered[mFirstCell] = true;
				// And no longer animating
				mCellAnimating[mFirstCell] = false;
				// Wait for second click
				if (mGameState == GameState.AnimFirstSelection) {
					mGameState = GameState.AwaitingSecondSelection;
				}
			}
			// Second
			// Update animation alpha
			animSecondAlpha += delta * 2;
			if (animSecondAlpha >= 1.0f) {
				animSecondAlpha = 1.0f;
				// Mark cell as uncovered
				mCellUncovered[mSecondCell] = true;
				// And no longer animating
				mCellAnimating[mSecondCell] = false;
				// Two cards turned, show the pair
				mGameState = GameState.ShowingPair;
				showTimer = 1.0f;
			}
			break;
		case ShowingPair:
			showTimer -= delta;
			if (showTimer <= 0.0f) {
				// TODO: Pair was a match?
				mCellUncovered[mFirstCell] = false;
				mCellUncovered[mSecondCell] = false;
				mFirstCell = -1;
				mSecondCell = -1;
				mGameState = GameState.AwaitingFirstSelection;
			}
			break;
		case Complete:
			// TODO: Some kind of "win" animation?
			break;
		default:
			// Otherwise, no-op
			break;
		}
	}
}
