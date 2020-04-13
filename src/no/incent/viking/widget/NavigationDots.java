package no.incent.viking.widget;

import no.incent.viking.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class NavigationDots extends LinearLayout {
	private Context context;
	private Drawable imageDotInActive;
	private Drawable imageDotActive;
	private int count;
	private int position;
	
	public NavigationDots(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationDots);
		imageDotInActive = typedArray.getDrawable(R.styleable.NavigationDots_imageDotInActive);
		imageDotActive = typedArray.getDrawable(R.styleable.NavigationDots_imageDotActive);
	}
	
	public void setDotsCount(int count) {
		removeAllViews();
		this.count = count;
		for(int i=0;i<count;i++) {
			ImageView imageView = new ImageView(context);
			imageView.setImageDrawable(imageDotInActive);
			imageView.setPadding(5, 0, 5, 0);
			addView(imageView);
		}
		setSelectedDot(0);
	}
	
	public void setSelectedDot(int position) {
		if(count > 0 && getChildCount() > 0) {
			ImageView prevImage = (ImageView) getChildAt(this.position);
			
			if(prevImage != null) {
				prevImage.setImageDrawable(imageDotInActive);
				
				if(position > count)
					throw new IndexOutOfBoundsException("Cannot locate position");
				
				ImageView image = (ImageView) getChildAt(position);
				image.setImageDrawable(imageDotActive);
				this.position = position;
				this.invalidate();
			}
		}
	}
}
