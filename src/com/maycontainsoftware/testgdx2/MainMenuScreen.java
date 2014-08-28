package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainMenuScreen implements Screen {
	
	private final MyGame game;
	private Texture texture;
	private TextureRegion region;
	private Sprite sprite;
	
	public MainMenuScreen(MyGame game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		game.batch.setProjectionMatrix(game.camera.combined);
		game.batch.begin();
		game.batch.draw(region, 0, 0, 720, 1000);
		//sprite.draw(game.batch);
		game.batch.end();
		
		// ??? stage.act(), stage.draw() ???
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(500.1f, 500.1f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
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
	}
}
