package com.maycontainsoftware.testgdx2;

/**
 * Representation of a turn that has been played.
 * 
 * @author Charlie
 */
public class Turn {

	/** The id of this turn. */
	private int id;

	/** The id of the player who played this turn. */
	private int playerId;

	/** The first-picked Card. */
	private Card firstPick;

	/** The second-picked Card. */
	private Card secondPick;

	/** Whether the turn resulted in a match. */
	private boolean match;

	/** Whether the turn resulted in the game finishing. */
	private boolean gameOver;

	/**
	 * @return The turn id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the turn id
	 * 
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the player id
	 * 
	 * @return The player id
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * Set the player id
	 * 
	 * @param playerId
	 *            The player id to set
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	/**
	 * @return The first card picked in this turn
	 */
	public Card getFirstPick() {
		return firstPick;
	}

	/**
	 * @param firstPick
	 *            The first card picked in this turn
	 */
	public void setFirstPick(Card firstPick) {
		this.firstPick = firstPick;
	}

	/**
	 * @return The second card picked in this turn
	 */
	public Card getSecondPick() {
		return secondPick;
	}

	/**
	 * @param secondPick
	 *            The second card picked in this turn
	 */
	public void setSecondPick(Card secondPick) {
		this.secondPick = secondPick;
	}

	/** Report whether the turn resulted in a match. */
	public final boolean isMatch() {
		return match;
	}

	/**
	 * Set whether the two card picks resulted in a match.
	 * 
	 * @param match
	 *            True if the cards were a match, false otherwise.
	 */
	public void setMatch(boolean match) {
		this.match = match;
	}

	/** Report whether the turn resulted in the game finishing. */
	public final boolean isGameOver() {
		return gameOver;
	}

	/**
	 * Set whether or not the game is over
	 * 
	 * @param gameOver
	 *            True if the game is over, false otherwise.
	 */
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	/** Convenience method for testing purposes. */
	@Override
	public String toString() {
		return "TurnResult[" + "" + ", " + match + ", " + gameOver + "]";
	}
}