package no.incent.viking.util;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class FlipAnimation extends Animation {
	private final float fromDegrees;
	private final float toDegrees;
	private final float centerX;
	private final float centerY;
	private Camera camera;
	
	public FlipAnimation(float fromDegrees, float toDegrees, float centerX, float centerY) {
		this.fromDegrees = fromDegrees;
		this.toDegrees = toDegrees;
		this.centerX = centerX;
		this.centerY = centerY;
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		camera = new Camera();
	}
	
	@Override
	public void applyTransformation(float interpolatedTime, Transformation trans) {
		float degrees = fromDegrees + ((toDegrees - fromDegrees) * interpolatedTime);
		Matrix matrix = trans.getMatrix();
		camera.save();
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();
		
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
