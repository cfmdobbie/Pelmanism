package com.maycontainsoftware.pelmanism;

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

		// Root table
		final Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(10.0f);
		// rootTable.debug();

		// How to play
		table.row();
		table.add(new Label("How to Play:", game.skin, "arcena64", Color.RED));

		// TODO: Improve help/credits text
		final String[] howToPlayText = {
				"Players take it in turns to pick two cards.\n" +
				"Find a pair: win a point, and you get to pick\n" +
				"again!  Highest score wins the game.",
				
				"Play solo, against a friend or against the\n" +
				"computer.",
				
				"Higher difficulties offer larger boards\n" +
				"and more challenging computer players." };

		for (final String line : howToPlayText) {
			table.row();
			table.add(new Label(line, game.skin, "arcena32", Color.WHITE)).fillX();
		}
		
		// Credits
		table.row();
		table.add(new Label("Credits:", game.skin, "arcena64", Color.RED));

		final String creditsText =
				"Game created by Charlie Dobbie\n" +
				"for MayContainSoftware.com.\n" +
				"Developed in libGDX.";

		table.row();
		table.add(new Label(creditsText, game.skin, "arcena32", Color.WHITE)).fillX();

		// scrollingTable.debug();

		// Back button
		table.row();
		final Drawable backButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("back_button_on"));
		final Drawable backButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("back_button_off"));
		final Button backButton = new Button(backButtonOff, backButtonOn);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				table.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.125f),
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
		table.add(backButton).padTop(40.0f).padBottom(20.0f);

		// Add the root table to the stage
		stage.addActor(table);

		// Fade in, then redirect all input events to the Stage
		table.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		table.addAction(Actions.sequence(Actions.fadeIn(0.125f), new SetInputProcessorAction(stage)));
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
