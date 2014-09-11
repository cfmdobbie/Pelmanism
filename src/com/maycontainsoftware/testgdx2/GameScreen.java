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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.maycontainsoftware.testgdx2.MyGame.CardSet;
import com.maycontainsoftware.testgdx2.MyGame.Difficulty;
import com.maycontainsoftware.testgdx2.MyGame.PlayerConfiguration;

public class GameScreen implements Screen {

	private static final String TAG = GameScreen.class.getSimpleName();

	private final MyGame game;
	
	// Game configuration
	private final PlayerConfiguration playerConfiguration;
	private final Difficulty difficulty;
	
	// Display-type properties
	private final Stage stage;
	private final TextureAtlas atlas;
	private final TextureRegion cardBackRegion;
	
	// Game model
	private final Pelmanism model;
	
	static class CardActor extends Image {
		final int index;
		final TextureRegion cardTexture;
		final TextureRegion cardBackTexture;

		public CardActor(final int index, final TextureRegion cardTexture, final TextureRegion cardBackTexture) {
			this.index = index;
			this.cardTexture = cardTexture;
			this.cardBackTexture = cardBackTexture;

			this.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					final Action setCardBack = new Action() {
						@Override
						public boolean act(float delta) {
							final TextureRegionDrawable drawable = (TextureRegionDrawable) (CardActor.this.getDrawable());
							drawable.setRegion(cardBackTexture);
							return true;
						}
					};
					CardActor.this.addAction(Actions.sequence(Actions.fadeOut(0.25f), setCardBack, Actions.fadeIn(0.25f)));
					return true;
				}
			});
		}
	}

	public GameScreen(MyGame game) {
		this.game = game;

		// Players
		final String playerConfigurationPref = game.mPrefs.getString(MyGame.PREF_PLAYERS, PlayerConfiguration.One.toString());
		playerConfiguration = PlayerConfiguration.valueOf(playerConfigurationPref);

		// Difficulty
		final String difficultyPref = game.mPrefs.getString(MyGame.PREF_DIFFICULTY, Difficulty.Easy.toString());
		difficulty = Difficulty.valueOf(difficultyPref);

		// Determine board size
		final int cardsPerRow;
		final int rows;
		cardsPerRow = rows = difficulty.boardSize;
		final int totalCards = cardsPerRow * rows;
		final int numberOfPairs = totalCards / 2;

		// Load graphic assets
		atlas = getCardSetAtlasFromPrefs();
		cardBackRegion = atlas.findRegion(CardSet.backRegionName);

		// Create game model
		model = new Pelmanism(playerConfiguration.numberOfPlayers, totalCards);

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
		// TODO: Note that the TiledDrawable doesn't render correctly; this should be replaced
		final TextureRegion background = game.uiAtlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));

		// Secondary score display

		if (playerConfiguration.secondPlayerExists()) {
			// Will need a second score display

			table.row().padTop(20.0f);
			final SpinningTable playerTwoTable = new SpinningTable();
			// p2Table.debug();
			playerTwoTable.add(new Label(playerConfiguration.secondPlayerName, game.skin, "archristy48", playerConfiguration.secondPlayerColor));
			playerTwoTable.add().expandX();
			// TODO: Need to be able to update points on the fly, so need access to this Label
			playerTwoTable.add(new Label("0 Points", game.skin, "archristy48", playerConfiguration.secondPlayerColor));
			table.add(playerTwoTable).colspan(2).fillX();

			// N.B. TiledDrawable leaves <1px gaps, so this is not usable
			// p2Table.setBackground(new TiledDrawable(game.uiAtlas.findRegion("yellow")));
		}

		// Game area

		table.row().padTop(30.0f);

		// Given numberOfPairs, return that number of unique random TextureRegions from appropriate TextureAtlas
		final TextureRegion[] regions = selectCardTextures(numberOfPairs);

		final Table gameArea = new Table();
		// gameArea.debug();

		// Set up card grid

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cardsPerRow; c++) {

				final int cardIndex = c + r * cardsPerRow;
				CardActor cardActor_ = new CardActor(cardIndex);
				gameArea.add(cardActor_).expand().pad(5.0f);

				final int card = (c + r * cardsPerRow) % regions.length;
				final Image cardActor = new Image(regions[card]);
				final String cardName = "(" + c + "," + r + ")";
				cardActor.addListener(new InputListener() {
					@Override
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
						System.out.println("touched " + cardName);

						Action setCardBack = new Action() {
							@Override
							public boolean act(float delta) {
								final TextureRegionDrawable drawable = (TextureRegionDrawable) (cardActor.getDrawable());
								drawable.setRegion(cardBackRegion);
								return true;
							}
						};
						cardActor.addAction(Actions.sequence(Actions.fadeOut(0.25f), setCardBack, Actions.fadeIn(0.25f)));

						return true;
					}
				});

				gameArea.add(cardActor).expand().pad(5.0f);
			}
			gameArea.row().expandY();
		}

		// Game area is fixed in size, but must take up all remaining space in table
		// Game area should be square to avoid rendering cards out-of-aspect
		// Hard-coded table size here, as that's the easiest way to do it
		table.add(gameArea).width(645.0f).height(645.0f).expandX().expandY().colspan(2);

		// Primary score display

		// Player one info
		table.row().padTop(30.0f);
		final SpinningTable playerOneTable = new SpinningTable();
		// p1Table.debug();
		playerOneTable.add(new Label("Player One", game.skin, "archristy48", Color.RED));
		playerOneTable.add().expandX();
		// TODO: Need to be able to update points on the fly, so need access to this Label
		playerOneTable.add(new Label("0 Points", game.skin, "archristy48", Color.RED));
		table.add(playerOneTable).colspan(2).fillX();

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
	
	private final TextureAtlas getCardSetAtlasFromPrefs() {
		final CardSet cardSet = game.getCardSetFromPrefs();
		return game.manager.get(cardSet.atlasName, TextureAtlas.class);
	}

	private final TextureRegion[] selectCardTextures(int n) {
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
