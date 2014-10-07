package com.maycontainsoftware.pelmanism.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopyStaticAssets {

	public static void main(String[] args) throws IOException {

		final File outputDirectory = new File("../Pelmanism-android/assets/");

		final File[] inputDirectories = new File[] { new File("./assets/graphics/static/"),
				new File("./assets/sound/"), };

		for (final File inputDirectory : inputDirectories) {
			for (final File inputFile : inputDirectory.listFiles()) {
				final String filename = inputFile.getName();
				final File outputFile = new File(outputDirectory, filename);
				System.out.println("Copy " + inputFile.getCanonicalPath() + " to " + outputFile.getCanonicalPath());
				copyFile(inputFile, outputFile);
			}
		}
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}
}
