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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.testgdx2.MyGame.CardSet;
import com.maycontainsoftware.testgdx2.MyGame.Difficulty;
import com.maycontainsoftware.testgdx2.MyGame.PlayerConfiguration;

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

	/** Player one's score label. */
	private Label playerOneScoreLabel;
	/** Player two's score label. */
	private Label playerTwoScoreLabel;

	/**
	 * Object representing the actual card on the screen.
	 * 
	 * @author Charlie
	 */
	static class CardActor extends Image {
		/** This card's index, an integer from 0 to N-1 where N is the total number of cards on the board. */
		final int index;
		/** A reference to the Screen. */
		final GameScreen screen;
		/** A reference to the game model. */
		final Pelmanism model;
		/** This card's front texture. */
		final TextureRegion cardTexture;
		/** This card's back texture. */
		final TextureRegion cardBackTexture;

		private static CardActor firstPick = null;

		/**
		 * Construct a new CardActor object.
		 * 
		 * @param index
		 * @param model
		 * @param cardTexture
		 * @param cardBackTexture
		 */
		public CardActor(final int index, final GameScreen screen, final TextureRegion cardTexture,
				final TextureRegion cardBackTexture) {

			// Pass default/initial texture to superclass' constructor
			super(cardBackTexture);

			this.index = index;
			this.screen = screen;
			this.model = screen.model;
			this.cardTexture = cardTexture;
			this.cardBackTexture = cardBackTexture;

			// Touch events on the card are intercepted by an InputListener.
			this.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

					switch (model.getGameState()) {
					case PendingFirstPick:
						if (model.turnCard(index)) {
							// Game state will now be "PendingSecondPick"

							// Remember that this is the first chosen card
							firstPick = CardActor.this;

							// Flip the card over
							CardActor.this.addAction(actionWinkToFront());
						}
						break;
					case PendingSecondPick:

						if (model.turnCard(index)) {
							// Game state will now be "CardsChosen"

							if (model.isMatch()) {

								final Action[] actions = new Action[] {
										// Flip the card over
										actionWinkToFront(),
										// Hold cards visible for a moment
										Actions.delay(0.5f),
										// Update score
										new Action() {
											@Override
											public boolean act(float delta) {
												// Determine current player
												final int currentPlayer = model.getCurrentPlayer();
												// Determine player's updated score
												final int newScore = model.getPlayerScore(currentPlayer);
												// Update UI
												screen.updateScore(currentPlayer, newScore);
												return true;
											}
										},
										// Fade out both chosen cards
										// TODO: Move to a utility method?
										new Action() {
											@Override
											public boolean act(float delta) {
												// Fade out first card
												firstPick.addAction(Actions.fadeOut(0.25f));
												return true;
											}
										}, Actions.fadeOut(0.25f),
										// Accept picks
										// TODO: Move to a utility method?
										new Action() {
											@Override
											public boolean act(float delta) {
												model.acceptPicks();
												// Game state will now be either "PendingFirstPick" or "GameOver"
												// TODO: Check for GameOver state
												return true;
											}
										}, };
								CardActor.this.addAction(Actions.sequence(actions));
								
							} else {

								final Action[] actions = new Action[] {
										// Flip the card over
										actionWinkToFront(),
										// Hold cards visible for a moment
										Actions.delay(0.5f),
										// Flip both cards back over
										// First card
										new Action() {
											@Override
											public boolean act(float delta) {
												firstPick.addAction(firstPick.actionWinkToBack());
												return true;
											}
										},
										// Second card
										actionWinkToBack(),
										// Accept picks
										new Action() {
											@Override
											public boolean act(float delta) {
												model.acceptPicks();
												// Game state will now be "PendingFirstPick"
												return true;
											}
										} };
								CardActor.this.addAction(Actions.sequence(actions));
							}
						}
						break;
					case CardsChosen:
						// We're mid-animation at the moment - ignore input
						break;
					case GameOver:
						// Game is over - ignore input
						break;
					default:
						// TODO: How to handle this exceptional condition?
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
		model = new Pelmanism(playerConfiguration.numberOfPlayers, difficulty.getTotalCards());

		// Determine board size
		final int numberOfPairs = model.getNumberOfPairs();

		// Load graphic assets
		atlas = game.manager.get(cardSet.atlasName, TextureAtlas.class);
		cardBackRegion = atlas.findRegion(cardSet.backRegionName);
		// Given numberOfPairs, return that number of unique random TextureRegions from appropriate TextureAtlas.
		// TODO: Want to select a new subset of regions for each game; need to rearrange this
		cardRegions = selectCardTextures(numberOfPairs);

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);
		// Use global camera
		stage.setCamera(game.camera);
		// Redirect all input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Create UI elements
		createUi();
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
		// TODO: Note that the TiledDrawable doesn't render correctly; this should be replaced.
		final TextureRegion background = game.uiAtlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));

		// Secondary score display

		if (playerConfiguration.secondPlayerExists()) {
			// Will need a second score display

			table.row().padTop(20.0f);
			final SpinningTable playerTwoTable = new SpinningTable();
			// p2Table.debug();
			playerTwoTable.add(new Label(playerConfiguration.secondPlayerName, game.skin, "archristy48",
					playerConfiguration.secondPlayerColor));
			playerTwoTable.add().expandX();
			// Need to be able to update points on the fly, so need access to this Label
			playerTwoScoreLabel = new Label("0 Points", game.skin, "archristy48", playerConfiguration.secondPlayerColor);
			playerTwoTable.add(playerTwoScoreLabel);
			table.add(playerTwoTable).colspan(2).fillX();

			// N.B. TiledDrawable leaves <1px gaps, so this is not usable.
			// p2Table.setBackground(new TiledDrawable(game.uiAtlas.findRegion("yellow")));
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
				final TextureRegion cardRegion = cardRegions[model.getCard(cardIndex)];
				final CardActor cardActor = new CardActor(cardIndex, this, cardRegion, cardBackRegion);
				gameArea.add(cardActor).expand().pad(5.0f);
			}
			gameArea.row().expandY();
		}

		// Game area is fixed in size, but must take up all remaining space in table.
		// Game area should be square to avoid rendering cards out-of-aspect.
		// Hard-coded table size here, as that's the easiest way to do it.
		table.add(gameArea).width(645.0f).height(645.0f).expandX().expandY().colspan(2);

		// Primary score display

		// Player one info
		table.row().padTop(30.0f);
		final SpinningTable playerOneTable = new SpinningTable();
		// p1Table.debug();
		playerOneTable.add(new Label("Player One", game.skin, "archristy48", Color.RED));
		playerOneTable.add().expandX();
		// Need to be able to update points on the fly, so need access to this Label
		playerOneScoreLabel = new Label("0 Points", game.skin, "archristy48", Color.RED);
		playerOneTable.add(playerOneScoreLabel);
		table.add(playerOneTable).colspan(2).fillX();

		// Buttons
		table.row().padTop(30.0f).padBottom(20.0f);
		// Back button
		final Button backButton = game.makeTexturedButton("quit_game_button", false);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameScreen.this.game.setScreen(new MainMenuScreen(GameScreen.this.game));
				GameScreen.this.dispose();
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
	}

	/** Update the score for the specified player. */
	private final void updateScore(final int player, final int score) {
		(player == 0 ? playerOneScoreLabel : playerTwoScoreLabel).setText(score + " points");
	}
}
