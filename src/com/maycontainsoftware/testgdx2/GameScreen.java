package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

/**
 * The screen that holds the actual game.
 * 
 * @author Charlie
 */
public class GameScreen implements Screen {

	/** Tag, for logging purposes. */
	private static final String TAG = GameScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	private final MyGame game;

	// Game configuration

	/** The player configuration loaded from preferences. */
	private final PlayerConfiguration playerConfiguration;

	/** The difficulty configuration loaded from preferences. */
	private final Difficulty difficulty;

	/** The card set configuration loaded from preferences. */
	private final CardSet cardSet;

	// Display-type properties

	/** This Screen's Stage object. */
	private final Stage stage;

	/** The atlas containing the cards. */
	private final TextureAtlas atlas;

	/** The region in the atlas containing the card back graphic. */
	private final TextureRegion cardBackRegion;

	/** The TextureRegions holding the card graphics. */
	private final TextureRegion[] cardRegions;

	// Game model

	/** The Pelmanism game model object. */
	private final Pelmanism model;

	// Graphical elements needed for future access

	/** Player one's score display. */
	private PlayerScoreActor playerOne;

	/** Player two's score display. */
	private PlayerScoreActor playerTwo;

	/**
	 * Object representing the actual card on the screen.
	 * 
	 * @author Charlie
	 */
	static class CardActor extends Image {

		// Game state

		/** Enumeration representing game state. */
		static enum GameState {
			PendingFirstPick,
			PendingSecondPick,
			Animating,
			GameOver,
		}

		/** The game state. */
		private static GameState gameState = GameState.PendingFirstPick;

		/** The card represented by this actor. */
		private final Card card;

		/** A reference to the Screen. */
		private final GameScreen screen;

		/** A reference to the game model. */
		private final Pelmanism model;

		/** This card's front texture. */
		private final TextureRegion cardTexture;

		/** This card's back texture. */
		private final TextureRegion cardBackTexture;

		/** The actor representing the first card to be picked. */
		private static CardActor firstPick = null;

		/** The actor representing the second card to be picked. */
		private static CardActor secondPick = null;

		/** Given two card picks, process the turn in the game model and update the interface as required. */
		private void processTurn() {

			// The turn to be submitted
			final Turn turn = new Turn(firstPick.card, secondPick.card);
			// The results of submitting the turn
			final TurnResult result = model.turn(turn);

			if (!result.isMatch()) {

				// Not a match

				firstPick.addAction(firstPick.actionDelayedWinkToBack());
				secondPick.addAction(Actions.sequence(secondPick.actionDelayedWinkToBack(), new Action() {
					@Override
					public boolean act(float delta) {

						// Player changed, change highlight
						if (model.getNumberOfPlayers() > 1) {
							screen.playerOne.setHighlight(model.getCurrentPlayerId() == 0);
							screen.playerTwo.setHighlight(model.getCurrentPlayerId() == 1);
						}

						// Update game state
						gameState = GameState.PendingFirstPick;

						return true;
					}
				}));

			} else {
				if (!result.isGameOver()) {

					// A match, and game is not over yet

					// TODO: Need to move all these float primitives to constant fields
					firstPick.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.25f)));
					secondPick.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.25f), new Action() {
						@Override
						public boolean act(float delta) {

							// Player not changed

							// Score changed, update label
							final int playerId = result.turn.getPlayerId();
							screen.updateScore(playerId, model.getPlayerScore(playerId));

							// Update game state
							gameState = GameState.PendingFirstPick;

							return true;
						}
					}));

					// Play sound effect
					screen.game.playCardMatchSound();

				} else {

					// A match, and game is over

					firstPick.addAction(Actions.sequence(Actions.delay(0.5f), firstPick.actionWinkOut()));
					secondPick.addAction(Actions.sequence(Actions.delay(0.5f), secondPick.actionWinkOut(),
							new Action() {
								@Override
								public boolean act(float delta) {

									// Player not changed

									// Score changed, update label
									final int playerId = result.turn.getPlayerId();
									screen.updateScore(playerId, model.getPlayerScore(playerId));

									// Update game state
									gameState = GameState.GameOver;
									
									return true;
								}
							}, new ScreenChangeAction(screen.game, screen, new GameOverScreen(screen.game))));
					
					// TODO: What do we do when the game is over?!?
				}
			}

		}

		/**
		 * Construct a new CardActor object.
		 * 
		 * @param index
		 * @param model
		 * @param cardTexture
		 * @param cardBackTexture
		 */
		public CardActor(final Card card, final GameScreen screen, final TextureRegion cardTexture,
				final TextureRegion cardBackTexture) {

			// Pass default/initial texture to superclass' constructor
			super(cardBackTexture);

			this.card = card;
			this.screen = screen;
			this.model = screen.model;
			this.cardTexture = cardTexture;
			this.cardBackTexture = cardBackTexture;

			// Touch events on the card are intercepted by an InputListener.
			this.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

					switch (gameState) {
					case PendingFirstPick:

						if (!CardActor.this.card.isMatched()) {
							// Remember that this is the first chosen card
							firstPick = CardActor.this;
							// Flip the card over
							CardActor.this.addAction(actionWinkToFront());
							// Play sound effect
							screen.game.playCardTurnSound();
							// Update state
							gameState = GameState.PendingSecondPick;
						}

						break;
					case PendingSecondPick:

						if (CardActor.this != firstPick && !CardActor.this.card.isMatched()) {
							// Remember that this is the second chosen card
							secondPick = CardActor.this;
							// Flip the card over
							CardActor.this.addAction(Actions.sequence(actionWinkToFront(), new Action() {
								@Override
								public boolean act(float delta) {
									CardActor.this.processTurn();
									return true;
								}
							}));
							// Play sound effect
							screen.game.playCardTurnSound();
							// Update state
							gameState = GameState.Animating;
						}

						break;
					case Animating:
						// Cards are animating - ignore all input

						// XXX: This means you can't pick a new card while it is animating to back. This causes a
						// significant lag in the usability.

						break;
					case GameOver:
						// TODO
						break;
					default:
						// TODO
						break;
					}

					return true;
				}
			});
		}

		/** Switch the current texture region for a different region. */
		private final void switchTexture(final TextureRegion region) {
			final TextureRegionDrawable drawable = (TextureRegionDrawable) (this.getDrawable());
			drawable.setRegion(region);
		}

		/** Return an Action that immediately switches the card texture region. */
		private final Action actionSpecifiedTexture(final TextureRegion region) {
			return new Action() {
				@Override
				public boolean act(float delta) {
					switchTexture(region);
					return true;
				}
			};
		}

		/** Return an action that immediately switches to the card front texture. */
		private final Action actionFrontTexture() {
			return actionSpecifiedTexture(cardTexture);
		}

		/** Return an action that immediately switches to the card back texture. */
		private final Action actionBackTexture() {
			return actionSpecifiedTexture(cardBackTexture);
		}

		/** Create an Action that implements the "wink out" animation. */
		private final Action actionWinkOut() {
			final float cardWidth = this.getWidth();
			final float duration = 0.125f;

			Action shiftRight = Actions.moveBy(cardWidth / 2, 0.0f, duration);
			Action scaleToZeroWidth = Actions.scaleTo(0.0f, 1.0f, duration);

			return Actions.parallel(shiftRight, scaleToZeroWidth);
		}

		/** Create an Action that implements the "wink in" animation. */
		private final Action actionWinkIn() {
			final float cardWidth = this.getWidth();
			final float duration = 0.125f;

			Action shiftLeft = Actions.moveBy(-cardWidth / 2, 0.0f, duration);
			Action scaleToFullWidth = Actions.scaleTo(1.0f, 1.0f, duration);

			return Actions.parallel(shiftLeft, scaleToFullWidth);
		}

		/** Return an action that winks in and out, switching to the card front texture. */
		private final Action actionWinkToFront() {
			return Actions.sequence(actionWinkOut(), actionFrontTexture(), actionWinkIn());
		}

		/** Return an action that winks in and out, switching to the card back texture. */
		private final Action actionWinkToBack() {
			return Actions.sequence(actionWinkOut(), actionBackTexture(), actionWinkIn());
		}

		/** Return an action that pauses then flips card to the back. */
		private final Action actionDelayedWinkToBack() {
			return Actions.sequence(Actions.delay(1.0f), actionWinkToBack());
		}
	}

	/**
	 * Construct a new game screen.
	 * 
	 * @param game
	 */
	public GameScreen(final MyGame game) {
		this.game = game;

		// Load game configuration from preferences
		playerConfiguration = game.getPlayerConfigurationFromPrefs();
		difficulty = game.getDifficultyFromPrefs();
		cardSet = game.getCardSetFromPrefs();

		// Create game model
		model = new Pelmanism(playerConfiguration.getNumberOfPlayers(), difficulty.getNumberOfPairs());

		// Load graphic assets
		atlas = game.manager.get(cardSet.atlasName, TextureAtlas.class);
		cardBackRegion = atlas.findRegion(cardSet.backRegionName);
		// Given numberOfPairs, return that number of unique random TextureRegions from appropriate TextureAtlas.
		// TODO: Want to select a new subset of regions for each game; need to rearrange this
		cardRegions = selectCardTextures(difficulty.getNumberOfPairs());

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);
		// Use global camera
		stage.setCamera(game.camera);
		// Redirect all input events to the Stage
		//Gdx.input.setInputProcessor(stage);

		// Create UI elements
		createUi();
		
		// Play shuffle sound
		game.playCardDealSound();
	}

	@Override
	public void render(float delta) {

		// Update and render Stage
		stage.act();
		stage.draw();

		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		// Update Stage's viewport calculations
		final Rectangle v = game.viewport;
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, v.x, v.y, v.width, v.height);
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	/**
	 * Pick a random selection of unique card textures from the atlas.
	 * 
	 * @param numberOfTexturesRequired
	 *            The number of unique textures required.
	 * @return An unsorted array containing the selected TextureRegions
	 */
	private final TextureRegion[] selectCardTextures(final int numberOfTexturesRequired) {
		final TextureRegion[] regions = new TextureRegion[numberOfTexturesRequired];

		// Generate list of numbers from 1 to 32
		final List<Integer> allNumbers = new ArrayList<Integer>(32);
		for (int i = 1; i <= 32; i++) {
			allNumbers.add(i);
		}

		// Shuffle the list
		Collections.shuffle(allNumbers);

		// Pick the first n textures
		for (int i = 0; i < numberOfTexturesRequired; i++) {
			// Determine the region name
			String regionName = String.valueOf(allNumbers.get(i));
			if (regionName.length() < 2) {
				regionName = "0" + regionName;
			}
			// Find the TextureRegion and store it
			regions[i] = atlas.findRegion(regionName);
		}

		return regions;
	}

	/** Create the user interface of the Screen. */
	private final void createUi() {

		// Root of the Stage is a Table, used to lay out all other widgets.
		final Table table = new Table();
		table.setFillParent(true);
		table.defaults().padLeft(30.0f).padRight(30.0f);
		// table.debug();
		stage.addActor(table);

		// Set tiled background for Table, thus for Screen.
		// final TextureRegion background = game.uiAtlas.findRegion("background");
		// table.setBackground(new TiledDrawable(background));

		// Drawable used for highlighting the player scores
		final Drawable highlightDrawable = new TiledDrawable(game.uiAtlas.findRegion("yellow"));

		// Secondary score display

		if (playerConfiguration.getNumberOfPlayers() > 1) {
			// Will need a second score display

			table.row().padTop(20.0f);

			playerTwo = new PlayerScoreActor(playerConfiguration.getPlayerName(1),
					playerConfiguration.getPlayerColor(1), game.skin, highlightDrawable);
			table.add(playerTwo).colspan(2).fillX();
		}

		// Game area

		table.row().padTop(30.0f);

		final Table gameArea = new Table();
		// gameArea.debug();

		// Set up card grid

		final int rows = difficulty.getBoardRows();
		final int columns = difficulty.getBoardColumns();

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {

				final int cardIndex = c + r * columns;
				final Card card = model.getCard(cardIndex);
				final TextureRegion cardRegion = cardRegions[card.getPairId()];
				final CardActor cardActor = new CardActor(card, this, cardRegion, cardBackRegion);
				gameArea.add(cardActor).expand().pad(5.0f);
			}
			gameArea.row().expandY();
		}

		/*
		 * Known fixed amount of space is available for the board. We are laying out the cards using a Table. Note that
		 * card layout is (or may be) non-square. The whole space must be consumed so that the rest of the screen lays
		 * out correctly, so need to "expand" cell that table is in. But to avoid card images being stretched, table
		 * size should be calculated given the available space and the final layout of the cards, and fixed within that
		 * cell.
		 */

		// Available space metrics
		final float availableSpaceWidth = 645.0f;
		final float availableSpaceHeight = 660.0f;
		final float availableSpaceAspect = availableSpaceWidth / availableSpaceHeight;

		// Board metrics
		final int boardColumns = difficulty.getBoardColumns();
		final int boardRows = difficulty.getBoardRows();
		final float boardAspect = boardColumns / (float) boardRows;

		// Relative aspect ratios of available space versus the layout
		final boolean heightConstrained = availableSpaceAspect > boardAspect;

		// Final size of board
		final float boardWidth;
		final float boardHeight;
		if (heightConstrained) {
			boardHeight = availableSpaceHeight;
			boardWidth = boardHeight * boardAspect;
		} else {
			boardWidth = availableSpaceWidth;
			boardHeight = availableSpaceWidth / boardAspect;
		}

		// Add game area, fix size and expand to consume all extra space in outer table.
		table.add(gameArea).width(boardWidth).height(boardHeight).expandX().expandY().colspan(2);

		// Primary score display

		// Player one info
		table.row().padTop(30.0f);

		playerOne = new PlayerScoreActor(playerConfiguration.getPlayerName(0), playerConfiguration.getPlayerColor(0), game.skin, highlightDrawable);
		table.add(playerOne).colspan(2).fillX();

		// Player one starts the game
		playerOne.setHighlight(true);

		// Buttons
		table.row().padTop(30.0f).padBottom(20.0f);
		// Back button
		final Button backButton = game.makeTexturedButton("quit_game_button", false);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				table.addAction(
					Actions.sequence(						
							Actions.fadeOut(0.25f),
							new ScreenChangeAction(game, GameScreen.this, new MainMenuScreen(game))
					)
				);
				
				//GameScreen.this.game.setScreen(new MainMenuScreen(GameScreen.this.game));
				//GameScreen.this.dispose();
			}
		});
		table.add(backButton).left();
		// Sound on/off
		final Button soundButton = game.makeTexturedButton("sound_button", true);
		soundButton.setChecked(game.sound);
		soundButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.sound = !game.sound;
				if (game.sound) {
					// TODO: Start music
				} else {
					// TODO: Stop music and any currently playing sounds
				}
				game.saveSoundToPrefs();
			}
		});

		table.add(soundButton).right();
		
		// Fade in, then redirect all input events to the Stage
		table.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		table.addAction(Actions.sequence(Actions.fadeIn(0.125f), new SetInputProcessorAction(stage)));
	}

	/** Update the score for the specified player. */
	private final void updateScore(final int player, final int score) {
		(player == 0 ? playerOne : playerTwo).updateScore(score);
	}

	/**
	 * An actor that represents a player's name and score. The actor contains convenience methods for highlighting and
	 * updating the score.
	 * 
	 * @author Charlie
	 */
	static class PlayerScoreActor extends SpinningTable {
		/** The label that holds the score; kept for future access. */
		private final Label scoreLabel;

		/** A drawable used as a background to highlight the actor. */
		private final Drawable highlightDrawable;

		/** Construct a new player and score display. */
		public PlayerScoreActor(final String playerName, final Color playerColor, final Skin skin,
				final Drawable highlightDrawable) {

			// Keep reference to the highlight drawable
			this.highlightDrawable = highlightDrawable;

			// Left edge, the player name
			final Label playerNameLabel = new Label(playerName, skin, "arcena48", playerColor);
			add(playerNameLabel).padLeft(10.0f);

			// Middle, an empty cell that takes up all remaining space
			add().expandX();

			// Right edge, the player score
			scoreLabel = new Label("", skin, "arcena48", playerColor);
			add(scoreLabel).padRight(10.0f);

			// Start off not highlighted
			setHighlight(false);
			// Start off at zero points
			updateScore(0);
		}

		/** Set whether or not to highlight this score display. */
		public void setHighlight(final boolean highlight) {
			if (highlight) {
				setBackground(highlightDrawable);
			} else {
				setBackground((Drawable)null);
			}
		}

		/** Update the score display to reflect the new score. */
		public void updateScore(final int score) {
			scoreLabel.setText(score + " Point" + (score != 1 ? "s" : ""));
		}
	}
}
