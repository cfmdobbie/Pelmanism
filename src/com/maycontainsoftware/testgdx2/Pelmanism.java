package com.maycontainsoftware.testgdx2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model that represents a game of Pelmanism.
 * 
 * @author Charlie
 */
public class Pelmanism {

	// Fundamental properties of the game model

	/** The number of players in the game. */
	private final int numberOfPlayers;

	/** The number of cards on the table. */
	private final int numberOfCards;

	/** The number of pairs on the table. */
	private final int numberOfPairs;

	// Fundamental properties of the current game

	/** Card arrangement, for determining matches. */
	private final int[] cards;

	// Current game state

	/** Game state engine */
	public static enum GameState {
		PendingFirstPick,
		PendingSecondPick,
		CardsChosen,
		GameOver
	}

	/** Game state engine state */
	private GameState gameState = GameState.PendingFirstPick;

	/** The number of the current player. Player numbers are zero-indexed. */
	private int currentPlayer = 0;

	/** Player scores. A player's score is the number of successful pairs found. */
	private final int[] playerScores;

	/** Pairs of cards found. */
	private int pairsFound;

	/** Cards that have already been matched. */
	private final boolean[] matched;

	/** The index of the first card picked. */
	private Integer firstPick = null;

	/** The index of the second card picked. */
	private Integer secondPick = null;

	/**
	 * Create a new instance of a game model.
	 * 
	 * @param numberOfPlayers
	 *            The number of players in the game.
	 * @param numberOfCards
	 *            The number of cards on the table.
	 */
	public Pelmanism(final int numberOfPlayers, final int numberOfCards) {

		// Check arguments
		if (numberOfPlayers <= 0) {
			throw new IllegalArgumentException("numberOfPlayers = " + numberOfPlayers);
		}
		if (numberOfCards <= 0 || (numberOfCards % 2) != 0) {
			throw new IllegalArgumentException("numberOfCards = " + numberOfCards);
		}

		// Set up all game properties
		this.numberOfPlayers = numberOfPlayers;
		this.playerScores = new int[numberOfPlayers];
		this.numberOfCards = numberOfCards;
		this.numberOfPairs = numberOfCards / 2;
		this.matched = new boolean[numberOfCards];
		this.cards = new int[numberOfCards];

		// Reset the game state
		resetGame();
	}

	/** Returns the current player number. */
	public final int getCurrentPlayer() {
		return currentPlayer;
	}

	/** Moves control to the next player. */
	private final void nextPlayer() {
		if (numberOfPlayers != 1) {
			currentPlayer = (currentPlayer + 1) % numberOfPlayers;
		}
	}

	/** Card picture id. */
	public final int getCard(int cardNumber) {
		if (cardNumber < 0 || cardNumber >= numberOfCards) {
			throw new IllegalArgumentException("Card number invalid: " + cardNumber);
		}
		return cards[cardNumber];
	}

	/** Whether card is a valid choice to turn over at this time. */
	public final boolean isCardPickable(final int cardNumber) {
		// Check arguments
		if (cardNumber < 0 || cardNumber >= numberOfCards) {
			throw new IllegalArgumentException("Card number invalid: " + cardNumber);
		}

		// If already matched (and removed) cannot pick again
		if (matched[cardNumber]) {
			return false;
		}

		// If waiting for second pick this card was picked first, cannot pick again
		if (gameState == GameState.PendingSecondPick && cardNumber == firstPick) {
			return false;
		}

		// Otherwise, pick away!
		return true;
	}

	/** Turn a card over. */
	public final boolean turnCard(final int cardNumber) {
		// Check arguments
		if (cardNumber < 0 || cardNumber >= numberOfCards) {
			throw new IllegalArgumentException("Card number invalid: " + cardNumber);
		}

		switch (gameState) {
		case PendingFirstPick:
			// Can't turn card if it's not pickable
			if (!isCardPickable(cardNumber)) {
				return false;
			}

			firstPick = cardNumber;
			gameState = GameState.PendingSecondPick;
			return true;
		case PendingSecondPick:
			// Can't turn card if it's not pickable
			if (!isCardPickable(cardNumber)) {
				return false;
			}

			secondPick = cardNumber;
			gameState = GameState.CardsChosen;
			return true;
		case CardsChosen:
		case GameOver:
		default:
			throw new IllegalStateException("Game in invalid state: " + gameState);
		}
	}

	/** Accept the chosen cards. */
	public final void acceptPicks() {
		switch (gameState) {
		case CardsChosen:
			// If it was a match, mark it
			if (isMatch()) {
				matched[firstPick] = true;
				matched[secondPick] = true;
				playerScores[currentPlayer]++;
				pairsFound++;
			}
			// Clear picked cards
			firstPick = null;
			secondPick = null;

			// Update state
			if (pairsFound >= numberOfPairs) {
				// Game must be over!
				gameState = GameState.GameOver;
			} else {
				// Next player's turn
				nextPlayer();
				gameState = GameState.PendingFirstPick;
			}
			break;
		case PendingFirstPick:
		case PendingSecondPick:
		case GameOver:
		default:
			throw new IllegalStateException("Game in invalid state: " + gameState);
		}
	}

	/** The number of players in the game. */
	public final int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/** The number of cards on the board. */
	public final int getNumberOfCards() {
		return numberOfCards;
	}

	/** The total number of pairs available. */
	public final int getNumberOfPairs() {
		return numberOfPairs;
	}

	/** Player score. */
	public final int getPlayerScore(final int playerNumber) {
		if (playerNumber < 0 || playerNumber >= numberOfPlayers) {
			throw new IllegalArgumentException("Invalid player number: " + playerNumber);
		}
		return playerScores[playerNumber];
	}

	/** Whether current game is over. */
	public final boolean isGameOver() {
		return gameState == GameState.GameOver;
	}

	/** Whether chosen two cards were a match. */
	public final boolean isMatch() {
		switch (gameState) {
		case CardsChosen:
			return cards[firstPick] == cards[secondPick];
		case GameOver:
		case PendingFirstPick:
		case PendingSecondPick:
		default:
			throw new IllegalStateException("Game in invalid state: " + gameState);
		}
	}

	/** Reset game state in preparation for next game. */
	public final void resetGame() {
		// First player always starts
		currentPlayer = 0;
		for (int i = 0; i < numberOfPlayers; i++) {
			// Scores reset to zero
			playerScores[i] = 0;
		}
		for (int i = 0; i < numberOfCards; i++) {
			// All cards revealed again
			matched[i] = false;
		}

		// Generate new card layout
		List<Integer> newCards = new ArrayList<Integer>(numberOfCards);
		for (int i = 0; i < numberOfPairs; i++) {
			newCards.add(i);
			newCards.add(i);
		}
		Collections.shuffle(newCards);
		for (int i = 0; i < numberOfCards; i++) {
			cards[i] = newCards.get(i);
		}

		// Game state
		gameState = GameState.PendingFirstPick;

		// No cards are picked
		firstPick = null;
		secondPick = null;

		// No pairs have been found
		pairsFound = 0;
	}

	/** The current state of the game. */
	public GameState getGameState() {
		return gameState;
	}
}
