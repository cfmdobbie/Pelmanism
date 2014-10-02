package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class PelmanismAI {
	// Needs to have a memory of previous picks
	// Needs to have an *imperfect* memory at times
	// Needs to vary based on difficulty level

	/** Difficulty level of the AI. */
	private final Difficulty difficulty;

	/** The Pelmanism game model. */
	private final Pelmanism model;

	/** List of all cards currently in the game. Will need to be updated wrt matched cards before use. */
	private final Set<Card> allCards = new HashSet<Card>();

	/** List of all cards that have been seen. Will need to be updated wrt matched cards before use. */
	private final Set<Card> seenCards = new HashSet<Card>();

	/** After an invocation to pickCards(), the first card picked. */
	private Card firstCard;

	/** After an invocation to pickCards(), the second card picked. */
	private Card secondCard;

	/** Random number generator. */
	private final Random random = new Random();

	/** Construct a new PelmanismAI object. */
	public PelmanismAI(final Difficulty difficulty, final Pelmanism model) {
		this.model = model;
		this.difficulty = difficulty;

		// Generate a list of all cards, but DON'T LOOK AT THEM! :-)
		for (int i = 0; i < model.getNumberOfCards(); i++) {
			allCards.add(model.getCard(i));
		}
	}

	/** Log a card as having been seen. */
	public void cardSeen(Card card) {
		seenCards.add(card);
	}

	/** Update information we know about the cards on the table. */
	public void updateCards() {
		
		// Forget about any previous selection
		firstCard = null;
		secondCard = null;
		
		// Remove any cards that have been matched
		Iterator<Card> i = allCards.iterator();
		while (i.hasNext()) {
			Card c = i.next();
			if (c.isMatched()) {
				i.remove();
				// If it had been seen previously, remove it from there as well
				if(seenCards.contains(c)) {
					seenCards.remove(c);
				}
			}
		}
		
		// Work out what cards are unknown
		Set<Card> unknownCards = new HashSet<Card>();
		for(Card c : allCards) {
			if(!seenCards.contains(c)) {
				unknownCards.add(c);
			}
		}
		
		// Work out whether any pairs are known
		
		// Arrange cards by pairId
		Map<Integer, Set<Card>> cardsByPairId = new HashMap<Integer, Set<Card>>();
		for(Card c : seenCards) {
			int pairId = c.getPairId();
			if(cardsByPairId.containsKey(pairId)) {
				cardsByPairId.get(pairId).add(c);
			} else {
				cardsByPairId.put(pairId, new HashSet<Card>(Arrays.asList(c)));
			}
		}
		// Check for any known pairs
		List<Integer> knownPairs = new ArrayList<Integer>();
		for(Integer pairId : cardsByPairId.keySet()) {
			if(cardsByPairId.get(pairId).size() == 2) {
				knownPairs.add(pairId);
			}
		}
		if(!knownPairs.isEmpty()) {
			// We know about at least one pair!
		}
		
	}

	public Card pickFirstCard() {
		// Moronic AI - pick a random card
		Card[] cards = allCards.toArray(new Card[]{});
		firstCard = cards[random.nextInt(allCards.size())];
		return firstCard;
	}

	public Card pickSecondCard() {
		// Moronic AI - pick a random card
		Card[] cards = allCards.toArray(new Card[]{});
		do {
			secondCard = cards[random.nextInt(allCards.size())];
		} while (firstCard == secondCard);
		return secondCard;
	}
}