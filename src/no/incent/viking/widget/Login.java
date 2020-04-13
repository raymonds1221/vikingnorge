package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.Car;
import no.incent.viking.pojo.User;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.db.DBUserAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Handler;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Login extends LinearLayout {
	private final String TAG = "VIKING";
	private Context context;
	private EditText field_username;
	private EditText field_password;
	private HttpRequest request;
	private DBCarAdapter carAdapter;
	private DBUserAdapter userAdapter;
	private final Handler handler = new Handler();
	private OnLoginListener loginListener;
	private PreferenceWrapper prefsWrapper;
	
	public Login(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.mycar_login, this);
		
		this.context = context;	
		field_username = (EditText) findViewById(R.id.field_username);
		field_password = (EditText) findViewById(R.id.field_password);
		
		field_username.setTypeface(Helpers.getArialFont(context));
		field_password.setTypeface(Helpers.getArialFont(context));
		
		request = HttpRequest.getInstance();
		carAdapter = new DBCarAdapter(context);
		userAdapter = new DBUserAdapter(context);
		
		loginListener = (OnLoginListener) context;
		prefsWrapper = new PreferenceWrapper(context);
		
		findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!field_username.getText().toString().trim().equals("") && !field_password.getText().toString().trim().equals(""))
					if(Helpers.isNetworkAvailable(context)) {
						final ProgressDialog progress = new ProgressDialog(context);
						progress.setMessage("Please wait...");
						progress.setCancelable(false);
						progress.show();
						
						final String url = String.format(context.getString(R.string.api_url) + "%s/%s/%s/json", "login", 
								field_username.getText().toString(), field_password.getText().toString());
						new Thread(new Runnable() {
							public void run() {
								String jsonString = request.send(url, null, HttpRequest.GET);
								
								if(jsonString != null) {
									try {
										final JSONObject json = new JSONObject(jsonString);
										final JSONObject info = json.getJSONObject("info");
										
										int success = info.getInt("success");
										
										if(success == 1) {
											final User user = new User();
											user.setUid(info.getInt("uid"));
											user.setFirstname(info.getString("firstname"));
											user.setLastname(info.getString("lastname"));
											user.setEmail(info.getString("email"));
											user.setAddress(info.getString("address"));
											user.setAreaCode(info.getString("areacode")); 
											user.setArea(info.getString("area"));
											user.setTelephone(info.getString("telephone"));
											user.setPassword(info.getString("password"));
											user.setCarRegNo(info.getString("car_reg_no"));
											user.setYearOfBirth(info.getString("year_of_birth"));
											user.setCountry(info.getString("country"));
											user.setPostbox(info.getString("postbox"));
											user.setGender(info.getString("gender"));
											user.setStatus(info.getString("status"));
											
											userAdapter.open();
											if(userAdapter.getUser(user.getUid()) != null) {
												userAdapter.updateUser(user);
											} else {
												userAdapter.insertUser(user);
											}
											userAdapter.close();
											Log.i(TAG, "Owner id: " + user.getUid());
											((VikingApplication) context.getApplicationContext()).setUser(user);
											
											/*
											final List<Car> cars = getAllCars(user.getUid());
											((VikingApplication) context.getApplicationContext()).setCars(cars);
											
											if(cars.size() > 0) {
												((VikingApplication) context.getApplicationContext()).setActiveCar(cars.get(0));
											}*/
											Helpers.Constants.mIsLoggedIn = true;
											prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN, true);
											prefsWrapper.setPreferenceIntValue(PreferenceWrapper.OWNER_ID, user.getUid());
											
											handler.post(new Runnable() {
												public void run() {
													progress.dismiss();
													loginListener.onLoginSuccess(null, user);
												}
											});
										} else {
											final String message = info.getString("message");
											
											handler.post(new Runnable() {
												public void run() {
													progress.dismiss();
													Helpers.showMessage(context, "", message);
												}
											});
										}
										
									} catch(JSONException ex) {
										Log.e(TAG, ex.getMessage());
									}
								} else {
									handler.post(new Runnable() {
										public void run() {
											userAdapter.open();
											User user;
											if((user = userAdapter.getUser(field_username.getText().toString(), 
													field_password.getText().toString())) != null) {
												((VikingApplication) context.getApplicationContext()).setUser(user);
												progress.dismiss();
												carAdapter.openReadable();
												loginListener.onLoginSuccess(carAdapter.getAllCars(user.getUid()), user);
												carAdapter.close();
											} else {
												Helpers.showMessage(context, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller pr�ve etter en tid.");
											}
											userAdapter.close();
										}
									});
								}
							}
						}).start();;
					} else {
						userAdapter.open();
						User user;
						if((user = userAdapter.getUser(field_username.getText().toString(), 
								field_password.getText().toString())) != null) {
							((VikingApplication) context.getApplicationContext()).setUser(user);
							carAdapter.openReadable();
							loginListener.onLoginSuccess(carAdapter.getAllCars(user.getUid()), user);
							carAdapter.close();
						} else {
							Helpers.showMessage(context, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller pr�ve etter en tid.");
						}
						userAdapter.close();
					}
			}
		});
		
		
	}
	
	private List<Car> getAllCars(int ownerId) {
		List<Car> cars = new ArrayList<Car>();
		
		String jsonString = request.send(context.getString(R.string.api_url) + String.format("car/%s/json", ownerId), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray jsonCars = json.getJSONArray("cars");
				
				carAdapter.openWritable();
				carAdapter.deleteAll(ownerId);
				for(int i=0;i<jsonCars.length();i++) {
					JSONObject info = jsonCars.getJSONObject(i).getJSONObject("info");
					Car car = new Car();
					car.setUid(info.getInt("uid"));
					car.setOwnerId(info.getInt("owner_id"));
					car.setRegistrationNumber(info.getString("registration_number"));
					car.setChassisNumber(info.getString("chassis_number"));
					car.setCarRegYear(info.getString("car_reg_year"));
					car.setRegFirstTimeInNorway(info.getString("reg_first_time_in_norway"));
					car.setBrandCode(info.getString("brand_code"));
					car.setCarModel(info.getString("car_model"));
					car.setEnginePerformance(info.getString("engine_performance"));
					car.setDisplacement(info.getString("displacement"));
					car.setFuelType(info.getString("fuel_type"));
					car.setLength(info.getString("length"));
					car.setWidth(info.getString("width"));
					car.setWeight(info.getString("weight"));
					car.setTotalWeight(info.getString("total_weight"));
					car.setColour(info.getString("colour"));
					car.setCo2Emission(info.getString("co2_emission"));
					car.setFilename(info.getString("filename"));
					car.setSharedCarId(info.getInt("sharedcar_id"));
					car.setShared(info.getBoolean("is_shared"));
					byte[] data = request.send(info.getString("filename"));
					
					String filename = Helpers.getLastPathSegment(car.getFilename());
					File f = Helpers.createDirectory("viking/cars");
					
					if(filename != null && f != null && Helpers.isValidDownloadFile(filename)) {
						f = new File(f, filename);
						Helpers.saveFile(f, data, true);
						car.setPath(f.getAbsolutePath());
					}
					cars.add(car);
					carAdapter.insertCar(car);
				}
				carAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return cars;
	}
	
	public static interface OnLoginListener {
		public void onLoginSuccess(List<Car> cars, User user);
	}
}
