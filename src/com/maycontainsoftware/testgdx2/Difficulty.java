package com.maycontainsoftware.testgdx2;

/**
 * Enumeration of the various difficulties implemented in the app. The difficulty affects board size, speed of
 * gameplay and skill of computer player (if applicable.)
 * 
 * @author Charlie
 */
public enum Difficulty {
	// Easy difficulty
	Easy(3, 4),
	// Medium difficulty
	// TODO: Temporary change for testing purposes - change medium difficulty to (4,5) when possible
	Medium(2, 2),
	// Hard difficulty
	Hard(5, 6);

	/** The number of columns on the board. */
	private final int numberOfColumns;

	/** The number of rows on the board. */
	private final int numberOfRows;

	/**
	 * Construct a new difficulty setting.
	 * 
	 * @param numberOfColumns
	 * @param numberOfRows
	 */
	private Difficulty(final int numberOfColumns, final int numberOfRows) {
		this.numberOfColumns = numberOfColumns;
		this.numberOfRows = numberOfRows;
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
}