package com.maycontainsoftware.testgdx2;

/** Logical representation of a card on the board. */
class Card {

	/** Unique id for this card. */
	private final int id;

	/** Id of the pair represented by this card. There will be exactly two cards that represent each pair. */
	private final int pairId;

	private boolean matched;

	/** Construct a new card. */
	public Card(final int id, final int pairId) {
		this.id = id;
		this.pairId = pairId;
	}

	/** Get the card id. */
	public final int getId() {
		return id;
	}

	/** Get the card's pair id. */
	public final int getPairId() {
		return pairId;
	}

	/** Check whether this card forms a matched pair with another. */
	public final boolean isMatch(final Card other) {
		if (this == other) {
			throw new IllegalArgumentException("Cards are identical");
		}
		return this.pairId == other.pairId;
	}

	/** Check whether two cards form a matched pair. */
	public static final boolean isMatch(final Card card1, final Card card2) {
		return card1.isMatch(card2);
	}

	/** Whether this card has been matched already. */
	public boolean isMatched() {
		return matched;
	}

	public void setMatched(final boolean matched) {
		this.matched = matched;
	}

	@Override
	public String toString() {
		return "Card[" + id + ", " + pairId + ", " + matched + "]";
	}
}