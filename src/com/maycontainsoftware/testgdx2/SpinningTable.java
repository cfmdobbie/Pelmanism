package com.maycontainsoftware.testgdx2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/** A Table that spins 180 degrees when touched. */
public class SpinningTable extends Table {
	private static final String TAG = SpinningTable.class.getSimpleName();
	
	public SpinningTable() {
		this.setTransform(true);
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "SpinningTable touchDown");
				if (SpinningTable.this.getActions().size == 0) {
					final RotateToAction rotateAction = new RotateToAction();
					rotateAction.setRotation(SpinningTable.this.getRotation() + 180.0f);
					rotateAction.setDuration(0.125f);
					SpinningTable.this.setOrigin(SpinningTable.this.getWidth() / 2, SpinningTable.this.getHeight() / 2);
					SpinningTable.this.addAction(rotateAction);
				}
				return true;
			}
		});
	}
}