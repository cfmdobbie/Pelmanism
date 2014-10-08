package com.maycontainsoftware.pelmanism;

/**
 * Enumeration of the various difficulties implemented in the app. The difficulty affects board size, speed of gameplay
 * and skill of computer player (if applicable.)
 * 
 * @author Charlie
 */
public enum Difficulty {
	// Easy difficulty
	Easy(3, 4, 0.3f),
	// Medium difficulty
	Medium(4, 5, 0.5f),
	// Hard difficulty
	Hard(5, 6, 0.9f);

	// Difficulties used for testing purposes
	// Easy(1, 2, 0.3f),
	// Medium(2, 2, 0.5f),
	// Hard(3, 2, 0.9f);

	/** The number of columns on the board. */
	private final int numberOfColumns;

	/** The number of rows on the board. */
	private final int numberOfRows;

	/** AI intelligence rating. */
	private final float aiIntelligence;

	/**
	 * Construct a new difficulty setting.
	 * 
	 * @param numberOfColumns
	 * @param numberOfRows
	 */
	private Difficulty(final int numberOfColumns, final int numberOfRows, final float aiIntelligence) {
		this.numberOfColumns = numberOfColumns;
		this.numberOfRows = numberOfRows;
		this.aiIntelligence = aiIntelligence;
	}

	/** The number of pairs on the board in this difficulty mode. */
	public final int getNumberOfPairs() {
		return getTotalCards() / 2;
	}

	/** The total number of cards on the board in this difficulty mode. */
	public final int getTotalCards() {
		return numberOfColumns * numberOfRows;
	}

	/** The number of rows of cards on the board in this difficulty mode. */
	public final int getBoardRows() {
		return numberOfRows;
	}

	/** The number of columns of cards on the board in this difficulty mode. */
	public final int getBoardColumns() {
		return numberOfColumns;
	}

	/** Get the AI's intelligence rating at this difficulty. 0.0f is moronic, 1.0f is Godly. */
	public float getAiIntelligence() {
		return aiIntelligence;
	}
}