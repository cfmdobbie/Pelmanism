package com.maycontainsoftware.testgdx2;

/**
 * Representation of a turn that is played.
 * 
 * @author Charlie
 */
class Turn {

	// Turn data
	/** The id of the player that played this turn. This is supplied by the game model when the turn is submitted. */
	private Integer playerId;

	/** The id of the turn. This is supplied by the game model when the turn is submitted. */
	private Integer turnId;

	// Cards
	/** The first picked card. */
	private Card firstPick;

	/** The second picked card. */
	private Card secondPick;

	/** Construct a new turn object. */
	public Turn(final Card firstPick, final Card secondPick) {
		if (firstPick == null || secondPick == null) {
			throw new IllegalArgumentException("Cards cannot be null!");
		}
		if (firstPick.isMatched() || secondPick.isMatched()) {
			throw new IllegalArgumentException("Cards cannot be already matched!");
		}

		this.firstPick = firstPick;
		this.secondPick = secondPick;
	}

	/** Set accessor for the player id. This is called by the game model. */
	public void setPlayerId(final int playerId) {
		this.playerId = playerId;
	}

	/** Get accessor. */
	public int getPlayerId() {
		return playerId;
	}

	/** Set accessor for the turn id. This is called by the game model. */
	public void setTurnId(final int turnId) {
		this.turnId = turnId;
	}

	/** Get accessor. */
	public int getTurnId() {
		return turnId;
	}

	/** Get accessor. */
	public Card getFirstPick() {
		return firstPick;
	}

	/** Get aet accessor. */
	public Card getSecondPick() {
		return secondPick;
	}

	/** Convenience method for testing purposes. */
	@Override
	public String toString() {
		if (playerId == null || turnId == null) {
			return "Turn[" + firstPick + ", " + secondPick + "]";
		} else {
			return "Turn[" + playerId + ", " + turnId + ", " + firstPick + ", " + secondPick + "]";
		}
	}
}