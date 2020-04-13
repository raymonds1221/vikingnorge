package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.User;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.db.DBUserAdapter;

import java.util.List;
import java.util.ArrayList;

import android.os.Handler;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Profile extends LinearLayout {
	private Context context;
	private final Handler handler = new Handler();
	private final HttpRequest request;
	private final DBUserAdapter userAdapter;
	private EditText profile_firstname;
	private EditText profile_lastname;
	private EditText profile_email;
	private EditText profile_address;
	private EditText profile_areacode;
	private EditText profile_area;
	private EditText profile_mobile_number;
	private EditText profile_reg_no;
	private EditText profile_country;
	private RadioGroup profile_gender;
	private EditText profile_year_of_birth;
	private User user;
	
	public Profile(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.profile, this);
		
		this.context = context;
		request = HttpRequest.getInstance();
		
		user = ((VikingApplication) context.getApplicationContext()).getUser();
		userAdapter = new DBUserAdapter(context);
		
		initViews();
		initEvents();
	}
	
	private void initViews() {
		profile_firstname = (EditText) findViewById(R.id.profile_firstname);
		profile_lastname = (EditText) findViewById(R.id.profile_lastname);
		profile_email = (EditText) findViewById(R.id.profile_email);
		profile_address = (EditText) findViewById(R.id.profile_address);
		profile_areacode = (EditText) findViewById(R.id.profile_areacode);
		profile_area = (EditText) findViewById(R.id.profile_area);
		profile_mobile_number = (EditText) findViewById(R.id.profile_mobile_number);
		profile_reg_no = (EditText) findViewById(R.id.profile_reg_no);
		profile_country = (EditText) findViewById(R.id.profile_country);
		profile_gender = (RadioGroup) findViewById(R.id.profile_gender);
		profile_year_of_birth = (EditText) findViewById(R.id.profile_year_of_birth);
		
		if(user != null) {
			profile_firstname.setHint(user.getFirstname());
			profile_lastname.setHint(user.getLastname());
			profile_email.setHint(user.getEmail());
			profile_address.setHint(user.getAddress());
			profile_areacode.setHint(user.getAreaCode());
			profile_area.setHint(user.getArea());
			profile_mobile_number.setHint(user.getTelephone());
			profile_reg_no.setHint(user.getCarRegNo());
			profile_country.setHint(user.getCountry());
			if(user.getGender() != null && user.getGender().equals("f")) {
				((RadioButton) findViewById(R.id.profile_gender_female)).setChecked(true);
			} else {
				((RadioButton) findViewById(R.id.profile_gender_male)).setChecked(true);
			}
			profile_year_of_birth.setHint(user.getYearOfBirth());
		}
	}
	
	public void initUser(User user) {
		this.user = user;
		profile_firstname.setHint(user.getFirstname());
		profile_lastname.setHint(user.getLastname());
		profile_email.setHint(user.getEmail());
		profile_address.setHint(user.getAddress());
		profile_areacode.setHint(user.getAreaCode());
		profile_area.setHint(user.getArea());
		profile_mobile_number.setHint(user.getTelephone());
		profile_reg_no.setHint(user.getCarRegNo());
		profile_country.setHint(user.getCountry());
		if(user.getGender() != null && user.getGender().equals("f")) {
			((RadioButton) findViewById(R.id.profile_gender_female)).setChecked(true);
		} else {
			((RadioButton) findViewById(R.id.profile_gender_male)).setChecked(true);
		}
		profile_year_of_birth.setHint(user.getYearOfBirth());
	}
	
	private void initEvents() {
		findViewById(R.id.save_profile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(user != null) {
					if(Helpers.isNetworkAvailable(context)) {
						final String firstname = (profile_firstname.getText().toString().equals(""))?user.getFirstname():profile_firstname.getText().toString();
						final String lastname = (profile_lastname.getText().toString().equals(""))?user.getLastname():profile_lastname.getText().toString();
						final String email = (profile_email.getText().toString().equals(""))?user.getEmail():profile_email.getText().toString();
						final String address = (profile_address.getText().toString().equals(""))?user.getAddress():profile_address.getText().toString();
						final String areaCode = (profile_areacode.getText().toString().equals(""))?user.getAreaCode():profile_areacode.getText().toString();
						final String area = (profile_area.getText().toString().equals(""))?user.getArea():profile_area.getText().toString();
						final String telephone = (profile_mobile_number.getText().toString().equals(""))?user.getTelephone():profile_mobile_number.getText().toString();
						final String carRegNo = (profile_reg_no.getText().toString().equals(""))?user.getCarRegNo():profile_reg_no.getText().toString();
						final String country = (profile_country.getText().toString().equals(""))?user.getCountry():profile_country.getText().toString();
						final String yearOfBirth = (profile_year_of_birth.getText().toString().equals(""))?user.getYearOfBirth():profile_year_of_birth.getText().toString();
						
						if(firstname != null && lastname != null && email != null && address != null && 
								areaCode != null && area != null && telephone != null && carRegNo != null && country != null && yearOfBirth != null) {
							new Thread(new Runnable() {
								public void run() {
									synchronized(Helpers.lockObject) {
										final User tempUser = new User();
										tempUser.setUid(user.getUid());
										tempUser.setFirstname(firstname);
										tempUser.setLastname(lastname);
										tempUser.setEmail(email);
										tempUser.setAddress(address);
										tempUser.setAreaCode(areaCode);
										tempUser.setArea(area);
										tempUser.setTelephone(telephone);
										tempUser.setCarRegNo(carRegNo);
										tempUser.setCountry(country);
										switch(profile_gender.getCheckedRadioButtonId()) {
											case R.id.profile_gender_male:
												tempUser.setGender("m");
												break;
											case R.id.profile_gender_female:
												tempUser.setGender("f");
												break;
										}
										tempUser.setYearOfBirth(yearOfBirth);
										
										if(updateUser(tempUser)) {
											userAdapter.open();
											userAdapter.updateUser(tempUser);
											userAdapter.close();
											((VikingApplication) context.getApplicationContext()).setUser(tempUser);
											handler.post(new Runnable() {
												public void run() {
													Helpers.showMessage(context, "", "Eier informajon har blitt oppdatert");
													((OwnerInfo.OnOwnerInfoCallback) context).onUpdateOwner(tempUser);
												}
											});
										}
									}
								}
							}).start();
						} else {
							Helpers.showMessage(context, "", "Alle felter må fylles ut");
						}
					} else {
						Helpers.showMessage(context, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller pr�ve etter en tid.");
					}
				}
			}
		});
	}
	
	public boolean updateUser(User user) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(user.getUid())));
		queries.add(new BasicNameValuePair("firstname", user.getFirstname()));
		queries.add(new BasicNameValuePair("lastname", user.getLastname()));
		queries.add(new BasicNameValuePair("email", user.getEmail()));
		queries.add(new BasicNameValuePair("address", user.getAddress()));
		queries.add(new BasicNameValuePair("areacode", user.getAreaCode()));
		queries.add(new BasicNameValuePair("telephone", user.getTelephone()));
		queries.add(new BasicNameValuePair("car_reg_no", user.getCarRegNo()));
		queries.add(new BasicNameValuePair("country", user.getCountry()));
		queries.add(new BasicNameValuePair("gender", user.getGender()));
		queries.add(new BasicNameValuePair("year_of_birth", user.getYearOfBirth()));
		
		String result = request.send(context.getString(R.string.api_url) + "edit_owner", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				userAdapter.updateUser(user);
				return true;
			}
		}
		
		return false;
	}
}
