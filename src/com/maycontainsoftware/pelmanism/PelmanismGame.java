package com.maycontainsoftware.pelmanism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PelmanismGame implements ApplicationListener {

	/** Tag for debug logging. */
	private static final String TAG = PelmanismGame.class.getName();

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pelmanism";
	private Preferences prefs;

	// App-global data members
	private final Random mRandom = new Random();

	// Camera
	private OrthographicCamera mCamera;

	// Drawing engine
	private SpriteBatch mBatch;

	// Assets
	private AssetManager mAssetManager;

	// UI Components - note that these are always processed in insertion order
	private List<Component> components;

	// Texture name constants
	static final String BACKGROUND_TEXTURE = "background.png";
	private static final String TILESET_TEXTURE = "test_card_4.png";
	static final String UI_TEXTURE = "ui_elements_1.png";

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

	// Current display metrics
	int mDisplayWidth;
	int mDisplayHeight;

	// Alpha value used for fading in/out of UI elements
	float mUiAlpha;

	// Game State Engine
	private enum GameState {
		AwaitingFirstSelection, AnimFirstSelection, AwaitingSecondSelection, AnimSecondSelection, ShowingPair, Complete,
	}

	// The game state
	private GameState mGameState = GameState.AwaitingFirstSelection;

	// User Interface State Engine
	enum UiState {
		Loading, LoadingToGame, Game, GameToOptions_Game, GameToOptions_Options, Options, OptionsToGame_Options, OptionsToGame_Game
	}

	// The UI state
	UiState mUiState = UiState.Loading;

	private final void startAssetLoad() {
		// Linear filtering of texture
		TextureParameter linearFilter = new TextureParameter();
		linearFilter.minFilter = TextureFilter.Linear;
		linearFilter.magFilter = TextureFilter.Linear;

		// Tiled texture
		TextureParameter wrapTexture = new TextureParameter();
		wrapTexture.wrapU = TextureWrap.Repeat;
		wrapTexture.wrapV = TextureWrap.Repeat;

		// Specify textures to be loaded
		mAssetManager.load(BACKGROUND_TEXTURE, Texture.class, wrapTexture);
		mAssetManager.load(TILESET_TEXTURE, Texture.class, linearFilter);
		mAssetManager.load(UI_TEXTURE, Texture.class, linearFilter);
	}

	@Override
	public final void create() {
		Gdx.app.log(TAG, "create()");

		// Get reference to preferences file
		prefs = Gdx.app.getPreferences(PREFERENCES_NAME);
		
		// Create camera
		mCamera = new OrthographicCamera();
		
		// Create sprite batch
		mBatch = new SpriteBatch();
		
		// Create asset manager and start loading assets
		mAssetManager = new AssetManager();
		startAssetLoad();

		// Create UI Components
		components = new ArrayList<Component>();
		components.add(new BackgroundComponent(this));
		components.add(new LogoComponent(this));
		components.add(new OptionsComponent(this));
		components.add(new NewGameComponent(this));
		components.add(new CancelComponent(this));
		// TODO: Add rest of UI components
	}

	@Override
	public final void dispose() {
		Gdx.app.log(TAG, "dispose()");

		// Dispose of sprite batch
		mBatch.dispose();

		// Dispose of textures
		// TODO: Dispose of all textures
	}

	private final void doInput(float delta) {

		switch (mUiState) {
		case Loading:
		case LoadingToGame:
		case GameToOptions_Game:
		case GameToOptions_Options:
		case OptionsToGame_Options:
		case OptionsToGame_Game:
			// Input ignored
			break;
		case Game:
			// TODO: Clean up game screen input handling
			if (Gdx.input.justTouched()) {
				// Get touch location
				Vector2 touch = getTouchLocation();
				
				for(Component c : components) {
					if(c.rect.contains(touch)) {
						c.input(touch);
					}
				}

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
				} else {
					// TODO: Other touch-sensitive components
				}
			}
			break;
		case Options:
			// TODO: Input handling in Options menu
			break;
		}
	}

	private final void doUpdate(float delta) {

		switch (mUiState) {
		case Loading:
			// Continue asset loading
			boolean finished = mAssetManager.update();
			if (finished) {
				// Assets have been loaded!

				// Acquire Texture references
				mTilesetTexture = mAssetManager.get(TILESET_TEXTURE);

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

				// Inform UI Components that assets are loaded
				for (Component c : components) {
					c.onAssetsLoaded(mAssetManager);
				}

				// Update UI Component sizes
				for (Component c : components) {
					c.resize(mDisplayWidth, mDisplayHeight);
				}

				// Start to fade in game interface
				mUiState = UiState.LoadingToGame;
				mUiAlpha = 0.0f;
			}
			break;
		case LoadingToGame:
			mUiAlpha += delta * 2;
			if (mUiAlpha >= 1.0f) {
				mUiState = UiState.Game;
			}
			break;
		case Game:

			// Perform game update logic
			// TODO: Extract to object?
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
				// TODO: Anim first as well!
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
			}

			break;
		case GameToOptions_Game:
			// Continue to fade out game screen
			mUiAlpha -= delta * 2;
			if (mUiAlpha <= 0.0f) {
				mUiState = UiState.GameToOptions_Options;
				mUiAlpha = 0.0f;
			}
			break;
		case GameToOptions_Options:
			// Continue to fade in options screen
			mUiAlpha += delta * 2;
			if (mUiAlpha >= 1.0f) {
				mUiState = UiState.Options;
			}
			break;
		case Options:
			// No-op
			break;
		case OptionsToGame_Options:
			// Continue to fade out options screen
			mUiAlpha -= delta * 2;
			if (mUiAlpha <= 0.0f) {
				mUiState = UiState.OptionsToGame_Game;
				mUiAlpha = 0.0f;
			}
			break;
		case OptionsToGame_Game:
			// Continue to fade in game screen
			mUiAlpha += delta * 2;
			if (mUiAlpha >= 1.0f) {
				mUiState = UiState.Game;
			}
			break;
		}
	}

	private final void doRender(float delta) {

		// Render screen

		// Clear background
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Gdx.gl.glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
		// Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// All drawing is done in a single SpriteBatch
		// Update camera
		mCamera.update();
		// Feed in current camera projection
		mBatch.setProjectionMatrix(mCamera.combined);
		// Start batch
		mBatch.begin();

		// Draw all UI components
		for (Component c : components) {
			c.render(mBatch);
		}

		switch (mUiState) {
		case Loading:
			// Assets not loaded, nothing more to do
			break;
		case LoadingToGame:
		case GameToOptions_Game:
		case OptionsToGame_Game:
			drawBoard(mUiAlpha);
			break;
		case Game:
			drawBoard(1.0f);
			break;
		case GameToOptions_Options:
		case Options:
		case OptionsToGame_Options:
			// TODO: Draw options screen
			break;
		}

		// End batch
		mBatch.end();
	}

	void drawTextureByRect(TextureRegion textureRegion, Rectangle rect) {
		mBatch.draw(textureRegion, rect.x, rect.y, rect.width, rect.height);
	}

	private final void drawBoard(float alpha) {
		// TODO: Draw board using alpha
		mBatch.setColor(1.0f, 1.0f, 1.0f, alpha);
		// Draw cells
		for (int i = 0; i < mBoardCells.length; i++) {
			final Rectangle r = mBoardCells[i];
			if (mCellAnimating[i]) {
				float cellAlpha = (i == mFirstCell) ? animFirstAlpha : animSecondAlpha;
				mBatch.draw(mCardTextures[mCellContents[i]], r.x, r.y, r.width, r.height);
				mBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f - cellAlpha);
				mBatch.draw(mCardTextures[mCardTextures.length - 1], r.x, r.y, r.width, r.height);
				mBatch.setColor(1.0f, 1.0f, 1.0f, alpha);
			} else if (mCellUncovered[i]) {
				mBatch.draw(mCardTextures[mCellContents[i]], r.x, r.y, r.width, r.height);
			} else {
				mBatch.draw(mCardTextures[mCardTextures.length - 1], r.x, r.y, r.width, r.height);
			}
		}
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();

		doInput(delta);
		doUpdate(delta);
		doRender(delta);

		/*
		 * // Process first cell animation if (mCellAnimating[mFirstCell]) { // Update animation alpha animFirstAlpha += delta * 2; if
		 * (animFirstAlpha >= 1.0f) { animFirstAlpha = 1.0f; // Mark cell as uncovered mCellUncovered[mFirstCell] = true; // And no longer
		 * animating mCellAnimating[mFirstCell] = false; // Wait for second click if(mGameState == GameState.AnimFirstSelection) {
		 * mGameState = GameState.AwaitingSecondSelection; } } } // Process second cell animation if (mCellAnimating[mSecondCell]) { //
		 * Update animation alpha animSecondAlpha += delta * 2; if (animSecondAlpha >= 1.0f) { animSecondAlpha = 1.0f; // Mark cell as
		 * uncovered mCellUncovered[mSecondCell] = true; // And no longer animating mCellAnimating[mSecondCell] = false; // Two cards
		 * turned, show the pair mGameState = GameState.ShowingPair; showTimer = 1.0f; } } if(mGameState == GameState.ShowingPair) {
		 * showTimer -= delta; if(showTimer <= 0.0f) { // TODO: Pair was a match? mCellUncovered[mFirstCell] = false;
		 * mCellUncovered[mSecondCell] = false; mFirstCell = -1; mSecondCell = -1; mGameState = GameState.AwaitingFirstSelection; } }
		 */
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");

		// Update camera wrt new screen dimensions
		mCamera.setToOrtho(false, width, height);

		// Save screen dimensions
		mDisplayWidth = width;
		mDisplayHeight = height;

		// Position UI Components
		if (mUiState != UiState.Loading) {
			for (Component c : components) {
				c.resize(width, height);
			}
		}

		// Position board cells on screen
		Rectangle availableArea = calculateAvailableArea(width, height);
		Rectangle gameArea = calculateBoardArea(availableArea);
		positionBoardCells(gameArea);
	}

	private final Vector2 getTouchLocation() {
		Vector3 pos = new Vector3();
		pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		mCamera.unproject(pos);
		return new Vector2(pos.x, pos.y);
	}

	private final int getTouchedCell(Vector2 touch) {
		for (int i = 0; i < mBoardCells.length; i++) {
			if (mBoardCells[i].contains(touch)) {
				return i;
			}
		}
		return -1;
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
			Integer card = availableCards.remove(mRandom.nextInt(availableCards.size()));
			chosenCards.add(card);
			chosenCards.add(card);
		}
		// Shuffle cards and position on board
		Collections.shuffle(chosenCards, mRandom);

		return chosenCards;
	}

	private final void positionBoardCells(Rectangle gameArea) {
		// Padding between cells
		final int cellPadding = 16;
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

	private final Rectangle calculateBoardArea(Rectangle availableArea) {
		final float availableAspect = availableArea.getAspectRatio(); // w/h
		Gdx.app.log(TAG, "availableAspect = " + availableAspect);
		final float desiredAspect = BOARD_WIDTH / (float) BOARD_HEIGHT;
		Gdx.app.log(TAG, "desiredAspect = " + desiredAspect);
		if (availableAspect < desiredAspect) {
			// Width is king
			float desiredHeight = availableArea.width / desiredAspect;
			float desiredY = (availableArea.height - desiredHeight) / 2;
			return new Rectangle(availableArea.x, desiredY, availableArea.width, desiredHeight);
		} else {
			// Height is king
			float desiredWidth = availableArea.height * desiredAspect;
			float desiredX = (availableArea.width - desiredWidth) / 2;
			return new Rectangle(desiredX, availableArea.y, desiredWidth, availableArea.height);
		}
	}

	private final Rectangle calculateAvailableArea(int width, int height) {
		// Leave 16px border on screen
		final int screenBorder = 16;
		return new Rectangle(screenBorder, screenBorder, width - screenBorder * 2, height - screenBorder * 2);
	}

	@Override
	public void pause() {
		Gdx.app.log(TAG, "pause()");
		// TODO: Same game state to preferences
	}

	@Override
	public void resume() {
		Gdx.app.log(TAG, "resume()");
		// TODO: Load game state from preferences
	}
}
