package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

	/** The computer player. */
	private final PelmanismAI ai;

	/** Map of Card to CardActor, used to relate AI's selections to Scene2D Actors. */
	private final Map<Card, CardActor> cardToCardActor = new HashMap<Card, CardActor>();

	// Graphical elements needed for future access

	/** Player one's score display. */
	private PlayerScoreActor playerOne;

	/** Player two's score display. */
	private PlayerScoreActor playerTwo;

	// XXX

	static class PelmanismAI {
		// Needs to have a memory of previous picks
		// Needs to have an *imperfect* memory at times
		// Needs to vary based on difficulty level

		/** Difficulty level of the AI. */
		private final Difficulty difficulty;

		/** The Pelmanism game model. */
		private final Pelmanism model;

		/** List of all cards currently in the game. Will need to be updated wrt matched cards before use. */
		private final Set<Card> allCards = new HashSet<Card>();

		/** List of all cards that have been seen. Will need to be updated wrt matched cards before use. */
		private final Set<Card> seenCards = new HashSet<Card>();

		/** After an invocation to pickCards(), the first card picked. */
		private Card firstCard;

		/** After an invocation to pickCards(), the second card picked. */
		private Card secondCard;

		/** Random number generator. */
		private final Random random = new Random();

		/** Construct a new PelmanismAI object. */
		public PelmanismAI(final Difficulty difficulty, final Pelmanism model) {
			this.model = model;
			this.difficulty = difficulty;

			// Generate a list of all cards, but DON'T LOOK AT THEM! :-)
			for (int i = 0; i < model.getNumberOfCards(); i++) {
				allCards.add(model.getCard(i));
			}
		}

		/** Log a card as having been seen. */
		public void cardSeen(Card card) {
			seenCards.add(card);
		}

		/** Update information we know about the cards on the table. */
		private void updateCards() {
			
			// Remove any cards that have been matched
			Iterator<Card> i = allCards.iterator();
			while (i.hasNext()) {
				Card c = i.next();
				if (c.isMatched()) {
					i.remove();
					// If it had been seen previously, remove it from there as well
					if(seenCards.contains(c)) {
						seenCards.remove(c);
					}
				}
			}
			
			// Work out what cards are unknown
			Set<Card> unknownCards = new HashSet<Card>();
			for(Card c : allCards) {
				if(!seenCards.contains(c)) {
					unknownCards.add(c);
				}
			}
			
			// Work out whether any pairs are known
			
			// Arrange cards by pairId
			Map<Integer, Set<Card>> cardsByPairId = new HashMap<Integer, Set<Card>>();
			for(Card c : seenCards) {
				int pairId = c.getPairId();
				if(cardsByPairId.containsKey(pairId)) {
					cardsByPairId.get(pairId).add(c);
				} else {
					cardsByPairId.put(pairId, new HashSet<Card>(Arrays.asList(c)));
				}
			}
			// Check for any known pairs
			List<Integer> knownPairs = new ArrayList<Integer>();
			for(Integer pairId : cardsByPairId.keySet()) {
				if(cardsByPairId.get(pairId).size() == 2) {
					knownPairs.add(pairId);
				}
			}
			if(!knownPairs.isEmpty()) {
				// We know about at least one pair!
			}
			
		}

		public Card pickFirstCard() {
			// Moronic AI - pick a random card
			Card[] cards = allCards.toArray(new Card[]{});
			firstCard = cards[random.nextInt(allCards.size())];
			return firstCard;
		}

		public Card pickSecondCard() {
			// Moronic AI - pick a random card
			Card[] cards = allCards.toArray(new Card[]{});
			do {
				secondCard = cards[random.nextInt(allCards.size())];
			} while (firstCard == secondCard);
			return secondCard;
		}
	}

	/** Enumeration representing game state. */
	static enum GameState {
		PendingFirstPick,
		PendingSecondPick,
		CardsPicked,
		GameOver,
	}

	/** The game state. */
	private GameState gameState = GameState.PendingFirstPick;

	/** The actor representing the first card to be picked. */
	private CardActor firstPick = null;

	/** The actor representing the second card to be picked. */
	private CardActor secondPick = null;

	private boolean isComputerTurn() {
		return !playerConfiguration.isPlayerUserControlled(model.getCurrentPlayerId());
	}

	private final void handleFirstPick(final CardActor firstPick) {
		// Remember that this is the first chosen card
		this.firstPick = firstPick;
		// Flip the card over
		firstPick.addAction(firstPick.actionWinkToFront());
		// Notify the AI of the new card
		ai.cardSeen(firstPick.card);
		// Play sound effect
		game.playCardTurnSound();
		// Update state
		gameState = GameState.PendingSecondPick;

		if (isComputerTurn()) {
			// Computer's turn!
			stage.addAction(Actions.sequence(Actions.delay(0.25f), new Action() {
				@Override
				public boolean act(float delta) {
					handleSecondPick(cardToCardActor.get(ai.pickSecondCard()));
					return true;
				}
			}));
		}
	}

	private final void handleSecondPick(final CardActor secondPick) {
		// Remember that this is the second chosen card
		this.secondPick = secondPick;
		// Flip the card over
		secondPick.addAction(Actions.sequence(secondPick.actionWinkToFront(), new Action() {
			@Override
			public boolean act(float delta) {
				processTurn();
				return true;
			}
		}));
		// Notify the AI of the new card
		ai.cardSeen(secondPick.card);
		// Play sound effect
		game.playCardTurnSound();
		// Update state
		gameState = GameState.CardsPicked;
	}

	/** Given two card picks, process the turn in the game model and update the interface as required. */
	private void processTurn() {

		// The results of submitting the turn
		final Turn turn = model.turn(firstPick.card, secondPick.card);

		if (turn.isMatch()) {

			// Play sound effect
			game.playCardMatchSound();

			// Score changed, update label
			final int playerId = turn.getPlayerId();
			updateScore(playerId, model.getPlayerScore(playerId));

			firstPick.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.25f)));
			secondPick.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(0.25f), new Action() {
				@Override
				public boolean act(float delta) {
					postTurn(turn);
					return true;
				}
			}));

		} else {

			firstPick.addAction(firstPick.actionDelayedWinkToBack());
			secondPick.addAction(Actions.sequence(secondPick.actionDelayedWinkToBack(), new Action() {
				@Override
				public boolean act(float delta) {
					postTurn(turn);
					return true;
				}
			}));

		}
	}

	private void postTurn(final Turn turn) {

		// TODO: Need to move timing-related float primitives to constant fields

		// Update highlights as required
		if (model.getNumberOfPlayers() > 1) {
			playerOne.setHighlight(model.getCurrentPlayerId() == 0);
			playerTwo.setHighlight(model.getCurrentPlayerId() == 1);
		}

		if (!turn.isMatch()) {
			// Update game state
			gameState = GameState.PendingFirstPick;
		} else if (turn.isGameOver()) {
			// A match, and game is over

			// TODO: Game over stuff to go here
			gameOverFlash.setColor(1.0f, 1.0f, 1.0f, 0.0f);
			gameOverFlash.setVisible(true);
			gameOverFlash.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions.delay(1.0f), new ScreenChangeAction(
					game, GameScreen.this, new GameOverScreen(game))));
			gameState = GameState.GameOver;
		} else {
			gameState = GameState.PendingFirstPick;
		}

		if (gameState == GameState.PendingFirstPick && isComputerTurn()) {
			// Computer's turn!
			stage.addAction(Actions.sequence(Actions.delay(0.5f), new Action() {
				@Override
				public boolean act(float delta) {
					ai.updateCards();
					handleFirstPick(cardToCardActor.get(ai.pickFirstCard()));
					return true;
				}
			}));
		}
	}

	/**
	 * Object representing the actual card on the screen.
	 * 
	 * @author Charlie
	 */
	class CardActor extends Image {

		// Game state

		/** The card represented by this actor. */
		private final Card card;

		/** This card's front texture. */
		private final TextureRegion cardTexture;

		/** This card's back texture. */
		private final TextureRegion cardBackTexture;

		/**
		 * Construct a new CardActor object.
		 * 
		 * @param index
		 * @param model
		 * @param cardTexture
		 * @param cardBackTexture
		 */
		public CardActor(final Card card, final TextureRegion cardTexture, final TextureRegion cardBackTexture) {

			// Pass default/initial texture to superclass' constructor
			super(cardBackTexture);

			this.card = card;
			this.cardTexture = cardTexture;
			this.cardBackTexture = cardBackTexture;

			// Touch events on the card are intercepted by an InputListener.
			this.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

					if (isComputerTurn()) {
						// Current player is the computer - ignore input
						return false;
					}

					switch (gameState) {
					case PendingFirstPick:
						if (!CardActor.this.card.isMatched()) {
							handleFirstPick(CardActor.this);
						}
						break;
					case PendingSecondPick:
						if (CardActor.this != firstPick && !CardActor.this.card.isMatched()) {
							handleSecondPick(CardActor.this);
						}
						break;
					case CardsPicked:
						// Cards are animating - ignore all input
						// TODO: This means you can't pick a new card while it is animating to back. Is this a problem?
						break;
					case GameOver:
						// Game is over - ignore all input. It will shortly change screen
						break;
					default:
						// Cannot reach this
						Gdx.app.log(TAG, "CardActor::touchDown::default - ERROR - Unreachable state");
						break;
					}

					return true;
				}
			});
		}

		/** Switch the current texture region for a different region. */
		/*
		 * private final void switchTexture(final TextureRegion region) { final TextureRegionDrawable drawable =
		 * (TextureRegionDrawable) (this.getDrawable()); drawable.setRegion(region); }
		 */

		/** Return an Action that immediately switches the card texture region. */
		private final Action actionSpecifiedTexture(final TextureRegion region) {
			return new Action() {
				@Override
				public boolean act(float delta) {
					final TextureRegionDrawable drawable = (TextureRegionDrawable) (CardActor.this.getDrawable());
					drawable.setRegion(region);
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
		/*
		 * private final Action actionWinkToBack() { return Actions.sequence(actionWinkOut(), actionBackTexture(),
		 * actionWinkIn()); }
		 */

		/** Return an action that pauses then flips card to the back. */
		private final Action actionDelayedWinkToBack() {
			return Actions.sequence(Actions.delay(1.0f), actionWinkOut(), actionBackTexture(), actionWinkIn());
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

		// Create AI player
		// TODO: Only when an AI player exists?
		ai = new PelmanismAI(difficulty, model);

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
		// Gdx.input.setInputProcessor(stage);

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
				final TextureRegion frontTexture = cardRegions[card.getPairId()];
				final CardActor cardActor = new CardActor(card, frontTexture, cardBackRegion);
				gameArea.add(cardActor).expand().pad(5.0f);

				// Add the new Card,CardActor pair to the map
				cardToCardActor.put(card, cardActor);
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

		playerOne = new PlayerScoreActor(playerConfiguration.getPlayerName(0), playerConfiguration.getPlayerColor(0),
				game.skin, highlightDrawable);
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

				table.addAction(Actions.sequence(Actions.fadeOut(0.25f), new ScreenChangeAction(game, GameScreen.this,
						new MainMenuScreen(game))));

				// GameScreen.this.game.setScreen(new MainMenuScreen(GameScreen.this.game));
				// GameScreen.this.dispose();
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

		// Game over flash panel
		gameOverFlash = new Table();
		gameOverFlash.setFillParent(true);
		// TODO: Needs to be something more complicated than an Image
		gameOverFlash.add(new Image(cardBackRegion)).expandX().fillX();
		gameOverFlash.setVisible(false);
		gameOverFlash.debug();
		stage.addActor(gameOverFlash);

		// Fade in, then redirect all input events to the Stage
		table.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		table.addAction(Actions.sequence(Actions.fadeIn(0.125f), new SetInputProcessorAction(stage)));
	}

	Table gameOverFlash;

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
				setBackground((Drawable) null);
			}
		}

		/** Update the score display to reflect the new score. */
		public void updateScore(final int score) {
			scoreLabel.setText(score + " Point" + (score != 1 ? "s" : ""));
		}
	}
}
