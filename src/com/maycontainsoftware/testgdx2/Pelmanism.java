package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Pelmanism {

	// Players
	final int numberOfPlayers;
	private int currentPlayerId;
	private final int[] playerScores;
	// Pairs
	private final int numberOfPairs;
	private int pairsFound;
	// Cards
	final int numberOfCards;
	private final Card[] cards;
	// Game state
	private int lastTurnId;
	private boolean gameOver;

	public Pelmanism(final int numberOfPlayers, final int numberOfPairs) {

		// Check arguments
		if (numberOfPlayers <= 0) {
			throw new IllegalArgumentException("numberOfPlayers = " + numberOfPlayers);
		}
		if (numberOfPairs <= 0) {
			throw new IllegalArgumentException("numberOfPairs = " + numberOfPairs);
		}

		// Set up all game properties
		// Constants
		this.numberOfPlayers = numberOfPlayers;
		this.numberOfPairs = numberOfPairs;
		this.numberOfCards = numberOfPairs * 2;
		// Constant arrays of variables
		this.playerScores = new int[numberOfPlayers];
		this.cards = new Card[numberOfCards];

		resetGame();
	}
	
	public int getNumberOfPairs() {
		return numberOfPairs;
	}

	/** Returns the current player number. */
	public final int getCurrentPlayerId() {
		return currentPlayerId;
	}
	
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public final int getPlayerScore(final int playerId) {
		if (playerId < 0 || playerId >= numberOfPlayers) {
			throw new IllegalArgumentException("Invalid player id: " + playerId);
		}
		return playerScores[playerId];
	}

	/** Moves control to the next player. */
	private final void nextPlayer() {
		if (numberOfPlayers != 1) {
			currentPlayerId = (currentPlayerId + 1) % numberOfPlayers;
		}
	}

	public final Card getCard(final int cardId) {
		// Check arguments
		if (cardId < 0 || cardId >= numberOfCards) {
			throw new IllegalArgumentException("Card id invalid: " + cardId);
		}
		return cards[cardId];
	}

	public final boolean isCardPickable(final int cardId) {
		// Check arguments
		if (cardId < 0 || cardId >= numberOfCards) {
			throw new IllegalArgumentException("Card id invalid: " + cardId);
		}

		// If already matched, cannot pick again
		if (cards[cardId].isMatched()) {
			return false;
		}

		return true;
	}

	public final boolean isGameOver() {
		return gameOver;
	}

	public final TurnResult turn(final Turn turn) {

		// TODO: Sanity check turn data

		// Increment turn counter
		lastTurnId++;

		// Update turn information
		turn.setTurnId(lastTurnId);
		turn.setPlayerId(currentPlayerId);

		// The result of this turn being played
		final TurnResult result;

		if (!Card.isMatch(turn.getFirstPick(), turn.getSecondPick())) {
			// No match!

			result = new TurnResult(turn, false, false);

			// Control passes to next player
			nextPlayer();
		} else {
			// Match!

			// Mark cards as matched
			turn.getFirstPick().setMatched(true);
			turn.getSecondPick().setMatched(true);

			// Update pairs found
			pairsFound++;
			// Update player score
			playerScores[currentPlayerId]++;

			if (pairsFound < numberOfPairs) {
				// Game continues!
				result = new TurnResult(turn, true, false);
			} else {
				// Game over!
				result = new TurnResult(turn, true, true);
				this.gameOver = true;
			}

		}

		return result;
	}

	private final void resetGame() {

		// Initialise variables

		// Before first turn, so last turn is -1
		this.lastTurnId = -1;

		// Always start with player one
		this.currentPlayerId = 0;

		// No pairs found at start of game
		this.pairsFound = 0;

		// Game is not over
		this.gameOver = false;

		// Reset all player scores to zero
		for (int i = 0; i < numberOfPlayers; i++) {
			playerScores[i] = 0;
		}

		// Generate new card layout
		final List<Integer> pairIds = new ArrayList<Integer>(numberOfCards);
		for (int i = 0; i < numberOfPairs; i++) {
			pairIds.add(i);
			pairIds.add(i);
		}
		Collections.shuffle(pairIds);
		for (int i = 0; i < numberOfCards; i++) {
			cards[i] = new Card(i, pairIds.get(i));
		}
	}
}