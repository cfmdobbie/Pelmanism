package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

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

	/** Sound objects representing the sound of a card being turned over. */
	Sound[] cardTurnSounds;

	/** The Scene2D UI skin instance. */
	Skin skin;

	private Drawable background;

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pelmanism";
	Preferences mPrefs;

	// Player configuration
	/** The name of the preference entry that holds the player configuration. */
	public static final String PREF_PLAYER_CONFIGURATION = "player_configuration";

	// Difficulty
	/** The name of the preference entry that holds the difficulty setting. */
	public static final String PREF_DIFFICULTY = "difficulty";

	// Card sets
	/** The name of the preference entry that holds the card set setting. */
	public static final String PREF_CARD_SET = "card_set";

	// Audio settings
	/** The name of the preference entry that holds the sound setting. */
	private static final String PREF_SOUND = "sound";

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

		// Get the background drawable
		background = createBackgroundDrawable();

		// Always start with the loading screen
		this.setScreen(new LoadingScreen(this));

		Gdx.app.log(TAG, "screen density = " + Gdx.graphics.getDensity());
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
		// TODO: This doesn't appear to be necessary - check this!
		batch.setColor(Color.WHITE);

		// Render background
		batch.begin();
		background.draw(batch, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		batch.end();

		// Pass render() call to active Screen
		super.render();
	}

	@Override
	public void dispose() {

		// Dispose of stuff
		if (batch != null) {
			batch.dispose();
		}
		if (manager != null) {
			manager.dispose();
		}
		if (skin != null) {
			skin.dispose();
		}

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

	/** Play a random card-turn sound, if sounds are enabled. */
	public void playCardTurnSound() {
		if (sound) {
			cardTurnSounds[MathUtils.random(cardTurnSounds.length - 1)].play();
		}
	}

	/** Play the card dealing sound, if sounds are enabled. */
	public void playCardDealSound() {
		if (sound) {
			manager.get("cardFan1.mp3", Sound.class).play();
		}
	}

	/** Play the "success" sound, if sounds are enabled. */
	public void playSuccessSound() {
		if (sound) {
			manager.get("success.mp3", Sound.class).play();
		}
	}

	/** Play the card match sound, if sounds are enabled. */
	public void playCardMatchSound() {
		if (sound) {
			manager.get("ting.mp3", Sound.class).play();
		}
	}

	private final Drawable createBackgroundDrawable() {

		// Texture containing color swatch to tint background pattern with
		final Texture backgroundColorTexture = new Texture(Gdx.files.internal("background_color.png"));

		// Background pattern to repeat over screen
		final Texture backgroundPatternTexture = new Texture(Gdx.files.internal("background_pattern.png"));
		final TextureRegion backgroundPatternTextureRegion = new TextureRegion(backgroundPatternTexture);

		// Drawable to render the background
		final Drawable background = new TiledDrawable(backgroundPatternTextureRegion) {
			@Override
			public void draw(final SpriteBatch batch, final float x, final float y, final float width,
					final float height) {
				// Draw color swatch
				batch.draw(backgroundColorTexture, x, y, width, height);

				// Save blending state, enable blending
				final boolean enabled = batch.isBlendingEnabled();
				batch.enableBlending();

				// Call superclass to draw tiled background graphic
				super.draw(batch, x, y, width, height);

				// Restore blending state
				if (!enabled) {
					batch.disableBlending();
				}
			}
		};

		return background;
	}
}
