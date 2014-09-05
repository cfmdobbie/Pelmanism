package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class MainMenuScreen implements Screen {

	private static final String TAG = MainMenuScreen.class.getSimpleName();

	static class OnePlayerActor extends Actor {
		private final TextureRegion region;

		public OnePlayerActor(TextureRegion region) {
			this.region = region;
			setPosition(100, 100);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			batch.draw(region, 0, 0);
		}

		@Override
		public void act(float delta) {
		}

		@Override
		public Actor hit(float x, float y, boolean touchable) {
			System.out.println("hit! 1P");
			return super.hit(x, y, touchable);
		}
	}

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
		table.setTransform(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);

		// Set tiled background for Table, thus for Screen
		final TextureRegion background = game.atlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));

		// Title
		// Need a wrapper around the title label that can be rotated
		final Table titleWrapper = new Table();
		titleWrapper.setTransform(true);
		// titleWrapper.debug();
		final Label title = new Label("Pelmanism!", game.skin, "archristy64", Color.RED);
		title.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "title touchDown");
				RotateToAction rotateAction = new RotateToAction();
				rotateAction.setRotation(titleWrapper.getRotation() + 180.0f);
				rotateAction.setDuration(0.125f);
				titleWrapper.setOrigin(titleWrapper.getWidth() / 2, titleWrapper.getHeight() / 2);
				titleWrapper.addAction(rotateAction);
				return true;
			}
		});
		titleWrapper.add(title);

		table.add(titleWrapper).colspan(3);
		table.row();

		// Players section
		table.add(new Label("Players:", game.skin, "archristy32", Color.WHITE)).colspan(3);
		table.row();
		Button onePlayerButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("player_1p_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("player_1p_on")));
		table.add(onePlayerButton);
		Button twoPlayersButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("player_2p_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("player_2p_on")));
		table.add(twoPlayersButton);
		Button onePlayerVsCpuButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("player_1pvscpu_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("player_1pvscpu_on")));
		table.add(onePlayerVsCpuButton);
		table.row();

		// Difficulty section
		table.add(new Label("Difficulty:", game.skin, "archristy32", Color.WHITE)).colspan(3);
		table.row();
		// TODO: Correct graphics
		Button difficultyEasyButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("difficulty_1_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("difficulty_1_on")));
		table.add(difficultyEasyButton);
		Button difficultyMediumButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("difficulty_2_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("difficulty_2_on")));
		table.add(difficultyMediumButton);
		Button difficultyHardButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("difficulty_3_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("difficulty_3_on")));
		table.add(difficultyHardButton);
		table.row();

		// Card set section
		table.add(new Label("Card set:", game.skin, "archristy32", Color.WHITE)).colspan(3);
		table.row();
		Button cardSetSimpleButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("cards_simple_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("cards_simple_on")));
		table.add(cardSetSimpleButton);
		Button cardSetSignsButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("cards_signs_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("cards_signs_on")));
		table.add(cardSetSignsButton);
		Button cardSetHardButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("cards_hard_off")), null,
				new TextureRegionDrawable(game.atlas.findRegion("cards_hard_on")));
		table.add(cardSetHardButton);
		// new ButtonGroup(cardSetSimpleButton, cardSetSignsButton, cardSetHardButton);
		table.row().padBottom(20.0f);

		// Buttons
		// Help Button
		Button helpButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("help")));
		table.add(helpButton).padTop(50.0f);
		// Start Game Button
		Button startButton = new Button(new TextureRegionDrawable(game.atlas.findRegion("start")));
		table.add(startButton).padTop(50.0f);
		table.row();

		// table.debug();
	}

	@Override
	public void render(float delta) {

		// Clear screen
		Gdx.gl.glClearColor(MyGame.BACKGROUND_COLOR.r, MyGame.BACKGROUND_COLOR.g, MyGame.BACKGROUND_COLOR.b, MyGame.BACKGROUND_COLOR.a);
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
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, game.viewport.x, game.viewport.y, game.viewport.width,
				game.viewport.height);
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
