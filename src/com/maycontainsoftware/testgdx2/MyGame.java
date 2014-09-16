package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The Game implementation.
 * 
 * @author Charlie
 */
public class MyGame extends Game {

	/** A tag for logging purposes. */
	private static final String TAG = MyGame.class.getSimpleName();

	// Virtual screen metrics
	/** The width of the virtual render area. */
	public static final int VIRTUAL_WIDTH = 720;
	/** The height of the virtual render area. */
	public static final int VIRTUAL_HEIGHT = 1000;
	/** The aspect ratio of the virtual render area. */
	private static final float VIRTUAL_ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;

	// Background colour
	/** The app's background color, the default color the virtual render area is cleared to. */
	private static final Color BACKGROUND_COLOR = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);

	/**
	 * The app-global SpriteBatch. For performance reasons, a single SpriteBatch exists and is accessed from all Screens
	 * in the app,
	 */
	SpriteBatch batch;

	/** The app-global camera. This is used by all Screens. */
	OrthographicCamera camera;

	/** Rectangle that represents the glViewport. */
	final Rectangle viewport = new Rectangle();

	/** The asset manager used by the loading screen to load all assets not directly required by the loading screen. */
	AssetManager manager;

	/**
	 * The atlas that contains all UI-related assets. This does not include the Scene2D UI "skin" assets, but all custom
	 * graphics created to represent backgrounds, buttons etc.
	 */
	TextureAtlas uiAtlas;
	
	Sound[] cardTurnSounds;

	/** The Scene2D UI skin instance. */
	Skin skin;

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pelmanism";
	Preferences mPrefs;

	// Player configuration
	/** The name of the preference entry that holds the player configuration. */
	public static final String PREF_PLAYER_CONFIGURATION = "player_configuration";

	/**
	 * Enumeration of play configurations defined by the app.
	 * 
	 * @author Charlie
	 */
	public static enum PlayerConfiguration {
		// One player, solitaire play
		One(1, null, null),
		// Two player, hot seat
		Two(2, "Player Two", Color.BLUE),
		// One human player versus computer-controlled player
		One_Vs_Cpu(2, "Computer", Color.GRAY);

		/** The number of players in this mode. */
		final int numberOfPlayers;

		/** The name of the second player, if appropriate. */
		final String secondPlayerName;

		/** The color used to represent the second player, if appropriate. */
		final Color secondPlayerColor;

		/**
		 * Construct a new player configuration.
		 * 
		 * @param numberOfPlayers
		 * @param secondPlayerName
		 * @param secondPlayerColor
		 */
		private PlayerConfiguration(final int numberOfPlayers, final String secondPlayerName,
				final Color secondPlayerColor) {
			this.numberOfPlayers = numberOfPlayers;
			this.secondPlayerName = secondPlayerName;
			this.secondPlayerColor = secondPlayerColor;
		}

		/** Whether a second player (human or computer) exists. */
		final boolean secondPlayerExists() {
			return numberOfPlayers != 1;
		}
	};

	// Difficulty
	/** The name of the preference entry that holds the difficulty setting. */
	public static final String PREF_DIFFICULTY = "difficulty";

	/**
	 * Enumeration of the various difficulties implemented in the app. The difficulty affects board size, speed of
	 * gameplay and skill of computer player (if applicable.)
	 * 
	 * @author Charlie
	 */
	public static enum Difficulty {
		// Easy difficulty
		Easy(8),
		// Medium difficulty
		Medium(18),
		// Hard difficulty
		Hard(32);

		/** The number of pairs on the board. */
		private final int numberOfPairs;

		/**
		 * Construct a new difficulty setting.
		 * 
		 * @param numberOfPairs
		 */
		private Difficulty(final int numberOfPairs) {
			this.numberOfPairs = numberOfPairs;
		}

		/** The number of pairs on the board in this difficulty mode. */
		public final int getNumberOfPairs() {
			return numberOfPairs;
		}

		/** The total number of cards on the board in this difficulty mode. */
		public final int getTotalCards() {
			return numberOfPairs * 2;
		}

		/** The number of rows of cards on the board in this difficulty mode. */
		public final int getBoardRows() {
			// TODO: Calculations for non-square boards
			return (int) Math.sqrt(getTotalCards());
		}

		/** The number of columns of cards on the board in this difficulty mode. */
		public final int getBoardColumns() {
			// TODO: Calculations for non-square boards
			return (int) Math.sqrt(getTotalCards());
		}
	};

	// Card sets
	/** The name of the preference entry that holds the card set setting. */
	public static final String PREF_CARD_SET = "card_set";

	/**
	 * Enumeration of the different card sets available in this app.
	 * 
	 * @author Charlie
	 */
	public static enum CardSet {
		// Simple, colorful and easy to recognise symbols
		Simple("simple.atlas"),
		// UK road signs
		Signs("signs.atlas"),
		// Abstract black-and-white dot-and-line designs
		Hard("hard.atlas");

		/** The name of the atlas containing the card set. */
		final String atlasName;

		/** The name of the region in the atlas that contains the "card back" graphic. */
		// Note: card back region is currently consistently-named in all atlases, but this may not be the case in future
		// versions.
		final String backRegionName = "back";

		/**
		 * Construct a new card set configuration.
		 * 
		 * @param atlasName
		 */
		private CardSet(final String atlasName) {
			this.atlasName = atlasName;
		}
	};

	// Audio settings
	/** The name of the preference entry that holds the sound setting. */
	public static final String PREF_SOUND = "sound";

	/**
	 * The sound setting. Current implementation treats both music and effects the same, and just holds a value
	 * representing mute or non-mute.
	 */
	boolean sound;

	@Override
	public void create() {

		// Set up SpriteBatch
		batch = new SpriteBatch();

		// Set up camera
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		// Move (0,0) point to bottom left of virtual area
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);

		manager = new AssetManager();

		// Get reference to preferences file
		mPrefs = Gdx.app.getPreferences(PREFERENCES_NAME);

		// Load sound preference
		sound = getSoundFromPrefs();

		// Always start with the loading screen
		this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void resize(int width, int height) {

		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");

		// Calculate display aspect ratio
		final float displayAspectRatio = (float) width / (float) height;

		// Recalculate glViewport
		if (displayAspectRatio > VIRTUAL_ASPECT_RATIO) {
			// Display is wider than the game
			viewport.setSize(height * VIRTUAL_ASPECT_RATIO, height);
			viewport.setPosition((width - height * VIRTUAL_ASPECT_RATIO) / 2, 0);
		} else if (displayAspectRatio < VIRTUAL_ASPECT_RATIO) {
			// Display is taller than the game
			viewport.setSize(width, width / VIRTUAL_ASPECT_RATIO);
			viewport.setPosition(0, (height - width / VIRTUAL_ASPECT_RATIO) / 2);
		} else {
			// Display exactly matches game
			viewport.setSize(width, height);
			viewport.setPosition(0, 0);
		}

		// Pass resize() call to active Screen
		super.resize(width, height);
	}

	@Override
	public void render() {

		// Clear colour buffer to black
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Don't scissor this clear operation
		Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Update the camera
		camera.update();

		// Map rendered scene to centred viewport of correct aspect ratio
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
		// Scissor buffer operations to the viewport
		Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);

		// Clear virtual render area with background color
		Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Reset SpriteBatch color to white
		batch.setColor(Color.WHITE);

		// Pass render() call to active Screen
		super.render();
	}

	@Override
	public void dispose() {

		// Dispose of stuff
		batch.dispose();
		manager.dispose();
		skin.dispose();

		// Pass render() call to active Screen
		super.dispose();
	}

	/**
	 * Make a Button out of a pair of texture regions loaded from an atlas.
	 * 
	 * @param textureRegionPrefix
	 *            Region name prefix in TextureAtlas.
	 * @param toggle
	 *            True if Button has a checked state, false otherwise.
	 * @return The created Button
	 */
	final Button makeTexturedButton(String textureRegionPrefix, boolean toggle) {
		final Drawable off = new TextureRegionDrawable(uiAtlas.findRegion(textureRegionPrefix + "_off"));
		final Drawable on = new TextureRegionDrawable(uiAtlas.findRegion(textureRegionPrefix + "_on"));
		final Button button = toggle ? new Button(off, on, on) : new Button(off, on);
		return button;
	}

	/** Load CardSet based on saved value in preferences, or default to Simple if preference not available. */
	public final CardSet getCardSetFromPrefs() {
		final String pref = mPrefs.getString(MyGame.PREF_CARD_SET, CardSet.Simple.toString());
		return CardSet.valueOf(pref);
	}

	/** Load Difficulty based on saved value in preferences, or default to Easy if preference not available. */
	public final Difficulty getDifficultyFromPrefs() {
		final String pref = mPrefs.getString(MyGame.PREF_DIFFICULTY, Difficulty.Easy.toString());
		return Difficulty.valueOf(pref);
	}

	/** Load PlayerConfiguration based on saved value in preferences, or default to One if preference not available. */
	public final PlayerConfiguration getPlayerConfigurationFromPrefs() {
		final String pref = mPrefs.getString(MyGame.PREF_PLAYER_CONFIGURATION, PlayerConfiguration.One.toString());
		return PlayerConfiguration.valueOf(pref);
	}

	/** Load audio setting based on saved value in preferences, or default to on if preference not available. */
	public final boolean getSoundFromPrefs() {
		return mPrefs.getBoolean(MyGame.PREF_SOUND, true);
	}

	/** Save current sound value to app's preferences file. */
	public final void saveSoundToPrefs() {
		mPrefs.putBoolean(PREF_SOUND, sound);
		mPrefs.flush();
	}

	/**
	 * Save (name,value) pair to app's preferences file.
	 * 
	 * @param name
	 *            The key name in the preferences file.
	 * @param value
	 *            The value to set.
	 */
	public final void savePreference(String name, String value) {
		mPrefs.putString(name, value);
		mPrefs.flush();
	}

	public void playCardTurnSound() {
		if(sound) {
			cardTurnSounds[MathUtils.random(cardTurnSounds.length - 1)].play();
		}
	}
	
	public void playCardDealSound() {
		if(sound) {
			manager.get("sound/cardFan1.mp3", Sound.class).play();
		}
	}
}
