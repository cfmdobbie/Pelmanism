package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
	
	static class TwoPlayerActor extends Actor {
		private final TextureRegion region;
		public TwoPlayerActor(TextureRegion region) {
			this.region = region;
			setPosition(200, 100);
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
			System.out.println("hit! 2P");
			return super.hit(x, y, touchable);
		}
	}
	
	static class OnePlayerVsCpuActor extends Actor {
		private final TextureRegion region;
		public OnePlayerVsCpuActor(TextureRegion region) {
			this.region = region;
			setPosition(300, 100);
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
			System.out.println("hit! 1P vs CPU");
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
		Table table = new Table();
		table.setFillParent(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);
		
		// Set tiled background for Table, thus for Screen
		TextureRegion background = game.atlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));
		
		// Title
		table.add(new Label("Pelmanism!", game.skin, "impact64", Color.WHITE)).colspan(3);
		table.row();
		
		// Players section
		table.add(new Label("Players:", game.skin, "impact48", Color.WHITE)).colspan(3);
		table.row();
		table.add(new Image(game.atlas.findRegion("player_1p_on")));
		table.add(new Image(game.atlas.findRegion("player_2p_off")));
		table.add(new Image(game.atlas.findRegion("player_1pvscpu_off")));
		table.row();
		
		// Sound section
		table.add(new Label("Sound:", game.skin, "impact48", Color.WHITE)).colspan(3);
		table.row();
		final Image sound = new Image(game.atlas.findRegion("sound_on"));
		sound.addListener(new InputListener() {
			boolean on = true;
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "sound touched");
				TextureRegionDrawable d = (TextureRegionDrawable)sound.getDrawable();
				d.setRegion(MainMenuScreen.this.game.atlas.findRegion(on ? "sound_off" : "sound_on"));
				on = !on;
				return true;
			}
		});
		table.add(sound);
		table.row();
		
		// Card set section
		table.add(new Label("Card set:", game.skin, "impact48", Color.WHITE)).colspan(3);
		table.row();
		table.add(new Image(game.atlas.findRegion("cards_simple_on")));
		table.add(new Image(game.atlas.findRegion("cards_signs_off")));
		table.add(new Image(game.atlas.findRegion("cards_hard_off")));
		table.row();
		
		//table.debug();
	}

	@Override
	public void render(float delta) {

		// Clear screen
		Gdx.gl.glClearColor(MyGame.BACKGROUND_COLOR.r, MyGame.BACKGROUND_COLOR.g, MyGame.BACKGROUND_COLOR.b, MyGame.BACKGROUND_COLOR.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Update and render Stage
		stage.act();
		stage.draw();
		
		//Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resize(" + width + ", " + height + ")");
		// Update Stage's viewport calculations
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, game.viewport.x, game.viewport.y, game.viewport.width, game.viewport.height);
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
