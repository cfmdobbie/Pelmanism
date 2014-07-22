package com.maycontainsoftware.pelmanism;

import java.util.ArrayList;
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
	final Random mRandom = new Random();

	// Camera
	private OrthographicCamera mCamera;

	// Drawing engine
	private SpriteBatch mBatch;

	// Assets
	private AssetManager mAssetManager;

	// UI Components - note that these are always processed in insertion order
	private List<Component> components;
	private Component logo;
	private Component newGame;

	// Texture name constants
	static final String BACKGROUND_TEXTURE = "felt_green_tiled.png";
	static final String TILESET_TEXTURE = "test_card_4.png";
	static final String UI_TEXTURE = "ui_elements_1.png";

	// Current display metrics - updated on resize()
	int mDisplayWidth;
	int mDisplayHeight;

	// Alpha value used for fading in/out of UI elements
	float mUiAlpha;

	// User Interface State Engine
	enum UiState {
		Loading, LoadingToGame, Game, GameToOptions_Game, GameToOptions_Options, Options, OptionsToGame_Options, OptionsToGame_Game
	}

	// The UI state
	UiState mUiState = UiState.Loading;

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
		components.add(logo = new LogoComponent(this));
		components.add(new OptionsComponent(this));
		components.add(newGame = new NewGameComponent(this));
		components.add(new CancelComponent(this));
		components.add(new GameAreaComponent(this));
		components.add(new OptionsAreaComponent(this));
	}
	
	final Rectangle calculateEmptyArea() {
		final int border = 16;
		float top = logo.rect.y;
		float bottom = newGame.rect.y + newGame.rect.height;
		return new Rectangle(border, bottom + border, mDisplayWidth - border * 2, top - bottom - border * 2);
	}

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
	public final void dispose() {
		Gdx.app.log(TAG, "dispose()");

		// Dispose of sprite batch
		mBatch.dispose();

		// Dispose of textures
		// TODO: Dispose of all textures
	}

	private final void doInput(float delta) {
		if (Gdx.input.justTouched()) {
			// Get touch location
			Vector2 touch = getTouchLocation();

			// Dispatch touch event to UI components
			for (Component c : components) {
				if (c.rect.contains(touch)) {
					c.input(touch);
				}
			}
		}
	}

	private final void doUpdate(float delta) {

		switch (mUiState) {
		case Loading:
			// Continue asset loading
			boolean finished = mAssetManager.update();
			if (finished) {
				// Assets have been loaded!

				for (Component c : components) {
					// Inform UI Components that assets are loaded
					c.onAssetsLoaded(mAssetManager);
					// Update UI Component sizes
					c.resize(mDisplayWidth, mDisplayHeight);
				}

				// Start to fade in game interface
				mUiState = UiState.LoadingToGame;
				mUiAlpha = 0.0f;
			}
			break;
		case LoadingToGame:
			mUiAlpha += delta * 4;
			if (mUiAlpha >= 1.0f) {
				mUiState = UiState.Game;
			}
			break;
		case Game:
			// Game is running, update all UI components
			for (Component c : components) {
				c.update(delta);
			}
			break;
		case GameToOptions_Game:
			// Continue to fade out game screen
			mUiAlpha -= delta * 4;
			if (mUiAlpha <= 0.0f) {
				mUiState = UiState.GameToOptions_Options;
				mUiAlpha = 0.0f;
			}
			break;
		case GameToOptions_Options:
			// Continue to fade in options screen
			mUiAlpha += delta * 4;
			if (mUiAlpha >= 1.0f) {
				mUiState = UiState.Options;
			}
			break;
		case Options:
			// No-op
			break;
		case OptionsToGame_Options:
			// Continue to fade out options screen
			mUiAlpha -= delta * 4;
			if (mUiAlpha <= 0.0f) {
				mUiState = UiState.OptionsToGame_Game;
				mUiAlpha = 0.0f;
			}
			break;
		case OptionsToGame_Game:
			// Continue to fade in game screen
			mUiAlpha += delta * 4;
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

		// End batch
		mBatch.end();
	}

	void drawTextureByRect(TextureRegion textureRegion, Rectangle rect) {
		mBatch.draw(textureRegion, rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();

		doInput(delta);
		doUpdate(delta);
		doRender(delta);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");

		// Save screen dimensions
		mDisplayWidth = width;
		mDisplayHeight = height;

		// Update camera wrt new screen dimensions
		mCamera.setToOrtho(false, width, height);

		// Position UI Components
		if (mUiState != UiState.Loading) {
			for (Component c : components) {
				c.resize(width, height);
			}
		}
	}

	private final Vector2 getTouchLocation() {
		Vector3 pos = new Vector3();
		pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		mCamera.unproject(pos);
		return new Vector2(pos.x, pos.y);
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
