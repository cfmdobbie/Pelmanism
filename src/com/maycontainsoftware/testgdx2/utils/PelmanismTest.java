package com.maycontainsoftware.testgdx2;

public class PelmanismTest {

	public static void main(String[] args) {
		
		Pelmanism model = new Pelmanism(1, 2);
		
		System.out.println(model.getCard(0));
		System.out.println(model.getCard(1));
		//System.out.println(model.getCard(2));
		//System.out.println(model.getCard(3));
		
		model.turnCard(0);
		model.turnCard(1);
		System.out.println(model.isMatch());
		model.acceptPicks();
		System.out.println(model.isGameOver());
	}
}
