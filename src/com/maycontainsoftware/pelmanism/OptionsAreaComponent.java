package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class OptionsAreaComponent extends Component {

	private static final String TAG = OptionsAreaComponent.class.getName();

	// Texture Regions
	private TextureRegion simpleTilesetTex;
	private TextureRegion signsTilesetTex;
	private TextureRegion hardTilesetTex;
	private TextureRegion volumeOnTex;
	private TextureRegion volumeOffTex;
	private TextureRegion tilesetTextTex;
	private TextureRegion soundTextTex;
	
	// Rectangles
	private Rectangle simpleTilesetRect = new Rectangle(0, 0, 128, 128);
	private Rectangle signsTilesetRect = new Rectangle(0, 0, 128, 128);
	private Rectangle hardTilesetRect = new Rectangle(0, 0, 128, 128);
	private Rectangle volumeOnRect = new Rectangle(0, 0, 128, 128);
	private Rectangle volumeOffRect = new Rectangle(0, 0, 128, 128);
	private Rectangle tilesetTextRect = new Rectangle(0, 0, 227 - 127, 152 - 110);
	private Rectangle soundTextRect = new Rectangle(0, 0, 311 - 227, 152 - 110);

	public OptionsAreaComponent(PelmanismGame game) {
		super(game);
	}

	@Override
	protected void resize(int screenWidth, int screenHeight) {
		updateBoardArea();
		
		// Options screen layout is: tileset text / tilesets / volume text / volumes
		tilesetTextRect.setPosition(rect.x, rect.y + rect.height - tilesetTextRect.height);
		simpleTilesetRect.setPosition(rect.x, tilesetTextRect.y - simpleTilesetRect.height);
		signsTilesetRect.setPosition(simpleTilesetRect.x + simpleTilesetRect.width, simpleTilesetRect.y);
		hardTilesetRect.setPosition(signsTilesetRect.x + signsTilesetRect.width, signsTilesetRect.y);
		soundTextRect.setPosition(rect.x, simpleTilesetRect.y - soundTextRect.height);
		volumeOnRect.setPosition(rect.x, soundTextRect.y - volumeOnRect.height);
		volumeOffRect.setPosition(volumeOnRect.x + volumeOnRect.width, volumeOnRect.y);
	}

	private final void updateBoardArea() {
		Rectangle emptyArea = game.calculateEmptyArea();
		Gdx.app.log(TAG, "updateBoardArea: emptyArea = " + emptyArea.toString());
		
		rect.set(emptyArea);

		/*
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
		*/

		Gdx.app.log(TAG, "updateBoardArea: rect = " + rect.toString());
	}

	@Override
	protected void onAssetsLoaded(AssetManager assetManager) {
		Texture uiTexture = assetManager.get(PelmanismGame.UI_TEXTURE);
		simpleTilesetTex = new TextureRegion(uiTexture, 384, 384, 128, 128);
		signsTilesetTex = new TextureRegion(uiTexture, 256, 384, 128, 128);
		hardTilesetTex = new TextureRegion(uiTexture, 128, 384, 128, 128);
		volumeOnTex = new TextureRegion(uiTexture, 384, 256, 128, 128);
		volumeOffTex = new TextureRegion(uiTexture, 256, 256, 128, 128);
		tilesetTextTex = new TextureRegion(uiTexture, 127, 110, 227 - 127, 152 - 110);
		soundTextTex = new TextureRegion(uiTexture, 227, 110, 311 - 227, 152 - 110);
	}
	
	private void drawUiElements(SpriteBatch batch) {
		game.drawTextureByRect(tilesetTextTex, tilesetTextRect);
		game.drawTextureByRect(soundTextTex, soundTextRect);
		game.drawTextureByRect(simpleTilesetTex, simpleTilesetRect);
		game.drawTextureByRect(signsTilesetTex, signsTilesetRect);
		game.drawTextureByRect(hardTilesetTex, hardTilesetRect);
		game.drawTextureByRect(volumeOnTex, volumeOnRect);
		game.drawTextureByRect(volumeOffTex, volumeOffRect);
	}

	@Override
	protected void render(SpriteBatch batch) {
		switch (game.mUiState) {
		case GameToOptions_Options:
		case OptionsToGame_Options:
			batch.setColor(1.0f, 1.0f, 1.0f, game.mUiAlpha);
			drawUiElements(batch);
		case Options:
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			// TODO: Fade will be 0.25f
			drawUiElements(batch);
			break;
		default:
			break;
		}
	}
}