package com.maycontainsoftware.testgdx2.utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ViewportStageTestGame implements ApplicationListener {

	private static final String TAG = ViewportStageTestGame.class.getSimpleName();

	// Virtual screen dimensions
	static final int VIRTUAL_WIDTH = 720;
	static final int VIRTUAL_HEIGHT = 1000;
	private static final float VIRTUAL_ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;

	SpriteBatch batch;
	OrthographicCamera camera;
	final Rectangle viewport = new Rectangle();
	Texture texture;

	// private final Color bgColour = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);
	private Stage stage;

	@Override
	public void create() {

		// Set up SpriteBatch
		batch = new SpriteBatch();
		stage = new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true, batch);
		Gdx.input.setInputProcessor(stage);

		// Set up camera
		camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
		// Move (0,0) point to bottom left of virtual area
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		// stage.setCamera(game.camera);

		texture = new Texture(Gdx.files.internal("loading.png"));

		Table table = new Table();
		// table.setSize(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT);
		table.setFillParent(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);

		// stage.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true, 0, 0, 720/2, 1000/2);

		Image image1 = new Image(texture);
		image1.setColor(Color.RED);
		image1.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "image1");
				return true;
			}
		});
		Image image2 = new Image(texture);
		image2.setColor(Color.GREEN);
		image2.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "image2");
				return true;
			}
		});
		Image image3 = new Image(texture);
		image3.setColor(Color.BLUE);
		image3.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "image3");
				return true;
			}
		});

		table.row();
		table.add(image1);
		table.add(image2);
		table.row();
		table.add(image3);

		table.debug();
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

		stage.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, viewport.x, viewport.y, viewport.width, viewport.height);
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

		// CLear active area
		Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		batch.setColor(Color.WHITE);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(texture, 0, 0, 720, 1000);
		batch.draw(texture, 0, 0);
		batch.draw(texture, 720 - 256, 1000 - 256, 256, 256);
		batch.end();

		stage.act();
		stage.draw();

		Table.drawDebug(stage);
	}

	@Override
	public void dispose() {
		// Dispose of stuff
		batch.dispose();
		stage.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
