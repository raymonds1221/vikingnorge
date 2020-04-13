package no.incent.viking.adapter;

import java.util.List;
import java.util.ArrayList;

import no.incent.viking.widget.DamageReportIntro;
import no.incent.viking.widget.DamageReportPage1;
import no.incent.viking.widget.DamageReportPage2;
import no.incent.viking.widget.DamageReportPage3;
import no.incent.viking.widget.DamageReportPage4;
import no.incent.viking.widget.DamageReportPage5;
import no.incent.viking.widget.DamageReportPage6;
import no.incent.viking.widget.DamageReportPage7;
import no.incent.viking.widget.DamageReportPage8;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class DamageReportPagerAdapter extends FragmentPagerAdapter {
	public List<Fragment> fragments = new ArrayList<Fragment>() {
		private static final long serialVersionUID = -6844185678137719397L;
		{
			add(new DamageReportIntro());
			add(new DamageReportPage1());
			add(new DamageReportPage2());
			add(new DamageReportPage3());
			add(new DamageReportPage4());
			add(new DamageReportPage5());
			add(new DamageReportPage6());
			add(new DamageReportPage7());
			add(new DamageReportPage8());
		}
	};
	
	public DamageReportPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
