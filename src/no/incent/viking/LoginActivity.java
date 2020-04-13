package no.incent.viking;

import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Car;
import no.incent.viking.pojo.CallToAction;
import no.incent.viking.db.DBUserAdapter;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.db.DBCallToActionAdapter;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;
import no.incent.viking.service.VikingService;
import no.incent.viking.service.IVikingService;
import no.incent.viking.service.IVikingServiceCallback;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ViewFlipper;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.flurry.android.FlurryAgent;

public class LoginActivity extends BaseActivity {
	private ViewFlipper login_flipper;
	private Animation fade_in;
	private Animation fade_out;
	private EditText field_username, field_password, field_sign_mobile, field_sign_pass, field_sign_email;
	private ImageView call_to_action;
	private DBUserAdapter userAdapter;
	private DBCarAdapter carAdapter;
	private DBCallToActionAdapter callToActionAdapter;
	private CallToAction callToAction;
	private PreferenceWrapper prefsWrapper;
	private ProgressDialog progress;
	private IVikingService mVikingService;
	
	@Override
	protected int contentView() {
		return R.layout.login;
	}

	@Override
	protected void initialize() {
		userAdapter = new DBUserAdapter(this);
		carAdapter = new DBCarAdapter(this);
		callToActionAdapter = new DBCallToActionAdapter(this);
		
		field_username = (EditText) findViewById(R.id.field_username);
        field_password = (EditText) findViewById(R.id.field_password);
        field_sign_mobile = (EditText) findViewById(R.id.field_sign_mobile);
        field_sign_pass = (EditText) findViewById(R.id.field_sign_pass);
        field_sign_email = (EditText) findViewById(R.id.field_sign_email);
        
        field_username.setTypeface(Helpers.getArialFont(this));
        field_password.setTypeface(Helpers.getArialFont(this));
        field_sign_mobile.setTypeface(Helpers.getArialFont(this));
        field_sign_pass.setTypeface(Helpers.getArialFont(this));
        field_sign_email.setTypeface(Helpers.getArialFont(this));
        
		login_flipper = (ViewFlipper) findViewById(R.id.login_flipper);
		fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		
		call_to_action = (ImageView) findViewById(R.id.call_to_action);
		prefsWrapper = new PreferenceWrapper(this);
		
		progress = new ProgressDialog(this);
		progress.setCancelable(false);
		progress.setMessage("Please wait...");
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		
		callToActionAdapter.openReadable();
		if(width >= 480 && height >= 800) {
			callToAction = callToActionAdapter.getCallToAction(6);
		} else {
			callToAction = callToActionAdapter.getCallToAction(7);
		}
		callToActionAdapter.close();
		
		if(callToAction != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(callToAction.getPath());
			call_to_action.setImageBitmap(bitmap);
		}
		
		Intent intent = new Intent(this, VikingService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
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
	
	public void onLoginClicked(View view) {
		Log.i(TAG, "Login clicked!");
		if(!field_username.getText().toString().trim().equals("") && !field_password.getText().toString().trim().equals(""))
			if(Helpers.isNetworkAvailable(this)) {
				progress.show();
				
				final String url = String.format(getString(R.string.api_url) + "%s/%s/%s/json", "login", 
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
									User user = new User();
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
									
									mVikingService.saveUser(user);
									/*
									userAdapter.open();
									if(userAdapter.getUser(user.getUid()) != null) {
										userAdapter.updateUser(user);
									} else {
										userAdapter.insertUser(user);
									}
									userAdapter.close();
									Log.i(TAG, "Owner id: " + user.getUid());
									((VikingApplication) getApplicationContext()).setUser(user);
									
									List<Car> cars = getAllCars(user.getUid());
									
									((VikingApplication) getApplicationContext()).setCars(cars);
									
									if(cars.size() > 0) {
										((VikingApplication) getApplicationContext()).setActiveCar(cars.get(0));
									}
									
									prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN, true);
									prefsWrapper.setPreferenceIntValue(PreferenceWrapper.OWNER_ID, user.getUid());
									
									handler.post(new Runnable() {
										public void run() {
											progress.dismiss();
											Helpers.Constants.mIsLoggedIn = true;
											final Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
											startActivity(intent);
										}
									});*/
								} else {
									final String message = info.getString("message");
									
									handler.post(new Runnable() {
										public void run() {
											progress.dismiss();
											Helpers.showMessage(LoginActivity.this, "", message);
										}
									});
								}
								
							} catch(JSONException ex) {
								Log.e(TAG, ex.getMessage());
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						} else {
							handler.post(new Runnable() {
								public void run() {
									userAdapter.open();
									User user;
									if((user = userAdapter.getUser(field_username.getText().toString(), 
											field_password.getText().toString())) != null) {
										((VikingApplication) getApplicationContext()).setUser(user);
										Helpers.Constants.mIsLoggedIn = true;
										final Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
								        startActivity(intent);
									} else {
										Helpers.showMessage(LoginActivity.this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
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
					((VikingApplication) getApplicationContext()).setUser(user);
					final Intent intent = new Intent(this, MainMenuActivity.class);
			        startActivity(intent);
				} else {
					Helpers.showMessage(this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
				}
				userAdapter.close();
			}
	}
	
	public void onRegisterClicked(View view) {
		if(!field_sign_mobile.getText().toString().trim().equals("") &&
				!field_sign_pass.getText().toString().trim().equals("") && !field_sign_email.getText().toString().trim().equals("")) {
			if(Helpers.isNetworkAvailable(this)) {
				final ProgressDialog progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("Processing...");
				progressDialog.show();
				
				new Thread(new Runnable() {
					public void run() {
						List<NameValuePair> queries = new ArrayList<NameValuePair>();
						queries.add(new BasicNameValuePair("telephone", field_sign_mobile.getText().toString().trim()));
						queries.add(new BasicNameValuePair("password", field_sign_pass.getText().toString().trim()));
						queries.add(new BasicNameValuePair("email", field_sign_email.getText().toString().trim()));
						
						String url = getString(R.string.api_url) + "signup";
						final String returnString = request.send(url, queries, HttpRequest.POST);
						
						handler.post(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								
								DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
								try {
									DocumentBuilder db = dbf.newDocumentBuilder();
									Document document = db.parse(new InputSource(new StringReader(returnString)));
									NodeList nl = document.getElementsByTagName("successful");
									
									int success = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
									
									if(success == 1) {
										nl = document.getElementsByTagName("owner_id");
										int owner_id = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
										
										User user = new User();
										user.setUid(owner_id);
										user.setTelephone(field_sign_mobile.getText().toString());
										user.setPassword(field_sign_pass.getText().toString());
										
										/*
										userAdapter.open();
										userAdapter.insertUser(user);
										userAdapter.close();*/
										
										if(mVikingService != null) {
											mVikingService.saveUser(user);
										}
										
										Helpers.Constants.mIsLoggedIn = true;
										
										final Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
								        startActivity(intent);
									} else {
										String message = Helpers.parseXMLNode(returnString, "message");
										
										if(message != null && !message.equals("")) {
											Helpers.showMessage(LoginActivity.this, "Obs!", message);
										}
									}
								} catch (ParserConfigurationException ex) {
									Log.e(TAG, ex.getMessage());
								} catch(IOException ex) {
									Log.e(TAG, ex.getMessage());
								} catch (SAXException ex) {
									Log.e(TAG, ex.getMessage());
								} catch(RemoteException ex) {
									Log.e(TAG, ex.getMessage());
								}
							}
						});
					}
				}).start();
			} else {
				Helpers.showMessage(this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
			}
		} else {
			Helpers.showMessage(this, "", "Du må fylle ut alle feltene.");
		}
	}
	
	public void onSkipClicked(View view) {
		final Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
        startActivity(intent);
	}

	public void onFriendBtnClicked(View view) {
		login_flipper.setInAnimation(fade_in);
		login_flipper.setOutAnimation(fade_out);
		login_flipper.showNext();
	}
	
	public void onCancelClicked(View view) {
		login_flipper.setInAnimation(fade_in);
		login_flipper.setOutAnimation(fade_out);
		login_flipper.showPrevious();
	}
	
	private List<Car> getAllCars(int ownerId) {
		List<Car> cars = new ArrayList<Car>();
		
		String jsonString = request.send(getString(R.string.api_url) + String.format("car/%s/json/" + System.currentTimeMillis(), ownerId), null, HttpRequest.GET);
		
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
					car.setOwnername(info.getString("ownername"));
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
					
					String filename = Helpers.getLastPathSegment(car.getFilename());
					File f = Helpers.createDirectory("viking/cars");
					
					if(filename != null && f != null && Helpers.isValidDownloadFile(filename)) {
						byte[] data = request.send(info.getString("filename"));
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
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mVikingService = IVikingService.Stub.asInterface(service);
			try {
				mVikingService.registerCallback(callback);
			} catch (RemoteException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mVikingService = null;
			try {
				mVikingService.unregisterCallback(callback);
			} catch(RemoteException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
	};
	
	private IVikingServiceCallback.Stub callback = new IVikingServiceCallback.Stub() {
		
		@Override
		public void onFinishSavingUser() throws RemoteException {
			progress.dismiss();
			Helpers.Constants.mIsLoggedIn = true;
			final Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
			startActivity(intent);
		}
		
		@Override
		public void onFinishRequestingAllCars() throws RemoteException {}
		@Override
		public void onFinishRequestingCarPhones() throws RemoteException {}
		@Override
		public void onFinishRequestingCarFiles() throws RemoteException {}
		@Override
		public void onFinishRequestingCarEvents() throws RemoteException {}
		@Override
		public void onFinishRequestingCallToActions() throws RemoteException {}
		@Override
		public void onFinishRequestingTraffics() throws RemoteException {
			Helpers.Constants.mFinishRequestingTraffics = true;
		}
		@Override
		public void onFinishRequestingNews() throws RemoteException {}
		@Override
		public void onFinishRequestingPhoneCategories() throws RemoteException {
			Helpers.Constants.mFinishRequestingPhoneCategories = true;
		}
	};

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
