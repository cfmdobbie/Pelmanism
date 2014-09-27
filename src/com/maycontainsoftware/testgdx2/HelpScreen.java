package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

		// All help/credit text exists within a Table
		// The table exists in a ScrollPane
		// The ScrollPane is added to the root Table
		// The root Table is added to the Stage

		final Table scrollingTable = new Table();
		scrollingTable.defaults().pad(10.0f);

		// How to play
		scrollingTable.row();
		scrollingTable.add(new Label("How to Play:", game.skin, "arcena64", Color.RED));

		// TODO: Improve help/credits text
		final String[] howToPlayText = {
				"Players take it in turns to pick two cards.\n" +
				"Find a pair and you win a point and get to pick\n" +
				"again!  Get the highest score to win the game.",
				
				"Play solo, against a friend or against the\n" +
				"computer.",
				
				"Higher difficulties offer larger boards,\n" +
				"faster play and more challenging computer\n" +
				"players." };

		for (final String line : howToPlayText) {
			scrollingTable.row();
			scrollingTable.add(new Label(line, game.skin, "arcena32", Color.WHITE)).fillX();
		}
		
		// Credits
		scrollingTable.row();
		scrollingTable.add(new Label("Credits:", game.skin, "arcena64", Color.RED));

		final String creditsText =
				"Game created by Charlie Dobbie\n" +
				"for MayContainSoftware.com.\n" +
				"Developed in libGDX.";

		scrollingTable.row();
		scrollingTable.add(new Label(creditsText, game.skin, "arcena32", Color.WHITE)).fillX();

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
		//rootTable.row().height(15.0f);
		//rootTable.add(new Image(game.uiAtlas.findRegion("scroll_indication_line2"))).padTop(5.0f);
		
		// TODO: If scrolling table is not required, rip all this out

		// Back button
		rootTable.row();
		final Drawable backButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("back_button_on"));
		final Drawable backButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("back_button_off"));
		final Button backButton = new Button(backButtonOff, backButtonOn);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				rootTable.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.125f),
						new Action() {
							@Override
							public boolean act(float delta) {
								HelpScreen.this.game.setScreen(new MainMenuScreen(HelpScreen.this.game));
								HelpScreen.this.dispose();
								return true;
							}
						}));
			}
		});
		rootTable.add(backButton).padTop(5.0f);

		// Add the root table to the stage
		stage.addActor(rootTable);

		// Fade in, then redirect all input events to the Stage
		rootTable.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		rootTable.addAction(Actions.sequence(Actions.fadeIn(0.125f), new SetInputProcessorAction(stage)));
	}

	@Override
	public void render(float delta) {

		// Update and render Stage
		stage.act();
		stage.draw();

		// Table.drawDebug(stage);
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
