package no.incent.viking.widget;

import no.incent.viking.R;

import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.content.Context;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SwipeNavigation extends View {
	private int navigationType;
	private Bitmap navigation;
	private GestureDetector gestureDetector;
	private OnSwipeListener onSwipeListener;
	
	public SwipeNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray swipeNavigation = context.obtainStyledAttributes(attrs, R.styleable.SwipeNavigation);
		navigationType = swipeNavigation.getInt(R.styleable.SwipeNavigation_navigation_type, 1);
		gestureDetector = new GestureDetector(new GestureListener());
		
		switch(navigationType) {
			case 1:
				navigation = BitmapFactory.decodeResource(context.getResources(), R.drawable.nav_icon_right);
				break;
			default:
				navigation = BitmapFactory.decodeResource(context.getResources(), R.drawable.nav_icon);
				break;
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(navigation, 0, 0, null);
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(navigation.getWidth(), navigation.getHeight());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		gestureDetector.onTouchEvent(e);
		return true;
	}
	
	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.onSwipeListener = onSwipeListener;
	}
	
	private class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		private int SWIPE_MIN_DISTANCE = 0;
		
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			float diffAbs = Math.abs(event1.getY() - event2.getY());
			float diff = event1.getX() - event2.getX();
			
			SWIPE_MIN_DISTANCE = (int) (navigation.getWidth() * 0.4f);
			
			if(diffAbs > SWIPE_MAX_OFF_PATH)
				return false;
			
			if(diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onSwipeListener.onSwipeLeft();
			} else if(-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onSwipeListener.onSwipeRight();
			}
			
			return true;
		}
	}
	
	public static interface OnSwipeListener {
		public void onSwipeLeft();
		public void onSwipeRight();
	}
}
