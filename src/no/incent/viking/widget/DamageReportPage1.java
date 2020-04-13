package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.EditText;

public class DamageReportPage1 extends Fragment {
	private EditText registration_number;
	private EditText counterpart_registration_number;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanState) {
		View view = inflater.inflate(R.layout.damage_report_page1, container, false);
		registration_number = (EditText) view.findViewById(R.id.registration_number);
		counterpart_registration_number = (EditText) view.findViewById(R.id.counterpart_registration_number);
		
		VikingApplication vikingApplication =  (VikingApplication) getActivity().getApplicationContext();
		
		if(vikingApplication.getCars() != null && vikingApplication.getCars().size() > 0) {
			registration_number.setText(vikingApplication.getCars().get(0).getRegistrationNumber());
		}
		
		return view;
	}
	
	public String getRegistrationNumberA() {
		return registration_number.getText().toString();
	}
	
	public String getRegistrationNumberB() {
		return counterpart_registration_number.getText().toString();
	}
}
