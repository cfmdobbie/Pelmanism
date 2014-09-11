package com.maycontainsoftware.testgdx2.utils;

import java.io.File;
import java.io.FileFilter;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class PackTextures {

	public static void main(String[] args) {
		final String inputDirStr = ".\\DevelopmentAssets";
		final File inputDir = new File(inputDirStr);
		
		final String outputDirStr = "..\\TestGdx2-android\\assets";
		
		final Settings settings = new Settings();
		settings.pot = true;
		//settings.forceSquareOutput = true;
		settings.minWidth = 256;
		settings.minHeight = 256;
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.filterMag = TextureFilter.Linear;
		settings.filterMin = TextureFilter.Linear;
		settings.alias = false;
		
		final File[] dirList = inputDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		for(File f : dirList) {
			final String subdirStr = f.getAbsolutePath();
			final String subdirName = f.getName();
			TexturePacker2.process(settings, subdirStr, outputDirStr, subdirName);
		}
	}
}
