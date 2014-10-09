package com.maycontainsoftware.pelmanism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * A Table that spins 180 degrees when touched.
 * 
 * @author Charlie
 */
public class SpinningTable extends Table {

	/** Tag for logging purposes. */
	private static final String TAG = SpinningTable.class.getSimpleName();

	/** Constructor. */
	public SpinningTable() {
		// Need to allow transformations on the surrounding table
		this.setTransform(true);

		// Add the on-click handler to the table
		addListener(new InputListener() {
			@Override
			public boolean touchDown(final InputEvent event, final float x, final float y, final int pointer,
					final int button) {

				Gdx.app.log(TAG, "SpinningTable touchDown");

				// If not already processing an action
				if (SpinningTable.this.getActions().size == 0) {

					// Rotate to current angle + 180 degrees, in 1/4 second
					final RotateToAction rotateAction = new RotateToAction();
					rotateAction.setRotation(SpinningTable.this.getRotation() + 180.0f);
					rotateAction.setDuration(0.25f);

					// Display on top of everything else
					SpinningTable.this.setZIndex(999);

					// Rotate around the middle of the table
					SpinningTable.this.setOrigin(SpinningTable.this.getWidth() / 2, SpinningTable.this.getHeight() / 2);

					SpinningTable.this.addAction(rotateAction);
				}
				return true;
			}
		});
	}
}
