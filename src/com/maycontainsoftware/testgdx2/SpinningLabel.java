package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/** A Label that spins 180 degrees when touched. */
public class SpinningLabel extends Table {
	private static final String TAG = SpinningLabel.class.getSimpleName();
	
	public SpinningLabel(MyGame game, String text, String font, Color color) {
		this.setTransform(true);
		final Label label = new Label(text, game.skin, font, color);
		label.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "SpinningLabel touchDown");
				if (SpinningLabel.this.getActions().size == 0) {
					final RotateToAction rotateAction = new RotateToAction();
					SpinningLabel.this.setZIndex(999);
					rotateAction.setRotation(SpinningLabel.this.getRotation() + 180.0f);
					rotateAction.setDuration(0.25f);
					SpinningLabel.this.setOrigin(SpinningLabel.this.getWidth() / 2, SpinningLabel.this.getHeight() / 2);
					SpinningLabel.this.addAction(rotateAction);
				}
				return true;
			}
		});
		this.add(label);
	}
}