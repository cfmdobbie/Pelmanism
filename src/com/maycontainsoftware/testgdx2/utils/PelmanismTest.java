package com.maycontainsoftware.testgdx2.utils;

import java.util.Random;

import com.maycontainsoftware.testgdx2.Card;
import com.maycontainsoftware.testgdx2.Pelmanism;
import com.maycontainsoftware.testgdx2.Turn;

/**
 * Test application for the Pelmanism game model.
 * 
 * @author Charlie
 */
public class PelmanismTest {

	public static void main(final String[] args) {

		// Let's play Pelmanism!
		// Test game

		final Pelmanism p = new Pelmanism(2, 8);
		final Random r = new Random();
		while (!p.isGameOver()) {
			// Random card 1
			Card card1 = p.getCard(r.nextInt(p.getNumberOfCards()));
			while (card1.isMatched()) {
				card1 = p.getCard(r.nextInt(p.getNumberOfCards()));
			}
			// Random card 2
			Card card2 = p.getCard(r.nextInt(p.getNumberOfCards()));
			while (card2.isMatched() || card1 == card2) {
				card2 = p.getCard(r.nextInt(p.getNumberOfCards()));
			}

			final Turn turn = p.turn(card1, card2);

			System.out.print("Player #" + turn.getPlayerId());
			System.out.print(" picked cards #" + turn.getFirstPick().getId());
			System.out.print(" and #" + turn.getSecondPick().getId());
			if (turn.isMatch()) {
				System.out.print(" and got a match");
			}
			if (turn.isGameOver()) {
				System.out.print(" - game is over!");
			}
			System.out.println();
		}
		for (int i = 0; i < p.getNumberOfPlayers(); i++) {
			System.out.println("Player #" + i + " got " + p.getPlayerScore(i) + " points");
		}
	}
}
