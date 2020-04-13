package no.incent.viking.widget;

import no.incent.viking.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DamageReportPage4 extends Fragment {
	private RadioGroup personal_damage;
	private EditText persanal_damage_name;
	private EditText persanal_damage_witnesses;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.damage_report_page4, container, false);
		
		personal_damage = (RadioGroup) view.findViewById(R.id.personal_damage);
		persanal_damage_name = (EditText) view.findViewById(R.id.persanal_damage_name);
		persanal_damage_witnesses = (EditText) view.findViewById(R.id.persanal_damage_witnesses);
		
		return view;
	}
	
	public boolean hasPersonalDamage() {
		return personal_damage.getCheckedRadioButtonId() == R.id.personal_damage_yes;
	}
	
	public String getPersonalDamageName() {
		return persanal_damage_name.getText().toString();
	}
	
	public String getPersonalDamageWitnesses() {
		return persanal_damage_witnesses.getText().toString();
	}
}
