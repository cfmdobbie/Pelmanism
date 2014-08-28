package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;

public class MyGame extends Game {

	public static final int VIRTUAL_WIDTH = 720;
	public static final int VIRTUAL_HEIGHT = 1000;
	private static final float VIRTUAL_ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;

	public SpriteBatch batch;
	public OrthographicCamera camera;
//	private Rectangle viewport;

	@Override
	public void create() {

		batch = new SpriteBatch();
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

		this.setScreen(new MainMenuScreen(this));
		// this.setScreen(new TestScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		// Calculate display aspect ratio
		float displayAspectRatio = (float) width / (float) height;
		
		// Based on display aspect ratio, calculate camera dimensions
		if (displayAspectRatio > VIRTUAL_ASPECT_RATIO) {
			// Display is wider than the game
			camera.setToOrtho(false, VIRTUAL_HEIGHT * displayAspectRatio, VIRTUAL_HEIGHT);
		} else if (displayAspectRatio < VIRTUAL_ASPECT_RATIO) {
			// Display is taller than the game
			camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_WIDTH / displayAspectRatio);
		} else {
			// Display exactly matches game
			camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		}
		// Move (0,0) point to bottom left of virtual area
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		
		/*
//		float scale = 1.0f;
//		Vector2 crop = new Vector2(0.0f, 0.0f);

		if (aspectRatio > VIRTUAL_ASPECT_RATIO) {
			scale = (float) height / (float) VIRTUAL_HEIGHT;
			crop.x = (width - VIRTUAL_WIDTH * scale) / 2f;
		} else if (aspectRatio < VIRTUAL_ASPECT_RATIO) {
			scale = (float) width / (float) VIRTUAL_WIDTH;
			crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
		} else {
			scale = (float) width / (float) VIRTUAL_WIDTH;
		}
		
		//crop.set(Scaling.fit.apply(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, width, height));
		
		float w = (float) VIRTUAL_WIDTH * scale;
		float h = (float) VIRTUAL_HEIGHT * scale;
		viewport = new Rectangle(crop.x, crop.y, w, h);
		 */
		
		super.resize(width, height);
	}

	@Override
	public void render() {

		// Clear screen to black
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		//camera.apply(Gdx.gl10);

		//Gdx.gl.glViewport((int) viewport.x, (int) viewport.y, (int) viewport.width, (int) viewport.height);
		Gdx.gl.glViewport(10, 10, 50, 50);

		super.render();
	}
}
