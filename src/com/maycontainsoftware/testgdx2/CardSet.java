package com.maycontainsoftware.testgdx2;

/**
 * Enumeration of the different card sets available in this app.
 * 
 * @author Charlie
 */
public enum CardSet {
	// Simple, colorful and easy to recognise symbols
	Simple("simple.atlas"),
	// UK road signs
	Signs("signs.atlas"),
	// Abstract black-and-white dot-and-line designs
	Hard("hard.atlas");

	/** The name of the atlas containing the card set. */
	final String atlasName;

	/** The name of the region in the atlas that contains the "card back" graphic. */
	// Note: card back region is currently consistently-named in all atlases, but this may not be the case in future
	// versions.
	final String backRegionName = "back";

	/**
	 * Construct a new card set configuration.
	 * 
	 * @param atlasName
	 */
	private CardSet(final String atlasName) {
		this.atlasName = atlasName;
	}
}