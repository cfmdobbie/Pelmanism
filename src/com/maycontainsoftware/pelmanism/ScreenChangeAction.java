package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Action that switches between two Screens.
 * 
 * @author Charlie
 */
class ScreenChangeAction extends Action {

	/** Reference to the Game object, used to tell the game which screen to show. */
	private final Game game;

	/** Reference to the current Screen object, used to dispose the old screen. */
	private final Screen from;

	/** Reference to the new Screen object, passed to the Game's setScreen method. */
	private final Screen to;

	/**
	 * Constructor
	 * 
	 * @param game
	 *            The Game object
	 * @param from
	 *            The current Screen
	 * @param to
	 *            The new Screen
	 */
	public ScreenChangeAction(final Game game, final Screen from, final Screen to) {
		this.game = game;
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean act(final float delta) {
		game.setScreen(to);
		from.dispose();
		return true;
	}
}