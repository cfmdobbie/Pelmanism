package com.maycontainsoftware.testgdx2;

/** Representation of a turn that is played. */
class Turn {
	// Turn data
	private Integer playerId;
	private Integer turnId;
	// Cards
	private Card firstPick;
	private Card secondPick;

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

	public void setPlayerId(final int playerId) {
		this.playerId = playerId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setTurnId(final int turnId) {
		this.turnId = turnId;
	}

	public int getTurnId() {
		return turnId;
	}

	public Card getFirstPick() {
		return firstPick;
	}

	public Card getSecondPick() {
		return secondPick;
	}

	@Override
	public String toString() {
		if (playerId == null || turnId == null) {
			return "Turn[" + firstPick + ", " + secondPick + "]";
		} else {
			return "Turn[" + playerId + ", " + turnId + ", " + firstPick + ", " + secondPick + "]";
		}
	}
}