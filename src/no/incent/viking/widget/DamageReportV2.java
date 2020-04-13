package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.adapter.DamageReportPagerAdapter;

import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class DamageReportV2 extends FrameLayout {
	private ViewPager damage_report_pager;
	
	public DamageReportV2(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/*LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.damage_reportv2, this);
		
		final DamageReportPagerAdapter adapter = new DamageReportPagerAdapter(null);
		
		damage_report_pager = (ViewPager) findViewById(R.id.damage_report_pager);
		damage_report_pager.setAdapter(adapter);
		damage_report_pager.setCurrentItem(8);
		
		damage_report_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});*/
	}
	
	public void updateDamageReportPictures(File picture, int activeDamageReportPicture) {
		DamageReportPagerAdapter adapter = (DamageReportPagerAdapter) damage_report_pager.getAdapter();
		
		/*
		if(adapter.getActiveView() instanceof DamageReportPage5 && picture != null) {
			((DamageReportPage5) adapter.getActiveView()).damagePictures.get(activeDamageReportPicture).setFile(picture);
			((DamageReportPage5) adapter.getActiveView()).damagePictures.get(activeDamageReportPicture).setThumbFile(picture);
			((DamageReportPage5) adapter.getActiveView()).updateDamagePictures();
		}*/
	}
}
