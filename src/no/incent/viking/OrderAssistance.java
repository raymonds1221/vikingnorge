package no.incent.viking;

import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Car;
import no.incent.viking.pojo.Geocode;
import no.incent.viking.pojo.CarEvent;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.db.DBCarEventAdapter;
import no.incent.viking.db.DBCarEventPictureAdapter;
import no.incent.viking.adapter.OrderAssistancePositionAdapter;
import no.incent.viking.receiver.OrderStatusReceiver;
import no.incent.viking.widget.MyCarEvents;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URLEncoder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.location.Location;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.maps.MapView;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.GeoPoint;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;

public class OrderAssistance extends BaseActivity {
	private final String TAG = "VIKING";
	private User user;
	private final String[] causes = {"STARTVANSKER", "MOTORSTOPP", "KOLLISJON", 
			"UTFORKJØRING", "FLATT BATTERI", "PUNKTERING", "UTELÅSING", "FASTKJØRING", "FEIL DRIVSTOFF FYLT", "ANNET"};
	private final String[] codes = {"STA", "MOT", "KOL", "UTF", "FLA", "PUN", "UTL", "FAS", "FYL", "ANN"};
	private Spinner damage_cause;
	private Location location;
	private AutoCompleteTextView asst_loc_field;
	private AutoCompleteTextView asst_car_reg_no;
	private MapView order_assistance_map;
	private double latitude = 0, longitude = 0;
	private String place_of_accident = "";
	private String waiting_phone = "";
	private List<Geocode> addresses;
	private PreferenceWrapper prefsWrapper;
	private DBCarAdapter carAdapter;
	private DBCarEventAdapter carEventAdapter;
	private DBCarEventPictureAdapter carEventPictureAdapter;
	
	@Override
	protected int contentView() {
		return R.layout.order_assistance;
	}

	@Override
	protected void initialize() {
		initViews();
		initEvents();
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
	
	private void initViews() {
		user = ((VikingApplication) getApplicationContext()).getUser();
		prefsWrapper = new PreferenceWrapper(this);
		carAdapter = new DBCarAdapter(this);
		carEventAdapter = new DBCarEventAdapter(this);
		carEventPictureAdapter = new DBCarEventPictureAdapter(this);
		
		damage_cause = (Spinner) findViewById(R.id.damage_cause);
		asst_loc_field = (AutoCompleteTextView) findViewById(R.id.asst_loc_field);
		asst_car_reg_no = (AutoCompleteTextView) findViewById(R.id.asst_car_reg_no);
		order_assistance_map = (MapView) findViewById(R.id.order_assistance_map);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, causes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		damage_cause.setAdapter(adapter);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	private void initEvents() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			if(location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				asst_loc_field.setText(latitude + ", " + longitude);
			} else {
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
				if(location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					asst_loc_field.setText(latitude + ", " + longitude);
				}
			}
		} else {
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			if(location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				asst_loc_field.setText(latitude + ", " + longitude);
			}
		}
		
		asst_loc_field.setEnabled(location == null);
		
		final MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, order_assistance_map);
		final MapController controller = order_assistance_map.getController();
		controller.setZoom(10);
		order_assistance_map.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				controller.animateTo(myLocationOverlay.getMyLocation());
			}
		});
		
		if(user != null) {
			List<Car> cars = carAdapter.getAllCars(user.getUid());
			String[] regNos = new String[cars.size()];
			
			if(cars.size() > 0)
				asst_car_reg_no.setText(cars.get(0).getRegistrationNumber());
			
			for(int i=0;i<cars.size();i++) {
				regNos[i] = cars.get(i).getRegistrationNumber();
			}
			
			asst_car_reg_no.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, regNos));
		}
		
		findViewById(R.id.call_now_btn).setOnClickListener(new View.OnClickListener() { 
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:06000"));
				startActivity(intent);
			}
		});
		
		findViewById(R.id.order_now_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED) {
					Helpers.showMessage(OrderAssistance.this, "" , "Kan ikke behandle ordren fordi en annen ordre i arbeid");
					return;
				}
				
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				
				if(latitude == 0 && longitude == 0) {
					Helpers.showMessage(OrderAssistance.this, "", "Angi din posisjon");
					asst_loc_field.requestFocus();
					inputMethodManager.showSoftInput(asst_loc_field, 0);
				} else if(asst_car_reg_no.getText().toString().equals("")) {
					Helpers.showMessage(OrderAssistance.this, "", "Skriv inn plate nummer");
					asst_car_reg_no.requestFocus();
					inputMethodManager.showSoftInput(asst_car_reg_no, 0);
				} else {
					AlertDialog alert = new AlertDialog.Builder(OrderAssistance.this)
					.setCancelable(true)
					.setMessage("Vil du fullføre bestillingen?")
					.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(Helpers.isNetworkAvailable(OrderAssistance.this)) {
								if(user != null) {
									waiting_phone = user.getTelephone();
								}
								
								if(waiting_phone.equals("")) {
									LinearLayout linearLayout = new LinearLayout(OrderAssistance.this);
									LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
									linearLayout.setLayoutParams(layoutParams);
									linearLayout.setOrientation(LinearLayout.VERTICAL);
									final TextView textView = new TextView(OrderAssistance.this);
									textView.setGravity(Gravity.CENTER_HORIZONTAL);
									textView.setText("Fyll inn mobilnummer");
									textView.setTextColor(Color.WHITE);
									final EditText phone = new EditText(OrderAssistance.this);
									phone.setGravity(Gravity.CENTER_HORIZONTAL);
									phone.setInputType(InputType.TYPE_CLASS_PHONE);
									linearLayout.addView(textView);
									linearLayout.addView(phone);
									
									AlertDialog alert = new AlertDialog.Builder(OrderAssistance.this)
										.setCancelable(true)
										.setView(linearLayout)
										.setPositiveButton("OK", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												if(!phone.getText().toString().equals("")) {
													waiting_phone = phone.getText().toString();
													
													new Thread(new Runnable() {
														public void run() {
															if(registerRoadAssistance()) {
																prefsWrapper.setPreferenceIntValue(PreferenceWrapper.ORDER_STATUS, PreferenceWrapper.ORDER_STATUS_RECEIVED);
																OrderStatusReceiver.startAlarm(OrderAssistance.this);
																setResult(RESULT_OK);
																finish();
															}
														}
													}).start();
												}
												dialog.dismiss();
											}
										})
										.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										}).create();
									
									alert.show();
								} else {
									new Thread(new Runnable() {
										public void run() {
											if(registerRoadAssistance()) {
												prefsWrapper.setPreferenceIntValue(PreferenceWrapper.ORDER_STATUS, PreferenceWrapper.ORDER_STATUS_RECEIVED);
												OrderStatusReceiver.startAlarm(OrderAssistance.this);
												setResult(RESULT_OK);
												finish();
											}
										}
									}).start();
								}
							} else {
								Helpers.showMessage(OrderAssistance.this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller pr�ve etter en tid.");
							}
						}
					})
					.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
					alert.show();
				}
			}
		});
		
		asst_loc_field.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditText) v).setText("");
			}
		});
		
		asst_loc_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					((EditText) v).setText("");
			}
		});
		
		asst_loc_field.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
				if(!s.toString().equals("") && Helpers.isNetworkAvailable(OrderAssistance.this)) {
					new Thread(new Runnable() {
						public void run() {
							addresses = getAllAddress(URLEncoder.encode(s.toString()));
							
							if(addresses.size() > 0) {
								OrderAssistance.this.runOnUiThread(new Runnable() {
									public void run() {
										final OrderAssistancePositionAdapter adapter = new OrderAssistancePositionAdapter(OrderAssistance.this, 
												R.layout.list_order_assitance_position, addresses);
										adapter.setDropDownViewResource(R.layout.list_order_assitance_position);
										asst_loc_field.setAdapter(adapter);
										
										adapter.setOnItemClickListener(new OrderAssistancePositionAdapter.OnItemClickListener() {
											@Override
											public void onItemClick(final Geocode geoCode) {
												asst_loc_field.setText(geoCode.getLatitude() + ", " + geoCode.getLongitude());
												InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
												inputMethodManager.hideSoftInputFromWindow(asst_loc_field.getWindowToken(), 0);
												GeoPoint geoPoint = new GeoPoint((int) (geoCode.getLatitude() * 1E6), (int) (geoCode.getLongitude() * 1E6));
												controller.animateTo(geoPoint);
												controller.setCenter(geoPoint);
												place_of_accident = geoCode.getPostalCode();
												if(place_of_accident == null)
													place_of_accident = geoCode.getAdministrativeArea();
												latitude = geoCode.getLatitude();
												longitude = geoCode.getLongitude();
												asst_loc_field.clearFocus();
											}
										});
									}
								});
							}
						}
					}).start();
				}
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void onCloseClicked(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private List<Geocode> getAllAddress(String address) {
		List<Geocode> geoCodes = new ArrayList<Geocode>();
		String googleGeocoder = request.send(String.format("http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=true", address), null, HttpRequest.GET);
		
		if(googleGeocoder != null) {
			try {
				JSONObject json = new JSONObject(googleGeocoder);
				
				if(json.getString("status").equals("OK")) {
					JSONArray results = json.getJSONArray("results");
					
					for(int i=0;i<results.length();i++) {
						JSONObject result = results.getJSONObject(i);
						Geocode geoCode = new Geocode();
						geoCode.setFormattedAddress(result.getString("formatted_address"));
						
						JSONArray addressComponents = result.getJSONArray("address_components");
						
						for(int j=0;j<addressComponents.length();j++) {
							JSONObject addressComponent = addressComponents.getJSONObject(j);
							JSONArray types = addressComponent.getJSONArray("types");
							
							for(int h=0;h<types.length();h++) {
								if(types.getString(h).equals("administrative_area_level_1")) {
									geoCode.setAdministrativeArea(addressComponent.getString("long_name"));
								} else if(types.getString(h).equals("postal_code")) {
									geoCode.setPostalCode(addressComponent.getString("long_name"));
								}
							}
						}
						
						JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
						geoCode.setLatitude(location.getDouble("lat"));
						geoCode.setLongitude(location.getDouble("lng"));
						
						geoCodes.add(geoCode);
					}
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return geoCodes;
	}
	
	private List<Geocode> getAllAddress(double latitude, double longitude) {
		List<Geocode> geoCodes = new ArrayList<Geocode>();
		String googleGeocoder = request.send(String.format("http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=true", latitude, longitude), null, HttpRequest.GET);
		
		if(googleGeocoder != null) {
			try {
				JSONObject json = new JSONObject(googleGeocoder);
				
				if(json.getString("status").equals("OK")) {
					JSONArray results = json.getJSONArray("results");
					
					for(int i=0;i<results.length();i++) {
						JSONObject result = results.getJSONObject(i);
						Geocode geoCode = new Geocode();
						geoCode.setFormattedAddress(result.getString("formatted_address"));
						
						JSONArray addressComponents = result.getJSONArray("address_components");
						
						for(int j=0;j<addressComponents.length();j++) {
							JSONObject addressComponent = addressComponents.getJSONObject(j);
							JSONArray types = addressComponent.getJSONArray("types");
							
							for(int h=0;h<types.length();h++) {
								if(types.getString(h).equals("administrative_area_level_1")) {
									geoCode.setAdministrativeArea(addressComponent.getString("long_name"));
								} else if(types.getString(h).equals("postal_code")) {
									geoCode.setPostalCode(addressComponent.getString("long_name"));
								}
							}
						}
						
						JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
						geoCode.setLatitude(location.getDouble("lat"));
						geoCode.setLongitude(location.getDouble("lng"));
						
						geoCodes.add(geoCode);
					}
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return geoCodes;
	}
	
	private Geocode getNearest(double latitude, double longitude, List<Geocode> geoCodes) {
		Geocode geoCode = null;
		final int EARTH_RADIUS = 3956;
		double nearestDest = 0;
		
		for(Geocode gc: geoCodes) {
			double lat = Math.pow(Math.sin((latitude - gc.getLatitude()) * Math.PI/180/2), 2);
			double lon = Math.pow(Math.sin((latitude - gc.getLatitude()) * Math.PI/180/2), 2);
			double dest = EARTH_RADIUS * 2 * Math.asin(Math.sqrt(lat + Math.cos(latitude * Math.PI/180) * Math.cos(gc.getLatitude() * Math.PI/180) * lon));
			
			if(nearestDest == 0) {
				nearestDest = dest;
				geoCode = gc;
			} else {
				if(nearestDest < dest && 
						(gc.getAdministrativeArea() != null || gc.getPostalCode() != null)) {
					nearestDest = dest;
					geoCode = gc;
				}
			}
		}
		
		return geoCode;
	}
	
	private boolean registerRoadAssistance() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Calendar calendar = Calendar.getInstance();
		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");
		
		String object_number = asst_car_reg_no.getText().toString();
		String recipient_number = "";
		String language = "N";
		String waiting_phone_serial_number = telephonyManager.getDeviceId();
		String time_of_accident = formatter1.format(calendar.getTime()) + "T" + formatter2.format(calendar.getTime());
		String accident_cause = codes[damage_cause.getSelectedItemPosition()];
		
		if(place_of_accident == null || place_of_accident.equals("")) {
			List<Geocode> addresses = getAllAddress(latitude, longitude);
			Geocode geoCode = getNearest(latitude, longitude, addresses);
			
			if(addresses.size() > 0) {
				place_of_accident = geoCode.getPostalCode();
				if(place_of_accident == null)
					place_of_accident = geoCode.getAdministrativeArea();
			}
		}
		if(user != null) {
			recipient_number = user.getFirstname() + " " + user.getLastname();
		}
		
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("object_number", object_number));
		queries.add(new BasicNameValuePair("recipient_number", recipient_number));
		queries.add(new BasicNameValuePair("language", language));
		queries.add(new BasicNameValuePair("waiting_phone", waiting_phone));
		queries.add(new BasicNameValuePair("waiting_phone_serial_number", waiting_phone_serial_number));
		queries.add(new BasicNameValuePair("time_of_accident", time_of_accident));
		queries.add(new BasicNameValuePair("accident_cause", accident_cause));
		queries.add(new BasicNameValuePair("accident_place", latitude + "," + longitude));
		queries.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		queries.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
		
		String result = request.send(getString(R.string.api_url) + "road_assistance_registeroppdrag/json", queries, HttpRequest.POST);
		
		if(result != null) {
			try {
				JSONObject json = new JSONObject(result);
				JSONObject registreroppdragresult = json.getJSONObject("registreroppdragresult");
				prefsWrapper.setPreferenceStringValue(PreferenceWrapper.ORDER_MISSION, 
						registreroppdragresult.getString("Oppdrag"));
				Log.i(TAG, "mission: " + registreroppdragresult.getString("Oppdrag"));
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
			
			CarEvent carEvent = new CarEvent();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			if(user != null) {
				carEvent.setOwnerId(user.getUid());
				carEvent.setName(user.getFirstname() + " " + user.getLastname());
			}
			
			carEvent.setRegistration(object_number);
			carEvent.setEvent("Veihjelp");
			carEvent.setPlace(latitude + "," + longitude);
			carEvent.setDateTime(formatter.format(new Date()));
			carEvent.setDateCreated(formatter.format(new Date()));
			
			if(Helpers.Constants.mIsLoggedIn) {
				saveCarEvent(carEvent);
			} else {
				MyCarEvents.addTempCarEvent(carEvent);
			}
			
			return true;
		}
		return false;
	}
	
	private boolean saveCarEvent(CarEvent carEvent) {
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
				carEvent.setUid(uid);
				carEventAdapter.insertCarEvent(carEvent);
				
				return true;
			}
		}
		return false;
	}
}
