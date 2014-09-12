package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * A Label that spins 180 degrees when touched.
 * 
 * @author Charlie
 */
public class SpinningLabel extends Table {

	/** Tag for logging purposes. */
	private static final String TAG = SpinningLabel.class.getSimpleName();

	public SpinningLabel(MyGame game, String text, String font, Color color) {

		// Need to allow transformations on the surrounding table
		this.setTransform(true);

		// Table contains a single Label
		final Label label = new Label(text, game.skin, font, color);
		// Add the on-click handler to the Label
		label.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				Gdx.app.log(TAG, "SpinningLabel touchDown");

				// If not already processing an action
				if (SpinningLabel.this.getActions().size == 0) {

					// Rotate to current angle + 180 degrees, in 1/4 second
					final RotateToAction rotateAction = new RotateToAction();
					rotateAction.setRotation(SpinningLabel.this.getRotation() + 180.0f);
					rotateAction.setDuration(0.25f);

					// Display on top of everything else
					SpinningLabel.this.setZIndex(999);

					// Rotate around the middle of the table
					SpinningLabel.this.setOrigin(SpinningLabel.this.getWidth() / 2, SpinningLabel.this.getHeight() / 2);

					SpinningLabel.this.addAction(rotateAction);
				}
				return true;
			}
		});
		this.add(label);
	}
}
