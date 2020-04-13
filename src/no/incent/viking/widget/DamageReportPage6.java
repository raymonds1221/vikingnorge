package no.incent.viking.widget;

import no.incent.viking.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.RadioGroup;
import android.widget.EditText;

public class DamageReportPage6 extends Fragment {
	private RadioGroup earlier_damage;
	private EditText if_yes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.damage_report_page6, container, false);
		earlier_damage = (RadioGroup) view.findViewById(R.id.earlier_damage);
		if_yes = (EditText) view.findViewById(R.id.if_yes);
		
		return view;
	}
	
	public boolean getEarlierDamage() {
		return earlier_damage.getCheckedRadioButtonId() == R.id.earlier_damage_yes;
	}
	
	public String getDescIfYes() {
		return if_yes.getText().toString();
	}
}
