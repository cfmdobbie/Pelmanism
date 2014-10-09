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

		// Cumulative UI delay, for timing Actor appearances
		float cumulativeDelay = 0.0f;

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
		final HorizontalGroup stars = new HorizontalGroup();
		table.add(stars);
		for (int i = 0; i < starCount; i++) {
			final Image star = new Image(game.uiAtlas.findRegion(starRegion));
			star.setOrigin(star.getWidth() / 2, star.getHeight() / 2);
			star.setColor(Color.CLEAR);
			cumulativeDelay += 0.3f;
			star.addAction(Actions.sequence(Actions.delay(cumulativeDelay), new Action() {
				@Override
				public boolean act(float delta) {
					game.playCardMatchSound();
					star.setColor(Color.WHITE);
					return true;
				}
			}, Actions.rotateTo(360.0f, 0.75f)));
			stars.addActor(star);
		}
		// Add extra cumulative delay
		cumulativeDelay += 0.75f;

		// Score display
		table.row();
		table.add(new Label("Scores:", game.skin, "arcena32", Color.WHITE));
		table.row();
		// Scores embedded in a separate table
		final Table scoreTable = new Table();
		scoreTable.defaults().pad(5.0f);
		// scoreTable.debug();
		table.add(scoreTable);

		for (int i = 0; i < model.getNumberOfPlayers(); i++) {
			scoreTable.row();
			// Player name
			final String playerName = playerConfiguration.getPlayerName(i);
			scoreTable.add(new Label(playerName + ":", game.skin, "arcena48", playerConfiguration.getPlayerColor(i)));
			// Player score
			final int playerScore = model.getPlayerScore(i);
			final String playerScoreMessage = playerScore + (playerScore == 1 ? " point" : " points");
			final Label scoreLabel = new Label(playerScoreMessage, game.skin, "arcena48",
					playerConfiguration.getPlayerColor(i));
			scoreLabel.setColor(Color.CLEAR);
			cumulativeDelay += 0.3f;
			scoreLabel.addAction(Actions.sequence(Actions.delay(cumulativeDelay), new Action() {
				@Override
				public boolean act(float delta) {
					game.playCardMatchSound();
					scoreLabel.setColor(Color.WHITE);
					return true;
				}
			}));
			scoreTable.add(scoreLabel);
		}

		// Extra cumulative delay
		cumulativeDelay += 0.75f;

		// Winner display
		table.row();
		final String winnerMessage;
		final Color winnerColor;
		final boolean win;

		switch (playerConfiguration) {
		case One:
			// Single player
			winnerMessage = "Congratulations!";
			winnerColor = playerConfiguration.getPlayerColor(0);
			win = true;
			break;
		case One_Vs_Cpu:
			// Player versus computer
			if (model.getPlayerScore(0) > model.getPlayerScore(1)) {
				// Player won
				winnerMessage = "You beat the computer!";
				winnerColor = playerConfiguration.getPlayerColor(0);
				win = true;
			} else if (model.getPlayerScore(0) < model.getPlayerScore(1)) {
				// Computer won
				winnerMessage = "The computer wins!";
				winnerColor = playerConfiguration.getPlayerColor(1);
				win = false;
			} else {
				// Tie
				winnerMessage = "It's a tie!";
				winnerColor = Color.WHITE;
				win = false;
			}
			break;
		case Two:
			// Two players
			if (model.getPlayerScore(0) > model.getPlayerScore(1)) {
				// Player 1 won
				winnerMessage = playerConfiguration.getPlayerName(0) + " is the winner!";
				winnerColor = playerConfiguration.getPlayerColor(0);
				win = true;
			} else if (model.getPlayerScore(0) < model.getPlayerScore(1)) {
				// Player 2 won
				winnerMessage = playerConfiguration.getPlayerName(1) + " is the winner!";
				winnerColor = playerConfiguration.getPlayerColor(1);
				win = true;
			} else {
				// Tie
				winnerMessage = "It's a tie!";
				winnerColor = Color.WHITE;
				win = false;
			}
			break;
		default:
			throw new IllegalStateException();
		}

		final Label winnerLabel = new Label(winnerMessage, game.skin, "arcena64", winnerColor);
		winnerLabel.setColor(Color.CLEAR);
		winnerLabel.addAction(Actions.sequence(Actions.delay(cumulativeDelay), new Action() {
			@Override
			public boolean act(float delta) {
				winnerLabel.setColor(Color.WHITE);
				if (win) {
					game.playGameWonSound();
				} else {
					game.playGameLostSound();
				}
				return true;
			}
		}));
		table.add(winnerLabel);

		// Buttons
		final HorizontalGroup buttons = new HorizontalGroup();
		table.row().padTop(50.0f);
		table.add(buttons);

		// Menu Button
		final Button menuButton = game.makeTexturedButton("menu_button", false);
		menuButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				table.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.125f),
						new ScreenChangeAction(game, GameOverScreen.this, new MainMenuScreen(game))));
			}
		});
		buttons.addActor(menuButton);

		// Padding
		final Actor buttonPadding = new Actor();
		buttonPadding.setWidth(20.0f);
		buttons.addActor(buttonPadding);

		// Restart Button
		final Button restartButton = game.makeTexturedButton("restart_button", false);
		restartButton.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				table.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.125f),
						new ScreenChangeAction(game, GameOverScreen.this, new GameScreen(game))));
			}
		});
		buttons.addActor(restartButton);

		// Fade in, then redirect all input events to the Stage
		table.setColor(Color.CLEAR);
		table.addAction(Actions.sequence(Actions.fadeIn(0.125f), new SetInputProcessorAction(stage)));

		// table.debug();
	}

	@Override
	public void render(final float delta) {

		// Update and render Stage
		stage.act();
		stage.draw();

		// Table.drawDebug(stage);
	}

	@Override
	public void resize(final int width, final int height) {
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
