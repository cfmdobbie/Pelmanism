package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LoadingScreen implements Screen {

	private final MyGame game;
	private static final Color bgColour = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);
	private Texture texture;
	private Stage stage;

	public LoadingScreen(MyGame game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {

		// Clear screen
		Gdx.gl.glClearColor(bgColour.r, bgColour.g, bgColour.b, bgColour.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();

		if (game.manager.update()) {
			System.out.println("game.manager.update() = true");
			game.postAssetLoad();
			game.setScreen(new MainMenuScreen(game));
			this.dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		System.out.println("LoadingScreen.show()");
		
		texture = new Texture(Gdx.files.internal("loading.png"));
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true);
		stage.addActor(new LoadingActor(texture));

		TextureParameter wrapTexture = new TextureParameter();
		wrapTexture.wrapU = TextureWrap.Repeat;
		wrapTexture.wrapV = TextureWrap.Repeat;
		
		// Load assets in AssetManager
		game.manager.load("pelmanism.atlas", TextureAtlas.class);
		//game.manager.load("background.png", Texture.class, wrapTexture);
		// TODO: AssetManager - load more graphic assets
		// TODO: AssetManager - load sound effect assets
		// TODO: AssetManager - load music assets
		game.manager.load("uiskin.json", Skin.class);
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
		texture.dispose();
		stage.dispose();
	}

	static class LoadingActor extends Actor {
		private Animation animation;
		float animTime = 0.0f;

		public LoadingActor(Texture texture) {
			final TextureRegion[] frames = new TextureRegion[4];
			for (int i = 0; i < 4; i++) {
				frames[i] = new TextureRegion(texture, 0.0f, i * 0.25f, 1.0f, (i + 1) * 0.25f);
			}
			animation = new Animation(0.1f, frames);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			TextureRegion currentFrame = animation.getKeyFrame(animTime, true);
			batch.draw(currentFrame, (720 - 256) / 2, (1000 - 64) / 2);
		}

		@Override
		public void act(float delta) {
			animTime += delta;
		}
	}
}
