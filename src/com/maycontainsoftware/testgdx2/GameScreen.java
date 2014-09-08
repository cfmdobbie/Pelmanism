package com.maycontainsoftware.testgdx2;

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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class GameScreen implements Screen {

	private static final String TAG = GameScreen.class.getSimpleName();

	private final MyGame game;
	private final Stage stage;
	private final TextureAtlas cardSetAtlas;
	private final MyGame.Players players;
	private final MyGame.Difficulty difficulty;
	
	private final TextureAtlas getCardSetAtlasFromPrefs() {
		final String cardSetFromPreferences = game.mPrefs.getString(MyGame.PREF_CARD_SET, MyGame.CardSet.Simple.toString());
		final MyGame.CardSet cardSet = MyGame.CardSet.valueOf(cardSetFromPreferences);
		switch(cardSet) {
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
		
		// Determine card set
		cardSetAtlas = getCardSetAtlasFromPrefs();
		
		// Players
		final String playersFromPreferences = game.mPrefs.getString(MyGame.PREF_PLAYERS, MyGame.Players.One.toString());
		players = MyGame.Players.valueOf(playersFromPreferences);
		// One, Two, One_Vs_Cpu
		
		// Difficulty
		final String difficultyFromPreferences = game.mPrefs.getString(MyGame.PREF_DIFFICULTY, MyGame.Difficulty.Easy.toString());
		difficulty = MyGame.Difficulty.valueOf(difficultyFromPreferences);
		// Easy, Medium or Hard

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

		// Player two info
		table.row().padTop(20.0f);
		final SpinningTable p2Table = new SpinningTable();
		// p2Table.debug();
		p2Table.add(new Label("Player Two", game.skin, "archristy48", Color.BLUE));
		p2Table.add().expandX();
		p2Table.add(new Label("N Points", game.skin, "archristy48", Color.BLUE));
		table.add(p2Table).colspan(2).fillX();

		// Game area
		table.row().padTop(30.0f);

		final Table gameArea = new Table();
		//gameArea.debug();

		gameArea.add(new Image(cardSetAtlas.findRegion("01"))).expand();
		gameArea.add(new Image(cardSetAtlas.findRegion("02"))).expand();
		gameArea.add(new Image(cardSetAtlas.findRegion("03"))).expand();
		gameArea.add(new Image(cardSetAtlas.findRegion("04"))).expand();
		gameArea.row().expandY();
		gameArea.add(new Image(cardSetAtlas.findRegion("05")));
		gameArea.add(new Image(cardSetAtlas.findRegion("06")));
		gameArea.add(new Image(cardSetAtlas.findRegion("07")));
		gameArea.add(new Image(cardSetAtlas.findRegion("08")));
		gameArea.row().expandY();
		gameArea.add(new Image(cardSetAtlas.findRegion("09")));
		gameArea.add(new Image(cardSetAtlas.findRegion("10")));
		gameArea.add(new Image(cardSetAtlas.findRegion("11")));
		gameArea.add(new Image(cardSetAtlas.findRegion("12")));
		gameArea.row().expandY();
		gameArea.add(new Image(cardSetAtlas.findRegion("13")));
		gameArea.add(new Image(cardSetAtlas.findRegion("14")));
		gameArea.add(new Image(cardSetAtlas.findRegion("15")));
		gameArea.add(new Image(cardSetAtlas.findRegion("16")));
		gameArea.row().expandY();

		table.add(gameArea).expandX().expandY().fillX().fillY().colspan(2);

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
		final Drawable backButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("quit_game_button_on"));
		final Drawable backButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("quit_game_button_off"));
		final Button backButton = new Button(backButtonOff, backButtonOn);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GameScreen.this.game.setScreen(new MainMenuScreen(GameScreen.this.game));
				GameScreen.this.dispose();
			}
		});
		table.add(backButton).left();
		// Sound on/off
		final Drawable soundOn = new TextureRegionDrawable(game.uiAtlas.findRegion("sound_button_on"));
		final Drawable soundOff = new TextureRegionDrawable(game.uiAtlas.findRegion("sound_button_off"));
		final Button soundButton = new Button(soundOff, null, soundOn);
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
}
