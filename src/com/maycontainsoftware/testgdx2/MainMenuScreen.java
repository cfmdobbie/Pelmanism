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
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.maycontainsoftware.testgdx2.MyGame.CardSet;
import com.maycontainsoftware.testgdx2.MyGame.Difficulty;
import com.maycontainsoftware.testgdx2.MyGame.PlayerConfiguration;

/**
 * The main menu screen. This screen is the first interactive screen the user sees, and allows them to set up a new
 * game.
 * 
 * @author Charlie
 */
public class MainMenuScreen implements Screen {

	/** Tag, for logging purposes. */
	private static final String TAG = MainMenuScreen.class.getSimpleName();

	/** Reference to the Game object. */
	private final MyGame game;

	/** This Screen's Stage. */
	private final Stage stage;

	/**
	 * Construct a new MainMenuScreen object.
	 * 
	 * @param game
	 *            The Game instance.
	 */
	public MainMenuScreen(final MyGame game) {
		this.game = game;

		// Create Stage
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);

		// Use global camera
		stage.setCamera(game.camera);

		// Root of the Stage is a Table, used to lay out all other widgets
		final Table table = new Table();
		table.setFillParent(true);
		// table.setTransform(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);
		
		table.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		//table.addAction(Actions.fadeIn(0.25f));
		// Fade in, then redirect all input events to the Stage
		table.addAction(Actions.sequence(Actions.fadeIn(0.25f), new SetInputProcessorAction(stage)));
		//Gdx.input.setInputProcessor(stage);
		

		// Set tiled background for Table, thus for Screen
		//final TextureRegion background = game.uiAtlas.findRegion("background");
		//final TextureRegion colorSquare = game.uiAtlas.findRegion("color_square2");
		//final TextureRegion bgPattern1 = game.uiAtlas.findRegion("bgPattern7");
		
		/*
		table.setBackground(new BaseDrawable() {
			@Override
			public void draw(SpriteBatch batch, float x, float y, float width, float height) {
				
				//batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				
				batch.draw(colorSquare, x, y, width, height);
				boolean enabled = batch.isBlendingEnabled();
				batch.enableBlending();
				batch.draw(bgPattern1, x, y, width, height);
				if(!enabled) {
					batch.disableBlending();
				}
			}
		});
		table.setBackground(new TiledDrawable(bgPattern1) {
			@Override
			public void draw(SpriteBatch batch, float x, float y, float width, float height) {
				//batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				//System.out.println(x+","+y+","+width+","+height);
				
				batch.draw(colorSquare, x, y, width, height);
				boolean enabled = batch.isBlendingEnabled();
				batch.enableBlending();
				super.draw(batch, x, y, width, height);
				if(!enabled) {
					batch.disableBlending();
				}
			}
		});
		*/

		// Title
		table.add(new SpinningLabel(game, "Pelmanism!", "arcena64", Color.RED)).colspan(3);
		table.row();

		// Players section
		// Label
		table.add(new SpinningLabel(game, "Players:", "arcena48", Color.WHITE)).colspan(3);
		table.row();
		// Buttons
		final String[] playerImagePrefixes = { "player_1p", "player_2p", "player_1pvscpu" };
		makeButtonSet(table, playerImagePrefixes, MyGame.PREF_PLAYER_CONFIGURATION, PlayerConfiguration.values());

		// Difficulty section
		// Label
		table.add(new SpinningLabel(game, "Difficulty:", "arcena48", Color.WHITE)).colspan(3);
		table.row();
		// Buttons
		final String[] difficultyImagePrefixes = { "difficulty_1", "difficulty_2", "difficulty_3" };
		makeButtonSet(table, difficultyImagePrefixes, MyGame.PREF_DIFFICULTY, Difficulty.values());

		// Card set section
		// Label
		table.add(new SpinningLabel(game, "Card set:", "arcena48", Color.WHITE)).colspan(3);
		table.row();
		// Buttons
		final String[] cardSetImagePrefixes = { "cards_simple", "cards_signs", "cards_hard" };
		makeButtonSet(table, cardSetImagePrefixes, MyGame.PREF_CARD_SET, CardSet.values());

		// Buttons
		// Help Button
		final Drawable helpButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("help_button_on"));
		final Drawable helpButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("help_button_off"));
		final Button helpButton = new Button(helpButtonOff, helpButtonOn);
		helpButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				table.addAction(Actions.sequence(new SetInputProcessorAction(null), Actions.fadeOut(0.25f), new Action() {
					@Override
					public boolean act(float delta) {
						MainMenuScreen.this.game.setScreen(new HelpScreen(MainMenuScreen.this.game));
						MainMenuScreen.this.dispose();
						return true;
					}
				}));
			}
		});
		table.add(helpButton).padTop(50.0f);
		// Start Game Button
		final Drawable startButtonOn = new TextureRegionDrawable(game.uiAtlas.findRegion("start_button_on"));
		final Drawable startButtonOff = new TextureRegionDrawable(game.uiAtlas.findRegion("start_button_off"));
		final Button startButton = new Button(startButtonOff, startButtonOn);
		startButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				MainMenuScreen.this.game.setScreen(new GameScreen(MainMenuScreen.this.game));
				MainMenuScreen.this.dispose();
			}
		});
		table.add(startButton).colspan(2).padTop(50.0f);
		table.row();

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

	/**
	 * Make a set of buttons whose state is displayed via textures and that alter a preference entry when selected.
	 * 
	 * @param table
	 *            The Table this row of Buttons is to be added to.
	 * @param imagePrefixes
	 *            An array of String prefixes for the Button state graphics.
	 * @param prefsName
	 *            The preference entry that will be changed when a button is selected.
	 * @param values
	 *            The preference values associated with the buttons, in the same order as the imagePrefixes.
	 */
	private void makeButtonSet(final Table table, final String[] imagePrefixes, final String prefsName,
			final Enum<?>[] values) {
		// METHOD
		final int n = imagePrefixes.length;
		final Button[] buttons = new Button[n];
		for (int i = 0; i < n; i++) {
			final int index = i;
			buttons[i] = game.makeTexturedButton(imagePrefixes[i], true);
			buttons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					game.savePreference(prefsName, values[index].toString());
				}
			});
		}
		final ButtonGroup group = new ButtonGroup(buttons);
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
}
