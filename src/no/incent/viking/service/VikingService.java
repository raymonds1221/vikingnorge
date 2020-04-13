package no.incent.viking.service;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.SplashActivity;
import no.incent.viking.pojo.CallToAction;
import no.incent.viking.pojo.News;
import no.incent.viking.pojo.PhoneCategory;
import no.incent.viking.pojo.Car;
import no.incent.viking.pojo.CarFile;
import no.incent.viking.pojo.CarPhone;
import no.incent.viking.pojo.CarEvent;
import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.pojo.CarEventSound;
import no.incent.viking.pojo.Traffic;
import no.incent.viking.pojo.User;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;
import no.incent.viking.db.DBCallToActionAdapter;
import no.incent.viking.db.DBPhoneCategoryAdapter;
import no.incent.viking.db.DBNewsAdapter;
import no.incent.viking.db.DBTrafficAdapter;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.db.DBCarFileAdapter;
import no.incent.viking.db.DBCarPhoneAdapter;
import no.incent.viking.db.DBCarEventAdapter;
import no.incent.viking.db.DBCarEventPictureAdapter;
import no.incent.viking.db.DBCarEventSoundAdapter;
import no.incent.viking.db.DBUserAdapter;
import no.incent.viking.widget.MyCarInfo;
import no.incent.viking.widget.MyCarFiles;
import no.incent.viking.widget.MyCarPhone;
import no.incent.viking.widget.MyCarEvents;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.widget.RemoteViews;
import android.util.Log;

public class VikingService extends Service {
	private final String TAG = "VIKING";
	private final RemoteCallbackList<IVikingServiceCallback> callbacks = new RemoteCallbackList<IVikingServiceCallback>();
	private static boolean mIsFinishRequestingPhoneCategories = false;
	private static boolean mIsFinishRequestingNews = false;
	private int notificationId = 21;
	private Object lockObject = new Object();
	private PreferenceWrapper prefsWrapper;
	private NotificationManager notificationManager;
	private Notification notification;
	private File carEventPictureDirectory;
	private File carEventSoundDirectory;
	private HttpRequest request;
	
	@Override
	public void onStart(Intent intent, int startId) {
		request = HttpRequest.getInstance();
	}
	
	@Override
	public void onCreate() {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.upload_notification);
		contentView.setProgressBar(R.id.progress, 100, 0, true);
		notification = new Notification(R.drawable.ic_stat_notify_viking, "Viking - Registrerer bruker", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = contentView;
		notification.contentIntent = contentIntent;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private IVikingService.Stub mBinder = new IVikingService.Stub() {
		@Override
		public void requestCallToActions() throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					VikingService.this.requestCallToActions();
					
					int n = callbacks.beginBroadcast();
					for(int i=0;i<n;i++) {
						try {
							callbacks.getBroadcastItem(i).onFinishRequestingCallToActions();
						} catch(RemoteException ex) {
							Log.e(TAG, ex.getMessage());
						}
					}
					callbacks.finishBroadcast();
				}
			}).start();
		}
		
		@Override
		public void uploadRecords() throws RemoteException {
			notificationManager.notify(notificationId, notification);
			VikingService.this.uploadRecords();
		}
		
		@Override
		public void requestTraffics() throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestTraffics();
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingTraffics();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}
		
		@Override
		public void requestTrafficsByRadius(final double latitude, final double longitude) throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestTrafficsByRadius(latitude, longitude);
						
						int n = callbacks.beginBroadcast();
						
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingTraffics();
								
							} catch(RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}

		@Override
		public void requestNews() throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestNews();
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingNews();
							} catch(RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}

		@Override
		public void requestPhoneCategories() throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestPhoneCategories();
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingPhoneCategories();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}
		
		@Override
		public void requestAllCars(final int ownerId) throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						List<Car> cars = VikingService.this.requestAllCars(ownerId);
						((VikingApplication) getApplicationContext()).setCars(cars);
						
						if(cars.size() > 0) {
							((VikingApplication) getApplicationContext()).setActiveCar(cars.get(0));
						}
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingAllCars();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}
		
		@Override
		public void requestMyCarFiles(final int ownerId) throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestMyCarFiles(ownerId);
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingCarFiles();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}

		@Override
		public void requestMyCarPhones(final int ownerId) throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestMyCarPhones(ownerId);
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingCarPhones();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}

		@Override
		public void requestMyCarEvents(final int ownerId) throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						VikingService.this.requestMyCarEvents(ownerId);
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishRequestingCarEvents();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
		}
		
		@Override
		public void saveUser(final User user) throws RemoteException {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						final DBUserAdapter userAdapter = new DBUserAdapter(VikingService.this);
						prefsWrapper = new PreferenceWrapper(VikingService.this);
						
						if(userAdapter.getUser(user.getUid()) != null) {
							userAdapter.updateUser(user);
						} else {
							userAdapter.insertUser(user);
						}
						
						((VikingApplication) getApplicationContext()).setUser(user);
						prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN, true);
						prefsWrapper.setPreferenceIntValue(PreferenceWrapper.OWNER_ID, user.getUid());
						
						int n = callbacks.beginBroadcast();
						for(int i=0;i<n;i++) {
							try {
								callbacks.getBroadcastItem(i).onFinishSavingUser();
							} catch (RemoteException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						callbacks.finishBroadcast();
					}
				}
			}).start();
			
		}
		
		@Override
		public void registerCallback(IVikingServiceCallback callback)
				throws RemoteException {
			callbacks.register(callback);
		}
		
		@Override
		public void unregisterCallback(IVikingServiceCallback callback)
				throws RemoteException {
			callbacks.unregister(callback);
		}
	};
	
	private void requestCallToActions() {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBCallToActionAdapter callToActionAdapter = new DBCallToActionAdapter(this);
		
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
	
	private void requestTraffics() {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBTrafficAdapter trafficAdapter = new DBTrafficAdapter(this);
		List<Traffic> trafficList = new ArrayList<Traffic>();
		
		String trafficString = request.send(getString(R.string.api_url) + "p4webservice/json/" + System.currentTimeMillis(), 
				null, HttpRequest.GET);
		
		if(trafficString != null) {
			try {
				JSONObject json = new JSONObject(trafficString);
				JSONArray traffics = json.getJSONArray("rtm_messages");
				
				DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
				trafficAdapter.openWriteable();
				trafficAdapter.deleteAll();
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
				trafficAdapter.insertAll(trafficList);
				trafficAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			} catch (ParseException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
	}
	
	private void requestTrafficsByRadius(double latitude, double longitude) {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBTrafficAdapter trafficAdapter = new DBTrafficAdapter(this);
		List<Traffic> trafficList = new ArrayList<Traffic>();
		
		String trafficString = request.send(getString(R.string.api_url) + "p4webservice_by_radius/" + latitude + "/" + longitude + "/json/" + System.currentTimeMillis(), 
				null, HttpRequest.GET);
		
		if(trafficString != null) {
			try {
				JSONObject json = new JSONObject(trafficString);
				JSONArray traffics = json.getJSONArray("rtm_messages");
				
				DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
				trafficAdapter.openWriteable();
				trafficAdapter.deleteAll();
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
				trafficAdapter.insertAll(trafficList);
				trafficAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			} catch (ParseException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
	}
	
	private void requestPhoneCategories() {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBPhoneCategoryAdapter phoneCategoryAdapter = new DBPhoneCategoryAdapter(getApplicationContext());
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
	
	private void requestNews() {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBNewsAdapter newsAdapter = new DBNewsAdapter(getApplicationContext());
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
	
	private List<Car> requestAllCars(int ownerId) {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBCarAdapter carAdapter = new DBCarAdapter(this);
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
					car.setStandardTireFront(info.getString("std_tire_front"));
					car.setStandardTireBack(info.getString("std_tire_back"));
					car.setDrivingWheels(info.getString("driving_wheels"));
					car.setTrailerWeightWithBrakes(info.getString("trailer_weight_with_brakes"));
					car.setTrailerWeightWithoutBrakes(info.getString("trailer_weight_without_brakes"));
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
	
	private void requestMyCarFiles(int ownerId) {
		File carFileDirectory = Helpers.createDirectory("viking/carfiles");
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBCarFileAdapter carFileAdapter = new DBCarFileAdapter(this);
		List<CarFile> carFiles = new ArrayList<CarFile>();
		String url  = getString(R.string.api_url) + String.format("car_files/%s/json/%s/2/" + System.currentTimeMillis(), ownerId, System.currentTimeMillis());
		String jsonString = request.send(url, null, HttpRequest.GET);
		Log.i(TAG, "car files: " + jsonString);
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_files = json.getJSONArray("car_files");
				
				carFileAdapter.open();
				carFileAdapter.deleteAll(ownerId);
				
				if(!carFileAdapter.hasDefaultFile()) {
					CarFile carFile = new CarFile();;
					carFile.setUid(-1);
					carFile.setName("Førerkort");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Forerkort");
					
					Drawable d = getResources().getDrawable(R.drawable.vognkort);
					Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					File f = new File(carFileDirectory, "vognkort.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
					
					carFile = new CarFile();
					carFile.setUid(-2);
					carFile.setName("Vognkort");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Vognkort");
					
					d = getResources().getDrawable(R.drawable.forerkort);
					bitmap = ((BitmapDrawable) d).getBitmap();
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					f = new File(carFileDirectory, "forerkort.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
					
					carFile = new CarFile();
					carFile.setUid(-3);
					carFile.setName("Servicehefte");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Servicehefte");
					
					d =  getResources().getDrawable(R.drawable.servicehefte);
					bitmap = ((BitmapDrawable) d).getBitmap();
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					f = new File(carFileDirectory, "servicehefte.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
					
					carFile = new CarFile();
					carFile.setUid(-4);
					carFile.setName("Forsikringsbevis");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Forsikringsbevis");
					
					d = getResources().getDrawable(R.drawable.forsikringsbevis);
					bitmap = ((BitmapDrawable) d).getBitmap();
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					f = new File(carFileDirectory, "forsikringsbevis.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
				} else {
					for(CarFile carFile: carFileAdapter.getAllDefaultCarFile()) {
						carFile.setOwnerId(ownerId);
						carFileAdapter.updateCarFile(carFile);
					}
				}
				
				for(int i=0;i<car_files.length();i++) {
					JSONObject file = car_files.getJSONObject(i).getJSONObject("file");
					CarFile carFile = new CarFile();
					carFile.setUid(file.getInt("uid"));
					carFile.setOwnerId(file.getInt("owner_id"));
					carFile.setName(file.getString("name"));
					carFile.setGallery(file.getString("gallery"));
					carFile.setFilename(file.getString("filename"));
					byte[] data = request.send(carFile.getFilename());
					
					String filename = Helpers.getLastPathSegment(carFile.getFilename());
					File f = new File(carFileDirectory, filename);
					
					if(filename != null && f != null && Helpers.isValidDownloadFile(filename)) {
						Helpers.saveFile(f, data, true);
						carFile.setPath(f.getAbsolutePath());
					}
					
					carFile.setIsDefault("");
					carFileAdapter.insertCarFile(carFile);
				}
				carFiles = carFileAdapter.getAllCarFiles(ownerId);
				carFileAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		} else {
			carFileAdapter.open();
			carFiles = carFileAdapter.getAllCarFiles(ownerId);
			carFileAdapter.close();
		}
	}
	
	private void requestMyCarPhones(int ownerId) {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBCarPhoneAdapter carPhoneAdapter = new DBCarPhoneAdapter(this);
		List<CarPhone> carPhones = new ArrayList<CarPhone>();
		String jsonString = request.send(getString(R.string.api_url) + 
				String.format("car_phones/%s/json/" + System.currentTimeMillis(), ownerId), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_phones = json.getJSONArray("car_phones");
				
				carPhoneAdapter.openWritable();
				carPhoneAdapter.deleteAll(ownerId);
				
				if(!carPhoneAdapter.hasDefaultCarPhones()) {
					CarPhone cp = new CarPhone();
					cp.setUid(-1);
					cp.setOwnerId(ownerId);
					cp.setOwnerId(ownerId);
					cp.setName("Veihjelp");
					cp.setTelephone("06000");
					cp.setIsDefault("yes");
					
					if(carPhoneAdapter.getCarPhone(cp.getUid()) != null)
						carPhoneAdapter.updateCarPhone(cp);
					else
						carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-2);
					cp.setOwnerId(ownerId);
					cp.setOwnerId(ownerId);
					cp.setName("Ambulanse");
					cp.setTelephone("113");
					cp.setIsDefault("yes");
					if(carPhoneAdapter.getCarPhone(cp.getUid()) != null)
						carPhoneAdapter.updateCarPhone(cp);
					else
						carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-3);
					cp.setOwnerId(ownerId);
					cp.setOwnerId(ownerId);
					cp.setName("Politi");
					cp.setTelephone("112");
					cp.setIsDefault("yes");
					if(carPhoneAdapter.getCarPhone(cp.getUid()) != null)
						carPhoneAdapter.updateCarPhone(cp);
					else
						carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-4);
					cp.setOwnerId(ownerId);
					cp.setOwnerId(ownerId);
					cp.setName("Brann");
					cp.setTelephone("110");
					cp.setIsDefault("yes");
					if(carPhoneAdapter.getCarPhone(cp.getUid()) != null)
						carPhoneAdapter.updateCarPhone(cp);
					else
						carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-5);
					cp.setOwnerId(ownerId);
					cp.setOwnerId(ownerId);
					cp.setName("Verksted");
					cp.setIsDefault("yes");
					if(carPhoneAdapter.getCarPhone(cp.getUid()) != null)
						carPhoneAdapter.updateCarPhone(cp);
					else
						carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-6);
					cp.setOwnerId(ownerId);
					cp.setOwnerId(ownerId);
					cp.setName("Forsikring");
					cp.setIsDefault("yes");
					if(carPhoneAdapter.getCarPhone(cp.getUid()) != null)
						carPhoneAdapter.updateCarPhone(cp);
					else
						carPhoneAdapter.insertCarPhone(cp);
				} else {
					carPhoneAdapter.openReadable();
					for(CarPhone carPhone: carPhoneAdapter.getAllDefaultCarPhones()) {
						carPhone.setOwnerId(ownerId);
						carPhoneAdapter.updateCarPhone(carPhone);
					}
					carPhones.addAll(carPhoneAdapter.getAllDefaultCarPhones());
					carPhoneAdapter.close();
				}
				
				for(int i=0;i<car_phones.length();i++) {
					JSONObject phone = car_phones.getJSONObject(i).getJSONObject("phone");
					CarPhone carPhone = new CarPhone();
					carPhone.setUid(phone.getInt("uid"));
					carPhone.setOwnerId(phone.getInt("owner_id"));
					carPhone.setCategory(phone.getString("category"));
					carPhone.setName(phone.getString("name"));
					carPhone.setTelephone(phone.getString("telephone"));
					carPhone.setIsDefault("");
					
					if(carPhoneAdapter.getCarPhone(carPhone.getUid()) != null) {
						carPhoneAdapter.updateCarPhone(carPhone);
					} else {
						carPhoneAdapter.insertCarPhone(carPhone);
					}
					carPhones.add(carPhone);
				}
				carPhoneAdapter.close();
				carPhoneAdapter.openReadable();
				carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
				carPhoneAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		} else {
			carPhoneAdapter.openReadable();
			carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
			carPhoneAdapter.close();
		}
	}
	
	private void requestMyCarEvents(int ownerId) {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		DBCarEventAdapter carEventAdapter = new DBCarEventAdapter(this);
		DBCarEventPictureAdapter carEventPictureAdapter = new DBCarEventPictureAdapter(this);
		DBCarEventSoundAdapter carEventSoundAdapter = new DBCarEventSoundAdapter(this);
		
		carEventPictureDirectory = Helpers.createDirectory("viking/carevents/picture");
		carEventSoundDirectory = Helpers.createDirectory("viking/carevents/sound");
		
		List<CarEvent> carEvents = new ArrayList<CarEvent>();
		String jsonString = request.send(getString(R.string.api_url) + 
				String.format("car_events/%s/json", ownerId), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_events = json.getJSONArray("car_events");
				
				carEventAdapter.openWritable();
				carEventAdapter.deleteAll(ownerId);
				carEventPictureAdapter.deleteAll(ownerId);
				carEventSoundAdapter.deleteAll(ownerId);
				
				List<CarEventPicture> carEventPictures = new ArrayList<CarEventPicture>();
				List<CarEventSound> carEventSounds = new ArrayList<CarEventSound>();
				
				for(int i=0;i<car_events.length();i++) {
					JSONObject event = car_events.getJSONObject(i).getJSONObject("event");
					CarEvent carEvent = new CarEvent();
					carEvent.setUid(event.getInt("uid"));
					carEvent.setOwnerId(event.getInt("owner_id"));
					carEvent.setName(event.getString("name"));
					carEvent.setRegistration(event.getString("registration"));
					carEvent.setEvent(event.getString("event"));
					carEvent.setPlace(event.getString("place"));
					carEvent.setDateTime(event.getString("event_datetime"));
					carEvent.setNote(event.getString("note"));
					carEvent.setDateCreated(event.getString("datecreated"));
					carEventAdapter.insertCarEvent(carEvent);
					
					carEvents.add(carEvent);
					
					carEventPictures.addAll(getAllCarEventPictures(ownerId, carEvent.getUid()));
					carEventSounds.addAll(getAllCarEventSounds(ownerId, carEvent.getUid()));
				}
				
				carEventPictureAdapter.insertAll(carEventPictures);
				carEventSoundAdapter.insertAll(carEventSounds);
				carEventAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, "car event error: " + ex.getMessage());
			}
		} else {
			carEventAdapter.openReadable();
			carEvents = carEventAdapter.getAllCarEvents(ownerId);
			carEventAdapter.close();
		}
	}
	
	private List<CarEventPicture> getAllCarEventPictures(int ownerId, int eventId) {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		List<CarEventPicture> carEventPictures = new ArrayList<CarEventPicture>();
		
		String jsonString = request.send(getString(R.string.api_url) + 
				String.format("car_events_pictures/%s/json/%s/" + System.currentTimeMillis(), ownerId, eventId), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_events_pictures = json.getJSONArray("car_events_pictures");
				
				for(int i=0;i<car_events_pictures.length();i++) {
					JSONObject car_events_picture = car_events_pictures.getJSONObject(i).getJSONObject("car_events_picture");
					CarEventPicture carEventPicture = new CarEventPicture();
					
					carEventPicture.setUid(car_events_picture.getInt("uid"));
					carEventPicture.setOwnerId(car_events_picture.getInt("owner_id"));
					carEventPicture.setEventId(car_events_picture.getInt("event_id"));
					carEventPicture.setName(car_events_picture.getString("name"));
					carEventPicture.setFilename(car_events_picture.getString("filename"));
					carEventPicture.setFileType(car_events_picture.getString("filetype"));
					
					byte[] data = request.send(carEventPicture.getFilename());
					String filename = Helpers.getLastPathSegment(carEventPicture.getFilename());
					File f = new File(carEventPictureDirectory, filename);
					
					if(filename != null && Helpers.isValidDownloadFile(filename)) {
						Helpers.saveFile(f, data, true);
						carEventPicture.setPath(f.getAbsolutePath());
					}
					carEventPictures.add(carEventPicture);
				}
			} catch(JSONException ex) {
				ex.printStackTrace();
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return carEventPictures;
	}
	
	private List<CarEventSound> getAllCarEventSounds(int ownerId, int eventId) {
		//HttpRequest request = HttpRequest.getInstance();
		if(request == null)
			request = HttpRequest.getInstance();
		List<CarEventSound> carEventSounds = new ArrayList<CarEventSound>();
		
		String jsonString = request.send(getString(R.string.api_url) + 
				String.format("car_events_sound/%s/json/%s/" + System.currentTimeMillis(), ownerId, eventId), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_events_sounds = json.getJSONArray("car_events_sounds");
				
				for(int i=0;i<car_events_sounds.length();i++) {
					JSONObject car_events_sound = car_events_sounds.getJSONObject(i).getJSONObject("car_events_sound");
					CarEventSound carEventSound = new CarEventSound();
					
					carEventSound.setUid(car_events_sound.getInt("uid"));
					carEventSound.setOwnerId(car_events_sound.getInt("owner_id"));
					carEventSound.setEventId(car_events_sound.getInt("event_id"));
					carEventSound.setName(car_events_sound.getString("name"));
					carEventSound.setFilename(car_events_sound.getString("filename"));
					carEventSound.setFileType(car_events_sound.getString("filetype"));
					//carEventSound.setSound(request.send(carEventSound.getFilename()));
					byte[] data = request.send(carEventSound.getFilename());
					String filename = Helpers.getLastPathSegment(carEventSound.getFilename());
					File f = new File(carEventSoundDirectory, filename);
					
					if(data != null && Helpers.isValidDownloadFile(filename)) {
						Helpers.saveFile(f, data, true);
						carEventSound.setPath(f.getAbsolutePath());
					}
					carEventSounds.add(carEventSound);
				}
				
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return carEventSounds;
	}
	
	
	private void uploadRecords() {
		prefsWrapper = new PreferenceWrapper(this);
		
		final int ownerId = prefsWrapper.getPreferenceIntValue(PreferenceWrapper.OWNER_ID);
		final List<Car> cars = MyCarInfo.getAllTempCars();
		final List<CarFile> carFiles = MyCarFiles.getAllTempCarFiles();
		final List<CarPhone> carPhones = MyCarPhone.getAllTempCarPhones();
		final List<CarEvent> carEvents = MyCarEvents.getAllTempCarEvents();
		final List<CarEventPicture> carEventPictures = MyCarEvents.getAllTempCarEventPictures();
		final List<CarEventSound> carEventSounds = MyCarEvents.getAllTempCarEventSounds();
		
		new Thread(new Runnable() {
			public void run() {
				for(Car car: cars) {
					car.setOwnerId(ownerId);
					saveCar(car);
				}
				
				for(CarFile carFile: carFiles) {
					carFile.setOwnerId(ownerId);
					saveCarFile(carFile);
				}
				
				for(CarPhone carPhone: carPhones) {
					carPhone.setOwnerId(ownerId);
					saveCarPhone(carPhone);
				}
				
				for(CarEvent carEvent: carEvents) {
					carEvent.setOwnerId(ownerId);
					saveCarEvent(carEvent, carEventPictures, carEventSounds);
				}
				
				handler.sendEmptyMessage(1);
			}
		}).start();
	}
	
	public static boolean isFinishRequestingPhoneCategories() {
		return mIsFinishRequestingPhoneCategories;
	}
	
	public static boolean isFinishRequestingNews() {
		return mIsFinishRequestingNews;
	}
	
	private boolean saveCar(Car car) {
		HttpRequest request = HttpRequest.getInstance();
		int ownerId = prefsWrapper.getPreferenceIntValue(PreferenceWrapper.OWNER_ID);
		
		File f = null;
		
		if(car.getPath() != null) {
			f = new File(car.getPath());
		}
		String result = null;
		
		if(f != null && f.exists()) {
			Map<String, String> queries = new HashMap<String, String>();
			queries.put("owner_id", String.valueOf(ownerId));
			queries.put("ownername", "");
			queries.put("registration_number", car.getRegistrationNumber());
			queries.put("chassis_number", car.getChassisNumber());
			queries.put("car_reg_year", car.getCarRegYear());
			queries.put("reg_first_time_in_norway", car.getRegFirstTimeInNorway());
			queries.put("brand_code", car.getBrandCode());
			queries.put("car_model", car.getCarModel());
			queries.put("engine_performance", car.getEnginePerformance());
			queries.put("displacement", car.getDisplacement());
			queries.put("fuel_type", car.getFuelType());
			queries.put("length", car.getLength());
			queries.put("width", car.getWidth());
			queries.put("weight", car.getWeight());
			queries.put("total_weight", car.getTotalWeight());
			queries.put("colour", car.getColour());
			queries.put("co2_emission", car.getCo2Emission());
			
			result = request.sendMultipart(getString(R.string.api_url) + "save_car", f, queries);
		} else {
			List<NameValuePair> queries = new ArrayList<NameValuePair>();
			queries.add(new BasicNameValuePair("owner_id", String.valueOf(ownerId)));
			queries.add(new BasicNameValuePair("ownername", ""));
			queries.add(new BasicNameValuePair("registration_number", car.getRegistrationNumber()));
			queries.add(new BasicNameValuePair("chassis_number", car.getChassisNumber()));
			queries.add(new BasicNameValuePair("car_reg_year", car.getCarRegYear()));
			queries.add(new BasicNameValuePair("reg_first_time_in_norway", car.getRegFirstTimeInNorway()));
			queries.add(new BasicNameValuePair("brand_code", car.getBrandCode()));
			queries.add(new BasicNameValuePair("car_model", car.getCarModel()));
			queries.add(new BasicNameValuePair("engine_performance", car.getEnginePerformance()));
			queries.add(new BasicNameValuePair("displacement", car.getDisplacement()));
			queries.add(new BasicNameValuePair("fuel_type", car.getFuelType()));
			queries.add(new BasicNameValuePair("length", car.getLength()));
			queries.add(new BasicNameValuePair("width", car.getWidth()));
			queries.add(new BasicNameValuePair("weight", car.getWeight()));
			queries.add(new BasicNameValuePair("total_weight", car.getTotalWeight()));
			queries.add(new BasicNameValuePair("colour", car.getColour()));
			queries.add(new BasicNameValuePair("co2_emission", car.getCo2Emission()));
			
			result = request.send(getString(R.string.api_url) + "save_car", queries, HttpRequest.POST);
		}
		
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean saveCarFile(CarFile carFile) {
		HttpRequest request = HttpRequest.getInstance();
		
		File f  = new File(carFile.getPath());
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("owner_id", String.valueOf(carFile.getOwnerId()));
		queries.put("name", carFile.getName());
		queries.put("gallery", carFile.getGallery());
		
		String result = request.sendMultipart(getString(R.string.api_url) + "save_car_files", f, queries);
		
		try {
			byte[] data = Helpers.toByteArray(new FileInputStream(f));
			
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return false;
	}
	
	private boolean saveCarPhone(CarPhone carPhone) {
		HttpRequest request = HttpRequest.getInstance();
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(carPhone.getOwnerId())));
		queries.add(new BasicNameValuePair("category", carPhone.getCategory()));
		queries.add(new BasicNameValuePair("name", carPhone.getName()));
		queries.add(new BasicNameValuePair("telephone", carPhone.getTelephone()));
		
		String result = request.send(getString(R.string.api_url) + "save_car_phones", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean saveCarEvent(CarEvent carEvent, List<CarEventPicture> tempCarEventPictures, List<CarEventSound> tempCarEventSounds) {
		HttpRequest request = HttpRequest.getInstance();
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
				carEvent.setUid(Integer.parseInt(Helpers.parseXMLNode(result, "uid")));
				
				for(CarEventPicture carEventPicture: tempCarEventPictures) {
					carEventPicture.setEventId(carEvent.getUid());
					carEventPicture.setOwnerId(carEvent.getOwnerId());
					saveCarEventPictures(carEventPicture, carEvent.getUid());
				}
				for(CarEventSound carEventSound: tempCarEventSounds) {
					carEventSound.setEventId(carEvent.getUid());
					carEventSound.setOwnerId(carEvent.getOwnerId());
					saveCarEventSound(carEventSound, carEvent.getUid());
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean saveCarEventPictures(CarEventPicture carEventPicture, int eventId) {
		HttpRequest request = HttpRequest.getInstance();
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("event_id", String.valueOf(eventId));
		queries.put("owner_id", String.valueOf(carEventPicture.getOwnerId()));
		queries.put("name", carEventPicture.getName());
		
		File f = new File(carEventPicture.getPath());
		
		String result = request.sendMultipart(getString(R.string.api_url) + "save_car_events_pictures", f, queries);
		
		if(result != null) {
			carEventPicture.setEventId(eventId);
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		return false;
	}
	
	private boolean saveCarEventSound(CarEventSound carEventSound, int eventId) {
		HttpRequest request = HttpRequest.getInstance();
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("event_id", String.valueOf(eventId));
		queries.put("owner_id", String.valueOf(carEventSound.getOwnerId()));
		queries.put("name", carEventSound.getName());
		
		File f = new File(carEventSound.getPath());
		
		String result = request.sendMultipart(getString(R.string.api_url) + "save_car_events_sound", f, queries);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1) {
				notificationManager.cancel(notificationId);
				PendingIntent contentIntent = PendingIntent.getActivity(VikingService.this, 0, new Intent(VikingService.this, SplashActivity.class), 0);
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.defaults |= Notification.DEFAULT_VIBRATE;
				notification.setLatestEventInfo(VikingService.this, "Viking", "Done registering user", contentIntent);
				notificationManager.notify(notificationId, notification);
			}
		}
	};
}
