package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class LogoComponent extends Component {
	private static final int TEX_X = 0;
	private static final int TEX_Y = 0;
	private static final int TEX_W = 231;
	private static final int TEX_H = 66;
	private TextureRegion textureRegion;

	public LogoComponent(PelmanismGame game) {
		super(game);
	}

	@Override
	protected void resize(int screenWidth, int screenHeight) {
		if (screenWidth < 250) {
			rect.setSize(TEX_W / 2, TEX_H / 2);
			rect.setPosition((game.mDisplayWidth - TEX_W / 2) / 2, game.mDisplayHeight - 5 - TEX_H / 2);
		} else {
			rect.setSize(TEX_W, TEX_H);
			rect.setPosition((game.mDisplayWidth - TEX_W) / 2, game.mDisplayHeight - 10 - TEX_H);
		}
	}

	@Override
	protected void onAssetsLoaded(AssetManager assetManager) {
		Texture uiTexture = assetManager.get(PelmanismGame.UI_TEXTURE);
		textureRegion = new TextureRegion(uiTexture, TEX_X, TEX_Y, TEX_W, TEX_H);
	}

	@Override
	protected void render(SpriteBatch batch) {
		switch (game.mUiState) {
		case Loading:
			break;
		case LoadingToGame:
			batch.setColor(1.0f, 1.0f, 1.0f, game.mUiAlpha);
			game.drawTextureByRect(textureRegion, rect);
			break;
		default:
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			game.drawTextureByRect(textureRegion, rect);
			break;
		}
	}
}