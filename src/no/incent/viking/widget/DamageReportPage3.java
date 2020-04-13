package no.incent.viking.widget;

import no.incent.viking.R;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;

public class DamageReportPage3 extends Fragment {
	private EditText damage_date;
	private EditText damage_time;
	private EditText place_of_damage;
	private RadioGroup about_damage_blame;
	private EditText speed_of_vehicle;
	private RadioGroup police;
	private EditText rescue_company;
	private EditText description_of_incident;
	private Calendar calendar = Calendar.getInstance();
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;
	private final DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yy");
	private final DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final DateFormat timeFormat = new SimpleDateFormat("HH:mm");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIntanceState) {
		View view = inflater.inflate(R.layout.damage_report_page3, container, false);
		damage_date = (EditText) view.findViewById(R.id.damage_date);
		damage_time = (EditText) view.findViewById(R.id.damage_time);
		place_of_damage = (EditText) view.findViewById(R.id.place_of_damage);
		about_damage_blame = (RadioGroup) view.findViewById(R.id.about_damage_blame);
		speed_of_vehicle = (EditText) view.findViewById(R.id.speed_of_vehicle);
		police = (RadioGroup) view.findViewById(R.id.police);
		rescue_company = (EditText) view.findViewById(R.id.rescue_company);
		description_of_incident = (EditText) view.findViewById(R.id.description_of_incident);
		
		datePickerDialog = new DatePickerDialog(getActivity(),
										dateSetListener,
										calendar.get(Calendar.YEAR),
										calendar.get(Calendar.MONTH),
										calendar.get(Calendar.DAY_OF_MONTH));
		timePickerDialog = new TimePickerDialog(getActivity(),
										timeSetListener,
										calendar.get(Calendar.HOUR_OF_DAY),
										calendar.get(Calendar.MINUTE),
										true);
		
		damage_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					datePickerDialog.show();
			}
		});
		
		damage_time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					timePickerDialog.show();
			}
		});
		
		return view;
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			damage_date.setText(displayDateFormat.format(calendar.getTime()));
		}
	};
	
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			damage_time.setText(timeFormat.format(calendar.getTime()));
		}
	};
	
	public String getDamageDate() {
		return outputDateFormat.format(calendar.getTime());
	}
	
	public String getDamageTime() {
		return timeFormat.format(calendar.getTime());
	}
	
	public String getPlaceOfDamage() {
		return place_of_damage.getText().toString();
	}
	
	public String getAboutDamageBlame() {
		return (about_damage_blame.getCheckedRadioButtonId() == R.id.blame_a)?"A":"B";
	}
	
	public String getSpeedOfVehicle() {
		return speed_of_vehicle.getText().toString();
	}
	
	public boolean hasPolice() {
		return police.getCheckedRadioButtonId() == R.id.police_ja;
	}
	
	public String getRescueCompany() {
		return rescue_company.getText().toString();
	}
	
	public String getDescriptionOfIncident() {
		return description_of_incident.getText().toString();
	}
}
