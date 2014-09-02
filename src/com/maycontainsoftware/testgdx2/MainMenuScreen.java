package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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

	//private final Color bgColour = new Color(154 / 256.0f, 207 / 256.0f, 250 / 256.0f, 1.0f);
	private final Stage stage;
//	private final Texture backgroundTexture;

	public MainMenuScreen(MyGame game) {
		this.game = game;
		
		stage = new Stage(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, true, game.batch);
		stage.setCamera(game.camera);
		//stage.setViewport(game.viewport.width, game.viewport.height, true);
		Gdx.input.setInputProcessor(stage);
		
		Table table = new Table();
		table.setSize(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT);
		//table.setFillParent(true);
		table.defaults().pad(10.0f);
		stage.addActor(table);
		
		TextureRegion background = game.atlas.findRegion("background");
		table.setBackground(new TiledDrawable(background));
		
		// XXX: Different constructors allow setting SpriteBatch and viewport metrics
		//LabelStyle labelStyle = new LabelStyle();
		//table.add(new Label("Label?!?", labelStyle));
		table.row();
		table.add(new Label("Pelmanism!", game.skin, "impact64", Color.WHITE)).colspan(3);
		table.row();
		table.add(new Label("Players:", game.skin, "impact48", Color.WHITE)).colspan(3);
		table.row();
		table.add(new Image(game.atlas.findRegion("player_1p_on")));
		table.add(new Image(game.atlas.findRegion("player_2p_off")));
		table.add(new Image(game.atlas.findRegion("player_1pvscpu_off")));
		table.row();
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
		table.add(new Label("Card set:", game.skin, "impact48", Color.WHITE)).colspan(3);
		table.row();
		table.add(new Image(game.atlas.findRegion("cards_simple_on")));
		table.add(new Image(game.atlas.findRegion("cards_signs_off")));
		table.add(new Image(game.atlas.findRegion("cards_hard_off")));
		table.row();
		
		table.debug();
	}

	@Override
	public void render(float delta) {

		// Clear screen
		//Gdx.gl.glClearColor(bgColour.r, bgColour.g, bgColour.b, bgColour.a);
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
		
		Table.drawDebug(stage);
		
//		game.batch.setProjectionMatrix(game.camera.combined);
//		game.batch.begin();
//		//game.batch.draw(region, 0, 0, 720, 1000);
//		// sprite.draw(game.batch);
//		game.batch.end();

		// ??? stage.act(), stage.draw() ???
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(MyGame.VIRTUAL_WIDTH, MyGame.VIRTUAL_HEIGHT, false, game.viewport.x, game.viewport.y, game.viewport.width, game.viewport.height);
	}

	@Override
	public void show() {

//		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
//		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//
//		region = new TextureRegion(texture, 0, 0, 512, 275);
//
//		sprite = new Sprite(region);
//		sprite.setSize(500.1f, 500.1f * sprite.getHeight() / sprite.getWidth());
//		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
//		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
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
