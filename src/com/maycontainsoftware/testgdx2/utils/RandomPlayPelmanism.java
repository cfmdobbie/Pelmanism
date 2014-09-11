package com.maycontainsoftware.testgdx2.utils;

import java.util.Random;

import com.maycontainsoftware.testgdx2.Pelmanism;
import com.maycontainsoftware.testgdx2.Pelmanism.GameState;

public class RandomPlayPelmanism {

	public static void main(String[] args) {

		Pelmanism model = new Pelmanism(1, 64);
		Random r = new Random();

		while (model.getGameState() != GameState.GameOver) {

			// Pick first card
			int card1 = r.nextInt(model.getNumberOfCards());
			while (!model.isCardPickable(card1)) {
				card1 = r.nextInt(model.getNumberOfCards());
			}
			model.turnCard(card1);

			// Pick second card
			int card2 = r.nextInt(model.getNumberOfCards());
			while (!model.isCardPickable(card2)) {
				card2 = r.nextInt(model.getNumberOfCards());
			}
			model.turnCard(card2);

			System.out.print("Turned cards " + card1 + " and " + card2);
			System.out.print(", " + (model.isMatch() ? "MATCH!" : "no match"));
			model.acceptPicks();
			System.out.print(", score = " + model.getPlayerScore(0));
			System.out.println();
		}
	}
}
