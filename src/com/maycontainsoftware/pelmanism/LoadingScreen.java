package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * The first displayed screen, which is non-interactive and exists solely to load assets in the background without
 * blocking the UI thread.
 * 
 * @author Charlie
 */
public class LoadingScreen implements Screen {

	/** Whether debug output should be logged. */
	private static final boolean DEBUG = false;

	/** Tag, for logging purposes. */
	private static final String TAG = LoadingScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	private final MyGame game;

	/**
	 * The Texture that contains the loading graphics. This texture is assumed to contain four frames of animation,
	 * stacked vertically.
	 */
	private Texture loadingTexture;

	/** This Screen's Stage. */
	private Stage stage;

	/** Names of the card-turn sound effects. */
	private final String[] cardTurnSoundNames = { "cardPlace1.mp3", "cardSlide4.mp3", "cardSlide5.mp3",
			"cardSlide6.mp3", "cardSlide7.mp3", "cardSlide8.mp3", };

	/**
	 * Construct a new LoadingScreen object.
	 * 
	 * @param game
	 */
	public LoadingScreen(final MyGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		System.out.println("LoadingScreen.show()");

		// Manually load any assets required for the loading screen
		loadingTexture = new Texture(Gdx.files.internal("loading.png"));

		// Create the Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);
		stage.addActor(new LoadingActor(loadingTexture));

		// Load assets in AssetManager
		// Texture atlases
		game.manager.load("ui.atlas", TextureAtlas.class);
		game.manager.load("simple.atlas", TextureAtlas.class);
		game.manager.load("signs.atlas", TextureAtlas.class);
		game.manager.load("hard.atlas", TextureAtlas.class);
		// Sound effects
		game.manager.load("cardFan1.mp3", Sound.class);
		for (final String name : cardTurnSoundNames) {
			game.manager.load(name, Sound.class);
		}
		game.manager.load("ting.mp3", Sound.class);
		game.manager.load("success.mp3", Sound.class);
		game.manager.load("win.mp3", Sound.class);
		game.manager.load("lose.mp3", Sound.class);
		// UI skin
		game.manager.load("uiskin.json", Skin.class);
	}

	@Override
	public void render(final float delta) {

		// Update and render the Stage
		stage.act();
		stage.draw();

		// Continue to load assets
		if (game.manager.update()) {
			// Assets have been loaded!
			if (DEBUG) {
				Gdx.app.log(TAG, "game.manager.update() = true");
			}

			// Perform any post-load tasks
			// Get reference to UI assets
			game.uiAtlas = game.manager.get("ui.atlas", TextureAtlas.class);
			game.skin = game.manager.get("uiskin.json", Skin.class);
			// Get references to sound effects
			game.cardTurnSounds = new Sound[cardTurnSoundNames.length];
			for (int i = 0; i < cardTurnSoundNames.length; i++) {
				game.cardTurnSounds[i] = game.manager.get(cardTurnSoundNames[i], Sound.class);
			}

			// Jump to main menu
			game.setScreen(new MainMenuScreen(game));

			// Get rid of loading screen
			this.dispose();
		}
	}

	@Override
	public void resize(final int width, final int height) {
		// Update Stage's viewport calculations
		if (DEBUG) {
			Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		}
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, game.viewport.x, game.viewport.y,
				game.viewport.width, game.viewport.height);
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
		loadingTexture.dispose();
		stage.dispose();
	}

	/**
	 * The Actor that displays the loading animation.
	 * 
	 * @author Charlie
	 */
	static class LoadingActor extends Actor {
		/** The Animation that is to be played. */
		private final Animation animation;

		/** The time the animation has been playing, in seconds. */
		float animTime = 0.0f;

		/**
		 * Construct a new LoadingActor.
		 * 
		 * @param texture
		 *            The Texture containing the four-frame animation, with frames stacked vertically.
		 */
		public LoadingActor(final Texture texture) {
			// Simple four-frame animation.
			// Switches frames every 1/10 second.
			final TextureRegion[] frames = new TextureRegion[4];
			for (int i = 0; i < 4; i++) {
				frames[i] = new TextureRegion(texture, 0.0f, i * 0.25f, 1.0f, (i + 1) * 0.25f);
			}
			animation = new Animation(0.1f, frames);
		}

		@Override
		public void act(final float delta) {
			// Update animation time
			animTime += delta;
		}

		@Override
		public void draw(final SpriteBatch batch, final float parentAlpha) {
			// Get current animation frame
			final TextureRegion currentFrame = animation.getKeyFrame(animTime, true);
			// Display in centre of screen
			batch.draw(currentFrame, (720 - 256) / 2, (1000 - 64) / 2);
		}
	}
}
