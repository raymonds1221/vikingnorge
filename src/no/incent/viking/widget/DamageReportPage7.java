package no.incent.viking.widget;

import no.incent.viking.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.EditText;

public class DamageReportPage7 extends Fragment {
	private EditText firstname;
	private EditText lastname;
	private EditText address;
	private EditText zipcity;
	private EditText phone_number;
	private EditText email;
	private EditText insurance_company;
	private EditText driverlicenseno;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.damage_report_page7, container, false);
		
		firstname = (EditText) view.findViewById(R.id.firstname);
		lastname = (EditText) view.findViewById(R.id.lastname);
		address = (EditText) view.findViewById(R.id.address);
		zipcity = (EditText) view.findViewById(R.id.zipcity);
		phone_number = (EditText) view.findViewById(R.id.phone_number);
		email = (EditText) view.findViewById(R.id.email);
		insurance_company = (EditText) view.findViewById(R.id.insurance_company);
		driverlicenseno = (EditText) view.findViewById(R.id.driverlicenseno);
		
		return view;
	}
	
	public String getFirstname() {
		return firstname.getText().toString();
	}
	
	public String getLastname() {
		return lastname.getText().toString();
	}
	
	public String getAddress() {
		return address.getText().toString();
	}
	
	public String getZipCity() {
		return zipcity.getText().toString();
	}
	
	public String getPhoneNumber() {
		return phone_number.getText().toString();
	}
	
	public String getEmail() {
		return email.getText().toString();
	}
	
	public String getInsuranceCompany() {
		return insurance_company.getText().toString();
	}
	
	public String getDriverLicenseNo() {
		return driverlicenseno.getText().toString();
	}
}
