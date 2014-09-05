package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LoadingScreen implements Screen {

	private static final String TAG = LoadingScreen.class.getSimpleName();
	
	private final MyGame game;
	private Texture loadingTexture;
	private Stage stage;

	public LoadingScreen(MyGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		System.out.println("LoadingScreen.show()");
		
		// Manually load any assets required for the loading screen
		loadingTexture = new Texture(Gdx.files.internal("loading.png"));
		
		// Create the Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true);
		stage.addActor(new LoadingActor(loadingTexture));

//		TextureParameter wrapTexture = new TextureParameter();
//		wrapTexture.wrapU = TextureWrap.Repeat;
//		wrapTexture.wrapV = TextureWrap.Repeat;
		
		// Load assets in AssetManager
		game.manager.load("pelmanism.atlas", TextureAtlas.class);
		// TODO: AssetManager - load more graphic assets
		// TODO: AssetManager - load sound effect assets
		// TODO: AssetManager - load music assets
		game.manager.load("uiskin.json", Skin.class);
	}

	@Override
	public void render(float delta) {

		// Clear screen
		Gdx.gl.glClearColor(MyGame.BACKGROUND_COLOR.r, MyGame.BACKGROUND_COLOR.g, MyGame.BACKGROUND_COLOR.b, MyGame.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Update and render the Stage
		stage.act();
		stage.draw();

		// Continue to load assets
		if (game.manager.update()) {
			// Assets have been loaded!
			Gdx.app.log(TAG, "game.manager.update() = true");
			
			// Perform any post-load tasks
			game.atlas = game.manager.get("pelmanism.atlas", TextureAtlas.class);
			game.skin = game.manager.get("uiskin.json", Skin.class);
			
			// Jump to main menu
			game.setScreen(new MainMenuScreen(game));
			
			// Get rid of loading screen
			this.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		// Update Stage's viewport calculations
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, game.viewport.x, game.viewport.y, game.viewport.width, game.viewport.height);
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

	static class LoadingActor extends Actor {
		private Animation animation;
		float animTime = 0.0f;

		public LoadingActor(Texture texture) {
			// Simple four-frame animation
			final TextureRegion[] frames = new TextureRegion[4];
			for (int i = 0; i < 4; i++) {
				frames[i] = new TextureRegion(texture, 0.0f, i * 0.25f, 1.0f, (i + 1) * 0.25f);
			}
			animation = new Animation(0.1f, frames);
		}

		@Override
		public void act(float delta) {
			animTime += delta;
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			// Get current animation frame
			TextureRegion currentFrame = animation.getKeyFrame(animTime, true);
			// Display in centre of screen
			batch.draw(currentFrame, (720 - 256) / 2, (1000 - 64) / 2);
		}
	}
}
