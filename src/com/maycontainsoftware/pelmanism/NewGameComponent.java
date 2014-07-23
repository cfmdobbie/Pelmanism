package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.maycontainsoftware.pelmanism.PelmanismGame.UiState;

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
		if (screenWidth < 250) {
			rect.setSize(TEX_W / 2, TEX_H / 2);
			rect.setPosition(screenWidth - 5 - (TEX_W / 2), 5);
		} else {
			rect.setSize(TEX_W, TEX_H);
			rect.setPosition(screenWidth - 10 - TEX_W, 10);
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

	@Override
	protected void input(Vector2 touch) {
		switch (game.mUiState) {
		case Options:
			// TODO: Set up for new game
			game.mUiState = UiState.OptionsToGame_Options;
			game.mUiAlpha = 1.0f;
			break;
		case Game:
			// TODO: Set up for new game
			break;
		default:
			break;
		}
	}
}