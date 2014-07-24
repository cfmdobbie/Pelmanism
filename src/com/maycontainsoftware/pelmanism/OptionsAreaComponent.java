package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

class OptionsAreaComponent extends Component {

	@SuppressWarnings("unused")
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

	enum TilesetSelection {
		Simple, RoadSigns, Hard
	}

	private TilesetSelection mTilesetSelection;

	enum VolumeSelection {
		On, Off
	}

	private VolumeSelection mVolumeSelection;

	private static final String PREF_TILESET = "pref_tileset";
	private static final String PREF_VOLUME = "pref_volume";

	public OptionsAreaComponent(PelmanismGame game) {
		super(game);

		// Load options from preferences
		loadOptions();
	}

	@Override
	protected void resize(int screenWidth, int screenHeight) {

		// Set active area to the whole empty area
		rect.set(game.calculateEmptyArea());

		// Options screen layout is:
		// tileset text
		// flexigap|card|gap|card|gap|card|flexigap
		// volume text
		// flexigap|card|gap|card|flexigap

		// Size of cards is dependent on line of three cards
		// Card maximum size is 128px, no minimum
		// Gap should be 2px to 16px
		// Flexigap will take up rest of space

		// rect.width

		float cardSize;
		float gap;
		float flexigap3;
		float flexigap2;

		// A bit messy, bit unavoidable until this is all refactored to use a proper layout system!
		if (rect.height < 250) {
			gap = 2;
			cardSize = 48;
		} else if (rect.width > (3 * 128 + 2 * 16)) {
			gap = 16;
			cardSize = 128;
		} else if (rect.width > (3 * 128 + 2 * 2)) {
			gap = 2;
			cardSize = 128;
		} else {
			gap = 2;
			cardSize = (rect.width - 2 * gap) / 3;
		}
		// Calculate flexigap spaces
		flexigap2 = (rect.width - 2 * cardSize - 1 * gap) / 2;
		flexigap3 = (rect.width - 3 * cardSize - 2 * gap) / 2;

		// Reset card sizes
		for (Rectangle r : new Rectangle[] { simpleTilesetRect, signsTilesetRect, hardTilesetRect, volumeOnRect, volumeOffRect }) {
			r.setSize(cardSize);
		}

		// Set all options element positions
		tilesetTextRect.setPosition(rect.x + (rect.width - tilesetTextRect.width) / 2, rect.y + rect.height - tilesetTextRect.height);
		simpleTilesetRect.setPosition(rect.x + flexigap3, tilesetTextRect.y - simpleTilesetRect.height);
		signsTilesetRect.setPosition(simpleTilesetRect.x + simpleTilesetRect.width + gap, simpleTilesetRect.y);
		hardTilesetRect.setPosition(signsTilesetRect.x + signsTilesetRect.width + gap, signsTilesetRect.y);
		soundTextRect.setPosition(rect.x + (rect.width - soundTextRect.width) / 2, simpleTilesetRect.y - soundTextRect.height);
		volumeOnRect.setPosition(rect.x + flexigap2, soundTextRect.y - volumeOnRect.height);
		volumeOffRect.setPosition(volumeOnRect.x + volumeOnRect.width + gap, volumeOnRect.y);
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

	private void drawUiElements(final SpriteBatch batch, final float alpha) {
		final float fadeAlpha = 0.25f;
		float componentAlpha;

		// Draw text labels
		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		game.drawTextureByRect(tilesetTextTex, tilesetTextRect);
		game.drawTextureByRect(soundTextTex, soundTextRect);

		// Draw tileset cards
		componentAlpha = mTilesetSelection == TilesetSelection.Simple ? 1.0f : fadeAlpha;
		batch.setColor(1.0f, 1.0f, 1.0f, alpha * componentAlpha);
		game.drawTextureByRect(simpleTilesetTex, simpleTilesetRect);

		componentAlpha = mTilesetSelection == TilesetSelection.RoadSigns ? 1.0f : fadeAlpha;
		batch.setColor(1.0f, 1.0f, 1.0f, alpha * componentAlpha);
		game.drawTextureByRect(signsTilesetTex, signsTilesetRect);

		componentAlpha = mTilesetSelection == TilesetSelection.Hard ? 1.0f : fadeAlpha;
		batch.setColor(1.0f, 1.0f, 1.0f, alpha * componentAlpha);
		game.drawTextureByRect(hardTilesetTex, hardTilesetRect);

		// Draw volume cards
		componentAlpha = mVolumeSelection == VolumeSelection.On ? 1.0f : fadeAlpha;
		batch.setColor(1.0f, 1.0f, 1.0f, alpha * componentAlpha);
		game.drawTextureByRect(volumeOnTex, volumeOnRect);

		componentAlpha = mVolumeSelection == VolumeSelection.Off ? 1.0f : fadeAlpha;
		batch.setColor(1.0f, 1.0f, 1.0f, alpha * componentAlpha);
		game.drawTextureByRect(volumeOffTex, volumeOffRect);

	}

	@Override
	protected void render(final SpriteBatch batch) {
		switch (game.mUiState) {
		case GameToOptions_Options:
		case OptionsToGame_Options:
			drawUiElements(batch, game.mUiAlpha);
			break;
		case Options:
			drawUiElements(batch, 1.0f);
			break;
		default:
			break;
		}
	}

	protected void saveOptions() {
		// Save selections to preferences
		game.mPrefs.putString(PREF_TILESET, mTilesetSelection.toString());
		game.mPrefs.putString(PREF_VOLUME, mVolumeSelection.toString());
		game.mPrefs.flush();
	}

	// Load tileset selection from preferences
	protected static final TilesetSelection getTilesetOption(Preferences prefs) {
		String prefTileset = prefs.getString(PREF_TILESET);
		for (TilesetSelection tilesetSelection : TilesetSelection.values()) {
			if (tilesetSelection.toString().equals(prefTileset)) {
				return tilesetSelection;
			}
		}
		return TilesetSelection.Simple;
	}
	
	// Load volume selection from preferences
	protected static final VolumeSelection getVolumeOption(Preferences prefs) {
		String prefVolume = prefs.getString(PREF_VOLUME);
		for (VolumeSelection volumeSelection : VolumeSelection.values()) {
			if (volumeSelection.toString().equals(prefVolume)) {
				return volumeSelection;
			}
		}
		return VolumeSelection.On;
	}
	
	protected void loadOptions() {
		mTilesetSelection = getTilesetOption(game.mPrefs);
		mVolumeSelection = getVolumeOption(game.mPrefs);
	}

	@Override
	protected void input(Vector2 touch) {
		switch (game.mUiState) {
		case Options:

			if (mTilesetSelection != TilesetSelection.Simple && simpleTilesetRect.contains(touch)) {
				mTilesetSelection = TilesetSelection.Simple;
			} else if (mTilesetSelection != TilesetSelection.RoadSigns && signsTilesetRect.contains(touch)) {
				mTilesetSelection = TilesetSelection.RoadSigns;
			} else if (mTilesetSelection != TilesetSelection.Hard && hardTilesetRect.contains(touch)) {
				mTilesetSelection = TilesetSelection.Hard;
			} else if (mVolumeSelection != VolumeSelection.On && volumeOnRect.contains(touch)) {
				mVolumeSelection = VolumeSelection.On;
			} else if (mVolumeSelection != VolumeSelection.Off && volumeOffRect.contains(touch)) {
				mVolumeSelection = VolumeSelection.Off;
			}

			// Just in case anything's changed, save the options to preferences
			// TODO: A better way to detect changed options?
			saveOptions();

			break;
		default:
			break;
		}
	}
}