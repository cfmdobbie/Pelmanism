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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * @author Charlie
 */
public class GameOverScreen implements Screen {

	/** Tag, for logging purposes. */
	private static final String TAG = GameOverScreen.class.getSimpleName();

	/** Reference to the Game object. */
	private final MyGame game;

	/** This Screen's Stage. */
	private final Stage stage;

	/**
	 * Construct a new GameOverScreen object.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public GameOverScreen(final MyGame game) {
		this.game = game;

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);

		// Use global camera
		stage.setCamera(game.camera);

		// Root of the Stage is a Table, used to lay out all other widgets
		final Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);
		
		// Title
		table.row();
		table.add(new Image(game.uiAtlas.findRegion("pelmanism_title"))).colspan(3);
		
		
		table.row();
		table.add(new Label("Game Over", game.skin, "arcena64", Color.WHITE));
		
		
		// Initial ideas for game over screen:-
		
		// Game could be one-player, two-player or player vs computer
		// Game can be won by player one, player two or computer
		// Or game can be a tie!
		// Winner has a score
		// Loser has a score
		// Or tied players have the same score
		
		// Buttons
		table.row().padTop(50.0f);
		// Help Button
		final Drawable buttonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("help_button_on"));
		final Drawable buttonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("help_button_off"));
		final Button button = new Button(buttonOff, buttonOn);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				table.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.125f), new Action() {
					@Override
					public boolean act(float delta) {
						GameOverScreen.this.game.setScreen(new MainMenuScreen(GameOverScreen.this.game));
						GameOverScreen.this.dispose();
						return true;
					}
				}));
			}
		});
		table.add(button);

		// Fade in, then redirect all input events to the Stage
		table.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		table.addAction(Actions.sequence(Actions.fadeIn(0.125f), new SetInputProcessorAction(stage)));
		
		// table.debug();
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
