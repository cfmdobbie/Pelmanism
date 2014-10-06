package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Action;

public class SetInputProcessorAction extends Action {
	
	private final InputProcessor processor;
	
	public SetInputProcessorAction(InputProcessor processor) {
		this.processor = processor;
	}
	
	@Override
	public boolean act(float delta) {
		Gdx.input.setInputProcessor(processor);
		return true;
	}
}
