package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class MainMenuScreen implements Screen {

	private static final String TAG = MainMenuScreen.class.getSimpleName();

	private final MyGame game;
	private final Stage stage;

	public MainMenuScreen(MyGame game) {
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
		//table.setTransform(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);

		// Set tiled background for Table, thus for Screen
		final TextureRegion background = game.uiAtlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));

		// Title
		table.add(new SpinningLabel(game, "Pelmanism!", "archristy64", Color.RED)).colspan(3);
		table.row();

		// Players section
		// Label
		table.add(new SpinningLabel(game, "Players:", "archristy48", Color.WHITE)).colspan(3);
		table.row();
		// Buttons
		final String[] playerImagePrefixes = { "player_1p", "player_2p", "player_1pvscpu" };
		makeButtonSet(table, playerImagePrefixes, MyGame.PREF_PLAYERS, MyGame.Players.values());

		// Difficulty section
		// Label
		table.add(new SpinningLabel(game, "Difficulty:", "archristy48", Color.WHITE)).colspan(3);
		table.row();
		// Buttons
		final String[] difficultyImagePrefixes = { "difficulty_1", "difficulty_2", "difficulty_3" };
		makeButtonSet(table, difficultyImagePrefixes, MyGame.PREF_DIFFICULTY, MyGame.Difficulty.values());

		// Card set section
		// Label
		table.add(new SpinningLabel(game, "Card set:", "archristy48", Color.WHITE)).colspan(3);
		table.row();
		// Buttons
		final String[] cardSetImagePrefixes = { "cards_simple", "cards_signs", "cards_hard" };
		makeButtonSet(table, cardSetImagePrefixes, MyGame.PREF_CARD_SET, MyGame.CardSet.values());

		// Buttons
		// Help Button
		final Drawable helpButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("help_button_on"));
		final Drawable helpButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("help_button_off"));
		final Button helpButton = new Button(helpButtonOff, helpButtonOn);
		helpButton.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
				MainMenuScreen.this.game.setScreen(new HelpScreen(MainMenuScreen.this.game));
				MainMenuScreen.this.dispose();
	        }
	    });
		table.add(helpButton).padTop(50.0f);
		// Start Game Button
		final Drawable startButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("start_button_on"));
		final Drawable startButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("start_button_off"));
		final Button startButton = new Button(startButtonOff, startButtonOn);
		startButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				MainMenuScreen.this.game.setScreen(new GameScreen(MainMenuScreen.this.game));
				MainMenuScreen.this.dispose();
			}
		});
		table.add(startButton).colspan(2).padTop(50.0f);
		table.row();

		// table.debug();
	}

	private void makeButtonSet(final Table table, final String[] imagePrefixes, final String prefsName, final Enum<?>[] values) {
		// METHOD
		final int n = imagePrefixes.length;
		Button[] buttons = new Button[n];
		for (int i = 0; i < n; i++) {
			final int index = i;
			buttons[i] = game.makeTexturedButton(imagePrefixes[i], true);
			buttons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					final Preferences prefs = MainMenuScreen.this.game.mPrefs;
					prefs.putString(prefsName, values[index].toString());
					prefs.flush();
				}
			});
		}
		ButtonGroup group = new ButtonGroup(buttons);
		// Check selected button
		final String currentPrefValue = game.mPrefs.getString(prefsName);
		for (int i = 0; i < n; i++) {
			if (currentPrefValue != null && currentPrefValue.equals(values[i].toString())) {
				buttons[i].setChecked(true);
			}
		}
		if (group.getAllChecked().size == 0) {
			buttons[0].setChecked(true);
		}
		// Update table
		for (Button b : buttons) {
			table.add(b);
		}
		table.row();
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
