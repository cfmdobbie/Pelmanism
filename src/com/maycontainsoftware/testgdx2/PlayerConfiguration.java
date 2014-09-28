package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.graphics.Color;

/**
 * Enumeration of player configurations defined by the app.
 * 
 * @author Charlie
 */
public enum PlayerConfiguration {
	// One player, solitaire play
	One(1, null, null),
	// Two player, hot seat
	Two(2, "Player Two", Color.BLUE),
	// One human player versus computer-controlled player
	One_Vs_Cpu(2, "Computer", Color.GRAY);

	/** The number of players in this mode. */
	final int numberOfPlayers;

	/** The name of the second player, if appropriate. */
	final String secondPlayerName;

	/** The color used to represent the second player, if appropriate. */
	final Color secondPlayerColor;

	/**
	 * Construct a new player configuration.
	 * 
	 * @param numberOfPlayers
	 * @param secondPlayerName
	 * @param secondPlayerColor
	 */
	private PlayerConfiguration(final int numberOfPlayers, final String secondPlayerName,
			final Color secondPlayerColor) {
		this.numberOfPlayers = numberOfPlayers;
		this.secondPlayerName = secondPlayerName;
		this.secondPlayerColor = secondPlayerColor;
	}

	/** Whether a second player (human or computer) exists. */
	final boolean secondPlayerExists() {
		return numberOfPlayers != 1;
	}
}