package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class OptionsAreaComponent extends Component {
	
	private static final String TAG = OptionsAreaComponent.class.getName();
	
	private static final int TEX_X = 0;
	private static final int TEX_Y = 0;
	private static final int TEX_W = 231;
	private static final int TEX_H = 66;
	private TextureRegion textureRegion;
	
	public OptionsAreaComponent(PelmanismGame game) {
		super(game);
	}
	
	@Override
	protected void resize(int screenWidth, int screenHeight) {
		updateBoardArea();
	}
	private final void updateBoardArea() {
		Rectangle emptyArea = game.calculateEmptyArea();
		Gdx.app.log(TAG, "updateBoardArea: emptyArea = " + emptyArea.toString());
		
		final float availableAspect = emptyArea.getAspectRatio(); // w/h
		Gdx.app.log(TAG, "updateBoardArea: availableAspect = " + availableAspect);
		
		final float desiredAspect = 1;
		// TODO: Calculate options area space required
		Gdx.app.log(TAG, "updateBoardArea: desiredAspect = " + desiredAspect);
		
		if (availableAspect < desiredAspect) {
			// Fill width, calculate height
			float desiredHeight = emptyArea.width / desiredAspect;
			float desiredY = (emptyArea.height - desiredHeight) / 2 + emptyArea.y;
			rect.setPosition(emptyArea.x, desiredY);
			rect.setSize(emptyArea.width, desiredHeight);
		} else {
			// Fill height, calculate width
			float desiredWidth = emptyArea.height * desiredAspect;
			float desiredX = (emptyArea.width - desiredWidth) / 2 + emptyArea.x;
			rect.setPosition(desiredX, emptyArea.y);
			rect.setSize(desiredWidth, emptyArea.height);
		}
		
		Gdx.app.log(TAG, "updateBoardArea: rect = " + rect.toString());
	}
	
	@Override
	protected void onAssetsLoaded(AssetManager assetManager) {
		Texture uiTexture = assetManager.get(PelmanismGame.UI_TEXTURE);
		textureRegion = new TextureRegion(uiTexture, TEX_X, TEX_Y, TEX_W, TEX_H);
	}
	
	@Override
	protected void render(SpriteBatch batch) {
		switch(game.mUiState) {
		case GameToOptions_Options:
		case OptionsToGame_Options:
			batch.setColor(1.0f, 1.0f, 1.0f, game.mUiAlpha);
			game.drawTextureByRect(textureRegion, rect);
		case Options:
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			game.drawTextureByRect(textureRegion, rect);
			break;
		default:
			break;
		}
	}
}