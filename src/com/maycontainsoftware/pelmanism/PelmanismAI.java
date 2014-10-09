package com.maycontainsoftware.pelmanism;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Computer player, with very simple AI.
 * 
 * @author Charlie
 */
public class PelmanismAI {
	// Needs to have a memory of previous picks
	// Needs to have an *imperfect* memory at times
	// Needs to vary based on difficulty level

	/** Difficulty level of the AI. */
	private final Difficulty difficulty;

	// ** The Pelmanism game model. */
	// private final Pelmanism model;

	/** All cards currently in the game. */
	private final Set<Card> allCards = new HashSet<Card>();

	/** All cards that have been seen. */
	private final Set<Card> seenCards = new HashSet<Card>();

	/** All cards that have not been revealed yet. */
	private final Set<Card> unknownCards = new HashSet<Card>();

	/** Mapping of pairId to collection of Cards. */
	private final Map<Integer, Set<Card>> cardsByPairId = new HashMap<Integer, Set<Card>>();

	/** All known pairIds where both Cards of the pair have been seen. */
	private final Set<Integer> knownPairs = new HashSet<Integer>();

	/** After an invocation to pickCards(), the first card picked. */
	private Card firstCard;

	/** After an invocation to pickCards(), the second card picked. */
	private Card secondCard;

	/** Random number generator. */
	private final Random random = new Random();

	/** Enumeration of the ways the AI could try to play the next turn. */
	private static enum Intention {
		/** Try to go for a pair, or at least be intelligent about the next turn. */
		PAIR,
		/** Behave randomly. */
		RANDOM,
	}

	/** The AI's current intention. */
	private Intention intention;

	/** Construct a new PelmanismAI object. */
	public PelmanismAI(final Difficulty difficulty, final Pelmanism model) {
		// this.model = model;
		this.difficulty = difficulty;

		// Generate a list of all cards, but DON'T LOOK AT THEM! :-)
		for (int i = 0; i < model.getNumberOfCards(); i++) {
			allCards.add(model.getCard(i));
		}

		// Update our current understanding of the cards
		updateCards();
	}

	/** Log a card as having been seen. */
	public void cardSeen(final Card card) {
		// Make sure it's been seen
		seenCards.add(card);
		// Make sure it's no longer unseen
		if (unknownCards.contains(card)) {
			unknownCards.remove(card);
		}
		// Update the record of pairs
		final int pairId = card.getPairId();
		if (cardsByPairId.containsKey(pairId)) {
			cardsByPairId.get(pairId).add(card);
		} else {
			cardsByPairId.put(pairId, new HashSet<Card>(Arrays.asList(card)));
		}
		// Update the log of known pairs
		if (cardsByPairId.get(pairId).size() == 2) {
			knownPairs.add(pairId);
		}
	}

	/** Update information we know about the cards on the table. */
	public void updateCards() {

		// Forget about any previous selection
		firstCard = null;
		secondCard = null;

		// Remove any cards that have been matched
		final Iterator<Card> i = allCards.iterator();
		while (i.hasNext()) {
			final Card c = i.next();
			if (c.isMatched()) {
				i.remove();
				// If it had been seen previously, remove it from there as well
				if (seenCards.contains(c)) {
					seenCards.remove(c);
				}
			}
		}

		// Work out what cards are unknown
		unknownCards.clear();
		for (final Card c : allCards) {
			if (!seenCards.contains(c)) {
				unknownCards.add(c);
			}
		}

		// Work out current approach to picking a card
		// Generate a random number between 0 and 1. If the AI intelligence is set higher, go for a pair.
		intention = (difficulty.getAiIntelligence() > random.nextFloat()) ? Intention.PAIR : Intention.RANDOM;

		// Work out whether any pairs are known

		// Arrange cards by pairId
		cardsByPairId.clear();
		for (final Card c : seenCards) {
			final int pairId = c.getPairId();
			if (cardsByPairId.containsKey(pairId)) {
				cardsByPairId.get(pairId).add(c);
			} else {
				cardsByPairId.put(pairId, new HashSet<Card>(Arrays.asList(c)));
			}
		}
		// Log ids for any known pairs
		knownPairs.clear();
		for (final Integer pairId : cardsByPairId.keySet()) {
			if (cardsByPairId.get(pairId).size() == 2) {
				knownPairs.add(pairId);
			}
		}
	}

	public Card pickFirstCard() {
		switch (intention) {
		case PAIR:
			if (!knownPairs.isEmpty()) {
				// We know about at least one pair! Pick a random one
				final int pairId = randomElement(knownPairs);
				final Card[] cards = cardsByPairId.get(pairId).toArray(new Card[] {});
				firstCard = cards[0];
				secondCard = cards[1];
			} else {
				// Don't know any pairs - pick a random unseen card
				firstCard = randomElement(unknownCards);
				// Can this fail? Only if no unseen cards exist. It is not possible to have both no known pairs and no
				// unseen cards.
			}
			break;
		case RANDOM:
			// Pick a random card
			firstCard = randomElement(allCards);
			break;
		default:
			throw new IllegalStateException();
		}
		return firstCard;
	}

	public Card pickSecondCard() {
		if (secondCard == null) {
			switch (intention) {
			case PAIR:
				// Want a pair, but didn't originally know any
				// Might have found one now?
				final int pairId = firstCard.getPairId();
				if (knownPairs.contains(pairId)) {
					// Have just found a pair!
					// Need to find the other half of it...
					final Card[] pair = cardsByPairId.get(pairId).toArray(new Card[] {});
					secondCard = (firstCard == pair[1]) ? pair[0] : pair[1];
				} else {
					// Still don't know any pairs.
					// Be a little clever - try and pick an already-known card so we don't give anything away
					if (seenCards.size() > 1) {
						do {
							secondCard = randomElement(seenCards);
						} while (firstCard == secondCard);
					} else {
						// Don't know any other cards; just pick a random one
						do {
							secondCard = randomElement(allCards);
						} while (firstCard == secondCard);
					}
				}
				break;
			case RANDOM:
				// Moronic AI - pick a random card
				do {
					secondCard = randomElement(allCards);
				} while (firstCard == secondCard);
				break;
			default:
				throw new IllegalStateException();
			}
		}
		return secondCard;
	}

	@SuppressWarnings("unchecked")
	private final <T extends Object> T randomElement(final Set<T> set) {
		final Object[] cards = set.toArray();
		return (T) cards[random.nextInt(cards.length)];
	}
}