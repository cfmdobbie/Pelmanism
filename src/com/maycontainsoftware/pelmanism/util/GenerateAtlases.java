package com.maycontainsoftware.pelmanism.util;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

/**
 * Utility to pack images in a specific directory tree into texture atlases.
 * 
 * @author Charlie
 */
public class GenerateAtlases {

	public static void main(String[] args) throws IOException {

		// Input directory
		final String inputDirStr = "./assets/graphics/atlases";
		final File inputDir = new File(inputDirStr);

		// Output directory
		final String outputDirStr = "../Pelmanism-android/assets";

		final Settings settings = new Settings();

		// Supporting OpenGL ES 1.0 - need to avoid non-PoT tectures
		settings.pot = true;

		// All devices should support non-square textures
		settings.forceSquareOutput = false;

		// Atlases should be at least 256x256
		settings.minWidth = 256;
		settings.minHeight = 256;

		// But don't want to have anything bigger than 1024x1024
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;

		// Always use linear filtering
		settings.filterMag = TextureFilter.Linear;
		settings.filterMin = TextureFilter.Linear;

		// Allow aliases if images are detected to be identical
		settings.alias = true;

		// Padding defaults to 2px in both directions; might need to tweak these
		// settings.paddingX = 4;
		// settings.paddingY = 4;

		// TiledDrawable exhibits odd lines between tiles, unless duplicatePadding is turned on
		settings.duplicatePadding = true;

		// Process all subdirectories
		for (final File atlasDirectory : inputDir.listFiles()) {
			final String subdirStr = atlasDirectory.getCanonicalPath();
			// Name of atlas is the directory name
			final String subdirName = atlasDirectory.getName();
			TexturePacker2.process(settings, subdirStr, outputDirStr, subdirName);
		}
	}
}
