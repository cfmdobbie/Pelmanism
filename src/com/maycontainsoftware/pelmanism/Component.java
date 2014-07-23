package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

abstract class Component {
	protected final PelmanismGame game;
	protected final Rectangle rect; 
	protected Component(PelmanismGame game) {
		this.game = game;
		this.rect = new Rectangle();
	}
	abstract protected void resize(int screenWidth, int screenHeight);
	abstract protected void render(SpriteBatch batch);
	protected void input(Vector2 touch) {}
	protected void update(float delta) {}
	abstract protected void onAssetsLoaded(AssetManager assetManager);
	protected void pause() {};
	protected void resume() {};
}