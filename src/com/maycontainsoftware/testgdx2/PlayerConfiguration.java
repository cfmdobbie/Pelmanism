package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.graphics.Color;

/**
 * Enumeration of player configurations defined by the app.
 * 
 * @author Charlie
 */
public enum PlayerConfiguration {

	// One player, solitaire play
	One(new String[] { "Player One" }, new Color[] { Color.RED }, new boolean[] { true }),
	// Two player, hot seat
	Two(new String[] { "Player One", "Player Two" }, new Color[] { Color.RED, Color.BLUE },
			new boolean[] { true, true }),
	// One human player versus computer-controlled player
	One_Vs_Cpu(new String[] { "Player One", "Computer" }, new Color[] { Color.RED, Color.GRAY }, new boolean[] { true,
			false });

	// TODO: Define a "Player" class to better encapsulate this data?

	/** The number of players in this mode. */
	private final int numberOfPlayers;

	/** Array of player names. */
	private final String[] playerNames;

	/** Array of player colors. */
	private final Color[] playerColors;

	/** Array of booleans representing whether player is user-controlled. */
	private final boolean[] playerIsUserControlled;

	/**
	 * Construct a new player configuration.
	 * 
	 * @param playerNames
	 * @param playerColors
	 * @param playerIsUserControlled
	 */
	private PlayerConfiguration(final String[] playerNames, final Color[] playerColors,
			final boolean[] playerIsUserControlled) {
		numberOfPlayers = playerNames.length;
		this.playerNames = playerNames;
		this.playerColors = playerColors;
		this.playerIsUserControlled = playerIsUserControlled;
	}

	/**
	 * Returns the number of players.
	 * 
	 * @return The number of players.
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * Get the color of the specified player.
	 * 
	 * @param n
	 *            The player index
	 * @return The color of the specified player.
	 */
	public Color getPlayerColor(final int n) {
		return playerColors[n];
	}

	/**
	 * Get the name of the specified player.
	 * 
	 * @param n
	 *            The player index.
	 * @return The name of the specified player.
	 */
	public String getPlayerName(final int n) {
		return playerNames[n];
	}

	/**
	 * Get whether the player is user-controlled.
	 * 
	 * @param n
	 *            The index of the player.
	 * @return True if the player is user-controlled, false if the player is computer-controlled.
	 */
	public boolean isPlayerUserControlled(final int n) {
		return playerIsUserControlled[n];
	}
}
