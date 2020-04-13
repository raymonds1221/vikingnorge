package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.CarEvent;
import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Damage;
import no.incent.viking.pojo.DamagePicture;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.db.DBCarEventAdapter;
import no.incent.viking.db.DBCarEventPictureAdapter;
import no.incent.viking.adapter.DamageCauseAdapter;
import no.incent.viking.adapter.DamagePictureAdapter;
import no.incent.viking.widget.MyCarEvents;
import no.incent.viking.widget.SwipeNavigation;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Handler;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.HeaderViewListAdapter;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class DamageReport extends LinearLayout {
	private final Handler handler = new Handler();
	private Context context;
	private HttpRequest request;
	private User user;
	private final Damage damage = new Damage();
	private ViewFlipper damage_report_flipper;
	private TextView page_number;
	private TextView header_name;
	private EditText registration_number;
	private EditText counterpart_registration_number;
	private EditText counterpart_firstname;
	private EditText counterpart_lastname;
	private EditText counterpart_address;
	private EditText counterpart_zipcity;
	private EditText counterpart_telephone;
	private EditText counterpart_email;
	private EditText counterpart_insurance_company;
	private EditText counterpart_driverlicenseno;
	private ListView damage_cause_list;
	private View footer_damagecause;
	private EditText damage_date;
	private EditText damage_time;
	private EditText place_of_damage;
	private RadioGroup about_damage_blame;
	private EditText speed_of_vehicle;
	private RadioGroup police;
	private EditText rescue_company;
	private EditText description_of_incident;
	private RadioGroup personal_damage;
	private EditText persanal_damage_name;
	private EditText persanal_damage_witnesses;
	private GridView damage_pictures;
	private RadioGroup earlier_damage;
	private EditText if_yes;
	private EditText firstname;
	private EditText lastname;
	private EditText address;
	private EditText zipcity;
	private EditText phone_number;
	private EditText email;
	private EditText insurance_company;
	private EditText driverlicenseno;
	private Calendar calendar = Calendar.getInstance();
	
	private final String[] causes = {"Pakert/stod stille i kollisonsøyeblikket",
		"Satte kjøretøyet i bevegelse/åpnet døra",
		"Var i ferd med å stoppe",
		"Kjørte ut fra p-plass/privat område",
		"Kjørte inn på p-plass/privat område",
		"Kjørte inn i rundkjøring",
		"Kjørte på bakfra i samme retning og kjørefelt",
		"kjørte i samme retning i annet kjørefelt",
		"skiftet kjørefelt",
		"kjørte forbi",
		"Svingte til høyre",
		"Svingte til venstre",
		"Rygget",
		"Kom inn på del av veien bestemt for trafikk i motsatt retning",
		"kom fra høyre i et kryss",
		"Fulgte ikke varsel om vikeplikt eller rødt lys"
	};
	private int ownerId;
	private DBCarEventAdapter carEventAdapter;
	private DBCarEventPictureAdapter carEventPictureAdapter;
	
	public final List<DamagePicture> damagePictures = new ArrayList<DamagePicture>() {
		private static final long serialVersionUID = 1L;
		{
			add(new DamagePicture("Bilens front"));
			add(new DamagePicture("Bilens bak"));
			add(new DamagePicture("Skade pa din bil"));
			add(new DamagePicture("Skade pa motpart"));
			add(new DamagePicture("Skade bremselengde"));
			add(new DamagePicture("Gateskilt/Kryss"));
			add(new DamagePicture("Værforhold"));
			add(new DamagePicture("Veidekke"));
			add(new DamagePicture("Lysforhold"));
			add(new DamagePicture("Annet"));
			add(new DamagePicture("Annet2"));
		}
	};
	
	public DamageReport(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.damage_report, this);
		
		this.context = context;
		request = HttpRequest.getInstance();
		carEventAdapter = new DBCarEventAdapter(context);
		carEventPictureAdapter = new DBCarEventPictureAdapter(context);
		
		user = ((VikingApplication) context.getApplicationContext()).getUser();
		
		if(user != null) {
			ownerId = user.getUid();
		}
		
		initViews();
		initEvents();
	}
	
	private void initViews() {
		damage_report_flipper = (ViewFlipper) findViewById(R.id.damage_report_flipper);
		page_number = (TextView) findViewById(R.id.page_number);
		header_name = (TextView) findViewById(R.id.header_name);
		registration_number = (EditText) findViewById(R.id.registration_number);
		counterpart_registration_number = (EditText) findViewById(R.id.counterpart_registration_number);
		counterpart_firstname = (EditText) findViewById(R.id.counterpart_firstname);
		counterpart_lastname = (EditText) findViewById(R.id.counterpart_lastname);
		counterpart_address = (EditText) findViewById(R.id.counterpart_address);
		counterpart_zipcity = (EditText) findViewById(R.id.counterpart_zipcity);
		counterpart_telephone = (EditText) findViewById(R.id.counterpart_phone_number);
		counterpart_email = (EditText) findViewById(R.id.counterpart_email);
		counterpart_insurance_company = (EditText) findViewById(R.id.counterpart_insurance_company);
		counterpart_driverlicenseno = (EditText) findViewById(R.id.counterpart_driverlicenseno);
		damage_cause_list = (ListView) findViewById(R.id.damage_cause_list);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer_damagecause = inflater.inflate(R.layout.footer_damagecause, null);
		damage_cause_list.addFooterView(footer_damagecause);
		damage_date = (EditText) findViewById(R.id.damage_date);
		damage_time = (EditText) findViewById(R.id.damage_time);
		place_of_damage = (EditText) findViewById(R.id.place_of_damage);
		about_damage_blame = (RadioGroup) findViewById(R.id.about_damage_blame);
		speed_of_vehicle = (EditText) findViewById(R.id.speed_of_vehicle);
		police = (RadioGroup) findViewById(R.id.police);
		rescue_company = (EditText) findViewById(R.id.rescue_company);
		description_of_incident = (EditText) findViewById(R.id.description_of_incident);
		personal_damage = (RadioGroup) findViewById(R.id.personal_damage);
		persanal_damage_name = (EditText) findViewById(R.id.persanal_damage_name);
		persanal_damage_witnesses = (EditText) findViewById(R.id.persanal_damage_witnesses);
		damage_pictures = (GridView) findViewById(R.id.damage_pictures);
		earlier_damage = (RadioGroup) findViewById(R.id.earlier_damage);
		if_yes = (EditText) findViewById(R.id.if_yes);
		firstname = (EditText) findViewById(R.id.firstname);
		lastname = (EditText) findViewById(R.id.lastname);
		address = (EditText) findViewById(R.id.address);
		zipcity = (EditText) findViewById(R.id.zipcity);
		phone_number = (EditText) findViewById(R.id.phone_number);
		email = (EditText) findViewById(R.id.email);
		insurance_company = (EditText) findViewById(R .id.insurance_company);
		driverlicenseno = (EditText) findViewById(R.id.driverlicenseno);
		damage_report_flipper.setInAnimation(context, R.anim.fade_in);
		damage_report_flipper.setOutAnimation(context, R.anim.fade_out);
		
		DamageCauseAdapter adapter = new DamageCauseAdapter(context, R.layout.list_damage_report, causes);
		adapter.setDropDownViewResource(R.layout.list_damage_report);
		damage_cause_list.setAdapter(adapter);
		damage_cause_list.setItemsCanFocus(true);
		damage_cause_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		DamagePictureAdapter adapterPicture = new DamagePictureAdapter(context, R.layout.list_damage_picture, damagePictures);
		adapterPicture.setDropDownViewResource(R.layout.list_damage_picture);
		damage_pictures.setAdapter(adapterPicture);
	}
	
	private void initEvents() {
		((SwipeNavigation) findViewById(R.id.informasjon_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				page_number.setText("1/8");
				header_name.setText("REGISTRERINGSNUMMER");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				
			}
		});
		
		((SwipeNavigation) findViewById(R.id.registrationnumber_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				damage.setCarRegNo(registration_number.getText().toString());
				damage.setCounterpartCarRegNo(counterpart_registration_number.getText().toString());
				page_number.setText("2/8");
				header_name.setText("HUK AV DET SOM PASSER");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				
			}
		});
		
		((SwipeNavigation) footer_damagecause.findViewById(R.id.damagecause_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				String damage_cause = "";
				
				DamageCauseAdapter adapter = (DamageCauseAdapter) ((HeaderViewListAdapter) damage_cause_list.getAdapter()).getWrappedAdapter();
				
				for(int i=0;i<adapter.getDamageCauses().length;i++) {
					damage_cause += adapter.getDamageCauses()[i] + ",";
				}
				
				//damage.setDamageCause(damage_cause);
				page_number.setText("3/8");
				header_name.setText("OM SKADEN");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				page_number.setText("1/8");
				header_name.setText("REGISTRERINGSNUMMER");
				damage_report_flipper.showPrevious();
			}
		});
		
		((SwipeNavigation) findViewById(R.id.damage_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				damage.setAboutDamageDate(dateFormat.format(calendar.getTime()));
				damage.setAboutDamageTime(damage_time.getText().toString());
				damage.setPlaceOfDamage(place_of_damage.getText().toString());
				if(about_damage_blame.getCheckedRadioButtonId() == R.id.blame_a) {
					damage.setAboutDamageBlame("A");
				} else if(about_damage_blame.getCheckedRadioButtonId() == R.id.blame_b) {
					damage.setAboutDamageBlame("B");
				}
				damage.setSpeedOfVehicle(speed_of_vehicle.getText().toString());
				damage.setPolice(police.getCheckedRadioButtonId() == R.id.police_ja);
				damage.setRescueCompany(rescue_company.getText().toString());
				damage.setDescriptionOfIncident(description_of_incident.getText().toString());
				page_number.setText("4/8");
				header_name.setText("PERSONSKADE");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				page_number.setText("2/8");
				header_name.setText("HUK AV DET SOM PASSER");
				damage_report_flipper.showPrevious();
			}
		});
		
		damage_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					new DatePickerDialog(context, 
							dateSetListener, 
							calendar.get(Calendar.YEAR), 
							calendar.get(Calendar.MONTH), 
							calendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			}
		});
		
		damage_time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					new TimePickerDialog(context, 
							timeSetListener, 
							calendar.get(Calendar.HOUR), 
							calendar.get(Calendar.MINUTE), 
							true).show();
				}
			}
		});
		
		((SwipeNavigation) findViewById(R.id.personal_damage_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				damage.setPersonalInjuryAnswer(personal_damage.getCheckedRadioButtonId() == R.id.personal_damage_yes);
				damage.setPersonalInjuryName(persanal_damage_name.getText().toString());
				damage.setPersonalInjuryWitnesses(persanal_damage_witnesses.getText().toString());
				page_number.setText("5/8");
				header_name.setText("TA DISSE BILDENE");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				page_number.setText("3/8");
				header_name.setText("OM SKADEN");
				damage_report_flipper.showPrevious();
			}
		});
		
		damage_pictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				((DamageReportCallback) context).onDamagePictureClick(position);
			}
		});
		
		((SwipeNavigation) findViewById(R.id.damage_pictures_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				page_number.setText("6/8");
				header_name.setText("ANNEN MATERIELL SKADE ENN BIL");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				page_number.setText("4/8");
				header_name.setText("PERSONSKADE");
				damage_report_flipper.showPrevious();
			}
		});
		
		((SwipeNavigation) findViewById(R.id.earlier_damage_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			
			@Override
			public void onSwipeRight() {
				damage.setEarlierDamageAnswer(earlier_damage.getCheckedRadioButtonId() == R.id.earlier_damage_yes);
				damage.setDescIfYes(if_yes.getText().toString());
				page_number.setText("7/8");
				header_name.setText("PERSONNOPPLYSNINGER KJØRETØY A");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				page_number.setText("5/8");
				header_name.setText("TA DISSE BILDENE");
				damage_report_flipper.showPrevious();
			}
		});
		
		((SwipeNavigation) findViewById(R.id.personal_info_a_nav)).setOnSwipeListener(new SwipeNavigation.OnSwipeListener() {
			@Override
			public void onSwipeRight() {
				damage.setFirstname(firstname.getText().toString());
				damage.setLastname(lastname.getText().toString());
				damage.setAddress(address.getText().toString());
				damage.setZipcity(zipcity.getText().toString());
				damage.setPhoneNumber(phone_number.getText().toString());
				damage.setEmail(email.getText().toString());
				damage.setInsuranceCompany(insurance_company.getText().toString());
				damage.setDriverLicenseNo(driverlicenseno.getText().toString());
				page_number.setText("8/8");
				header_name.setText("PERSONNOPPLYSNINGER KJØRETØY B");
				damage_report_flipper.showNext();
			}
			
			@Override
			public void onSwipeLeft() {
				page_number.setText("6/8");
				header_name.setText("ANNEN MATERIELL SKADE ENN BIL");
				damage_report_flipper.showPrevious();
			}
		});
		
		findViewById(R.id.save_damage_report).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				damage.setCounterpartFirstname(counterpart_firstname.getText().toString());
				damage.setCounterpartLastname(counterpart_lastname.getText().toString());
				damage.setCounterpartAddress(counterpart_address.getText().toString());
				damage.setCounterpartZipcity(counterpart_zipcity.getText().toString());
				damage.setCounterpartPhoneNumber(counterpart_telephone.getText().toString());
				damage.setCounterpartEmail(counterpart_email.getText().toString());
				damage.setCounterpartInsuranceCompany(counterpart_insurance_company.getText().toString());
				damage.setCounterpartDriverLicenseNo(counterpart_driverlicenseno.getText().toString());
				
				if(Helpers.isNetworkAvailable(context)) {
					findViewById(R.id.damage_report_loader).setVisibility(View.VISIBLE);
					new Thread(new Runnable() {
						public void run() {
							if(saveDamageReport(damage)) {
								DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
								CarEvent carEvent = new CarEvent();
								
								if(user != null) {
									carEvent.setOwnerId(user.getUid());
									carEvent.setName(user.getFirstname() + " " + user.getLastname());
								}
								
								carEvent.setUid((int) System.currentTimeMillis());
								carEvent.setRegistration(damage.getCarRegNo());
								carEvent.setEvent("Skademelding");
								carEvent.setPlace(damage.getPlaceOfDamage());
								carEvent.setDateTime(formatter.format(new Date()));
								carEvent.setDateCreated(formatter.format(new Date()));
								MyCarEvents.addTempCarEvent(carEvent);
								
								if(Helpers.Constants.mIsLoggedIn) {
									CarEventPicture carEventPicture = new CarEventPicture();
									
									if(saveCarEvent(carEvent, carEventPicture)) {
										for(DamagePicture damagePicture: damagePictures) {
											if(damagePicture.getFile() != null) {
												carEventPicture.setOwnerId(user.getUid());
												carEventPicture.setName(damagePicture.getName());
												carEventPicture.setPath(damagePicture.getFile().getAbsolutePath());
												carEventPicture.setImageFile(damagePicture.getFile());
												saveCarEventPicture(carEventPicture);
											}
										}
									}
								} else {
									for(DamagePicture damagePicture: damagePictures) {
										if(damagePicture.getFile() != null) {
											CarEventPicture carEventPicture = new CarEventPicture();
											carEventPicture.setEventId(carEvent.getUid());
											carEventPicture.setName(damagePicture.getName());
											carEventPicture.setPath(damagePicture.getFile().getAbsolutePath());
											carEventPicture.setImageFile(damagePicture.getFile());
											MyCarEvents.addTempCarEventPictures(carEventPicture);
										}
									}
								}
								
								
								handler.post(new Runnable() {
									public void run() {
										findViewById(R.id.damage_report_loader).setVisibility(View.GONE);
										damage_report_flipper.setDisplayedChild(0);
										
										if(Helpers.Constants.mIsLoggedIn) {
											Helpers.showMessage(context, "Skademelding lagret", "Skademeldingen ble lagret i BILioteket under “Hendelser” og sendt til oppgitt e-post adresse.");
										} else {
											Helpers.showMessage(context, "Skademelding lagret", "Skademeldingen ble sendt til oppgitt e-post adresse. Lag Venn av Viking-bruker for å kunne lagre informasjon under \"Hendelser\" i app.");
										}
										
										((DamageReportCallback) context).onDamageSuccess();
									}
								});
							} else {
								handler.post(new Runnable() {
									public void run() {
										findViewById(R.id.damage_report_loader).setVisibility(View.GONE);
										Helpers.showMessage(context, "", "Unable to send damage report.");
									}
								});
							}
						}
					}).start();
				} else {
					Helpers.showMessage(context, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
				}
			}
		});
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			
			damage_date.setText(dateFormat.format(calendar.getTime()));
		}
	};
	
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			DateFormat dateFormat = new SimpleDateFormat("hh:mm");
			calendar.set(Calendar.HOUR, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			
			damage_time.setText(dateFormat.format(calendar.getTime()));
		}
	};
	
	public static interface DamageReportCallback {
		public void onDamagePictureClick(int position);
		public void onDamageSuccess();
	}
	
	public void updateDamageReportPictures() {
		DamagePictureAdapter adapterPicture = new DamagePictureAdapter(context, R.layout.list_damage_picture, damagePictures);
		adapterPicture.setDropDownViewResource(R.layout.list_damage_picture);
		damage_pictures.setAdapter(adapterPicture);
	}
	
	public boolean saveDamageReport(Damage damage) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(ownerId)));
		queries.add(new BasicNameValuePair("car_reg_no", damage.getCarRegNo()));
		queries.add(new BasicNameValuePair("counterpart_car_reg_no", damage.getCounterpartCarRegNo()));
		queries.add(new BasicNameValuePair("firstname", damage.getFirstname()));
		queries.add(new BasicNameValuePair("lastname", damage.getLastname()));
		queries.add(new BasicNameValuePair("address", damage.getAddress()));
		queries.add(new BasicNameValuePair("zipcity", damage.getZipcity()));
		queries.add(new BasicNameValuePair("phone_number", damage.getPhoneNumber()));
		queries.add(new BasicNameValuePair("email", damage.getEmail()));
		queries.add(new BasicNameValuePair("insurance_company", damage.getInsuranceCompany()));
		queries.add(new BasicNameValuePair("driverlicenseno", damage.getDriverLicenseNo()));
		queries.add(new BasicNameValuePair("counterpart_firstname", damage.getCounterpartFirstname()));
		queries.add(new BasicNameValuePair("counterpart_lastname", damage.getCounterpartLastname()));
		queries.add(new BasicNameValuePair("counterpart_address", damage.getCounterpartAddress()));
		queries.add(new BasicNameValuePair("counterpart_zipcity", damage.getCounterpartZipcity()));
		queries.add(new BasicNameValuePair("counterpart_phone_number", damage.getCounterpartPhoneNumber()));
		queries.add(new BasicNameValuePair("counterpart_email", damage.getCounterpartEmail()));
		queries.add(new BasicNameValuePair("counterpart_insurance_company", damage.getCounterpartInsuranceCompany()));
		queries.add(new BasicNameValuePair("counterpart_driverlicenseno", damage.getCounterpartDriverLicenseNo()));
		//queries.add(new BasicNameValuePair("damage_cause", damage.getDamageCause()));
		queries.add(new BasicNameValuePair("damage_other", damage.getDamageOther()));
		queries.add(new BasicNameValuePair("about_damage_date", damage.getAboutDamageDate()));
		queries.add(new BasicNameValuePair("about_damage_time", damage.getAboutDamageTime()));
		queries.add(new BasicNameValuePair("place_of_damage", damage.getPlaceOfDamage()));
		queries.add(new BasicNameValuePair("about_damage_blame", damage.getAboutDamageBlame()));
		queries.add(new BasicNameValuePair("speed_of_vehicle", damage.getSpeedOfVehicle()));
		queries.add(new BasicNameValuePair("police", (damage.getPolice())?"1":"0"));
		queries.add(new BasicNameValuePair("rescue_company", damage.getRescueCompany()));
		queries.add(new BasicNameValuePair("description_of_incident", damage.getDescriptionOfIncident()));
		queries.add(new BasicNameValuePair("personal_injury_answer", (damage.isPersonalInjuryAnswer())?"1":"0"));
		queries.add(new BasicNameValuePair("personal_injury_name", damage.getPersonalInjuryName()));
		queries.add(new BasicNameValuePair("personal_injury_witnesses", damage.getPersonalInjuryWitnesses()));
		queries.add(new BasicNameValuePair("earlier_damage_answer", (damage.getEarlierDamageAnswer())?"1":"0"));
		queries.add(new BasicNameValuePair("desc_if_yes", damage.getDescIfYes()));
		
		String result = request.send(context.getString(R.string.api_url) + "save_crash", queries, HttpRequest.POST);
		Log.i("VIKING", "damage report result: " + result);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				int crash_uid = Integer.parseInt(Helpers.parseXMLNode(result, "crash_uid"));
				
				for(DamagePicture damagePicture: damagePictures) {
					if(damagePicture.getFile() != null) {
						Map<String, String> q = new HashMap<String, String>();
						q.put("crash_uid", String.valueOf(crash_uid));
						q.put("description", damagePicture.getName());
						request.sendMultipart(context.getString(R.string.api_url) + "save_crash_pictures", damagePicture.getFile(), q);
					}
				}
				
				if(!damage.getEmail().equals("")) {
					if(sendDamageReport(crash_uid, damage.getEmail()))
						return true;
					else
						return false;
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean saveCarEvent(CarEvent carEvent, CarEventPicture carEventPicture) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(carEvent.getOwnerId())));
		queries.add(new BasicNameValuePair("name", carEvent.getName()));
		queries.add(new BasicNameValuePair("registration", carEvent.getRegistration()));
		queries.add(new BasicNameValuePair("event", carEvent.getEvent()));
		queries.add(new BasicNameValuePair("place", carEvent.getPlace()));
		queries.add(new BasicNameValuePair("event_datetime", carEvent.getDateTime()));
		queries.add(new BasicNameValuePair("note", carEvent.getNote()));
		
		String result = request.send(context.getString(R.string.api_url) + "save_car_events", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				int uid = Integer.parseInt(Helpers.parseXMLNode(result, "uid"));
				carEventPicture.setEventId(uid);
				carEvent.setUid(uid);
				carEventAdapter.insertCarEvent(carEvent);
				
				return true;
			}
		}
		return false;
	}
	
	private boolean saveCarEventPicture(CarEventPicture carEventPicture) {
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("event_id", String.valueOf(carEventPicture.getEventId()));
		queries.put("owner_id", String.valueOf(carEventPicture.getOwnerId()));
		queries.put("name", carEventPicture.getName());
		
		String result = request.sendMultipart(context.getString(R.string.api_url) + "save_car_events_pictures", carEventPicture.getImageFile(), queries);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				int uid = Integer.parseInt(Helpers.parseXMLNode(result, "uid"));
				carEventPicture.setUid(uid);
				carEventPictureAdapter.open();
				carEventPictureAdapter.insertCarEventPicture(carEventPicture);
				carEventPictureAdapter.close();
				
				return true;
			}
		}
		return false;
	}
	
	
	private boolean sendDamageReport(int crash_uid, String email) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("crash_uid", String.valueOf(crash_uid)));
		queries.add(new BasicNameValuePair("email", email));
		
		String result = request.send(context.getString(R.string.api_url) + "send_damagereport2", queries, HttpRequest.POST);
		Log.i("VIKING", "send damage report: " + result);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		
		return false;
	}
	
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
