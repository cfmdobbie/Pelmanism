package com.maycontainsoftware.testgdx2;


/** Representation of the result of a turn being played. */
class TurnResult {

	// Turn
	final Turn turn;
	// Results
	private final boolean match;
	private final boolean gameOver;

	public TurnResult(final Turn turn, final boolean match, final boolean gameOver) {
		this.turn = turn;
		this.match = match;
		this.gameOver = gameOver;

	}

	public final Turn getTurn() {
		return turn;
	}

	public final boolean isMatch() {
		return match;
	}

	public final boolean isGameOver() {
		return gameOver;
	}

	@Override
	public String toString() {
		return "TurnResult[" + turn + ", " + match + ", " + gameOver + "]";
	}
}