package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class NewGameComponent extends Component {
	private static final int TEX_X = 0;
	private static final int TEX_Y = 66;
	private static final int TEX_W = 124;
	private static final int TEX_H = 44;
	private TextureRegion textureRegion;
	public NewGameComponent(PelmanismGame game) {
		super(game);
	}
	@Override
	protected void resize(int screenWidth, int screenHeight) {
		rect.setSize(TEX_W, TEX_H);
		rect.setPosition(screenWidth - 10 - TEX_W, 10);
	}
	@Override
	protected void onAssetsLoaded(AssetManager assetManager) {
		Texture uiTexture = assetManager.get(PelmanismGame.UI_TEXTURE);
		textureRegion = new TextureRegion(uiTexture, TEX_X, TEX_Y, TEX_W, TEX_H);
	}
	@Override
	protected void render(SpriteBatch batch) {
		switch(game.mUiState) {
		case LoadingToGame:
		case GameToOptions_Game:
		case GameToOptions_Options:
		case OptionsToGame_Game:
		case OptionsToGame_Options:
			batch.setColor(1.0f, 1.0f, 1.0f, game.mUiAlpha);
			game.drawTextureByRect(textureRegion, rect);
			break;
		case Game:
		case Options:
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			game.drawTextureByRect(textureRegion, rect);
			break;
		default:
			break;
		}
	}
}