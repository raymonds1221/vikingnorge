package no.incent.viking.widget;

import no.incent.viking.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Gallery;

public class PictureView extends LinearLayout  {
	private LayoutAnimationController layoutFadeIn;
	private LayoutAnimationController layoutFadeOut;
	private RelativeLayout picture_view_item_holder;
	private Gallery file_picture_gallery;

	public PictureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.picture_view, this);
		
		layoutFadeIn = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fade_in);
		layoutFadeOut = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fade_out);
		picture_view_item_holder = (RelativeLayout) findViewById(R.id.picture_view_item_holder);
		file_picture_gallery = (Gallery) findViewById(R.id.file_picture_gallery);
		
		file_picture_gallery.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					hideShowPictureMenu();
				}
				return false;
			}
		});
		
		findViewById(R.id.prev_picture_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = file_picture_gallery.getSelectedItemPosition() - 1;
				if(position > -1) {
					file_picture_gallery.setSelection(position, true);
				}
			}
		});
		
		findViewById(R.id.next_picture_view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = file_picture_gallery.getSelectedItemPosition() + 1;
				
				if(position < file_picture_gallery.getAdapter().getCount()) {
					file_picture_gallery.setSelection(position, true);
				}
			}
		});
	}
	
	private void hideShowPictureMenu() {
		if(picture_view_item_holder.getVisibility() == View.VISIBLE) {
			picture_view_item_holder.setLayoutAnimation(layoutFadeOut);
			picture_view_item_holder.startLayoutAnimation();
			layoutFadeOut.getAnimation().setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					picture_view_item_holder.setVisibility(View.GONE);
				}
			});
		} else if(picture_view_item_holder.getVisibility() == View.GONE) {
			picture_view_item_holder.setLayoutAnimation(layoutFadeIn);
			picture_view_item_holder.setVisibility(View.VISIBLE);
			picture_view_item_holder.startLayoutAnimation();
		}
	}
}
