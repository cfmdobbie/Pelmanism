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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MyGame extends Game {

	private static final String TAG = MyGame.class.getSimpleName();

	// Virtual screen metrics
	static final int VIRTUAL_WIDTH = 720;
	static final int VIRTUAL_HEIGHT = 1000;
	private static final float VIRTUAL_ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;

	// Background colour
	static final Color BACKGROUND_COLOR = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);

	SpriteBatch batch;
	OrthographicCamera camera;
	final Rectangle viewport = new Rectangle();
	AssetManager manager;
	TextureAtlas uiAtlas;
	Skin skin;
	TextureAtlas simpleCardSet;
	TextureAtlas signsCardSet;
	TextureAtlas hardCardSet;

	/** Name of preferences file for state persistence. */
	private static final String PREFERENCES_NAME = "com.maycontainsoftware.pelmanism";
	Preferences mPrefs;
	
	// Number of players
	static final String PREF_PLAYERS = "number_of_players";
	static final String PLAYERS_ONE = "One";
	static final String PLAYERS_TWO = "Two";
	static final String PLAYERS_ONE_VS_CPU = "One_Vs_Cpu";
	static final String[] PLAYERS_OPTIONS = {PLAYERS_ONE, PLAYERS_TWO, PLAYERS_ONE_VS_CPU};
	static enum PlayersEnum {One, Two, One_Vs_Cpu};
	
	// Difficulty
	static final String PREF_DIFFICULTY = "difficulty";
	static final String DIFFICULTY_EASY = "Easy";
	static final String DIFFICULTY_MEDIUM = "Medium";
	static final String DIFFICULTY_HARD = "Hard";
	static final String[] DIFFICULTY_OPTIONS = {DIFFICULTY_EASY, DIFFICULTY_MEDIUM, DIFFICULTY_HARD};
	
	// Card sets
	static final String PREF_CARD_SET = "card_set";
	static final String CARD_SET_SIMPLE = "Simple";
	static final String CARD_SET_SIGNS = "Signs";
	static final String CARD_SET_HARD = "Hard";
	static final String[] CARD_SET_OPTIONS = {CARD_SET_SIMPLE, CARD_SET_SIGNS, CARD_SET_HARD};
	//static enum CardEnum { Simple, Signs, Hard };

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
}
