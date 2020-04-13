package no.incent.viking;

import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import no.incent.viking.adapter.DamageReportPagerAdapter;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.widget.DamageReportPage1;
import no.incent.viking.widget.DamageReportPage2;
import no.incent.viking.widget.DamageReportPage3;
import no.incent.viking.widget.DamageReportPage4;
import no.incent.viking.widget.DamageReportPage5;
import no.incent.viking.widget.DamageReportPage6;
import no.incent.viking.widget.DamageReportPage7;
import no.incent.viking.widget.DamageReportPage8;
import no.incent.viking.widget.MyCarEvents;
import no.incent.viking.pojo.CarEvent;
import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Damage;
import no.incent.viking.pojo.DamagePicture;
import no.incent.viking.db.DBCarEventAdapter;
import no.incent.viking.db.DBCarEventPictureAdapter;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

public class DamageReportActivity extends FragmentActivity implements DamageReportPage5.DamageReportCallback {
	private ViewPager damage_report_pager;
	private final int DAMAGE_REPORT_PAGE5_CAMERA = 0x000234;
	private int mCurrentPictureIndex = 0;
	private int mPreviousPageIndex = 0;
	private final Damage damage = new Damage();
	private List<DamagePicture> damagePictures;
	private DamageReportPage8 damage_report_page8;
	private HttpRequest request;
	private User user;
	private int ownerId;
	private final DBCarEventAdapter carEventAdapter = new DBCarEventAdapter(this);
	private final DBCarEventPictureAdapter carEventPictureAdapter = new DBCarEventPictureAdapter(this);
	private TextView page_number;
	private TextView header_name;
	private String email;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.damage_reportv2);
		
		request = HttpRequest.getInstance();
		user = ((VikingApplication) getApplicationContext()).getUser();
		
		if(user != null) {
			ownerId = user.getUid();
			email = user.getEmail();
		}
		
		page_number = (TextView) findViewById(R.id.page_number);
		header_name = (TextView) findViewById(R.id.header_name);
		damage_report_pager = (ViewPager) findViewById(R.id.damage_report_pager);
		final DamageReportPagerAdapter adapter = new DamageReportPagerAdapter(getSupportFragmentManager());
		damage_report_pager.setAdapter(adapter);
		damage_report_page8 = (DamageReportPage8) adapter.fragments.get(adapter.getCount() - 1);
		
		damage_report_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				Fragment fragment = adapter.fragments.get(mPreviousPageIndex);
				if(fragment instanceof DamageReportPage1) {
					DamageReportPage1 damage_report_page1 = (DamageReportPage1) fragment;
					damage.setOwnerId(ownerId);
					damage.setCarRegNo(damage_report_page1.getRegistrationNumberA());
					damage.setCounterpartCarRegNo(damage_report_page1.getRegistrationNumberB());
				} else if(fragment instanceof DamageReportPage2) {
					DamageReportPage2 damage_report_page2 = (DamageReportPage2) fragment;
					damage.setDamageCauses(damage_report_page2.getDamageCauses());
				} else if(fragment instanceof DamageReportPage3) {
					DamageReportPage3 damage_report_page3 = (DamageReportPage3) fragment;
					damage.setAboutDamageDate(damage_report_page3.getDamageDate());
					damage.setAboutDamageTime(damage_report_page3.getDamageTime());
					damage.setPlaceOfDamage(damage_report_page3.getPlaceOfDamage());
					damage.setAboutDamageBlame(damage_report_page3.getAboutDamageBlame());
					damage.setSpeedOfVehicle(damage_report_page3.getSpeedOfVehicle());
					damage.setPolice(damage_report_page3.hasPolice());
					damage.setRescueCompany(damage_report_page3.getRescueCompany());
					damage.setDescriptionOfIncident(damage_report_page3.getDescriptionOfIncident());
				} else if(fragment instanceof DamageReportPage4) {
					DamageReportPage4 damage_report_page4 = (DamageReportPage4) fragment;
					damage.setPersonalInjuryAnswer(damage_report_page4.hasPersonalDamage());
					damage.setPersonalInjuryName(damage_report_page4.getPersonalDamageName());
					damage.setPersonalInjuryWitnesses(damage_report_page4.getPersonalDamageWitnesses());
				} else if(fragment instanceof DamageReportPage5) {
					DamageReportPage5 damage_report_page5 = (DamageReportPage5) fragment;
					damagePictures = damage_report_page5.damagePictures;
				} else if(fragment instanceof DamageReportPage6) {
					DamageReportPage6 damage_report_page6 = (DamageReportPage6) fragment;
					damage.setEarlierDamageAnswer(damage_report_page6.getEarlierDamage());
					damage.setDescIfYes(damage_report_page6.getDescIfYes());
				} else if(fragment instanceof DamageReportPage7) {
					DamageReportPage7 damage_report_page7 = (DamageReportPage7) fragment;
					damage.setFirstname(damage_report_page7.getFirstname());
					damage.setLastname(damage_report_page7.getLastname());
					damage.setAddress(damage_report_page7.getAddress());
					damage.setZipcity(damage_report_page7.getZipCity());
					damage.setPhoneNumber(damage_report_page7.getPhoneNumber());
					damage.setEmail(damage_report_page7.getEmail());
					damage.setInsuranceCompany(damage_report_page7.getInsuranceCompany());
					damage.setDriverLicenseNo(damage_report_page7.getDriverLicenseNo());
				}
				
				switch(arg0) {
					case 0:
						page_number.setText("");
						header_name.setText("INFORMASJON");
						break;
					case 1:
						page_number.setText("1/8");
						header_name.setText("REGISTRERINGSNUMMER");
						break;
					case 2:
						page_number.setText("2/8");
						header_name.setText("HUK AV DET SOM PASSER");
						break;
					case 3:
						page_number.setText("3/8");
						header_name.setText("OM SKADEN");
						break;
					case 4:
						page_number.setText("4/8");
						header_name.setText("PERSONSKADE");
						break;
					case 5:
						page_number.setText("5/8");
						header_name.setText("TA DISSE BILDENE");
						break;
					case 6:
						page_number.setText("6/8");
						header_name.setText("ANNEN MATERIELL SKADE ENN BIL");
						break;
					case 7:
						page_number.setText("7/8");
						header_name.setText("PERSONOPPLYSNINGER KJØRETØY A");
						break;
					case 8:
						page_number.setText("8/8");
						header_name.setText("PERSONOPPLYSNINGER KJØRETØY B");
						break;
				}
				mPreviousPageIndex = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		damage_report_page8.setOnSaveDamageReport(new DamageReportPage8.OnSaveDamageReport() {
			@Override
			public void onSaveDamageReport() {
				if(email != null && !email.equals("")) {
					processDamageReport();
				} else {
					DamageReportAlert alert = new DamageReportAlert(DamageReportActivity.this);
					alert.show();
				}
			}
		});
		
		findViewById(R.id.close_damage_report).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_application_key));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	private class DamageReportAlert extends AlertDialog {
		private EditText email_field;
		public DamageReportAlert(Context context) {
			super(context);
			setCancelable(false);
			
			LinearLayout linearLayout = new LinearLayout(DamageReportActivity.this);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setPadding(10, 10, 10, 10);
			TextView textView = new TextView(DamageReportActivity.this);
			textView.setText("Email: ");
			textView.setTextColor(Color.WHITE);
			textView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f));
			email_field = new EditText(DamageReportActivity.this);
			email_field.setSingleLine(true);
			email_field.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f));
			linearLayout.addView(textView);
			linearLayout.addView(email_field);
			setView(linearLayout);
			setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(email_field.getText().toString().equals("")) {
						Toast.makeText(DamageReportActivity.this, "Skriv inn din e-post", Toast.LENGTH_LONG).show();
					} else if(!Pattern.matches("[a-zA-Z0-9]+@+([a-zA-Z0-9]*)\\.[a-z]{1,3}([\\.a-zA-Z0-9]{1,3})?", email_field.getText().toString())) {
						Toast.makeText(DamageReportActivity.this, "Ugyldig e-format", Toast.LENGTH_LONG).show();
					} else {
						email = email_field.getText().toString();
						processDamageReport();
						dismiss();
					}
				}
			});
			getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		}
	}
	
	private void processDamageReport() {
		damage.setCounterpartFirstname(damage_report_page8.getCounterpartFirstname());
		damage.setCounterpartLastname(damage_report_page8.getCounterpartLastname());
		damage.setCounterpartAddress(damage_report_page8.getCounterpartAddress());
		damage.setCounterpartZipcity(damage_report_page8.getCounterpartZipCity());
		damage.setCounterpartPhoneNumber(damage_report_page8.getCounterpartPhoneNumber());
		damage.setCounterpartEmail(damage_report_page8.getCounterpartEmail());
		damage.setCounterpartInsuranceCompany(damage_report_page8.getCounterpartInsuranceCompany());
		damage.setCounterpartDriverLicenseNo(damage_report_page8.getCounterpartDriverLicenseNo());
		
		if(Helpers.isNetworkAvailable(DamageReportActivity.this)) {
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
						
						
						runOnUiThread(new Runnable() {
							public void run() {
								findViewById(R.id.damage_report_loader).setVisibility(View.GONE);
								//damage_report_flipper.setDisplayedChild(0);
								
								if(Helpers.Constants.mIsLoggedIn) {
									final AlertDialog alert = new AlertDialog.Builder(DamageReportActivity.this)
																.setTitle("Skademelding lagret")
																.setMessage("Skademeldingen ble lagret i BILioteket under “Hendelser” og sendt til oppgitt e-post adresse. Du kan også hente den frem på din konto på vikingredning.no")
																.setCancelable(false)
																.setNeutralButton("OK", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(DialogInterface dialog, int which) {
																		setResult(Helpers.Constants.RESULT_DAMAGE_REPORT_SUCCESS);
																		finish();
																	}
																}).create();
									alert.show();
								} else {
									final AlertDialog alert = new AlertDialog.Builder(DamageReportActivity.this)
																.setTitle("Skademelding lagret")
																.setMessage("Skademeldingen ble sendt til oppgitt e-post adresse. Lag Venn av Viking-bruker for å kunne lagre informasjon under \"Hendelser\" i app.")
																.setCancelable(false)
																.setNeutralButton("OK", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(DialogInterface dialog, int which) {
																		setResult(Helpers.Constants.RESULT_DAMAGE_REPORT_SUCCESS);
																		finish();
																	}
																}).create();
									alert.show();
								}
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								findViewById(R.id.damage_report_loader).setVisibility(View.GONE);
								Helpers.showMessage(DamageReportActivity.this, "", "Unable to send damage report.");
							}
						});
					}
				}
			}).start();
		} else {
			Helpers.showMessage(DamageReportActivity.this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
		}
	}

	@Override
	public void onDamagePictureClick(int position) {
		mCurrentPictureIndex = position;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, DAMAGE_REPORT_PAGE5_CAMERA);
	}

	@Override
	public void onDamageSuccess() {
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == DAMAGE_REPORT_PAGE5_CAMERA) {
			Helpers.createDirectory("viking/damagereport");
			File fileDamageReportImage = new File(Environment.getExternalStorageDirectory(), "viking/damagereport/" + System.currentTimeMillis() + ".png");
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Helpers.saveFileFromBitmap(fileDamageReportImage, bitmap, Bitmap.CompressFormat.PNG);
			
			DamageReportPagerAdapter adapter = (DamageReportPagerAdapter) damage_report_pager.getAdapter();
			((DamageReportPage5) adapter.getItem(damage_report_pager.getCurrentItem())).damagePictures.get(mCurrentPictureIndex).setThumbFile(fileDamageReportImage);
			((DamageReportPage5) adapter.getItem(damage_report_pager.getCurrentItem())).damagePictures.get(mCurrentPictureIndex).setFile(fileDamageReportImage);
			((DamageReportPage5) adapter.getItem(damage_report_pager.getCurrentItem())).updateDamagePictures();
		}
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
		
		for(int i=0;i<damage.getDamageCauses().length;i++) {
			queries.add(new BasicNameValuePair("damage_cause" + (i + 1), (damage.getDamageCauses()[i])?"1":"0"));
		}
		
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
		
		String result = request.send(getString(R.string.api_url) + "save_crash", queries, HttpRequest.POST);
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
						request.sendMultipart(getString(R.string.api_url) + "save_crash_pictures", damagePicture.getFile(), q);
					}
				}
				
				if(email != null && !email.equals("")) {
					if(sendDamageReport(crash_uid, email))
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
		
		String result = request.send(getString(R.string.api_url) + "save_car_events", queries, HttpRequest.POST);
		
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
		
		String result = request.sendMultipart(getString(R.string.api_url) + "save_car_events_pictures", carEventPicture.getImageFile(), queries);
		
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
		
		String result = request.send(getString(R.string.api_url) + "send_damagereport2", queries, HttpRequest.POST);
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
