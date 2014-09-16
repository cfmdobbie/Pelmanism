package com.maycontainsoftware.testgdx2.utils;

import java.io.File;
import java.io.FileFilter;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

/**
 * Utility to pack images in a specific directory tree into texture atlases.
 * 
 * @author Charlie
 */
public class PackTextures {

	public static void main(String[] args) {

		// Hard-coded input directory
		final String inputDirStr = ".\\DevelopmentAssets";
		final File inputDir = new File(inputDirStr);

		// Hard-coded output directory
		final String outputDirStr = "..\\TestGdx2-android\\assets";

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

		// Subdirectories to process
		final File[] dirList = inputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		// Process all subdirectories
		for (File f : dirList) {
			final String subdirStr = f.getAbsolutePath();
			// Name of atlas is the directory name
			final String subdirName = f.getName();
			TexturePacker2.process(settings, subdirStr, outputDirStr, subdirName);
		}
	}
}
