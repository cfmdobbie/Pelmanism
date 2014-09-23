package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The help screen. This screen is accessible from the main menu and gives some basic instructions for play, and
 * credits.
 * 
 * @author Charlie
 */
public class HelpScreen implements Screen {

	/** Tag, for logging purposes. */
	private static final String TAG = HelpScreen.class.getSimpleName();

	/** Reference to the Game instance. */
	private final MyGame game;

	/** This Screen's Stage. */
	private final Stage stage;

	/**
	 * Construct a new HelpScreen object.
	 * 
	 * @param game
	 */
	public HelpScreen(final MyGame game) {

		this.game = game;

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);

		// Use global camera
		stage.setCamera(game.camera);

		// Redirect all input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Root of the Stage is a Table, used to lay out all other widgets
		final Table scrollingTable = new Table();
		// table.setFillParent(true);
		// table.setTransform(true);
		scrollingTable.defaults().pad(10.0f);
		// stage.addActor(table);

		// How to play
		scrollingTable.add(new SpinningLabel(game, "How to Play:", "archristy64", Color.RED));
		scrollingTable.row();

		final String[] howToPlayText = {
				"Players take it in turns to pick two cards.\n" + "Find a pair and you win a point!\n"
						+ "Get the highest score you can.",
				"Play solo, against a friend or against the\n" + "computer.",
				"Higher difficulties offer larger boards,\n" + "faster play and more challenging computer\n"
						+ "players." };

		for (final String line : howToPlayText) {
			scrollingTable.add(new SpinningLabel(game, line, "archristy32", Color.WHITE)).fillX();
			scrollingTable.row();
		}

		// Credits
		scrollingTable.add(new SpinningLabel(game, "Credits:", "archristy64", Color.RED));
		scrollingTable.row();

		final String creditsText = "Game created by Charlie Dobbie\n" + "for MayContainSoftware.com.\n"
				+ "Developed in libGDX";

		scrollingTable.add(new SpinningLabel(game, creditsText, "archristy32", Color.WHITE));
		scrollingTable.row();

		// scrollingTable.debug();

		// ScrollPane to hold scrolling table
		final ScrollPane scroll = new ScrollPane(scrollingTable, game.skin);
		scroll.setFadeScrollBars(false);

		// Root table
		final Table rootTable = new Table();
		rootTable.setFillParent(true);
		// rootTable.debug();

		// Add the scrolling area
		rootTable.row();
		rootTable.add(scroll).fill().expand();

		// TODO: A better scroll indication line? Improve the scrollbar?
		rootTable.row().height(15.0f);
		rootTable.add(new Image(game.uiAtlas.findRegion("scroll_indication_line2"))).padTop(5.0f);

		// Back button
		rootTable.row();
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
		rootTable.add(backButton).padTop(5.0f);

		// Set tiled background for the root table, thus for the whold Screen
		//final TextureRegion background = game.uiAtlas.findRegion("background");
		//rootTable.setBackground(new TiledDrawable(background));

		// Add the root table to the stage
		stage.addActor(rootTable);
	}

	@Override
	public void render(float delta) {

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
