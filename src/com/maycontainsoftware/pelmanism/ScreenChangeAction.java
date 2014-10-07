package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Action;

class ScreenChangeAction extends Action {
	MyGame game;
	Screen from;
	Screen to;
	public ScreenChangeAction(MyGame game, Screen from, Screen to) {
		this.game = game;
		this.from = from;
		this.to = to;
	}
	@Override
	public boolean act(float delta) {
		game.setScreen(to);
		from.dispose();
		return true;
	}
}