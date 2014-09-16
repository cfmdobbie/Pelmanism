package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pelmanism game model.
 * 
 * @author Charlie
 */
class Pelmanism {

	// Players

	/** The number of players in the game. */
	final int numberOfPlayers;

	/** The id of the current player. Player ids are zero-indexed, of course. */
	private int currentPlayerId;

	/** Player scores. A player's score is the number of successful pairs found. */
	private final int[] playerScores;

	// Pairs

	/** The number of pairs on the table. */
	private final int numberOfPairs;

	/** How many pairs of cards have been found so far in this game. */
	private int pairsFound;

	// Cards

	/** The number of cards on the table. */
	final int numberOfCards;

	/** The cards on the board. */
	private final Card[] cards;

	// Game state

	/** The last turn id to be completed. */
	private int lastTurnId;

	/** Whether or not the game is over. */
	private boolean gameOver;

	/**
	 * Create a new instance of the game model.
	 * 
	 * @param numberOfPlayers
	 *            The number of players in the game.
	 * @param numberOfPairs
	 *            The number of pairs on the table.
	 */
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

	/** The total number of pairs available on the board. */
	public final int getNumberOfPairs() {
		return numberOfPairs;
	}

	/** The number of cards on the board. */
	public final int getNumberOfCards() {
		return numberOfCards;
	}

	/** Returns the current player number. */
	public final int getCurrentPlayerId() {
		return currentPlayerId;
	}

	/** The number of players in the game. */
	public final int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/** Player score. */
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

	/** Get a card, by id. */
	public final Card getCard(final int cardId) {
		// Check arguments
		if (cardId < 0 || cardId >= numberOfCards) {
			throw new IllegalArgumentException("Card id invalid: " + cardId);
		}
		return cards[cardId];
	}

	/** Whether card is a valid choice to turn over at this time. */
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

	/** Whether current game is over. */
	public final boolean isGameOver() {
		return gameOver;
	}

	/** Submit a new turn. */
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

	/** Reset game state in preparation for next game. */
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