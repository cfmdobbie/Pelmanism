package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class BackgroundComponent extends Component {
	Texture texture;
	float u1, v1, u2, v2;
	int screenWidth, screenHeight;
	private static final int TEXTURE_WIDTH = 512;
	private static final int TEXTURE_HEIGHT = 512;
	public BackgroundComponent(PelmanismGame game) {
		super(game);
	}
	@Override
	protected void resize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		u1 = 0.0f;
		v1 = screenHeight / (float) TEXTURE_HEIGHT;
		u2 = screenWidth / (float) TEXTURE_WIDTH;
		v2 = 0.0f;
	}
	@Override
	protected void onAssetsLoaded(AssetManager assetManager) {
		texture = assetManager.get(PelmanismGame.BACKGROUND_TEXTURE);
	}
	@Override
	protected void render(SpriteBatch batch) {
		switch(game.mUiState) {
		case Loading:
			break;
		case LoadingToGame:
			batch.setColor(1.0f, 1.0f, 1.0f, game.mUiAlpha);
			batch.draw(texture, 0, 0, screenWidth, screenHeight, u1, v1, u2, v2);
			break;
		default:
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			batch.draw(texture, 0, 0, screenWidth, screenHeight, u1, v1, u2, v2);
			break;
		}
	}
}