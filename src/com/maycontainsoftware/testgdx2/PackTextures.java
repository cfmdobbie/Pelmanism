package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class PackTextures {

	public static void main(String[] args) {
		String inputDir = ".\\DevelopmentAssets";
		String outputDir = "..\\TestGdx2-android\\assets";
		String packFileName = "pelmanism.atlas";

		Settings settings = new Settings();
		settings.pot = true;
		//settings.forceSquareOutput = true;
		settings.minWidth = 256;
		settings.minHeight = 256;
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.filterMag = TextureFilter.Linear;
		settings.filterMin = TextureFilter.Linear;

		TexturePacker2.process(settings, inputDir, outputDir, packFileName);
	}
}
