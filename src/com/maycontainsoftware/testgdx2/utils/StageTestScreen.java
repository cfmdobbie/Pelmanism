package com.maycontainsoftware.testgdx2.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.maycontainsoftware.testgdx2.MyGame;

public class StageTestScreen implements Screen {

	static class TempActor extends Actor {
		private Texture texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		private TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			batch.draw(region, 0, 0);
		}

		@Override
		public void act(float delta) {
		}
	}

	@SuppressWarnings("unused")
	private final MyGame game;

	private Color bgColour = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);
	private Texture texture;
	// private TextureRegion region;
	// private Sprite sprite;
	private Stage stage;

	public StageTestScreen(MyGame game) {
		this.game = game;
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true);
		// N.B. Different constructors allow setting SpriteBatch and viewport metrics
		stage.addActor(new TempActor());
	}

	@Override
	public void render(float delta) {

		// Clear screen
		Gdx.gl.glClearColor(bgColour.r, bgColour.g, bgColour.b, bgColour.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();

		// game.batch.setProjectionMatrix(game.camera.combined);
		// game.batch.begin();
		// //game.batch.draw(region, 0, 0, 720, 1000);
		// // sprite.draw(game.batch);
		// game.batch.end();

		// ??? stage.act(), stage.draw() ???
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {

		// texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		// texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		//
		// region = new TextureRegion(texture, 0, 0, 512, 275);
		//
		// sprite = new Sprite(region);
		// sprite.setSize(500.1f, 500.1f * sprite.getHeight() / sprite.getWidth());
		// sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		// sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
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
}
