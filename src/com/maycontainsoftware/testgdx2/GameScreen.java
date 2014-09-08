package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.testgdx2.MyGame.Players;

public class GameScreen implements Screen {

	private static final String TAG = GameScreen.class.getSimpleName();

	private final MyGame game;
	private final Stage stage;
	private final MyGame.Players players;
	private final MyGame.Difficulty difficulty;

	private final TextureAtlas getCardSetAtlasFromPrefs() {
		final String cardSetFromPreferences = game.mPrefs.getString(MyGame.PREF_CARD_SET, MyGame.CardSet.Simple.toString());
		final MyGame.CardSet cardSet = MyGame.CardSet.valueOf(cardSetFromPreferences);
		switch (cardSet) {
		default:
			// This should be unreachable code
			// Fall through to "Simple" behaviour
		case Simple:
			return game.simpleCardSet;
		case Signs:
			return game.signsCardSet;
		case Hard:
			return game.hardCardSet;
		}
	}

	public GameScreen(MyGame game) {
		this.game = game;

		// Players
		final String playersFromPreferences = game.mPrefs.getString(MyGame.PREF_PLAYERS, MyGame.Players.One.toString());
		players = MyGame.Players.valueOf(playersFromPreferences);

		// Difficulty
		final String difficultyFromPreferences = game.mPrefs.getString(MyGame.PREF_DIFFICULTY, MyGame.Difficulty.Easy.toString());
		difficulty = MyGame.Difficulty.valueOf(difficultyFromPreferences);

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);

		// Use global camera
		stage.setCamera(game.camera);

		// Redirect all input events to the Stage
		Gdx.input.setInputProcessor(stage);

		// Root of the Stage is a Table, used to lay out all other widgets
		final Table table = new Table();
		table.setFillParent(true);
		table.defaults().padLeft(30.0f).padRight(30.0f);
		// table.debug();
		stage.addActor(table);

		// Set tiled background for Table, thus for Screen
		final TextureRegion background = game.uiAtlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));

		// TODO

		// Secondary score display

		switch (players) {
		default:
		case One:
			// One player, secondary score display not needed
			break;
		case One_Vs_Cpu:
		case Two:
			// Second player exists, either human or computer - will need a second score display

			// Player two info
			table.row().padTop(20.0f);
			final SpinningTable p2Table = new SpinningTable();
			// p2Table.debug();
			final String playerTwoName = players == Players.Two ? "Player Two" : "Computer";
			final Color color = players == Players.Two ? Color.BLUE : Color.GRAY;
			p2Table.add(new Label(playerTwoName, game.skin, "archristy48", color));
			p2Table.add().expandX();
			p2Table.add(new Label("N Points", game.skin, "archristy48", color));
			table.add(p2Table).colspan(2).fillX();

			break;
		}

		// Game area
		table.row().padTop(30.0f);

		final int cardsPerRow;
		final int rows;
		switch (difficulty) {
		default:
			// Fall through to Easy
		case Easy:
			cardsPerRow = rows = 4;
			break;
		case Medium:
			cardsPerRow = rows = 6;
			break;
		case Hard:
			cardsPerRow = rows = 8;
			break;
		}
		final int totalCards = cardsPerRow * rows;
		final int numberOfPairs = totalCards / 2;

		// Given numberOfPairs, return that number of unique random TextureRegions from appropriate TextureAtlas
		final TextureRegion[] regions = selectCardTextures(numberOfPairs);

		final Table gameArea = new Table();
		// gameArea.debug();

		// XXX: Temp code
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cardsPerRow; c++) {
				final int card = (c + r * cardsPerRow) % regions.length;
				gameArea.add(new Image(regions[card])).expand().pad(5.0f);
			}
			gameArea.row().expandY();
		}

		// Game area takes all remaining space in table
		table.add(gameArea).expandX().expandY().fillX().fillY().colspan(2);

		// Primary score display

		// Player one info
		table.row().padTop(30.0f);
		final SpinningTable p1Table = new SpinningTable();
		// p1Table.debug();
		p1Table.add(new Label("Player One", game.skin, "archristy48", Color.RED));
		p1Table.add().expandX();
		p1Table.add(new Label("N Points", game.skin, "archristy48", Color.RED));
		table.add(p1Table).colspan(2).fillX();

		// Buttons
		table.row().padTop(30.0f).padBottom(20.0f);
		// Back button
		final Button backButton = game.makeTexturedButton("quit_game_button", false);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameScreen.this.game.setScreen(new MainMenuScreen(GameScreen.this.game));
				GameScreen.this.dispose();
			}
		});
		table.add(backButton).left();
		// Sound on/off
		final Button soundButton = game.makeTexturedButton("sound_button", true);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: Toggle sound on/off
			}
		});
		table.add(soundButton).right();
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

	private final TextureRegion[] selectCardTextures(int n) {
		final TextureAtlas atlas = getCardSetAtlasFromPrefs();
		final TextureRegion[] regions = new TextureRegion[n];

		// Generate list of numbers from 1 to 32
		final List<Integer> allNumbers = new ArrayList<Integer>(32);
		for (int i = 1; i <= 32; i++) {
			allNumbers.add(i);
		}

		// Shuffle the list
		Collections.shuffle(allNumbers);

		// Pick the first n textures
		for (int i = 0; i < n; i++) {
			// Determine the region name
			String regionName = String.valueOf(allNumbers.get(i));
			if (regionName.length() < 2) {
				regionName = "0" + regionName;
			}
			// Find the TextureRegion and store it
			regions[i] = atlas.findRegion(regionName);
		}

		return regions;
	}

}
