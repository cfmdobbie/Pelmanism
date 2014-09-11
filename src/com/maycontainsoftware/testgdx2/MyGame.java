package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyGame extends Game {

	private static final String TAG = MyGame.class.getSimpleName();

	// Virtual screen metrics
	public static final int VIRTUAL_WIDTH = 720;
	public static final int VIRTUAL_HEIGHT = 1000;
	private static final float VIRTUAL_ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;

	// Background colour
	static final Color BACKGROUND_COLOR = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);

	SpriteBatch batch;
	OrthographicCamera camera;
	final Rectangle viewport = new Rectangle();
	AssetManager manager;
	TextureAtlas uiAtlas;
	Skin skin;

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pelmanism";
	Preferences mPrefs;

	// Number of players
	public static final String PREF_PLAYER_CONFIGURATION = "player_configuration";

	public static enum PlayerConfiguration {
		One(1, null, null),
		Two(2, "Player Two", Color.BLUE),
		One_Vs_Cpu(2, "Computer", Color.GRAY);

		final int numberOfPlayers;
		final String secondPlayerName;
		final Color secondPlayerColor;

		private PlayerConfiguration(final int numberOfPlayers, final String secondPlayerName, final Color secondPlayerColor) {
			this.numberOfPlayers = numberOfPlayers;
			this.secondPlayerName = secondPlayerName;
			this.secondPlayerColor = secondPlayerColor;
		}

		final boolean secondPlayerExists() {
			return numberOfPlayers != 1;
		}
	};

	// Difficulty
	public static final String PREF_DIFFICULTY = "difficulty";

	public static enum Difficulty {
		Easy(4),
		Medium(6),
		Hard(8);

		private final int boardSize;

		private Difficulty(final int boardSize) {
			this.boardSize = boardSize;
		}
		
		public final int getTotalCards() {
			return getBoardRows() * getBoardColumns();
		}
		public final int getBoardRows() {
			return boardSize;
		}
		public final int getBoardColumns() {
			return boardSize;
		}
	};

	// Card sets
	public static final String PREF_CARD_SET = "card_set";

	public static enum CardSet {
		Simple("simple.atlas"),
		Signs("signs.atlas"),
		Hard("hard.atlas");

		final String atlasName;
		final String backRegionName = "back";

		private CardSet(final String atlasName) {
			this.atlasName = atlasName;
		}
	};
	
	// Audio settings
	public static final String PREF_SOUND = "sound";
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
	
	public final void saveSoundToPrefs() {
		mPrefs.putBoolean(PREF_SOUND, sound);
		mPrefs.flush();
	}
	
}
