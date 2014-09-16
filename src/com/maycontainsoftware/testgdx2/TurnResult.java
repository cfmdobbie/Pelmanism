package com.maycontainsoftware.testgdx2;

/**
 * Representation of the result of a turn being played.
 * 
 * @author Charlie
 */
class TurnResult {

	/** The turn that caused this result. */
	final Turn turn;

	/** Whether the turn resulted in a match. */
	private final boolean match;

	/** Whether the turn resulted in the game finishing. */
	private final boolean gameOver;

	/** Construct a new TurnResult. */
	public TurnResult(final Turn turn, final boolean match, final boolean gameOver) {
		this.turn = turn;
		this.match = match;
		this.gameOver = gameOver;

	}

	/** Get the turn that caused this result. */
	public final Turn getTurn() {
		return turn;
	}

	/** Report whether the turn resulted in a match. */
	public final boolean isMatch() {
		return match;
	}

	/** Report whether the turn resulted in the game finishing. */
	public final boolean isGameOver() {
		return gameOver;
	}

	/** Convenience method for testing purposes. */
	@Override
	public String toString() {
		return "TurnResult[" + turn + ", " + match + ", " + gameOver + "]";
	}
}