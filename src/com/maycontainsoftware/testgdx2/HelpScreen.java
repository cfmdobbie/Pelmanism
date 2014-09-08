package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class HelpScreen implements Screen {

	private static final String TAG = HelpScreen.class.getSimpleName();

	private final MyGame game;
	private final Stage stage;

	public HelpScreen(MyGame game) {
		this.game = game;

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);

		// Use global camera
		stage.setCamera(game.camera);

		// Redirect all input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Root of the Stage is a Table, used to lay out all other widgets
		final Table table = new Table();
		table.setFillParent(true);
		table.setTransform(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);

		// Set tiled background for Table, thus for Screen
		final TextureRegion background = game.uiAtlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));

		// How to play
		table.add(new SpinningLabel(game, "How to Play:", "archristy64", Color.RED));
		table.row();

		String[] howToPlayText = {
				"Players take it in turns to pick two cards.\n" +
				"Find a pair and you win a point!\n" +
				"Get the highest score you can.",
				"Play solo, against a friend or against the\n" +
				"computer.",
				"Higher difficulties offer larger boards,\n" +
				"faster play and more challenging computer\n" +
				"players." };

		for(String line : howToPlayText) {
			table.add(new SpinningLabel(game, line, "archristy32", Color.WHITE));
			table.row();
		}

		// Credits
		table.add(new SpinningLabel(game, "Credits:", "archristy64", Color.RED));
		table.row();

		String creditsText = "Game created by Charlie Dobbie\n" + "for MayContainSoftware.com.\n" + "Developed in libGDX";

		table.add(new SpinningLabel(game, creditsText, "archristy32", Color.WHITE));
		table.row();

		// Back button
		final Drawable backButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("back_button_on"));
		final Drawable backButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("back_button_off"));
		final Button backButton = new Button(backButtonOff, backButtonOn);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				HelpScreen.this.game.setScreen(new MainMenuScreen(HelpScreen.this.game));
				HelpScreen.this.dispose();
			}
		});
		table.add(backButton).padTop(50.0f);
		table.row();

		// table.debug();
	}

	@Override
	public void render(float delta) {

		// Clear screen
		final Color c = MyGame.BACKGROUND_COLOR;
		Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Update and render Stage
		stage.act();
		stage.draw();

		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		// Update Stage's viewport calculations
		final Rectangle v = game.viewport;
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, v.x, v.y, v.width, v.height);
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
		stage.dispose();
	}
}
