package no.incent.viking.widget;

import no.incent.viking.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class Access extends LinearLayout {
	public Access(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.car_access, this);
	}
}
