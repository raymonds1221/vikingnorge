package no.incent.viking.widget;

import no.incent.viking.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.ImageView;

public class DamageReportPage8 extends Fragment {
	private EditText counterpart_firstname;
	private EditText counterpart_lastname;
	private EditText counterpart_address;
	private EditText counterpart_zipcity;
	private EditText counterpart_phone_number;
	private EditText counterpart_email;
	private EditText counterpart_insurance_company;
	private EditText counterpart_driverlicenseno;
	private ImageView save_damage_report;
	private OnSaveDamageReport onSaveDamageReport;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.damage_report_page8, container, false);
		
		counterpart_firstname = (EditText) view.findViewById(R.id.counterpart_firstname);
		counterpart_lastname = (EditText) view.findViewById(R.id.counterpart_lastname);
		counterpart_address = (EditText) view.findViewById(R.id.counterpart_address);
		counterpart_zipcity = (EditText) view.findViewById(R.id.counterpart_zipcity);
		counterpart_phone_number = (EditText) view.findViewById(R.id.counterpart_phone_number);
		counterpart_email = (EditText) view.findViewById(R.id.counterpart_email);
		counterpart_insurance_company = (EditText) view.findViewById(R.id.counterpart_insurance_company);
		counterpart_driverlicenseno = (EditText) view.findViewById(R.id.counterpart_driverlicenseno);
		save_damage_report = (ImageView) view.findViewById(R.id.save_damage_report);
		
		save_damage_report.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSaveDamageReport.onSaveDamageReport();
			}
		});
		
		return view;
	}
	
	public String getCounterpartFirstname() {
		return counterpart_firstname.getText().toString();
	}
	
	public String getCounterpartLastname() {
		return counterpart_lastname.getText().toString();
	}
	
	public String getCounterpartAddress() {
		return counterpart_address.getText().toString();
	}
	
	public String getCounterpartZipCity() {
		return counterpart_zipcity.getText().toString();
	}
	
	public String getCounterpartPhoneNumber() {
		return counterpart_phone_number.getText().toString();
	}
	
	public String getCounterpartEmail() {
		return counterpart_email.getText().toString();
	}
	
	public String getCounterpartInsuranceCompany() {
		return counterpart_insurance_company.getText().toString();
	}
	
	public String getCounterpartDriverLicenseNo() {
		return counterpart_driverlicenseno.getText().toString();
	}
	
	public void setOnSaveDamageReport(OnSaveDamageReport onSaveDamageReport) {
		this.onSaveDamageReport = onSaveDamageReport;
	}
	
	public static interface OnSaveDamageReport {
		public void onSaveDamageReport();
	}
}
