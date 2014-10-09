package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Action that sets the InputProcessor for the application.
 * 
 * @author Charlie
 */
public class SetInputProcessorAction extends Action {

	/** The InputProcessor to target input to. */
	private final InputProcessor processor;

	/**
	 * Constructor.
	 * 
	 * @param processor
	 *            The target InputProcessor. This may be null, in which case input events will no longer be processed by
	 *            the application.
	 */
	public SetInputProcessorAction(final InputProcessor processor) {
		this.processor = processor;
	}

	@Override
	public boolean act(final float delta) {
		Gdx.input.setInputProcessor(processor);
		return true;
	}
}
