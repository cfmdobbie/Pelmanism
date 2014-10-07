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
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
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
	public GameOverScreen(final MyGame game, final Difficulty difficulty,
			final PlayerConfiguration playerConfiguration, final Pelmanism model) {
		this.game = game;

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);

		// Use global camera
		stage.setCamera(game.camera);

		// Root of the Stage is a Table, used to lay out all other widgets
		final Table table = new Table();
		table.setFillParent(true);
		table.defaults().padTop(10.0f);
		stage.addActor(table);

		// Title
		table.row();
		table.add(new Image(game.uiAtlas.findRegion("pelmanism_title"))).colspan(3);

		table.row();
		table.add(new Label("Game Over!", game.skin, "arcena64", Color.WHITE));

		// Difficulty display
		table.row();
		table.add(new Label("Difficulty:", game.skin, "arcena32", Color.WHITE));
		table.row().padTop(0.0f);
		// Work out type and number of stars
		final String starRegion; 
		final int starCount;
		switch (difficulty) {
		case Easy:
			starRegion = "bronze_star";
			starCount = 1;
			break;
		case Medium:
			starRegion = "silver_star";
			starCount = 2;
			break;
		case Hard:
			starRegion = "gold_star";
			starCount = 3;
			break;
		default:
			throw new IllegalStateException();
		}
		// Display stars, with sound and animation
		HorizontalGroup stars = new HorizontalGroup();
		table.add(stars);
		for(int i = 0 ; i < starCount ; i++) {
			final Image star = new Image(game.uiAtlas.findRegion(starRegion));
			star.setOrigin(star.getWidth() / 2, star.getHeight() / 2);
			star.setColor(1.0f, 1.0f, 1.0f, 0.0f);
			star.addAction(Actions.sequence(Actions.delay(0.3f * i), new Action() {
				@Override
				public boolean act(float delta) {
					game.playCardMatchSound();
					star.setColor(Color.WHITE);
					return true;
				}
			}, Actions.rotateTo(360.0f, 0.75f)));
			stars.addActor(star);
		}

		// Score display
		table.row();
		table.add(new Label("Scores:", game.skin, "arcena32", Color.WHITE));
		for (int i = 0; i < model.getNumberOfPlayers(); i++) {
			table.row().padTop(0.0f);
			final int score = model.getPlayerScore(i);
			final String line = playerConfiguration.getPlayerName(i) + ": " + score + " point" + (score != 1 ? "s" : "");
			table.add(new Label(line, game.skin, "arcena48", playerConfiguration.getPlayerColor(i)));
		}
		
		// Winner display
		table.row();
		// TODO: What about a tie?
		final String winnerText;
		final Color winnerColor;
		if(model.getNumberOfPlayers() == 1) {
			winnerText = "Congratulations!";
			winnerColor = playerConfiguration.getPlayerColor(0);
		} else if(model.getPlayerScore(0) == model.getPlayerScore(1)) {
			winnerText = "It's a tie!";
			winnerColor = Color.WHITE;
		} else if(model.getPlayerScore(0) > model.getPlayerScore(1)) {
			winnerText = playerConfiguration.getPlayerName(0) + " is the winner!";
			winnerColor = playerConfiguration.getPlayerColor(0);
		} else {
			winnerText = playerConfiguration.getPlayerName(1) + " is the winner!";
			winnerColor = playerConfiguration.getPlayerColor(1);
		}
		table.add(new Label(winnerText, game.skin, "arcena64", winnerColor));

		// Buttons
		table.row();
		table.add(new Label("[Play Again]", game.skin, "arcena32", Color.WHITE));
		table.row();
		table.add(new Label("[Quit to Menu]", game.skin, "arcena32", Color.WHITE));
		
		
		// Temp Buttons
		table.row().padTop(50.0f);
		// Help Button
		final Drawable buttonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("quit_game_button_on"));
		final Drawable buttonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("quit_game_button_off"));
		final Button button = new Button(buttonOff, buttonOn);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				table.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.125f),
						new Action() {
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
