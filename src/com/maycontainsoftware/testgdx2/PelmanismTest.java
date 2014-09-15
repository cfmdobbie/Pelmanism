package com.maycontainsoftware.testgdx2;

import java.util.Random;

public class PelmanismTest {

	public static void main(String[] args) {

		// Let's play Pelmanism!
		// Test game

		Pelmanism p = new Pelmanism(2, 8);
		Random r = new Random();
		while (!p.isGameOver()) {
			// Random card 1
			Card card1 = p.getCard(r.nextInt(p.numberOfCards));
			while (card1.isMatched()) {
				card1 = p.getCard(r.nextInt(p.numberOfCards));
			}
			// Random card 2
			Card card2 = p.getCard(r.nextInt(p.numberOfCards));
			while (card2.isMatched() || card1 == card2) {
				card2 = p.getCard(r.nextInt(p.numberOfCards));
			}

			Turn t = new Turn(card1, card2);
			TurnResult tr = p.turn(t);

			System.out.print("Player #" + tr.turn.getPlayerId());
			System.out.print(" picked cards #" + tr.turn.getFirstPick().getId());
			System.out.print(" and #" + tr.turn.getSecondPick().getId());
			if (tr.isMatch()) {
				System.out.print(" and got a match");
			}
			if (tr.isGameOver()) {
				System.out.print(" - game is over!");
			}
			System.out.println();
		}
		for (int i = 0; i < p.numberOfPlayers; i++) {
			System.out.println("Player #" + i + " got " + p.getPlayerScore(i) + " points");
		}
	}
}
