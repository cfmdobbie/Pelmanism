package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class TestScreen implements Screen {
	
	private final MyGame game;
	
	private Stage stage;
	
	static class MyActor extends Actor {
		Texture texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			batch.draw(texture, 0, 0);
		}
	}
	
	public TestScreen(MyGame game) {
		this.game = game;
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage.addActor(new MyActor());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
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
	}
}
