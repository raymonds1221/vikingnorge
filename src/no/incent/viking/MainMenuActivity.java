package no.incent.viking;

import java.io.File; 
import java.io.FileInputStream; 
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import no.incent.viking.pojo.Car;
import no.incent.viking.pojo.CarAccess;
import no.incent.viking.pojo.CarFile;
import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.pojo.Traffic;
import no.incent.viking.pojo.User;
import no.incent.viking.pojo.SharedCar;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.db.DBCarFileAdapter;
import no.incent.viking.adapter.CarFilePictureViewAdapter;
import no.incent.viking.adapter.CarAccessAdapter;
import no.incent.viking.service.VikingService;
import no.incent.viking.service.IVikingService;
import no.incent.viking.service.IVikingServiceCallback;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.MyCarEntity;
import no.incent.viking.widget.MyCarPhone;
import no.incent.viking.widget.Panel;
import no.incent.viking.widget.MyCarInfo;
import no.incent.viking.widget.MyCarFiles;
import no.incent.viking.widget.MyCarEvents;
import no.incent.viking.widget.DamageReport;
import no.incent.viking.widget.DamageReportV2;
import no.incent.viking.widget.Login;
import no.incent.viking.widget.InitialPage;
import no.incent.viking.widget.OwnerInfo;
import no.incent.viking.widget.EUControl;
import no.incent.viking.util.NotLoggedInCallback;
import no.incent.viking.util.PreferenceWrapper;

import android.app.AlertDialog;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Process;
import android.os.Build;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.Contacts;
import android.location.LocationManager;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.View;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ViewFlipper;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.ListView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.flurry.android.FlurryAgent;

public class MainMenuActivity extends BaseActivity implements DamageReport.DamageReportCallback, 
			Login.OnLoginListener, MyCarFiles.OnMyCarFilesCallback, InitialPage.OnInitialPageCallback,
			OwnerInfo.OnOwnerInfoCallback, NotLoggedInCallback, MyCarEvents.OnMyCarEventsCallback, no.incent.viking.widget.Traffic.OnTrafficListener {
	private Panel topPanel;
	private ViewFlipper main_flipper;
	private ViewFlipper mainmenu_flipper;
	private ViewFlipper owner_info_flipper;
	private ViewFlipper mycar_flipper;
	private ViewFlipper panel_flipper;
	private TabHost mycar_tabhost;
	private final int MYCARINFO_CAMERA = 1;
	private final int MYCARINFO_GALLERY = 2;
	private final int ACCESS_PICK_CONTACT = 3;
	private final int MYCARFILE_CAMERA = 4;
	private final int MYCARFILE_GALLERY = 5;
	private final int MYCAREVENT_CAMERA = 6;
	private final int DAMAGE_REPORT_CAMERA = 7;
	private final int ORDER_ASSISTANCE = 8;
	private final int MYCARFILE_PICTURE_VIEW_CAMERA = 9;
	private Uri mycarinfo_image_camera;
	private DBCarAdapter carAdapter;
	private DBCarFileAdapter carFileAdapter;
	private MyCarInfo mycar_info;
	private Uri car_image_uri;
	private Uri car_file_uri;
	private Uri car_event_picture_uri;
	private Uri damage_report_picture_uri;
	private Uri car_file_picture_uri;
	private final int PAGE_INITIAL = 0;
	private final int PAGE_MYCAR = 1;
	private final int PAGE_OWNERINFO = 2;
	private final int PAGE_FRIEND = 3;
	private final int PAGE_TRAFFIC = 4;
	private final int PAGE_EU_CONTROL = 5;
	private final int PAGE_GAME = 6;
	private final int PAGE_DAMAGE_REPORT = 7;
	private final int PAGE_FIRST_AID = 8;
	private final int PAGE_INFO = 9;
	private int mPageShowing = PAGE_INITIAL;
	private int activeDamageReportPicture;
	private CarFile activeCarFile;
	private String activeGallery;
	private List<CarFile> carFilesPictureView;
	private Gallery file_picture_gallery;
	private PreferenceWrapper prefsWrapper;
	private IVikingService mVikingService;
	private File fileCarImage = null;
	private File fileCarFileImage = null;
	
	@Override
	protected int contentView() {
		return R.layout.mainmenu;
	}

	@Override
	protected void initialize() {
		topPanel = (Panel) findViewById(R.id.topPanel);
		topPanel.setOpen(true, false);
		
		prefsWrapper = new PreferenceWrapper(this);
		
		main_flipper = (ViewFlipper) findViewById(R.id.main_flipper);
		mainmenu_flipper = (ViewFlipper) findViewById(R.id.mainmenu_flipper);
		owner_info_flipper = (ViewFlipper) findViewById(R.id.owner_info_flipper);
		mycar_flipper = (ViewFlipper) findViewById(R.id.mycar_flipper);
		panel_flipper = (ViewFlipper) findViewById(R.id.panel_flipper);
		
		Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		
		mainmenu_flipper.setInAnimation(fade_in);
		mainmenu_flipper.setOutAnimation(fade_out);
		
		owner_info_flipper.setInAnimation(fade_in);
		owner_info_flipper.setOutAnimation(fade_out);
		
		mycar_tabhost = (TabHost) findViewById(R.id.mycar_tabhost);
		mycar_tabhost.setup();
		
		setupTab(mycar_tabhost, "Min Bil", R.id.mycar_info);
		setupTab(mycar_tabhost, "Filer", R.id.mycar_files);
		setupTab(mycar_tabhost, "Telefon", R.id.mycar_phone);
		setupTab(mycar_tabhost, "Hendelser", R.id.mycar_events);
		
		carAdapter = new DBCarAdapter(this);
		carFileAdapter = new DBCarFileAdapter(this);
		mycar_info = (MyCarInfo) findViewById(R.id.mycar_info);
		file_picture_gallery = (Gallery) findViewById(R.id.picture_view).findViewById(R.id.file_picture_gallery);
		
		findViewById(R.id.owner_close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(topPanel.isShown()) {
					topPanel.setOpen(false, true);
				}
				mainmenu_flipper.setDisplayedChild(1);
			}
		});
		
		findViewById(R.id.mycar_files).findViewById(R.id.new_img_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alert = new AlertDialog.Builder(MainMenuActivity.this)
										.setCancelable(true)
										.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
											}
										})
										.setItems(R.array.car_files_menu, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												switch(which) {
													case 0:
														car_file_uri = startCamera(MYCARFILE_CAMERA);
														break;
													case 1:
														startGallery(MYCARFILE_GALLERY);
														break;
												}
											}
										}).create();
				alert.show();
			}
		});
		
		findViewById(R.id.mycar_files).findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = ((TextView)findViewById(R.id.mycar_files).findViewById(R.id.file_name_field)).getText().toString();
				
				if(name.equals("")) {
					Helpers.showMessage(MainMenuActivity.this, "", "Gi filen ett navn og trykk lagrekanppen til høyre");
					return;
				}
				
				new Thread(new Runnable() {
					public void run() {
						synchronized(Helpers.lockObject) {
							if(fileCarFileImage != null && name != null) {
								MainMenuActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).showNext();
									}
								});
								int ownerId = 0;
								if(((VikingApplication) getApplicationContext()).getUser() != null) {
									ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
								}
								CarFile carFile = new CarFile();
								carFile.setOwnerId(ownerId);
								carFile.setName(name);
								carFile.setGallery(name);
								
								if(Helpers.Constants.mIsLoggedIn) {
									if(saveCarFile(carFile, fileCarFileImage)) {
										car_file_uri = null;
										fileCarFileImage = null;
										
										handler.post(new Runnable() {
											public void run() {
												((TextView)findViewById(R.id.mycar_files).findViewById(R.id.file_name_field)).setText("");
												((MyCarFiles) findViewById(R.id.mycar_files)).updateCarFileList();
												((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).setDisplayedChild(0);
												Animation animation = AnimationUtils.loadAnimation(MainMenuActivity.this, R.anim.fade_out);
												findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setAnimation(animation);
												findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setVisibility(View.GONE);
											}
										});
									} else {
										fileCarFileImage = null;
										
										handler.post(new Runnable() {
											public void run() {
												((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).setDisplayedChild(0);
												Helpers.showMessage(MainMenuActivity.this, "", "Unable to save file");
												Animation animation = AnimationUtils.loadAnimation(MainMenuActivity.this, R.anim.fade_out);
												findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setAnimation(animation);
												findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setVisibility(View.GONE);
											}
										});
									}
								} else {
									/*String[] projection = {MediaStore.Images.Media.DATA};
									Cursor cursor = managedQuery(car_file_uri, projection, null, null, null);
									cursor.moveToNext();
									String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));*/
									
									carFile.setPath(fileCarFileImage.getAbsolutePath());
									carFileAdapter.insertCarFile(carFile);
									car_file_uri = null;
									fileCarFileImage = null;
									
									handler.post(new Runnable() {
										public void run() {
											((TextView)findViewById(R.id.mycar_files).findViewById(R.id.file_name_field)).setText("");
											((MyCarFiles) findViewById(R.id.mycar_files)).updateCarFileList();
											((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).setDisplayedChild(0);
											Animation animation = AnimationUtils.loadAnimation(MainMenuActivity.this, R.anim.fade_out);
											findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setAnimation(animation);
											findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setVisibility(View.GONE);
										}
									});
								}
							} else {
								handler.post(new Runnable() {
									public void run() {
										Helpers.showMessage(MainMenuActivity.this, "", "Please select a picture / Enter a name");
									}
								});
							}
						}
					}
				}).start();
			}
		});
		
		/*
		startService(intent);
		MyCarEntity mycarfiles = (MyCarEntity) findViewById(R.id.mycar_files);
		MyCarEntity mycarphone = (MyCarEntity) findViewById(R.id.mycar_phone);
		MyCarEntity mycarevent = (MyCarEntity) findViewById(R.id.mycar_events);
		
		mycarfiles.setSuccessor(mycarphone);
		mycarphone.setSuccessor(mycarevent);
		
		mycarfiles.load();
		*/
		//initSharedCar();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		if(Helpers.Constants.mIsLoggedIn) {
			if(((VikingApplication) getApplicationContext()).getUser() != null)
				((MyCarInfo) findViewById(R.id.mycar_info)).showImageProgress();
			((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).setDisplayedChild(1);
			((ViewFlipper) findViewById(R.id.mycar_phone).findViewById(R.id.mycar_phone_list_flipper)).setDisplayedChild(1);
			((ViewFlipper) findViewById(R.id.mycar_events).findViewById(R.id.mycar_events_list_flipper)).setDisplayedChild(1);
		}
		if(!Helpers.Constants.mFinishRequestingTraffics) {
			((ViewFlipper) findViewById(R.id.initialpage).findViewById(R.id.top_news_list_flipper)).setDisplayedChild(1);
		}
		((ViewFlipper) findViewById(R.id.news).findViewById(R.id.news_list_flipper)).setDisplayedChild(1);
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Intent intent = new Intent(this, VikingService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
		handler.postDelayed(new Runnable() {
			public void run() {
				if(mVikingService != null && !Helpers.Constants.mMyCarRequested) {
					/*int ownerId;
					if(((VikingApplication) getApplicationContext()).getUser() != null) {
						ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
					}*/
					
					if(!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.HAS_USE_LOCATION)) {
						AlertDialog alert = new AlertDialog.Builder(MainMenuActivity.this)
							.setTitle("Viking+ vil bruke din posisjon")
							.setMessage("Aksepter for å begrense trafikkmeldingene til ditt område.")
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
									Location location = null;
									
									if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
										location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
										
										if(location == null) {
											location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
										}
									} else {
										location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
									}
									
									int ownerId = 0;
									if(((VikingApplication) getApplicationContext()).getUser() != null) {
										ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
									}
									
									try {
										mVikingService.requestTrafficsByRadius(location.getLatitude(), location.getLongitude());
										prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.HAS_USE_LOCATION, true);
										prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.USE_LOCATION, true);
										
										if(Helpers.Constants.mIsLoggedIn) {
											mVikingService.requestAllCars(ownerId);
											mVikingService.requestMyCarFiles(ownerId);
											mVikingService.requestMyCarPhones(ownerId);
											mVikingService.requestMyCarEvents(ownerId);
										} else {
											((MyCarFiles) findViewById(R.id.mycar_files)).load();
											((MyCarEntity) findViewById(R.id.mycar_phone)).load();
											((MyCarEvents) findViewById(R.id.mycar_events)).load();
										}
										mVikingService.requestNews();
									} catch(RemoteException ex) {
										ex.printStackTrace();
									}
									
								}
							})
							.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									int ownerId = 0;
									if(((VikingApplication) getApplicationContext()).getUser() != null) {
										ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
									}
									
									try {
										mVikingService.requestTraffics();
										prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.HAS_USE_LOCATION, true);
										prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.USE_LOCATION, false);
										
										if(Helpers.Constants.mIsLoggedIn) {
											mVikingService.requestAllCars(ownerId);
											mVikingService.requestMyCarFiles(ownerId);
											mVikingService.requestMyCarPhones(ownerId);
											mVikingService.requestMyCarEvents(ownerId);
										} else {
											((MyCarFiles) findViewById(R.id.mycar_files)).load();
											((MyCarEntity) findViewById(R.id.mycar_phone)).load();
											((MyCarEvents) findViewById(R.id.mycar_events)).load();
										}
										mVikingService.requestNews();
									} catch(RemoteException ex) {
										ex.printStackTrace();
									}
								}
							}).create();
						alert.show();
					} else {
						int ownerId = 0;
						if(((VikingApplication) getApplicationContext()).getUser() != null) {
							ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
						}
						
						if(prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.USE_LOCATION)) {
							try {
								LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
								Location location = null;
								
								if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
									location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
									
									if(location == null) {
										location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
									}
								} else {
									location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								}
								
								mVikingService.requestTrafficsByRadius(location.getLatitude(), location.getLongitude());
								
								if(Helpers.Constants.mIsLoggedIn) {
									mVikingService.requestAllCars(ownerId);
									mVikingService.requestMyCarFiles(ownerId);
									mVikingService.requestMyCarPhones(ownerId);
									mVikingService.requestMyCarEvents(ownerId);
								} else {
									((MyCarFiles) findViewById(R.id.mycar_files)).load();
									((MyCarEntity) findViewById(R.id.mycar_phone)).load();
									((MyCarEvents) findViewById(R.id.mycar_events)).load();
								}
								mVikingService.requestNews();
							} catch(RemoteException ex) {
								ex.printStackTrace();
							}
						} else {
							try {
								mVikingService.requestTraffics();
								
								if(Helpers.Constants.mIsLoggedIn) {
									mVikingService.requestAllCars(ownerId);
									mVikingService.requestMyCarFiles(ownerId);
									mVikingService.requestMyCarPhones(ownerId);
									mVikingService.requestMyCarEvents(ownerId);
								} else {
									((MyCarFiles) findViewById(R.id.mycar_files)).load();
									((MyCarEntity) findViewById(R.id.mycar_phone)).load();
									((MyCarEvents) findViewById(R.id.mycar_events)).load();
								}
								mVikingService.requestNews();
							} catch(RemoteException ex) {
								ex.printStackTrace();
							}
						}
					}
					
					/*
					try {
						Log.i(TAG, "requesting car records");
						if(Helpers.Constants.mIsLoggedIn) {
							mVikingService.requestAllCars(ownerId);
							mVikingService.requestMyCarFiles(ownerId);
							mVikingService.requestMyCarPhones(ownerId);
							mVikingService.requestMyCarEvents(ownerId);
						} else {
							((MyCarFiles) findViewById(R.id.mycar_files)).load();
							((MyCarEntity) findViewById(R.id.mycar_phone)).load();
							((MyCarEvents) findViewById(R.id.mycar_events)).load();
						}
						mVikingService.requestNews();
					} catch (RemoteException ex) {
						Log.e(TAG, ex.getMessage());
					}*/
				} else {
					Log.i(TAG, "requesting failed");
				}
			}
		}, 2000);
	}
	
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "viking destroyed");
		unbindService(serviceConnection);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_application_key));
		FlurryAgent.setLogEnabled(true);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	private void setupTab(TabHost mTabHost, final String tag, final int viewId) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(viewId);
		mTabHost.addTab(setContent);
	}
	
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
	public View createOwnerTab(Context context, String text, int background) {
		View view = LayoutInflater.from(context).inflate(R.layout.car_owner_tab, null);
		view.setBackgroundResource(background);
		TextView car_owner_tab_text = (TextView) view.findViewById(R.id.car_owner_tab_text);
		car_owner_tab_text.setText(text);
		return view;
	}
	
	public void onAsstBtnClick(View view) {
		FlurryAgent.logEvent("OrderAssistanceEvent");
		Intent intent = new Intent(this, OrderAssistance.class);
		startActivityForResult(intent, ORDER_ASSISTANCE);
		//onPageItemClicked();
	}
	
	public void onDamageBtnClick(View view) {
		/*mPageShowing = PAGE_DAMAGE_REPORT;
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(1);*/
		FlurryAgent.logEvent("DamageReportEvent");
		Intent intent = new Intent(this, DamageReportActivity.class);
		startActivityForResult(intent, Helpers.Constants.PAGE_DAMAGEREPORT);
		//onPageItemClicked();
	}
	
	public void onFirstAidBtnClick(View view) {
		FlurryAgent.logEvent("FirstAidEvent");
		mPageShowing = PAGE_FIRST_AID;
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(2);
		//onPageItemClicked();
	}
	
	public void onCloseDamageReportClicked(View view) {
		mPageShowing = PAGE_INITIAL;
		main_flipper.setInAnimation(this, R.anim.slide_right_in);
		main_flipper.setOutAnimation(this, R.anim.slide_right_out);
		main_flipper.setDisplayedChild(0);
		//onPageItemClicked();
	}
	
	public void onInfoBtnClick(View view) {
		mPageShowing = PAGE_INFO;
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(4);
		//onPageItemClicked();
	}
	
	public void onMyCarClicked(View view) {
		FlurryAgent.logEvent("MyCarEvent");
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED)) {
			panel_flipper.setDisplayedChild(2);
		} else {
			panel_flipper.setDisplayedChild(1);
			if(topPanel.isShown()) {
				topPanel.setOpen(false, true);
			}
		}
		for(int i=0;i<((LinearLayout) findViewById(R.id.bottomMenu)).getChildCount();i++) {
			((LinearLayout)findViewById(R.id.bottomMenu)).getChildAt(i).setBackgroundDrawable(null);
		}
		view.setBackgroundResource(R.drawable.menu_highlight);
		mycar_flipper.setDisplayedChild(0);
		mainmenu_flipper.setDisplayedChild(PAGE_MYCAR);
		mPageShowing = PAGE_MYCAR;
		//onPageItemClicked();
	}
	
	public void onCarOwnerInfoClicked(View view) {
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED)) {
			panel_flipper.setDisplayedChild(2);
		} else {
			panel_flipper.setDisplayedChild(1);
			if(topPanel.isShown()) {
				topPanel.setOpen(false, true);
			}
		}
		for(int i=0;i<((LinearLayout) findViewById(R.id.bottomMenu)).getChildCount();i++) {
			((LinearLayout)findViewById(R.id.bottomMenu)).getChildAt(i).setBackgroundDrawable(null);
		}
		view.setBackgroundResource(R.drawable.menu_highlight);
		mainmenu_flipper.setDisplayedChild(PAGE_FRIEND);
		if(Helpers.Constants.mIsLoggedIn)
			((ViewFlipper) findViewById(R.id.news).findViewById(R.id.friend_flipper)).setDisplayedChild(1);
		else
			((ViewFlipper) findViewById(R.id.news).findViewById(R.id.friend_flipper)).setDisplayedChild(2);
		findViewById(R.id.news).findViewById(R.id.info_news).setBackgroundResource(R.drawable.submenu_btn);
		findViewById(R.id.news).findViewById(R.id.info_profile).setBackgroundResource(R.drawable.submenu_btn_active);
		mPageShowing = PAGE_FRIEND;
		//onPageItemClicked();
	}
	
	public void onFriendClicked(View view) {
		FlurryAgent.logEvent("NewsEvent");
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED)) {
			panel_flipper.setDisplayedChild(2);
		} else {
			panel_flipper.setDisplayedChild(1);
			if(topPanel.isShown()) {
				topPanel.setOpen(false, true);
			}
		}
		for(int i=0;i<((LinearLayout) findViewById(R.id.bottomMenu)).getChildCount();i++) {
			((LinearLayout)findViewById(R.id.bottomMenu)).getChildAt(i).setBackgroundDrawable(null);
		}
		view.setBackgroundResource(R.drawable.menu_highlight);
		mainmenu_flipper.setDisplayedChild(PAGE_FRIEND);
		mPageShowing = PAGE_FRIEND;
		//onPageItemClicked();
	}
	
	public void onTrafficClicked(View view) {
		FlurryAgent.logEvent("TrafficEvent");
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED)) {
			panel_flipper.setDisplayedChild(2);
		} else {
			panel_flipper.setDisplayedChild(1);
			if(topPanel.isShown()) {
				topPanel.setOpen(false, true);
			}
		}
		for(int i=0;i<((LinearLayout) findViewById(R.id.bottomMenu)).getChildCount();i++) {
			((LinearLayout)findViewById(R.id.bottomMenu)).getChildAt(i).setBackgroundDrawable(null);
		}
		view.setBackgroundResource(R.drawable.menu_highlight);
		mainmenu_flipper.setDisplayedChild(PAGE_TRAFFIC);
		mPageShowing = PAGE_TRAFFIC;
		//onPageItemClicked();
	}
	
	public void onEUControlClicked(View view) {
		FlurryAgent.logEvent("EUControlEvent");
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED)) {
			panel_flipper.setDisplayedChild(2);
		} else {
			panel_flipper.setDisplayedChild(1);
			if(topPanel.isShown()) {
				topPanel.setOpen(false, true);
			}
		}
		for(int i=0;i<((LinearLayout) findViewById(R.id.bottomMenu)).getChildCount();i++) {
			((LinearLayout)findViewById(R.id.bottomMenu)).getChildAt(i).setBackgroundDrawable(null);
		}
		view.setBackgroundResource(R.drawable.menu_highlight);
		mainmenu_flipper.setDisplayedChild(PAGE_EU_CONTROL);
		mPageShowing = PAGE_EU_CONTROL;
		//onPageItemClicked();
	}
	
	public void onGameClicked(View view) {
		FlurryAgent.logEvent("GameEvent");
		if(prefsWrapper.getPreferenceIntValue(PreferenceWrapper.ORDER_STATUS) == PreferenceWrapper.ORDER_STATUS_RECEIVED &&
				!prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED)) {
			panel_flipper.setDisplayedChild(2);
		} else {
			panel_flipper.setDisplayedChild(1);
			if(topPanel.isShown()) {
				topPanel.setOpen(false, true);
			}
		}
		for(int i=0;i<((LinearLayout) findViewById(R.id.bottomMenu)).getChildCount();i++) {
			((LinearLayout)findViewById(R.id.bottomMenu)).getChildAt(i).setBackgroundDrawable(null);
		}
		view.setBackgroundResource(R.drawable.menu_highlight);
		mainmenu_flipper.setDisplayedChild(PAGE_GAME);
		mPageShowing = PAGE_GAME;
		//onPageItemClicked();
	}
	
	public void onWaitingFirstAidClicked(View view) {
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(2);
	}
	
	public void onWaithingDamgeReportClicked(View view) {
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(1);
	}
	
	public void onWaitingGameClicked(View view) {
		mPageShowing = PAGE_INITIAL;
		panel_flipper.setDisplayedChild(1);
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(0);
		onGameClicked(findViewById(R.id.game_menu));
	}
	
	public void onEditImgBtnClick(View view) {
		AlertDialog alert = new AlertDialog.Builder(this)
					.setCancelable(true)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.setItems(R.array.edit_image, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch(which) {
								case 0:
									mycarinfo_image_camera = startCamera(MYCARINFO_CAMERA);
									break;
								case 1:
									startGallery(MYCARINFO_GALLERY);
									break;
								case 2:
									if(Helpers.Constants.mIsLoggedIn) {
										new Thread(new Runnable() {
											public void run() {
												synchronized(Helpers.lockObject) {
													final Car car = ((VikingApplication) getApplicationContext()).getActiveCar();
													
													if(deleteCar(car.getUid())) {
														int ownerId = 0;
														if(((VikingApplication) getApplicationContext()).getUser() != null) {
															ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
														}
														
														carAdapter.openWritable();
														carAdapter.delete(car.getUid());
														carAdapter.close();
														
														carAdapter.openReadable();
														final List<Car> cars = carAdapter.getAllCars(ownerId);
														carAdapter.close();
														
														Car tempCar = new Car();
														tempCar.setUid(-1);
														cars.add(tempCar);
														((VikingApplication) getApplicationContext()).setActiveCar(cars.get(0));
														
														handler.post(new Runnable() {
															public void run() {
																((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfo(true);
															}
														});
													}
												}
												
											}
										}).start();
									} else {
										((MyCarInfo) findViewById(R.id.mycar_info)).removeInTempCars(((VikingApplication) getApplicationContext()).getActiveCar());
										((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfoNotLoggedIn();
									}
									break;
							}
						}
					})
					.create();
		alert.show();
	}
	
	public void onShareCarBtnClick(View view) {
		AlertDialog alert = new AlertDialog.Builder(this)
		.setCancelable(true)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setItems(R.array.share_via, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
					case 0:
						Uri uri = Contacts.Phones.CONTENT_URI;
						
						if(Build.VERSION.SDK_INT > 4) {
							try {
								Class<?> c = Class.forName("android.provider.ContactsContract$Contacts");
								uri = (Uri) c.getField("CONTENT_URI").get(null);
							} catch (ClassNotFoundException ex) {
								Log.e(TAG, ex.getMessage());
							} catch (IllegalArgumentException ex) {
								Log.e(TAG, ex.getMessage());
							} catch (SecurityException ex) {
								Log.e(TAG, ex.getMessage());
							} catch (IllegalAccessException ex) {
								Log.e(TAG, ex.getMessage());
							} catch (NoSuchFieldException ex) {
								Log.e(TAG, ex.getMessage());
							}
						}
						
						Intent intent = new Intent(Intent.ACTION_PICK, uri);
						startActivityForResult(intent, ACCESS_PICK_CONTACT);
						break;
					case 1:
						mPageShowing = PAGE_OWNERINFO;
						final Car car = ((VikingApplication) getApplicationContext()).getActiveCar();
						
						if(Helpers.isNetworkAvailable(MainMenuActivity.this) && car != null) {
							new Thread(new Runnable() {
								public void run() {
									final List<CarAccess> carAccesses = getAllShareCarUsers(car.getUid());
									
									runOnUiThread(new Runnable() {
										public void run() {
											final CarAccessAdapter adapter = new CarAccessAdapter(MainMenuActivity.this, R.layout.list_car_access, carAccesses);
											adapter.setDropDownViewResource(R.layout.list_car_access);
											((ListView) findViewById(R.id.access).findViewById(R.id.car_access_list)).setAdapter(adapter);
										}
									});
								}
							}).start();
						}
						
						mainmenu_flipper.setDisplayedChild(2);
						break;
				}
			}
		})
		.create();
		alert.show();
	}
	
	public void back(View view) {
		panel_flipper.setDisplayedChild(0);
		main_flipper.setDisplayedChild(0);
		mainmenu_flipper.setDisplayedChild(0);
		mPageShowing = PAGE_INITIAL;
	}
	
	public void backToOwnerInfo(View vie) {
		panel_flipper.setDisplayedChild(1);
		main_flipper.setDisplayedChild(0);
		mainmenu_flipper.setDisplayedChild(1);
		mPageShowing = PAGE_INITIAL;
	}
	
	public void onPictureBilderBtnClicked(View view) {
		car_event_picture_uri = startCamera(MYCAREVENT_CAMERA);
	}
	
	public void onCloseStatusBar(View view) {
		prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.ORDER_STATUS_CLOSED, true);
		panel_flipper.setDisplayedChild(1);
	}
	
	public void onRefreshOrderStatus(View view) {
		new Thread(new Runnable() {
			public void run() {
				String mission = prefsWrapper.getPreferenceStringValue(PreferenceWrapper.ORDER_MISSION);
				Log.i(TAG, "mission: " + mission);
				if(mission != null) {
					int status = checkOrderAssistanceStatus(mission);
					
					if(status != PreferenceWrapper.ORDER_STATUS_RECEIVED) {
						runOnUiThread(new Runnable() {
							public void run() {
								if(mPageShowing == PAGE_INITIAL) {
									panel_flipper.setDisplayedChild(0);
								} else {
									if(topPanel.isShown()) {
										topPanel.setOpen(false, true);
									}
									panel_flipper.setDisplayedChild(1);
								}
							}
						});
					}
					prefsWrapper.setPreferenceIntValue(PreferenceWrapper.ORDER_STATUS, status);
				}
			}
		}).start();
	}
	
	private Uri startCamera(int requestCode) {
		String filename = String.valueOf(System.currentTimeMillis()).substring(0, 6) + ".jpg";
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Images.Media.TITLE, filename);
		Uri car_image_camera = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//intent.putExtra(MediaStore.EXTRA_OUTPUT, car_image_camera);
		startActivityForResult(intent, requestCode);
		return car_image_camera;
	}
	
	private void startGallery(int requestCode) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
	}
	
	private byte[] saveCarImage(Car car, Uri car_image_uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor c = managedQuery(car_image_uri, projection, null, null, null);
		c.moveToNext();
		String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
		
		final File f = new File(path);
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("car_uid", String.valueOf(car.getUid()));
		
		request.sendMultipart(getString(R.string.api_url) + "save_car_picture", f, queries);
		
		try {
			carAdapter.openWritable();
			byte[] image = Helpers.toByteArray(new FileInputStream(f));
			car.setPath(f.getAbsolutePath());
			carAdapter.updateCar(car);
			car.setPath(f.getAbsolutePath());
			carAdapter.close();
			
			return image;
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return null;
	}
	
	private byte[] saveCarImage(Car car, File fileCarImage) {
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("car_uid", String.valueOf(car.getUid()));
		
		request.sendMultipart(getString(R.string.api_url) + "save_car_picture", fileCarImage, queries);
		
		try {
			carAdapter.openWritable();
			byte[] image = Helpers.toByteArray(new FileInputStream(fileCarImage));
			car.setPath(fileCarImage.getAbsolutePath());
			carAdapter.updateCar(car);
			car.setPath(fileCarImage.getAbsolutePath());
			carAdapter.close();
			
			return image;
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return null;
	}
	
	private boolean saveCarFile(CarFile carFile, Uri car_file_uri) {
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery(car_file_uri, projection, null, null, null);
		cursor.moveToNext();
		String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		
		File f  = new File(path);
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("owner_id", String.valueOf(carFile.getOwnerId()));
		queries.put("name", carFile.getName());
		queries.put("gallery", carFile.getGallery());
		
		String result = request.sendMultipart(getString(R.string.api_url) + "save_car_files", f, queries);
		Log.i(TAG, "save car files: " + result);
		try {
			byte[] data = Helpers.toByteArray(new FileInputStream(f));
			
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				int uid = Integer.parseInt(Helpers.parseXMLNode(result, "uid"));
				
				for(CarFile cf: carFileAdapter.getAllDefaultCarFile()) {
					cf.setOwnerId(carFile.getOwnerId());
					carFileAdapter.updateCarFile(cf);
				}
				
				carFileAdapter.open();
				carFile.setUid(uid);
				carFile.setPath(path);
				carFile.setUid(Integer.parseInt(Helpers.parseXMLNode(result, "uid")));
				carFileAdapter.insertCarFile(carFile);
				carFileAdapter.close();
				
				return true;
			}
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		
		return false;
	}
	
	private boolean saveCarFile(CarFile carFile, File fileCarFileImage) {
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("owner_id", String.valueOf(carFile.getOwnerId()));
		queries.put("name", carFile.getName());
		queries.put("gallery", carFile.getGallery());
		
		String result = request.sendMultipart(getString(R.string.api_url) + "save_car_files", fileCarFileImage, queries);
		Log.i(TAG, "save car files: " + result);
		
		if(result != null) {
			try {
				byte[] data = Helpers.toByteArray(new FileInputStream(fileCarFileImage));
				
				int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
				
				if(success == 1) {
					int uid = Integer.parseInt(Helpers.parseXMLNode(result, "uid"));
					
					for(CarFile cf: carFileAdapter.getAllDefaultCarFile()) {
						cf.setOwnerId(carFile.getOwnerId());
						carFileAdapter.updateCarFile(cf);
					}
					
					carFileAdapter.open();
					carFile.setUid(uid);
					carFile.setPath(fileCarFileImage.getAbsolutePath());
					carFile.setUid(Integer.parseInt(Helpers.parseXMLNode(result, "uid")));
					carFileAdapter.insertCarFile(carFile);
					carFileAdapter.close();
					
					return true;
				}
			} catch(IOException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean updateCarImage = false;
		Helpers.createDirectory("viking/cars");
		
		if(resultCode == RESULT_OK && requestCode == MYCARINFO_CAMERA) {
			mycar_info.showImageProgress();
			//car_image_uri = mycarinfo_image_camera;
			fileCarImage = new File(Environment.getExternalStorageDirectory(), "viking/cars/" + System.currentTimeMillis() + ".png");
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Helpers.saveFileFromBitmap(fileCarImage, bitmap, Bitmap.CompressFormat.PNG);
			updateCarImage = true;
		} else if(resultCode == RESULT_OK && requestCode == MYCARINFO_GALLERY) {
			mycar_info.showImageProgress();
			car_image_uri = data.getData();
			String[] projection = {MediaStore.Images.Media.DATA};
			Cursor c = managedQuery(car_image_uri, projection, null, null, null);
			c.moveToNext();
			String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
			fileCarImage = new File(path);
			updateCarImage = true;
		} else if(resultCode == RESULT_OK && requestCode == MYCARFILE_CAMERA) {
			fileCarFileImage = new File(Environment.getExternalStorageDirectory(), "viking/carfiles/" + System.currentTimeMillis() + ".png");
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Helpers.saveFileFromBitmap(fileCarFileImage, bitmap, Bitmap.CompressFormat.PNG);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
			findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setAnimation(animation);
			findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setVisibility(View.VISIBLE);
		} else if(resultCode == RESULT_OK && requestCode == MYCARFILE_GALLERY) {
			car_file_uri = data.getData();
			String[] projection = {MediaStore.Images.Media.DATA};
			Cursor c = managedQuery(car_file_uri, projection, null, null, null);
			c.moveToNext();
			String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
			fileCarFileImage = new File(path);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
			findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setAnimation(animation);
			findViewById(R.id.mycar_files).findViewById(R.id.car_file_message).setVisibility(View.VISIBLE);
		} else if(resultCode == RESULT_OK && requestCode == ACCESS_PICK_CONTACT) {
			final Uri contactData = data.getData();
			final Cursor cursor = this.managedQuery(contactData, null, null, null, null);
			
			if(cursor.moveToNext() && ((VikingApplication) getApplicationContext()).getUser() != null) {
				final int ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
				final int carUid = ((VikingApplication) getApplicationContext()).getActiveCar().getUid();
				
				new Thread(new Runnable() {
					public void run() {
						String name = "";
						String phone_number = "";
						
						if(Build.VERSION.SDK_INT > 4) {
							name = cursor.getString(cursor.getColumnIndex("display_name"));
							String contactId = cursor.getString(cursor.getColumnIndex("_id"));
							String hasPhoneNumber = cursor.getString(cursor.getColumnIndex("has_phone_number"));
							
							if(hasPhoneNumber.equals("1")) {
								try {
									Class<?> cls = Class.forName("android.provider.ContactsContract$CommonDataKinds$Phone");
									Uri uri = (Uri) cls.getField("CONTENT_URI").get(null);
									Cursor c = getContentResolver().query(uri, null, "contact_id=" + contactId, null, null);
									if(c.moveToNext()) {
										phone_number = c.getString(c.getColumnIndex("data1"));
									}
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (SecurityException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (NoSuchFieldException e) {
									e.printStackTrace();
								}
							}
						} else {
							name = cursor.getString(cursor.getColumnIndex(Contacts.Phones.DISPLAY_NAME));
							phone_number = cursor.getString(cursor.getColumnIndex(Contacts.Phones.NUMBER));
						}
						
						if(!phone_number.equals("")) {
							CarAccess carAccess = new CarAccess();
							carAccess.setOwnerId(ownerId);
							carAccess.setCarUid(carUid);
							carAccess.setName(name);
							carAccess.setTelephone(phone_number);
							
							saveShareCar(carAccess);
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									Helpers.showMessage(MainMenuActivity.this, "", "Valgt kontakt ikke har telefonnummeret");
								}
							});
						}
						
					}
				}).start();
			}
		} else if(resultCode == RESULT_OK && requestCode == MYCAREVENT_CAMERA) {
			/*String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Thumbnails.DATA};
			Cursor cursor = managedQuery(car_event_picture_uri, projection, null, null, null);
			
			if(cursor.moveToNext()) {
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				File f = new File(path);
				((MyCarEvents) findViewById(R.id.mycar_events)).setCameraImage(f);
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
				f = new File(path);
				((MyCarEvents) findViewById(R.id.mycar_events)).setCameraImageThumb(f);
			}*/
			Helpers.createDirectory("viking/carevents/picture/");
			File fileCarEventImage = new File(Environment.getExternalStorageDirectory(), "viking/carevents/picture/" + System.currentTimeMillis() + ".png");
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Helpers.saveFileFromBitmap(fileCarEventImage, bitmap, Bitmap.CompressFormat.PNG);
			((MyCarEvents) findViewById(R.id.mycar_events)).setCameraImage(fileCarEventImage);
			((MyCarEvents) findViewById(R.id.mycar_events)).setCameraImageThumb(fileCarEventImage);
		} else if(resultCode == RESULT_OK && requestCode == DAMAGE_REPORT_CAMERA) {
			/*String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Thumbnails.DATA};
			Cursor cursor = managedQuery(damage_report_picture_uri, projection, null, null, null);
			
			if(cursor.moveToNext()) {
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				File f = new File(path);
				((DamageReport) findViewById(R.id.damage_report)).damagePictures.get(activeDamageReportPicture).setFile(f);
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
				f = new File(path);
				((DamageReport) findViewById(R.id.damage_report)).damagePictures.get(activeDamageReportPicture).setThumbFile(f);
				((DamageReport) findViewById(R.id.damage_report)).updateDamageReportPictures();
			}*/
			Helpers.createDirectory("viking/damagereport");
			File fileDamageReportImage = new File(Environment.getExternalStorageDirectory(), "viking/damagereport/" + System.currentTimeMillis() + ".png");
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Helpers.saveFileFromBitmap(fileDamageReportImage, bitmap, Bitmap.CompressFormat.PNG);
			/*((DamageReport) findViewById(R.id.damage_report)).damagePictures.get(activeDamageReportPicture).setFile(fileDamageReportImage);
			((DamageReport) findViewById(R.id.damage_report)).damagePictures.get(activeDamageReportPicture).setThumbFile(fileDamageReportImage);
			((DamageReport) findViewById(R.id.damage_report)).updateDamageReportPictures();*/
			((DamageReportV2) findViewById(R.id.damage_report)).updateDamageReportPictures(fileDamageReportImage, activeDamageReportPicture);
		} else if(resultCode == RESULT_OK && requestCode == ORDER_ASSISTANCE) {
			((MyCarEvents) findViewById(R.id.mycar_events)).updateCarEvents();
			main_flipper.setDisplayedChild(3);
		} else if(resultCode == RESULT_OK && requestCode == MYCARFILE_PICTURE_VIEW_CAMERA) {
			if(activeGallery != null && !activeGallery.equals("")) {
				final File fileCarFilePicture = new File(Environment.getExternalStorageDirectory(), "viking/carfiles/" + System.currentTimeMillis() + ".png");
				final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				Helpers.saveFileFromBitmap(fileCarFilePicture, bitmap, Bitmap.CompressFormat.PNG);
				
				LinearLayout linearLayout = new LinearLayout(this);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				linearLayout.setLayoutParams(layoutParams);
				linearLayout.setOrientation(LinearLayout.VERTICAL);
				final TextView textView = new TextView(this);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				textView.setText("Filename");
				textView.setTextColor(Color.WHITE);
				final EditText filename = new EditText(this);
				filename.setGravity(Gravity.CENTER_HORIZONTAL);
				linearLayout.addView(textView);
				linearLayout.addView(filename);
				
				AlertDialog alert = new AlertDialog.Builder(this)
					.setCancelable(false)
					.setView(linearLayout)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(!filename.getText().toString().equals("")) {
								((ViewFlipper) findViewById(R.id.file_picture_flipper)).showNext();
								new Thread(new Runnable() {
									public void run() {
										CarFile carFile = new CarFile();
										int ownerId = 0;
										if(((VikingApplication) getApplicationContext()).getUser() != null) {
											ownerId = ((VikingApplication) getApplicationContext()).getUser().getUid();
										}
										carFile.setOwnerId(ownerId);
										carFile.setName(filename.getText().toString());
										carFile.setGallery(activeGallery);
										
										if(Helpers.Constants.mIsLoggedIn) {
											if(saveCarFile(carFile, fileCarFilePicture)) {
												handler.post(new Runnable() {
													public void run() {
														carFileAdapter.open();
														carFilesPictureView = carFileAdapter.getAllCarFilesByGallery(activeGallery);
														carFileAdapter.close();
														CarFilePictureViewAdapter adapter = new CarFilePictureViewAdapter(MainMenuActivity.this, getWindowManager(), carFilesPictureView);
														file_picture_gallery.setAdapter(adapter);
														adapter.notifyDataSetChanged();
														file_picture_gallery.setCallbackDuringFling(true);
														
														file_picture_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

															@Override
															public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
																activeCarFile = carFilesPictureView.get(position);
																((TextView) findViewById(R.id.picture_name)).setText(URLDecoder.decode(activeCarFile.getName()) + "\n" + (position + 1) + " of " + carFilesPictureView.size());
															}

															@Override
															public void onNothingSelected(AdapterView<?> parent) {
															}
														});
														((ViewFlipper) findViewById(R.id.file_picture_flipper)).showPrevious();
														((MyCarFiles) findViewById(R.id.mycar_files)).updateCarFileList();
													}
												});
											}
										} else {
											carFile.setUid((int)System.currentTimeMillis());
											carFile.setPath(fileCarFilePicture.getAbsolutePath());
											carFileAdapter.insertCarFile(carFile);
											
											handler.post(new Runnable() {
												public void run() {
													carFileAdapter.open();
													carFilesPictureView = carFileAdapter.getAllCarFilesByGallery(activeGallery);
													carFileAdapter.close();
													CarFilePictureViewAdapter adapter = new CarFilePictureViewAdapter(MainMenuActivity.this, getWindowManager(), carFilesPictureView);
													file_picture_gallery.setAdapter(adapter);
													file_picture_gallery.setCallbackDuringFling(true);
													
													file_picture_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

														@Override
														public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
															activeCarFile = carFilesPictureView.get(position);
															((TextView) findViewById(R.id.picture_name)).setText(URLDecoder.decode(activeCarFile.getName()) + "\n" + (position + 1) + " of " + carFilesPictureView.size());
														}

														@Override
														public void onNothingSelected(AdapterView<?> parent) {
														}
													});
													((ViewFlipper) findViewById(R.id.file_picture_flipper)).showPrevious();
													((MyCarFiles) findViewById(R.id.mycar_files)).updateCarFileList();
												}
											});
										}
									}
								}).start();
							}
						}
					}).create();
				alert.show();
			}
		} else if(resultCode == Helpers.Constants.RESULT_REGISTERED && requestCode == Helpers.Constants.PAGE_REGISTER) {
			/*User user = ((VikingApplication) getApplicationContext()).getUser();
			((MyCarInfo) findViewById(R.id.mycar_info)).setUser(user);
			((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfo();
			((News) findViewById(R.id.news)).setUser(user);
			((Profile) findViewById(R.id.news).findViewById(R.id.profile)).initUser(user);
			
			MyCarEntity mycarfiles = (MyCarEntity) findViewById(R.id.mycar_files);
			MyCarEntity mycarphone = (MyCarEntity) findViewById(R.id.mycar_phone);
			MyCarEntity mycarevent = (MyCarEntity) findViewById(R.id.mycar_events);
			mycarfiles.setOwnerId(user.getUid());
			mycarphone.setOwnerId(user.getUid());
			mycarevent.setOwnerId(user.getUid());*/
			
			//initSharedCar();
			/*
			if(mVikingService != null) {
				Log.i(TAG, "entering upload records");
				try {
					mVikingService.uploadRecords();
				} catch(RemoteException ex) {
					Log.e(TAG, ex.getMessage());
				}
				Helpers.Constants.mIsLoggedIn = false;
				Helpers.Constants.mNotLoggedInCount = 0;
				((VikingApplication) getApplicationContext()).setUser(null);
				((VikingApplication) getApplicationContext()).setCars(null);
				finish();
			}*/
			Log.i(TAG, "result registered");
			Helpers.Constants.mIsLoggedIn = false;
			Helpers.Constants.mNotLoggedInCount = 0;
			((VikingApplication) getApplicationContext()).setUser(null);
			((VikingApplication) getApplicationContext()).setCars(null);
			Helpers.Constants.mMyCarRequested = false;
			finish();
		} else if(resultCode == RESULT_CANCELED && requestCode == Helpers.Constants.PAGE_REGISTER) {
			Helpers.Constants.mIsLoggedIn = false;
			Helpers.Constants.mNotLoggedInCount = 0;
			((VikingApplication) getApplicationContext()).setUser(null);
			((VikingApplication) getApplicationContext()).setCars(null);
			MyCarInfo.tempCars.clear();
			if(carFileAdapter != null) carFileAdapter.deleteAll(0);
			if(MyCarPhone.tempCarPhones != null) MyCarPhone.tempCarPhones.clear();
			MyCarEvents.mCarEvents.clear();
			MyCarEvents.getAllTempCarEventPictures().clear();
			MyCarEvents.getAllTempCarEventSounds().clear();
			finish();
		} else if(resultCode == Helpers.Constants.RESULT_DAMAGE_REPORT_SUCCESS && requestCode == Helpers.Constants.PAGE_DAMAGEREPORT) {
			mPageShowing = PAGE_INITIAL;
			
			if(Helpers.Constants.mIsLoggedIn)
				((MyCarEvents) findViewById(R.id.mycar_events)).updateCarEvents();
			else
				((MyCarEvents) findViewById(R.id.mycar_events)).updateCarEventsNotLoggedIn();
		}
		
		if(updateCarImage) {
			new Thread(new Runnable() {
				public void run() {
					synchronized(Helpers.lockObject) {
						final Car car = ((VikingApplication) getApplicationContext()).getActiveCar();
						
						if(Helpers.Constants.mIsLoggedIn) {
							if(fileCarImage != null) {
								byte[] image = saveCarImage(car, fileCarImage);
								car.setPath(fileCarImage.getAbsolutePath());
								carAdapter.openWritable();
								carAdapter.updateCar(car);
								carAdapter.close();
								
								((VikingApplication) getApplicationContext()).setActiveCar(car);
								
								handler.post(new Runnable() {
									public void run() {
										//mycar_info.updateCarInfo(car);
										mycar_info.updateCarInfo(false);
										mycar_info.showCarImage();
									}
								});
							}
						} else {
							if(fileCarImage != null) {
								/*String[] projection = {MediaStore.Images.Media.DATA};
								Cursor c = managedQuery(car_image_uri, projection, null, null, null);
								c.moveToNext();
								String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));*/
								if(((VikingApplication) getApplicationContext()).getActiveCar() != null) {
									((VikingApplication) getApplicationContext()).getActiveCar().setPath(fileCarImage.getAbsolutePath());
									
									handler.post(new Runnable() {
										public void run() {
											mycar_info.updateCarInfoNotLoggedIn();
											mycar_info.showCarImage();
										}
									});
								} else {
									handler.post(new Runnable() {
										public void run() {
											mycar_info.showCarImage();
										}
									});
								}
							}
						}
					}
					
				}
			}).start();
		}
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onDamagePictureClick(int position) {
		activeDamageReportPicture = position;
		damage_report_picture_uri = startCamera(DAMAGE_REPORT_CAMERA);
	}
	
	@Override
	public void onDamageSuccess() {
		mPageShowing = PAGE_INITIAL;
		main_flipper.setInAnimation(this, R.anim.slide_right_in);
		main_flipper.setOutAnimation(this, R.anim.slide_right_out);
		main_flipper.setDisplayedChild(0);
		((MyCarEvents) findViewById(R.id.mycar_events)).updateCarEvents();
	}

	@Override
	public void onLoginSuccess(List<Car> cars, User user) {
		Helpers.Constants.mIsLoggedIn = true;
		
		if(mVikingService != null) {
			try {
				((MyCarInfo) findViewById(R.id.mycar_info)).showImageProgress();
				((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).setDisplayedChild(1);
				((ViewFlipper) findViewById(R.id.mycar_phone).findViewById(R.id.mycar_phone_list_flipper)).setDisplayedChild(1);
				((ViewFlipper) findViewById(R.id.mycar_events).findViewById(R.id.mycar_events_list_flipper)).setDisplayedChild(1);
				mVikingService.requestAllCars(user.getUid());
				mVikingService.requestMyCarFiles(user.getUid());
				mVikingService.requestMyCarPhones(user.getUid());
				mVikingService.requestMyCarEvents(user.getUid());
				mainmenu_flipper.setDisplayedChild(1);
			} catch(RemoteException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		/*
		if(cars.size() > 0) {
			Car car = new Car();
			car.setUid(-1);
			cars.add(car);
			((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfo(cars.get(0), cars);
			((EUControl) findViewById(R.id.eu_control)).setCars(cars);
			((EUControl) findViewById(R.id.eu_control)).setUser(user);
			((EUControl) findViewById(R.id.eu_control)).initEUControl(cars.get(0));
			((DamageReport) findViewById(R.id.damage_report)).setUser(user);
			((DamageReport) findViewById(R.id.damage_report)).setOwnerId(user.getUid());
		}
		
		mainmenu_flipper.setDisplayedChild(1);
		mycar_flipper.setDisplayedChild(0);
		((News) findViewById(R.id.news)).setUser(user);
		((Profile) findViewById(R.id.news).findViewById(R.id.profile)).initUser(user);
		
		prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN, true);
		prefsWrapper.setPreferenceIntValue(PreferenceWrapper.OWNER_ID, user.getUid());
		
		initSharedCar();
		
		MyCarEntity mycarfiles = (MyCarEntity) findViewById(R.id.mycar_files);
		MyCarEntity mycarphone = (MyCarEntity) findViewById(R.id.mycar_phone);
		MyCarEntity mycarevent = (MyCarEntity) findViewById(R.id.mycar_events);
		
		mycarfiles.setSuccessor(mycarphone);
		mycarphone.setSuccessor(mycarevent);
		
		mycarfiles.load();*/
	}

	@Override
	public void onCarFileClick(String gallery) {
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(5);
		
		activeGallery = gallery;
		if(carFilesPictureView != null)
			carFilesPictureView.clear();
		carFileAdapter.open();
		carFilesPictureView = carFileAdapter.getAllCarFilesByGallery(gallery);
		carFileAdapter.close();
		
		((TextView) findViewById(R.id.picture_name)).setText(gallery + "\n1 of " + carFilesPictureView.size());
		
		file_picture_gallery.setAdapter(null);
		CarFilePictureViewAdapter adapter = new CarFilePictureViewAdapter(this, getWindowManager(), carFilesPictureView);
		file_picture_gallery.setAdapter(adapter);
		file_picture_gallery.setSelection(0);
		adapter.notifyDataSetChanged();
		file_picture_gallery.setCallbackDuringFling(true);
		
		/*
		if(carFilesPictureView.size() > 0 && carFilesPictureView.get(0).getIsDefault().equals("yes")) {
			((ImageButton) findViewById(R.id.add_picture)).setVisibility(View.GONE);
			((ImageButton) findViewById(R.id.delete_picture)).setVisibility(View.GONE);
		} else {
			((ImageButton) findViewById(R.id.add_picture)).setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.delete_picture)).setVisibility(View.VISIBLE);
		}*/
		
		file_picture_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				activeCarFile = carFilesPictureView.get(position);
				((TextView) findViewById(R.id.picture_name)).setText(URLDecoder.decode(activeCarFile.getName()) + "\n" + (position + 1) + " of " + carFilesPictureView.size());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	public void onClosePictureClicked(View view) {
		mPageShowing = PAGE_INITIAL;
		main_flipper.setInAnimation(this, R.anim.slide_right_in);
		main_flipper.setOutAnimation(this, R.anim.slide_right_out);
		main_flipper.setDisplayedChild(0);
	}
	
	public void onDeletePicture(View view) {
		if(activeGallery != null && !activeGallery.equals("")) {
			if(activeCarFile.getIsDefault().equals("yes")) {
				Helpers.showMessage(this, "", "Unable to delete default file.");
			} else { 
				AlertDialog alert = new AlertDialog.Builder(MainMenuActivity.this)
				.setCancelable(false)
				.setMessage("Are you sure you want to delete this car file?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(new Runnable() {
							public void run() {
								if(deleteCarFile(activeCarFile.getUid())) {
									handler.post(new Runnable() {
										public void run() {
											carFilesPictureView.remove(activeCarFile);
											
											if(carFilesPictureView.size() > 0) {
												CarFilePictureViewAdapter adapter = new CarFilePictureViewAdapter(MainMenuActivity.this, getWindowManager(), carFilesPictureView);
												file_picture_gallery.setAdapter(adapter);
												file_picture_gallery.setCallbackDuringFling(true);
												
												file_picture_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

													@Override
													public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
														activeCarFile = carFilesPictureView.get(position);
														((TextView) findViewById(R.id.picture_name)).setText(URLDecoder.decode(activeCarFile.getName()) + "\n" + (position + 1) + " of " + carFilesPictureView.size());
													}

													@Override
													public void onNothingSelected(AdapterView<?> parent) {
													}
												});
												
												((MyCarFiles) findViewById(R.id.mycar_files)).updateCarFileList();
											} else {
												((MyCarFiles) findViewById(R.id.mycar_files)).updateCarFileList();
												onClosePictureClicked(null);
											}
										}
									});
								}
							}
						}).start();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
				alert.show();
			}
		}
	}
	
	public void onAddPictureClicked(View view) {
		car_file_picture_uri = startCamera(MYCARFILE_PICTURE_VIEW_CAMERA);
	}
	
	private boolean deleteCarFile(int uid) {
		if(!Helpers.Constants.mIsLoggedIn) {
			carFileAdapter.delete(uid);
			return true;
		}
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		
		String result = request.send(getString(R.string.api_url) + "delete_car_file", queries, HttpRequest.POST);
		Log.i(TAG, "delete car file result: " + result);
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			Log.i(TAG, "delete car file: " + uid + "; success: " + success);
			if(success == 1) {
				carFileAdapter.open();
				carFileAdapter.delete(uid);
				carFileAdapter.close();
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void onTrafficClick(Traffic traffic) {
		panel_flipper.setDisplayedChild(1);
		if(topPanel.isShown()) {
			topPanel.setOpen(false, true);
		}
		mainmenu_flipper.setDisplayedChild(4);
		mPageShowing = PAGE_TRAFFIC;
		((no.incent.viking.widget.Traffic) findViewById(R.id.traffic)).showTraffic(traffic);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(mPageShowing != PAGE_INITIAL) {
				back(null);
				return false;
			} else {
				/*Helpers.Constants.mIsLoggedIn = false;
				Helpers.Constants.mNotLoggedInCount = 0;
				((VikingApplication) getApplicationContext()).setUser(null);
				Process.killProcess(Process.myPid());
				finish();*/
				if(Helpers.Constants.mIsLoggedIn) {
					Helpers.Constants.mIsLoggedIn = false;
					Helpers.Constants.mNotLoggedInCount = 0;
					((VikingApplication) getApplicationContext()).setUser(null);
					((VikingApplication) getApplicationContext()).setCars(null);
					Process.killProcess(Process.myPid());
					Helpers.Constants.mMyCarRequested = false;
					finish();
				} else {
					onCarSaving();
				}
			}
		}
		
		return true;
	}

	@Override
	public void onUpdateOwner(final User user) {
		((TextView) findViewById(R.id.mycar_info).
				findViewById(R.id.car_info_eier)).
				setText(user.getFirstname() + " " + user.getLastname());
	}
	
	public boolean deleteCar(int uid) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		
		String result = request.send(getString(R.string.api_url) + "delete_car", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		
		return false;
	}

	@Override
	public void onPageItemClicked() {
		Helpers.Constants.mNotLoggedInCount++;
		
		if(!Helpers.Constants.mIsLoggedIn && Helpers.Constants.mNotLoggedInCount == 5) {
			Helpers.Constants.mNotLoggedInCount = 0;
			
			Intent intent = new Intent(this, Register.class);
			startActivityForResult(intent, Helpers.Constants.PAGE_REGISTER);
		}
	}
	
	@Override
	public void onCarSaving() {
		Intent intent = new Intent(this, Register.class);
		startActivityForResult(intent, Helpers.Constants.PAGE_REGISTER);
	}

	@Override
	public void onCarEventPictureClicked(CarEventPicture carEventPicture) {
		main_flipper.setInAnimation(this, R.anim.slide_left_in);
		main_flipper.setOutAnimation(this, R.anim.slide_left_out);
		main_flipper.setDisplayedChild(6);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(carEventPicture.getPath(), options);
		final int requiredSize = metrics.widthPixels;
		int scale = 1;
		
		while(options.outWidth/scale/2 >= requiredSize && options.outHeight/scale/2 >= requiredSize) {
			scale *= 2;
		}
		
		BitmapFactory.Options options2 = new BitmapFactory.Options();
		options2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(carEventPicture.getPath(), options2);
		
		bitmap = Helpers.resizeBitmap(this, bitmap, metrics.widthPixels, metrics.heightPixels);
		((ImageView) findViewById(R.id.mycar_event_picture_view).findViewById(R.id.event_picture)).setImageBitmap(bitmap);
		((TextView) findViewById(R.id.mycar_event_picture_view).findViewById(R.id.picture_name)).setText(carEventPicture.getName());
	}
	
	private void initSharedCar() {
		if(((VikingApplication) getApplicationContext()).getUser() != null && Helpers.isNetworkAvailable(this)) {
			final User user = ((VikingApplication) getApplicationContext()).getUser();
			
			new Thread(new Runnable() {
				public void run() {
					final List<SharedCar> sharedCars = getAllNewSharedCar(user.getTelephone());
					
					if(sharedCars.size() > 0) {
						handler.post(new Runnable() {
							public void run() {
								String message = String.format("%s ønsker å dele bil %s med deg. Vil du legge til bilen i BILioteket?", sharedCars.get(0).getOwnername(), sharedCars.get(0).getRegistrationNumber());
								
								AlertDialog alert = new AlertDialog.Builder(MainMenuActivity.this)
										.setCancelable(false)
										.setMessage(message)
										.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												final SharedCar sharedCar = sharedCars.get(0);
												updateShareCarStatus(sharedCar.getId(), user.getUid(), "APPROVED");
												
												new Thread(new Runnable() {
													public void run() {
														String filename = Helpers.getLastPathSegment(sharedCars.get(0).getFilename());
														File f = Helpers.createDirectory("viking/cars");
														
														if(filename != null && f != null && Helpers.isValidDownloadFile(filename)) {
															f = new File(f, filename);
															byte[] data = request.send(sharedCar.getFilename());
															Helpers.saveFile(f, data, true);
															sharedCar.setPath(f.getAbsolutePath());
															carAdapter.updateCar(sharedCar);
															
															runOnUiThread(new Runnable() {
																public void run() {
																	((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfo(true);
																}
															});
														}
													}
												}).start();
												
												carAdapter.insertCar(sharedCar);
												((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfo(true);
												dialog.dismiss();
											}
										})
										.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												updateShareCarStatus(sharedCars.get(0).getId(), user.getUid(), "CANCELLED");
												dialog.dismiss();
											}
										}).create();
								alert.show();
							}
						});
					}
				}
			}).start();
		}
	}
	
	private List<SharedCar> getAllNewSharedCar(String telephone) {
		List<SharedCar> sharedCars = new ArrayList<SharedCar>();
		
		if(request == null)
			request = HttpRequest.getInstance();
		
		String jsonString = request.send(getString(R.string.api_url) + String.format("list_new_sharedcar/%s/json/" + System.currentTimeMillis(), telephone), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray cars = json.getJSONArray("cars");
				
				for(int i=0;i<cars.length();i++) {
					JSONObject info = cars.getJSONObject(i).getJSONObject("info");
					SharedCar sharedCar = new SharedCar();
					sharedCar.setId(info.getInt("uid"));
					sharedCar.setUid(info.getInt("car_uid"));
					sharedCar.setOwnerId(info.getInt("owner_id"));
					sharedCar.setOwnername(info.getString("ownername"));
					sharedCar.setRegistrationNumber(info.getString("registration_number"));
					sharedCar.setChassisNumber(info.getString("chassis_number"));
					sharedCar.setCarRegYear(info.getString("car_reg_year"));
					sharedCar.setRegFirstTimeInNorway(info.getString("reg_first_time_in_norway"));
					sharedCar.setBrandCode(info.getString("brand_code"));
					sharedCar.setCarModel(info.getString("car_model"));
					sharedCar.setEnginePerformance(info.getString("engine_performance"));
					sharedCar.setDisplacement(info.getString("displacement"));
					sharedCar.setFuelType(info.getString("fuel_type"));
					sharedCar.setLength(info.getString("length"));
					sharedCar.setWidth(info.getString("width"));
					sharedCar.setWeight(info.getString("weight"));
					sharedCar.setTotalWeight(info.getString("total_weight"));
					sharedCar.setColour(info.getString("colour"));
					sharedCar.setCo2Emission(info.getString("co2_emission"));
					sharedCar.setFilename(info.getString("filename"));
					sharedCar.setShared(true);
					sharedCars.add(sharedCar);
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		return sharedCars;
	}
	
	private boolean updateShareCarStatus(int uid, int ownerId, String status) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(ownerId)));
		queries.add(new BasicNameValuePair("status", status));
		
		String result = request.send(getString(R.string.api_url) + "update_sharecar_status", queries, HttpRequest.POST);
		Log.i(TAG, "update share car result: " + result);
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		
		return false;
	}
	
	private boolean saveShareCar(CarAccess carAccess) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(carAccess.getOwnerId())));
		queries.add(new BasicNameValuePair("car_uid", String.valueOf(carAccess.getCarUid())));
		queries.add(new BasicNameValuePair("telephone", carAccess.getTelephone()));
		queries.add(new BasicNameValuePair("description", carAccess.getDescription()));
		queries.add(new BasicNameValuePair("name", carAccess.getName()));
		
		String result = request.send(getString(R.string.api_url) + "save_sharecar", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		
		return false;
	}
	
	private List<CarAccess> getAllShareCarUsers(int carUid) {
		List<CarAccess> carAccesses = new ArrayList<CarAccess>();
		
		String jsonString = request.send(getString(R.string.api_url) + String.format("list_sharedcar_users/%s/json/" + System.currentTimeMillis(), carUid), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray sharecars = json.getJSONArray("sharecars");
				
				for(int i=0;i<sharecars.length();i++) {
					JSONObject sharecar = sharecars.getJSONObject(i);
					CarAccess carAccess = new CarAccess();
					carAccess.setUid(sharecar.getInt("uid"));
					carAccess.setCarUid(sharecar.getInt("car_uid"));
					carAccess.setTelephone(sharecar.getString("telephone"));
					carAccess.setDescription(sharecar.getString("description"));
					carAccess.setName(sharecar.getString("name"));
					
					carAccesses.add(carAccess);
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return carAccesses;
	}
	
	private int checkOrderAssistanceStatus(String mission) {
		HttpRequest request = HttpRequest.getInstance();
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		
		queries.add(new BasicNameValuePair("mission", mission));
		
		String result = request.send(getString(R.string.api_url) + "road_assistance_hentoppdrag/json", queries, HttpRequest.POST);
		Log.i(TAG, "status: " + result);
		if(result != null) {
			try {
				JSONObject json = new JSONObject(result);
				JSONArray oppdragrekords = json.getJSONArray("oppdragrekords");
				
				if(oppdragrekords.length() > 0) {
					String status = oppdragrekords.getJSONObject(0).getString("Status");
					
					if(status.equals("RECEIVED")) {
						return PreferenceWrapper.ORDER_STATUS_RECEIVED;
					} else if(status.equals("ORDERED")) {
						return PreferenceWrapper.ORDER_STATUS_ORDERED;
					} else if(status.equals("CANCELLED")) {
						return PreferenceWrapper.ORDER_STATUS_CANCELLED;
					}
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		
		return PreferenceWrapper.ORDER_STATUS_RECEIVED;
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
				stopService(new Intent(MainMenuActivity.this, VikingService.class));
			} catch (RemoteException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
	};
	
	private IVikingServiceCallback.Stub callback = new IVikingServiceCallback.Stub() {
		@Override
		public void onFinishSavingUser() throws RemoteException {}
		
		@Override
		public void onFinishRequestingCarPhones() throws RemoteException {
			Log.i(TAG, "finish requesting car phones");
			
			runOnUiThread(new Runnable() {
				public void run() {
					((ViewFlipper) findViewById(R.id.mycar_phone).findViewById(R.id.mycar_phone_list_flipper)).setDisplayedChild(0);
					((MyCarEntity) findViewById(R.id.mycar_phone)).load();
				}
			});
			
			Helpers.Constants.mMyCarRequested = true;
		}
		
		@Override
		public void onFinishRequestingCarFiles() throws RemoteException {
			Log.i(TAG, "finish requesting car files");
			
			runOnUiThread(new Runnable() {
				public void run() {
					((ViewFlipper) findViewById(R.id.mycar_files).findViewById(R.id.mycar_files_list_flipper)).setDisplayedChild(0);
					((MyCarEntity) findViewById(R.id.mycar_files)).load();
				}
			});
			
			Helpers.Constants.mMyCarRequested = true;
		}
		
		@Override
		public void onFinishRequestingCarEvents() throws RemoteException {
			Log.i(TAG, "finish requesting car events");
			
			runOnUiThread(new Runnable() {
				public void run() {
					((ViewFlipper) findViewById(R.id.mycar_events).findViewById(R.id.mycar_events_list_flipper)).setDisplayedChild(0);
					((MyCarEntity) findViewById(R.id.mycar_events)).load();
				}
			});
			
			Helpers.Constants.mMyCarRequested = true;
		}
		
		@Override
		public void onFinishRequestingAllCars() throws RemoteException {
			Log.i(TAG, "finish requesting cars");
			Helpers.Constants.mFinishRequestingAllCars = true;
			
			if(((VikingApplication) getApplicationContext()).getUser() != null) {
				runOnUiThread(new Runnable() {
					public void run() {
						((MyCarInfo) findViewById(R.id.mycar_info)).showCarImage();
						((MyCarInfo) findViewById(R.id.mycar_info)).updateCarInfo(true);
						int ownerId = prefsWrapper.getPreferenceIntValue(PreferenceWrapper.OWNER_ID);
						List<Car> cars = carAdapter.getAllCars(ownerId);
						((EUControl) findViewById(R.id.eu_control)).setCars(cars);
						if(cars.size() > 0) {
							((EUControl) findViewById(R.id.eu_control)).initEUControl(cars.get(0));
						}
						
						initSharedCar();
					}
				});
			}
			
			Helpers.Constants.mMyCarRequested = true;
		}

		@Override
		public void onFinishRequestingCallToActions() throws RemoteException {}
		@Override
		public void onFinishRequestingTraffics() throws RemoteException {
			Helpers.Constants.mFinishRequestingTraffics = true;
			runOnUiThread(new Runnable() {
				public void run() {
					((ViewFlipper) findViewById(R.id.initialpage).findViewById(R.id.top_news_list_flipper)).setDisplayedChild(0);
					((InitialPage) findViewById(R.id.initialpage)).updateTrafficList();
					((no.incent.viking.widget.Traffic) findViewById(R.id.traffic)).loadTraffics();
				}
			});
		}
		@Override
		public void onFinishRequestingNews() throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					((ViewFlipper) findViewById(R.id.news).findViewById(R.id.news_list_flipper)).setDisplayedChild(0);
					((no.incent.viking.widget.News) findViewById(R.id.news)).loadNews();
				}
			});
		}
		@Override
		public void onFinishRequestingPhoneCategories() throws RemoteException {
			Helpers.Constants.mFinishRequestingPhoneCategories = true;
		}
	};

	@Override
	public void onLoadAllTraffic() {
		((ViewFlipper) findViewById(R.id.initialpage).findViewById(R.id.top_news_list_flipper)).setDisplayedChild(0);
		((InitialPage) findViewById(R.id.initialpage)).updateTrafficList();
	}
}
