package no.incent.viking;

import no.incent.viking.pojo.Traffic;
import no.incent.viking.pojo.CallToAction;
import no.incent.viking.pojo.PhoneCategory;
import no.incent.viking.pojo.News;
import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Car;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;
import no.incent.viking.db.DBTrafficAdapter;
import no.incent.viking.db.DBCallToActionAdapter;
import no.incent.viking.db.DBPhoneCategoryAdapter;
import no.incent.viking.db.DBNewsAdapter;
import no.incent.viking.db.DBUserAdapter;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.service.VikingService;
import no.incent.viking.service.IVikingService;
import no.incent.viking.service.IVikingServiceCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.content.ServiceConnection;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class SplashActivity extends BaseActivity {
	private DBTrafficAdapter trafficAdapter;
	private DBCallToActionAdapter callToActionAdapter;
	private DBPhoneCategoryAdapter phoneCategoryAdapter;
	private DBNewsAdapter newsAdapter;
	private DBUserAdapter userAdapter;
	private DBCarAdapter carAdapter;
	private IVikingService mVikingService;
	private PreferenceWrapper prefsWrapper;
	
	@Override
	protected int contentView() {
		return R.layout.splash;
	}

	@Override
	protected void initialize() {
		request = HttpRequest.getInstance();
		trafficAdapter = new DBTrafficAdapter(this);
		callToActionAdapter = new DBCallToActionAdapter(this);
		phoneCategoryAdapter = new DBPhoneCategoryAdapter(this);
		newsAdapter = new DBNewsAdapter(this);
		userAdapter = new DBUserAdapter(this);
		carAdapter = new DBCarAdapter(this);
		prefsWrapper = new PreferenceWrapper(this);
		
		if(Helpers.createDirectory("viking") != null) {
			Helpers.writeNoMedia();
			Helpers.Constants.mDirectoryCreated = true;
		}
		
		final boolean isLoggedIn = prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN);
		Intent intent = new Intent(this, VikingService.class);
		startService(intent);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
		if(Helpers.isNetworkAvailable(this)) {
			new Thread(new Runnable() {
				public void run() {
					/* -- obselete
					trafficAdapter.open();
					trafficAdapter.deleteAll();
					trafficAdapter.insertAll(getTrafficList());
					trafficAdapter.close();
					
					getCallToActions();
					getPhoneCategories();
					getNews();*/
					
					//getCallToActions();
					
					/*handler.postDelayed(new Runnable() {
						public void run() {
							if(mVikingService != null) {
								try {
									Log.i(TAG, "requesting traffic and news");
									mVikingService.requestRecords();
								} catch (RemoteException ex) {
									Log.e(TAG, ex.getMessage());
								}
							}
						}
					}, 2000);*/
					
					getCallToActions();
					
					try {
						//mVikingService.requestTraffics();
						mVikingService.requestPhoneCategories();
					} catch(RemoteException ex) {
						Log.e(TAG, ex.getMessage());
					}
					
					if(isLoggedIn) {
						User user = userAdapter.getUser(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.OWNER_ID));
						((VikingApplication) getApplicationContext()).setUser(user);
						
						Helpers.Constants.mIsLoggedIn = true;
					}
					
					handler.post(new Runnable() {
						public void run() {
							if(!isLoggedIn) {
								Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
								startActivity(intent);
							} else {
								Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
								startActivity(intent);
							}
						}
					});
					
					/*
					handler.postDelayed(new Runnable() {
						public void run() {
							try {
								mVikingService.requestCallToActions();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
					}, 2000);*/
				}
			}).start();
		} else {
			if(isLoggedIn) {
				User user = userAdapter.getUser(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.OWNER_ID));
				((VikingApplication) getApplicationContext()).setUser(user);
				List<Car> cars = carAdapter.getAllCars(user.getUid());
				((VikingApplication) getApplicationContext()).setCars(cars);
				Helpers.Constants.mIsLoggedIn = true;
			}
			
			handler.postDelayed(new Runnable() {
				public void run() {
					if(!isLoggedIn) {
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
						startActivity(intent);
					}
				}
			}, 2000);
		}
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
	
	private List<Traffic> getTrafficList() {
		List<Traffic> trafficList = new ArrayList<Traffic>();
		
		String trafficString = request.send(getString(R.string.api_url) + "p4webservice/json/" + System.currentTimeMillis(), 
				null, HttpRequest.GET);
		if(trafficString != null) {
			try {
				JSONObject json = new JSONObject(trafficString);
				JSONArray traffics = json.getJSONArray("rtm_messages");
				
				DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
				
				for(int i=0;i<traffics.length();i++) {
					JSONObject jsonTraffic = traffics.getJSONObject(i);
					Traffic traffic = new Traffic();
					
					traffic.setRoadId(jsonTraffic.getInt("road_id"));
					traffic.setRoadName(jsonTraffic.getString("road_name"));
					traffic.setAreaName(jsonTraffic.getString("area_name"));
					traffic.setLatitude(jsonTraffic.getString("latitude"));
					traffic.setLongitude(jsonTraffic.getString("longitude"));
					traffic.setOptionalText(jsonTraffic.getString("optional_text"));
					traffic.setShortText(jsonTraffic.getString("short_text"));
					traffic.setStartTime(jsonTraffic.getString("start_time"));
					traffic.setShortText(jsonTraffic.getString("short_text"));
					traffic.setEndTime(jsonTraffic.getString("end_time"));
					traffic.setTimestamp(dateFormat.parse(jsonTraffic.getString("start_time")).getTime());
					trafficList.add(traffic);
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			} catch (ParseException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return trafficList;
	}
	
	private void getCallToActions() {
		String jsonString = request.send(getString(R.string.api_url) + "calltoaction/android/json", null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray calltoactions = json.getJSONArray("calltoactions");
				
				callToActionAdapter.openWritable();
				callToActionAdapter.deleteAll();
				for(int i=0;i<calltoactions.length();i++) {
					JSONObject calltoaction = calltoactions.getJSONObject(i);
					
					CallToAction callToAction = new CallToAction();
					callToAction.setUid(calltoaction.getInt("uid"));
					callToAction.setDescription(calltoaction.getString("description"));
					callToAction.setFilename(calltoaction.getString("filename"));
					callToAction.setDevice(calltoaction.getString("device"));
					callToAction.setDimension(calltoaction.getString("dimension"));
					byte[] data = request.send(calltoaction.getString("filename"));
					
					String filename = Helpers.getLastPathSegment(callToAction.getFilename());
					File f = Helpers.createDirectory("viking/calltoaction");
					
					if(f != null && filename != null && Helpers.isValidDownloadFile(filename)) {
						f = new File(f, filename);
						Helpers.saveFile(f, data, true);
						callToAction.setPath(f.getAbsolutePath());
					}
					callToActionAdapter.insertCallToAction(callToAction);
				}
				callToActionAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
	}
	
	private void getPhoneCategories() {
		String jsonString = request.send(getString(R.string.api_url) + "phone_categories/1/json", null, HttpRequest.GET);
		List<PhoneCategory> phoneCategories = new ArrayList<PhoneCategory>();
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray phone_categories = json.getJSONArray("phone_categories");
				
				phoneCategoryAdapter.open();
				phoneCategoryAdapter.deleteAll();
				for(int i=0;i<phone_categories.length();i++) {
					PhoneCategory phoneCategory = new PhoneCategory();
					JSONObject phone_category = phone_categories.getJSONObject(i).getJSONObject("phone_category");
					
					phoneCategory.setUid(phone_category.getInt("uid"));
					phoneCategory.setCategory(phone_category.getString("category"));
					phoneCategory.setName(phone_category.getString("name"));
					phoneCategory.setTelephone(phone_category.getString("telephone"));
					phoneCategories.add(phoneCategory);
					//phoneCategoryAdapter.insertPhoneCategory(phoneCategory);
				}
				phoneCategoryAdapter.insertAll(phoneCategories);
				phoneCategoryAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
	}
	
	private void getNews() {
		String jsonString = request.send(getString(R.string.api_url) + "newsfeed/json/" + System.currentTimeMillis(), null, HttpRequest.GET);
		List<News> newsList = new ArrayList<News>();
		
		if(jsonString != null) {
			newsAdapter.open();
			newsAdapter.deleteAll();
			
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray newsfeeds = json.getJSONArray("newsfeeds");
				
				for(int i=0;i<newsfeeds.length();i++) {
					JSONObject news = newsfeeds.getJSONObject(i);
					News n = new News();
					n.setTitle(news.getString("title"));
					n.setUrl(news.getString("url"));
					n.setCreationDate(news.getString("creation_date"));
					n.setPublicationDate(news.getString("publication_date"));
					n.setCategory(news.getString("category"));
					n.setShortText(news.getString("shorttext"));
					n.setFullText(news.getString("fulltext"));
					n.setImage(news.getString("image"));
					n.setMetaDesc(news.getString("metadesc"));
					n.setMetaKey(news.getString("metakey"));
					n.setMetaData(news.getString("metadata"));
					newsList.add(n);
					//newsAdapter.insertNews(n);
				}
				newsAdapter.insertAll(newsList);
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
			
			newsAdapter.close();
		}
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
		public void onFinishSavingUser() throws RemoteException {}
		@Override
		public void onFinishRequestingAllCars() throws RemoteException {}
		@Override
		public void onFinishRequestingCarPhones() throws RemoteException {}
		@Override
		public void onFinishRequestingCarFiles() throws RemoteException {}
		@Override
		public void onFinishRequestingCarEvents() throws RemoteException {}
		
		@Override
		public void onFinishRequestingCallToActions() throws RemoteException {
			if(mVikingService != null) {
				try {
					Log.i(TAG, "requesting traffics");
					mVikingService.requestPhoneCategories();
					//mVikingService.requestTraffics();
				} catch (RemoteException ex) {
					Log.e(TAG, ex.getMessage());
				}
			}
			
			if(prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN)) {
				User user = userAdapter.getUser(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.OWNER_ID));
				((VikingApplication) getApplicationContext()).setUser(user);
				
				Helpers.Constants.mIsLoggedIn = true;
			}
			
			handler.post(new Runnable() {
				public void run() {
					if(!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN)) {
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
						startActivity(intent);
					}
				}
			});
		}
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
}
