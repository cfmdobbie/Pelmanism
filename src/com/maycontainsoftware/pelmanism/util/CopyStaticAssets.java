package com.maycontainsoftware.pelmanism.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Utility to copy all static graphics and sound to the assets directory in the Android application directory.
 * 
 * @author Charlie
 */
public class CopyStaticAssets {

	public static void main(String[] args) throws IOException {

		// Target directory - note assumed project name!
		final File outputDirectory = new File("../Pelmanism-android/assets/");

		// Fixed assets in the local assets directory
		final File[] inputDirectories = new File[] { new File("./assets/graphics/static/"),
				new File("./assets/sound/"), };

		// Copy all files in the input directories to the output directory
		for (final File inputDirectory : inputDirectories) {
			for (final File inputFile : inputDirectory.listFiles()) {
				final String filename = inputFile.getName();
				final File outputFile = new File(outputDirectory, filename);
				System.out.println("Copy " + inputFile.getCanonicalPath() + " to " + outputFile.getCanonicalPath());
				copyFile(inputFile, outputFile);
			}
		}
	}

	/**
	 * Efficiently copy contents of one file to another.
	 * 
	 * @param sourceFile
	 *            The file to copy from
	 * @param destFile
	 *            The file to copy to
	 * @throws IOException
	 */
	private static void copyFile(final File sourceFile, final File destFile) throws IOException {
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
