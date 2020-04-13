package no.incent.viking.widget;

import no.incent.viking.MainMenuActivity;
import no.incent.viking.R;
import no.incent.viking.db.DBTrafficAdapter;
import no.incent.viking.map.TrafficItemizedOverlay;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.adapter.TrafficAdapter;
import no.incent.viking.util.PreferenceWrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Bundle;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.ViewFlipper;
import android.widget.ExpandableListView;
import android.widget.EditText;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.MapController;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MyLocationOverlay;

public class Traffic extends LinearLayout {
	private final String TAG = "VIKING";
	private Context context;
	private HttpRequest request;
	private DBTrafficAdapter trafficAdapter;
	private List<no.incent.viking.pojo.Traffic> traffics;
	private MapView traffic_map;
	private MapController controller;
	private Location location;
	private int latitude;
	private int longitude;
	private ProgressBar refresh_map_loader;
	private ViewFlipper traffic_flipper;
	private ExpandableListView traffic_expandable_list;
	private final Handler handler = new Handler();
	private final int SORT_AREA_NAME = 1;
	private final int SORT_ROAD_NAME = 2;
	private final int SORT_TIME = 3;
	private int TRAFFIC_SORT = SORT_AREA_NAME;
	private String search = null;
	private List<List<no.incent.viking.pojo.Traffic>> traffic_list;
	private final int PAGE_MAP = 1;
	private final int PAGE_LIST = 2;
	private int TRAFFIC_PAGE = PAGE_MAP;
	private int activeRoadId;
	private PreferenceWrapper prefsWrapper;
	
	public Traffic(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.traffic, this);
		
		this.context = context;
		request = HttpRequest.getInstance();
		trafficAdapter = new DBTrafficAdapter(context);
		prefsWrapper = new PreferenceWrapper(context);
		
		traffic_map = (MapView) findViewById(R.id.traffic_map);
		refresh_map_loader = (ProgressBar) findViewById(R.id.refresh_map_loader);
		traffic_flipper = (ViewFlipper) findViewById(R.id.traffic_flipper);
		traffic_flipper.setInAnimation(context, R.anim.fade_in);
		traffic_flipper.setOutAnimation(context, R.anim.fade_out);
		traffic_expandable_list = (ExpandableListView) findViewById(R.id.traffic_expandable_list);
		
		controller = traffic_map.getController();
		controller.setZoom(10);
		
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			if(location != null) {
				latitude = (int) (location.getLatitude() * 1E6);
				longitude = (int) (location.getLongitude() * 1E6);
				controller.setCenter(new GeoPoint(latitude, longitude));
			}
		} else {
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			if(location != null) {
				latitude = (int) (location.getLatitude() * 1E6);
				longitude = (int) (location.getLongitude() * 1E6);
				controller.setCenter(new GeoPoint(latitude, longitude));
			}
		}
		
		findViewById(R.id.refresh_map).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				refresh_map_loader.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					public void run() {
						refreshMap();
						handler.post(new Runnable() {
							public void run() {
								refresh_map_loader.setVisibility(View.GONE);
								if(location != null) {
									latitude = (int) (location.getLatitude() * 1E6);
									longitude = (int) (location.getLongitude() * 1E6);
									controller.setCenter(new GeoPoint(latitude, longitude));
								}
								displayTraffics();
							}
						});
					}
				}).start();
			}
		});
		
		findViewById(R.id.traffic_map_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Button) v).setBackgroundResource(R.drawable.tr_submenu_btn_active);
				((Button) findViewById(R.id.traffic_list_btn)).setBackgroundResource(R.drawable.tr_submenu_btn);
				traffic_flipper.setDisplayedChild(0);
				TRAFFIC_PAGE = PAGE_MAP;
			}
		});
		
		findViewById(R.id.traffic_list_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Button) findViewById(R.id.traffic_map_btn)).setBackgroundResource(R.drawable.tr_submenu_btn);
				((Button) v).setBackgroundResource(R.drawable.tr_submenu_btn_active);
				traffic_flipper.setDisplayedChild(1);
				
				trafficAdapter.openReadable();
				traffic_list =  trafficAdapter.getAllTrafficsByAreaName(search);
				TrafficAdapter adapter = new TrafficAdapter(context, trafficAdapter.getAreaNameGroup(search), traffic_list);
				trafficAdapter.close();
				
				traffic_expandable_list.setAdapter(adapter);
				for(int i=0;i<adapter.getGroupCount();i++) {
					traffic_expandable_list.expandGroup(i);
				}
				TRAFFIC_PAGE = PAGE_LIST;
			}
		});
		
		findViewById(R.id.sort_area_name).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.sort_county_btn_active);
				findViewById(R.id.sort_road_name).setBackgroundResource(R.drawable.sort_road_btn);
				findViewById(R.id.sort_time).setBackgroundResource(R.drawable.sort_time_btn);
				
				trafficAdapter.openReadable();
				traffic_list = trafficAdapter.getAllTrafficsByAreaName(search);
				TrafficAdapter adapter = new TrafficAdapter(context, trafficAdapter.getAreaNameGroup(search), traffic_list);
				trafficAdapter.close();
				
				traffic_expandable_list.setAdapter(adapter);
				for(int i=0;i<adapter.getGroupCount();i++) {
					traffic_expandable_list.expandGroup(i);
				}
				TRAFFIC_SORT = SORT_AREA_NAME;
			}
		});
		
		findViewById(R.id.sort_road_name).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.sort_road_btn_active);
				findViewById(R.id.sort_area_name).setBackgroundResource(R.drawable.sort_county_btn);
				findViewById(R.id.sort_time).setBackgroundResource(R.drawable.sort_time_btn);
				
				trafficAdapter.openReadable();
				traffic_list = trafficAdapter.getAllTrafficsByRoadName(search);
				TrafficAdapter adapter = new TrafficAdapter(context, trafficAdapter.getRoadNameGroup(search), traffic_list);
				trafficAdapter.close();
				
				traffic_expandable_list.setAdapter(adapter);
				for(int i=0;i<adapter.getGroupCount();i++) {
					traffic_expandable_list.expandGroup(i);
				}
				TRAFFIC_SORT = SORT_ROAD_NAME;
			}
		});
		
		findViewById(R.id.sort_time).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.sort_time_btn_active);
				findViewById(R.id.sort_area_name).setBackgroundResource(R.drawable.sort_county_btn);
				findViewById(R.id.sort_road_name).setBackgroundResource(R.drawable.sort_road_btn);
				
				trafficAdapter.openReadable();
				traffic_list = trafficAdapter.getAllTrafficsByTime(search);
				TrafficAdapter adapter = new TrafficAdapter(context, trafficAdapter.getTimeGroup(search), traffic_list);
				trafficAdapter.close();
				
				traffic_expandable_list.setAdapter(adapter);
				for(int i=0;i<adapter.getGroupCount();i++) {
					traffic_expandable_list.expandGroup(i);
				}
				TRAFFIC_SORT = SORT_TIME;
			}
		});
		
		((EditText) findViewById(R.id.search_field)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					if(!v.getText().toString().equals("")) {
						searchTraffic(v.getText().toString());
						search = v.getText().toString();
						return true;
					}
				}
				return false;
			}
		});
		
		findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String s = ((EditText) findViewById(R.id.search_field)).getText().toString();
				searchTraffic(s);
				search = s;
			}
		});
		
		findViewById(R.id.refresh_list).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.refresh_list_loader).setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					public void run() {
						refreshMap();
						handler.post(new Runnable() {
							public void run() {
								search = null;
								refreshList();
								findViewById(R.id.refresh_list_loader).setVisibility(View.GONE);
							}
						});
					}
				}).start();
			}
		});
		
		traffic_expandable_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				if(traffic_list != null) {
					no.incent.viking.pojo.Traffic traffic = traffic_list.get(groupPosition).get(childPosition);
					((TextView) findViewById(R.id.traffic_road_name)).setText(traffic.getRoadName());
					((TextView) findViewById(R.id.traffic_optional_text)).setText(traffic.getOptionalText());
					((TextView) findViewById(R.id.traffic_time)).setText("Sist oppdatert " + traffic.getStartTime());
					((TextView) findViewById(R.id.traffic_area_name)).setText("Kilde: " + traffic.getAreaName());
					traffic_flipper.setDisplayedChild(2);
					activeRoadId = traffic.getRoadId();
				}
				return false;
			}
		});
		
		findViewById(R.id.traffic_info_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(TRAFFIC_PAGE) {
					case PAGE_MAP:
						traffic_flipper.setDisplayedChild(0);
						break;
					case PAGE_LIST:
						traffic_flipper.setDisplayedChild(1);
						break;
				}
			}
		});
		
		findViewById(R.id.refresh_traffic_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.refresh_info_loader).setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					public void run() {
						refreshMap();
						handler.post(new Runnable() {
							public void run() {
								no.incent.viking.pojo.Traffic traffic;
								
								trafficAdapter.openReadable();
								traffic = trafficAdapter.getTraffic(activeRoadId);
								trafficAdapter.close();
								
								((TextView) findViewById(R.id.traffic_road_name)).setText(traffic.getRoadName());
								((TextView) findViewById(R.id.traffic_optional_text)).setText(traffic.getOptionalText());
								((TextView) findViewById(R.id.traffic_time)).setText("Sist oppdatert " + traffic.getStartTime());
								((TextView) findViewById(R.id.traffic_area_name)).setText("Kilde: " + traffic.getAreaName());
								findViewById(R.id.refresh_info_loader).setVisibility(View.GONE);
							}
						});
					}
				}).start();
			}
		});
		
		findViewById(R.id.load_all_map).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadAllTraffics();
				((OnTrafficListener) context).onLoadAllTraffic();
			}
		});
		
		findViewById(R.id.load_all_list).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadAllTraffics();
				((OnTrafficListener) context).onLoadAllTraffic();
			}
		});
		
		loadTraffics();
	}
	
	public void loadTraffics() {
		trafficAdapter.openReadable();
		traffics = trafficAdapter.getAllTraffics(0);
		trafficAdapter.close();
		
		displayTraffics();
	}
	
	private void displayTraffics() {
		List<Overlay> mapOverlays = traffic_map.getOverlays();
		Drawable map_pin = context.getResources().getDrawable(R.drawable.map_pin);
		TrafficItemizedOverlay trafficOverlay = new TrafficItemizedOverlay(map_pin, context, traffic_map);
		
		traffic_map.getOverlays().clear();
		traffic_map.invalidate();
		
		final MyLocationOverlay myLocationOverlay = new MyLocationOverlay(context, traffic_map);
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				traffic_map.getController().animateTo(myLocationOverlay.getMyLocation());
			}
		});
		mapOverlays.add(myLocationOverlay);
		
		List<OverlayItem> items = new ArrayList<OverlayItem>();
		
		for(no.incent.viking.pojo.Traffic traffic: traffics) {
			if(!traffic.getLatitude().equals("") && !traffic.getLongitude().equals("")) {
				int latitude = (int) (Double.parseDouble(traffic.getLatitude()) * 1E6);
				int longitude = (int) (Double.parseDouble(traffic.getLongitude()) * 1E6);
				
				GeoPoint geoPoint = new GeoPoint(latitude, longitude);
				OverlayItem overlayItem = new OverlayItem(geoPoint, traffic.getRoadName(), String.valueOf(traffic.getRoadId()));
				items.add(overlayItem);
				//trafficOverlay.addItem(overlayItem);
			}
		}
		
		trafficOverlay.addAll(items);
	
		trafficOverlay.setOnMarkerClickListener(new TrafficItemizedOverlay.OnMarkerClickListener() {
			@Override
			public void onMarkerClick(int roadId) {
				no.incent.viking.pojo.Traffic traffic;
				
				trafficAdapter.openReadable();
				traffic = trafficAdapter.getTraffic(roadId);
				trafficAdapter.close();
				
				((TextView) findViewById(R.id.traffic_road_name)).setText(traffic.getRoadName());
				((TextView) findViewById(R.id.traffic_optional_text)).setText(traffic.getOptionalText());
				((TextView) findViewById(R.id.traffic_time)).setText("Sist oppdatert " + traffic.getStartTime());
				((TextView) findViewById(R.id.traffic_area_name)).setText("Kilde: " + traffic.getAreaName());
				traffic_flipper.setDisplayedChild(2);
				activeRoadId = roadId;
			}
		});
		mapOverlays.add(trafficOverlay);
	}
	
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			int latitude = (int) (location.getLatitude() * 1E6);
			int longitude = (int) (location.getLongitude() * 1E6);
			controller.setCenter(new GeoPoint(latitude, longitude));
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	
	private void refreshMap() {
		String segment = "p4webservice/json";
		
		if(prefsWrapper.getPreferenceBooleanValue(PreferenceWrapper.USE_LOCATION)) {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Location location = null;
			
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				
				if(location == null) {
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
			} else {
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			
			segment = "p4webservice_by_radius/" + location.getLatitude() + "/" + location.getLongitude() + "/json";
		}
		
		String trafficString = request.send(context.getString(R.string.api_url) + segment, 
				null, HttpRequest.GET);
		List<no.incent.viking.pojo.Traffic> trafficList = new ArrayList<no.incent.viking.pojo.Traffic>();
		
		if(trafficString != null) {
			try {
				JSONObject json = new JSONObject(trafficString);
				JSONArray traffics = json.getJSONArray("rtm_messages");
				
				trafficAdapter.openWriteable();
				trafficAdapter.deleteAll();
				
				this.traffics.clear();
				DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
				
				for(int i=0;i<traffics.length();i++) {
					JSONObject jsonTraffic = traffics.getJSONObject(i);
					no.incent.viking.pojo.Traffic traffic = new no.incent.viking.pojo.Traffic();
					
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
					//trafficAdapter.insertTraffic(traffic);
					this.traffics.add(traffic);
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
	
	private void loadAllTraffics() {
		final ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait..");
		progressDialog.show();
		
		new Thread(new Runnable() {
			public void run() {
				String trafficString = request.send(context.getString(R.string.api_url) + "p4webservice/json", 
						null, HttpRequest.GET);
				List<no.incent.viking.pojo.Traffic> trafficList = new ArrayList<no.incent.viking.pojo.Traffic>();
				
				if(trafficString != null) {
					try {
						JSONObject json = new JSONObject(trafficString);
						JSONArray traffics = json.getJSONArray("rtm_messages");
						
						trafficAdapter.openWriteable();
						trafficAdapter.deleteAll();
						
						Traffic.this.traffics.clear();
						DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
						
						for(int i=0;i<traffics.length();i++) {
							JSONObject jsonTraffic = traffics.getJSONObject(i);
							no.incent.viking.pojo.Traffic traffic = new no.incent.viking.pojo.Traffic();
							
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
							//trafficAdapter.insertTraffic(traffic);
							Traffic.this.traffics.add(traffic);
						}
						trafficAdapter.insertAll(trafficList);
						trafficAdapter.close();
					} catch(JSONException ex) {
						Log.e(TAG, ex.getMessage());
					} catch (ParseException ex) {
						Log.e(TAG, ex.getMessage());
					}
				}
				
				handler.post(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						//prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.USE_LOCATION, false);
						
						TrafficAdapter adapter = null;
						
						trafficAdapter.openReadable();
						
						switch(TRAFFIC_SORT) {
							case SORT_AREA_NAME:
								traffic_list = trafficAdapter.getAllTrafficsByAreaName(search);
								adapter = new TrafficAdapter(context, trafficAdapter.getAreaNameGroup(null), traffic_list);
								break;
							case SORT_ROAD_NAME:
								traffic_list = trafficAdapter.getAllTrafficsByRoadName(search);
								adapter = new TrafficAdapter(context, trafficAdapter.getRoadNameGroup(null), traffic_list);
								break;
							case SORT_TIME:
								traffic_list = trafficAdapter.getAllTrafficsByTime(search);
								adapter = new TrafficAdapter(context, trafficAdapter.getTimeGroup(null), traffic_list);
								break;
						}
						
						trafficAdapter.close();
						if(adapter != null) {
							traffic_expandable_list.setAdapter(adapter);
							
							for(int i=0;i<adapter.getGroupCount();i++) {
								traffic_expandable_list.expandGroup(i);
							}
						}
						
						Traffic.this.traffics.clear();
						for(List<no.incent.viking.pojo.Traffic> traffics: traffic_list) {
							for(no.incent.viking.pojo.Traffic traffic: traffics) {
								Traffic.this.traffics.add(traffic);
							}
						}
						displayTraffics();
					}
				});
			}
		}).start();
		
	}
	
	private void searchTraffic(String search) {
		TrafficAdapter adapter = null;
		
		trafficAdapter.openReadable();
		
		switch(TRAFFIC_SORT) {
			case SORT_AREA_NAME:
				traffic_list = trafficAdapter.getAllTrafficsByAreaName(search);
				adapter = new TrafficAdapter(context, trafficAdapter.getAreaNameGroup(search), traffic_list);
				break;
			case SORT_ROAD_NAME:
				traffic_list = trafficAdapter.getAllTrafficsByRoadName(search);
				adapter = new TrafficAdapter(context, trafficAdapter.getRoadNameGroup(search), traffic_list);
				break;
			case SORT_TIME:
				traffic_list = trafficAdapter.getAllTrafficsByTime(search);
				adapter = new TrafficAdapter(context, trafficAdapter.getTimeGroup(search), traffic_list);
				break;
		}
		
		trafficAdapter.close();
		if(adapter != null) {
			traffic_expandable_list.setAdapter(adapter);
			
			for(int i=0;i<adapter.getGroupCount();i++) {
				traffic_expandable_list.expandGroup(i);
			}
		}
		
		this.traffics.clear();
		for(List<no.incent.viking.pojo.Traffic> traffics: traffic_list) {
			for(no.incent.viking.pojo.Traffic traffic: traffics) {
				this.traffics.add(traffic);
			}
		}
		Log.i(TAG, "traffic count: " + this.traffics.size());
		displayTraffics();
	}
	
	private void refreshList() {
		TrafficAdapter adapter = null;
		
		trafficAdapter.openReadable();
		switch(TRAFFIC_SORT) {
			case SORT_AREA_NAME:
				traffic_list = trafficAdapter.getAllTrafficsByAreaName(null);
				adapter = new TrafficAdapter(context, trafficAdapter.getAreaNameGroup(null), traffic_list);
				break;
			case SORT_ROAD_NAME:
				traffic_list = trafficAdapter.getAllTrafficsByRoadName(null);
				adapter = new TrafficAdapter(context, trafficAdapter.getRoadNameGroup(null), traffic_list);
				break;
			case SORT_TIME:
				traffic_list = trafficAdapter.getAllTrafficsByTime(null);
				adapter = new TrafficAdapter(context, trafficAdapter.getTimeGroup(null), traffic_list);
				break;
		}
		trafficAdapter.close();
		
		if(adapter != null) {
			traffic_expandable_list.setAdapter(adapter);
			
			for(int i=0;i<adapter.getGroupCount();i++) {
				traffic_expandable_list.expandGroup(i);
			}
		}
	}
	
	public void showTraffic(no.incent.viking.pojo.Traffic traffic) {
		((TextView) findViewById(R.id.traffic_road_name)).setText(traffic.getRoadName());
		((TextView) findViewById(R.id.traffic_optional_text)).setText(traffic.getOptionalText());
		((TextView) findViewById(R.id.traffic_time)).setText("Sist oppdatert " + traffic.getStartTime());
		((TextView) findViewById(R.id.traffic_area_name)).setText("Kilde: " + traffic.getAreaName());
		traffic_flipper.setDisplayedChild(2);
		activeRoadId = traffic.getRoadId();
	}
	
	public static interface OnTrafficListener {
		void onLoadAllTraffic();
	}
}
