package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.CarEvent;
import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.pojo.CarEventSound;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.AudioRecorder;
import no.incent.viking.util.MyCarEntity;
import no.incent.viking.db.DBCarEventAdapter;
import no.incent.viking.db.DBCarEventPictureAdapter;
import no.incent.viking.db.DBCarEventSoundAdapter;
import no.incent.viking.adapter.MyCarEventsAdapter;
import no.incent.viking.adapter.CarEventPictureAdapter;
import no.incent.viking.adapter.CarEventSoundAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import android.app.AlertDialog;
import android.os.Handler;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.AdapterView;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MyCarEvents extends MyCarEntity {
	private final String TAG = "VIKING";
	private final Handler handler = new Handler();
	private Context context;
	private HttpRequest request;
	private DBCarEventAdapter carEventAdapter;
	private DBCarEventPictureAdapter carEventPictureAdapter;
	private DBCarEventSoundAdapter carEventSoundAdapter;
	private ListView mycar_events_list;
	private ListView recorded_sound_list;
	private int ownerId = 0;
	private ViewFlipper car_events_flipper;
	private EditText event_navn_field;
	private EditText event_regnr_field;
	private EditText event_hendelse_field;
	private EditText event_sted_field;
	private EditText event_datetime_field;
	private EditText event_notat_field;
	private GridView pictures_list;
	private CarEvent carEvent = new CarEvent();
	private File cameraImage;
	private File cameraImageThumb;
	public static final List<CarEvent> mCarEvents = new ArrayList<CarEvent>();
	private static final List<CarEventPicture> mCarEventPictures = new ArrayList<CarEventPicture>();
	private static final List<CarEventSound> mCarEventSounds = new ArrayList<CarEventSound>();
	private List<CarEventPicture> tempCarEventPictures = new ArrayList<CarEventPicture>();
	private List<CarEventSound> tempCarEventSounds = new ArrayList<CarEventSound>();
	private boolean mRecording = false;
	private String mAudioName;
	private AudioRecorder audioRecorder;
	private int activeEventId;
	private CarEvent activeCarEvent;
	private boolean mFromInfo = false;
	private List<CarEventPicture> carEventPictures;
	private File carEventPictureDirectory;
	private File carEventSoundDirectory;
	private boolean mNewCarEvent = false;
	
	public MyCarEvents(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.mycar_events, this);
		
		if(((VikingApplication) context.getApplicationContext()).getUser() != null) {
			ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
		}
		this.context = context;
		request = HttpRequest.getInstance();
		carEventAdapter = new DBCarEventAdapter(context);
		carEventPictureAdapter = new DBCarEventPictureAdapter(context);
		carEventSoundAdapter = new DBCarEventSoundAdapter(context);
		mycar_events_list = (ListView) findViewById(R.id.events_list);
		recorded_sound_list = (ListView) findViewById(R.id.recorded_sound_list);
		car_events_flipper = (ViewFlipper) findViewById(R.id.car_events_flipper);
		
		car_events_flipper.setInAnimation(context, R.anim.fade_in);
		car_events_flipper.setOutAnimation(context, R.anim.fade_out);
		
		event_navn_field = (EditText) findViewById(R.id.event_navn_field);
		event_regnr_field = (EditText) findViewById(R.id.event_regnr_field);
		event_hendelse_field = (EditText) findViewById(R.id.event_hendelse_field);
		event_sted_field = (EditText) findViewById(R.id.event_sted_field);
		event_datetime_field  = (EditText) findViewById(R.id.event_datetime_field);
		event_notat_field = (EditText) findViewById(R.id.event_notat_field);
		pictures_list = (GridView) findViewById(R.id.pictures_list);
		
		audioRecorder = new AudioRecorder(context);
		
		initEvents();
	}
	
	private void initEvents() {
		pictures_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(carEventPictures != null) {
					CarEventPicture carEventPicture;
					
					if(mNewCarEvent) {
						carEventPicture = tempCarEventPictures.get(position);
					} else {
						carEventPicture = carEventPictures.get(position);
					}
					((OnMyCarEventsCallback) context).onCarEventPictureClicked(carEventPicture);
				}
			}
		});
		
		findViewById(R.id.add_new_event_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				event_navn_field.setText("");
				event_regnr_field.setText("");
				event_hendelse_field.setText("");
				event_sted_field.setText("");
				event_datetime_field.setText("");
				event_notat_field.setText("");
				pictures_list.setAdapter(null);
				recorded_sound_list.setAdapter(null);
				car_events_flipper.setDisplayedChild(2);
				findViewById(R.id.next_informasjon_btn).setVisibility(View.VISIBLE);
				findViewById(R.id.next_bilder_btn).setVisibility(View.VISIBLE);
				findViewById(R.id.save_event_btn).setVisibility(View.VISIBLE);
				mNewCarEvent = true;
				tempCarEventPictures.clear();
				tempCarEventSounds.clear();
			}
		});
		findViewById(R.id.back_event_menu).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				car_events_flipper.showPrevious();
			}
		});
		findViewById(R.id.car_event_informasjon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CarEvent ce;
				
				if(Helpers.Constants.mIsLoggedIn) {
					carEventAdapter.openReadable();
					ce = carEventAdapter.getCarEvent(activeEventId);
					carEventAdapter.close();
				} else {
					ce = activeCarEvent;
				}
				
				event_navn_field.setText(ce.getName());
				event_regnr_field.setText(ce.getRegistration());
				event_hendelse_field.setText(ce.getEvent());
				event_sted_field.setText(ce.getPlace());
				event_datetime_field.setText(ce.getDateTime());
				event_notat_field.setText(ce.getNote());
				car_events_flipper.setDisplayedChild(2);
				findViewById(R.id.next_informasjon_btn).setVisibility(View.GONE);
				mFromInfo = true;
			}
		});
		findViewById(R.id.car_event_bilder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Helpers.Constants.mIsLoggedIn) {
					carEventPictureAdapter.open();
					carEventPictures = carEventPictureAdapter.getAllCarEventPictures(ownerId, activeEventId);
					carEventPictureAdapter.close();
				} else {
					carEventPictures = new ArrayList<CarEventPicture>();
					
					for(CarEventPicture cep: mCarEventPictures) {
						if(cep.getEventId() == activeEventId) {
							carEventPictures.add(cep);
						}
					}
				}
				
				CarEventPictureAdapter adapter = new CarEventPictureAdapter(context, R.layout.list_car_event_picture, carEventPictures);
				adapter.setDropDownViewResource(R.layout.list_car_event_picture);
				pictures_list.setAdapter(adapter);
				car_events_flipper.setDisplayedChild(3);
				findViewById(R.id.next_bilder_btn).setVisibility(View.GONE);
				mFromInfo = true;
			}
		});
		findViewById(R.id.car_event_lydopptak).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				carEventSoundAdapter.open();
				List<CarEventSound> carEventSounds = null;
				
				if(Helpers.Constants.mIsLoggedIn) {
					carEventSounds = carEventSoundAdapter.getAllCarEventSounds(ownerId, activeEventId);
				} else {
					carEventSounds = new ArrayList<CarEventSound>();
					
					for(CarEventSound ces: mCarEventSounds) {
						if(ces.getEventId() == activeEventId) {
							carEventSounds.add(ces);
						}
					}
				}
				
				CarEventSoundAdapter adapter = new CarEventSoundAdapter(context, R.layout.list_recorded_sound, carEventSounds);
				adapter.setDropDownViewResource(R.layout.list_recorded_sound);
				recorded_sound_list.setAdapter(adapter);
				recorded_sound_list.setDivider(null);
				carEventSoundAdapter.close();
				car_events_flipper.setDisplayedChild(4);
				findViewById(R.id.save_event_btn).setVisibility(View.GONE);
				mFromInfo = true;
			}
		});
		findViewById(R.id.abort_informasjon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mFromInfo) {
					event_navn_field.setText("");
					event_regnr_field.setText("");
					event_hendelse_field.setText("");
					event_sted_field.setText("");
					event_datetime_field.setText("");
					event_notat_field.setText("");
					car_events_flipper.setDisplayedChild(1);
					mFromInfo = false;
				} else {
					car_events_flipper.setDisplayedChild(0);
				}
			}
		});
		findViewById(R.id.prev_informasjon_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mFromInfo) {
					car_events_flipper.setDisplayedChild(1);
					mFromInfo = false;
				} else {
					car_events_flipper.setDisplayedChild(0);
				}
			}
		});
		findViewById(R.id.next_informasjon_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = event_navn_field.getText().toString();
				String registration  = event_regnr_field.getText().toString();
				String event = event_hendelse_field.getText().toString();
				String place = event_sted_field.getText().toString();
				String dateTime = event_datetime_field.getText().toString();
				String note = event_notat_field.getText().toString();
				
				if(!name.equals("") && !registration.equals("") && !event.equals("") &&
						!place.equals("") && !dateTime.equals("") && !note.equals("")) {
					carEvent = new CarEvent();
					carEvent.setUid((int) System.currentTimeMillis());
					carEvent.setOwnerId(ownerId);
					carEvent.setName(name);
					carEvent.setRegistration(registration);
					carEvent.setEvent(event);
					carEvent.setPlace(place);
					carEvent.setDateTime(dateTime);
					carEvent.setNote(note);
					car_events_flipper.showNext();
				}
			}
		});
		
		findViewById(R.id.save_bilder_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cameraImage != null && cameraImageThumb != null) {
					String name = ((EditText) findViewById(R.id.picture_bilder_text)).getText().toString();
					
					if(name.equals("")) {
						Helpers.showMessage(context, "", "Gi filen ett navn og trykk lagrekanppen til høyre");
					} else {
						try {
							CarEventPicture carEventPicture = new CarEventPicture();
							carEventPicture.setEventId(carEvent.getUid());
							carEventPicture.setOwnerId(ownerId);
							carEventPicture.setName(name);
							byte[] data = Helpers.toByteArray(new FileInputStream(cameraImage));
							carEventPicture.setImageFile(cameraImage);
							carEventPicture.setImageFileThumb(cameraImageThumb);
							carEventPicture.setPath(cameraImage.getAbsolutePath());
							tempCarEventPictures.add(carEventPicture);
							mCarEventPictures.add(carEventPicture);
							
							CarEventPictureAdapter adapter = new CarEventPictureAdapter(context, 
									R.layout.list_car_event_picture, tempCarEventPictures);
							adapter.setDropDownViewResource(R.layout.list_car_event_picture);
							pictures_list.setAdapter(adapter);
							cameraImage = null;
							cameraImageThumb = null;
							((EditText) findViewById(R.id.picture_bilder_text)).setText("");
						} catch(IOException ex) {
							Log.e(TAG, ex.getMessage());
						}
					}
				} else {
					Helpers.showMessage(context, "Error", "Please take a picture");
				}
			}
		});
		findViewById(R.id.abort_bilder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mFromInfo) {
					pictures_list.setAdapter(null);
					car_events_flipper.setDisplayedChild(1);
					mFromInfo = false;
				} else {
					car_events_flipper.setDisplayedChild(0);
				}
			}
		});
		findViewById(R.id.prev_bilder_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mFromInfo) {
					car_events_flipper.setDisplayedChild(1);
					mFromInfo = false;
				} else {
					car_events_flipper.showPrevious();
				}
				
			}
		});
		
		findViewById(R.id.next_bilder_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				car_events_flipper.showNext();
			}
		});
		
		findViewById(R.id.record_sound_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mRecording) {
					LinearLayout linearLayout = new LinearLayout(context);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					linearLayout.setLayoutParams(layoutParams);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					final TextView textView = new TextView(context);
					textView.setGravity(Gravity.CENTER_HORIZONTAL);
					textView.setText("Filename");
					textView.setTextColor(Color.WHITE);
					final EditText filename = new EditText(context);
					filename.setGravity(Gravity.CENTER_HORIZONTAL);
					linearLayout.addView(textView);
					linearLayout.addView(filename);
					
					AlertDialog alert = new AlertDialog.Builder(context)
							.setView(linearLayout)
							.setCancelable(true)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(!filename.getText().toString().equals("")) {
										mRecording = true;
										mAudioName = filename.getText().toString();
										audioRecorder.startRecording(mAudioName);
										((ImageButton) findViewById(R.id.record_sound_btn)).setImageResource(R.drawable.record_sound_btn_active);
										dialog.dismiss();
									}
								}
							}).create();
					alert.show();
				} else {
					audioRecorder.stopRecording();
					CarEventSound carEventSound = new CarEventSound();
					carEventSound.setEventId(carEvent.getUid());
					carEventSound.setOwnerId(ownerId);
					carEventSound.setName(mAudioName);
					//carEventSound.setSound(Helpers.toByteArray(new FileInputStream(audioRecorder.getAudioFile())));
					carEventSound.setPath(audioRecorder.getAudioFile().getAbsolutePath());
					carEventSound.setSoundFile(audioRecorder.getAudioFile());
					tempCarEventSounds.add(carEventSound);
					mCarEventSounds.add(carEventSound);
					CarEventSoundAdapter adapter = new CarEventSoundAdapter(context, R.layout.list_recorded_sound, tempCarEventSounds);
					adapter.setDropDownViewResource(R.layout.list_recorded_sound);
					recorded_sound_list.setAdapter(adapter);
					recorded_sound_list.setDivider(null);
					((ImageButton) findViewById(R.id.record_sound_btn)).setImageResource(R.drawable.record_sound_btn);
					mRecording = false;
				}
			}
		});
		findViewById(R.id.abort_lydopptak).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mFromInfo) {
					recorded_sound_list.setAdapter(null);
					car_events_flipper.setDisplayedChild(1);
					mFromInfo = false;
				} else {
					car_events_flipper.showPrevious();
				}
			}
		});
		findViewById(R.id.prev_lydopptak_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mFromInfo) {
					car_events_flipper.setDisplayedChild(1);
					mFromInfo = false;
				} else {
					car_events_flipper.showPrevious();
				}
			}
		});
		
		findViewById(R.id.save_event_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.event_loader).setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					public void run() {
						if(Helpers.Constants.mIsLoggedIn) {
							if(saveCarEvent(carEvent)) {
								carEventAdapter.openReadable();
								final List<CarEvent> carEvents = carEventAdapter.getAllCarEvents(ownerId);
								carEventAdapter.close();
								
								handler.post(new Runnable() {
									public void run() {
										MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, R.layout.list_mycar_events, carEvents);
										adapter.setDropDownViewResource(R.layout.list_mycar_events);
										mycar_events_list.setAdapter(adapter);
										car_events_flipper.setDisplayedChild(0);
										findViewById(R.id.event_loader).setVisibility(View.GONE);
										
										adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
											@Override
											public void onItemClick(CarEvent carEvent) {
												activeCarEvent = carEvent;
												activeEventId = carEvent.getUid();
												((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
												car_events_flipper.showNext();
												mNewCarEvent = false;
											}
										});
									}
								});
							}
						} else {
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							carEvent.setDateCreated(df.format(new Date()));
							mCarEvents.add(carEvent);
							
							handler.post(new Runnable() {
								public void run() {
									MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, R.layout.list_mycar_events, mCarEvents);
									adapter.setDropDownViewResource(R.layout.list_mycar_events);
									mycar_events_list.setAdapter(adapter);
									car_events_flipper.setDisplayedChild(0);
									findViewById(R.id.event_loader).setVisibility(View.GONE);
									
									adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
										@Override
										public void onItemClick(CarEvent carEvent) {
											activeCarEvent = carEvent;
											activeEventId = carEvent.getUid();
											((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
											car_events_flipper.showNext();
											mNewCarEvent = false;
										}
									});
								}
							});
						}
					}
				}).start();
			}
		});
	}
	
	private synchronized List<CarEvent> getAllCarEvents(int ownerId) {
		List<CarEvent> carEvents = new ArrayList<CarEvent>();
		String jsonString = request.send(context.getString(R.string.api_url) + 
				String.format("car_events/%s/json", ownerId), null, HttpRequest.GET);
			
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_events = json.getJSONArray("car_events");
				
				carEventAdapter.openWritable();
				carEventAdapter.deleteAll(ownerId);
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
				Log.e(TAG, ex.getMessage());
			}
		} else {
			carEventAdapter.openReadable();
			carEvents = carEventAdapter.getAllCarEvents(ownerId);
			carEventAdapter.close();
		}
		
		return carEvents;
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
		
		String result = request.send(context.getString(R.string.api_url) + "save_car_events", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				carEvent.setUid(Integer.parseInt(Helpers.parseXMLNode(result, "uid")));
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				carEvent.setDateCreated(formatter.format(new Date()));
				carEventAdapter.openWritable();
				carEventAdapter.insertCarEvent(carEvent);
				carEventAdapter.close();
				
				for(CarEventPicture carEventPicture: tempCarEventPictures) {
					carEventPicture.setEventId(carEvent.getUid());
					saveCarEventPictures(carEventPicture, carEvent.getUid());
				}
				for(CarEventSound carEventSound: tempCarEventSounds) {
					carEventSound.setEventId(carEvent.getUid());
					saveCarEventSound(carEventSound, carEvent.getUid());
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean saveCarEventPictures(CarEventPicture carEventPicture, int eventId) {
		Map<String, String> queries = new HashMap<String, String>();;
		queries.put("event_id", String.valueOf(eventId));
		queries.put("owner_id", String.valueOf(carEventPicture.getOwnerId()));
		queries.put("name", carEventPicture.getName());
		
		String result = request.sendMultipart(context.getString(R.string.api_url) + "save_car_events_pictures", carEventPicture.getImageFile(), queries);
		
		if(result != null) {
			carEventPicture.setEventId(eventId);
			carEventPictureAdapter.open();
			carEventPictureAdapter.insertCarEventPicture(carEventPicture);
			carEventPictureAdapter.close();
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		return false;
	}
	
	private boolean saveCarEventSound(CarEventSound carEventSound, int eventId) {
		Map<String, String> queries = new HashMap<String, String>();
		queries.put("event_id", String.valueOf(eventId));
		queries.put("owner_id", String.valueOf(carEventSound.getOwnerId()));
		queries.put("name", carEventSound.getName());
		
		String result = request.sendMultipart(context.getString(R.string.api_url) + "save_car_events_sound", carEventSound.getSoundFile(), queries);
		
		if(result != null) {
			carEventSound.setEventId(eventId);
			carEventSoundAdapter.open();
			carEventSoundAdapter.insertCarEventSound(carEventSound);
			carEventSoundAdapter.close();
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<CarEventPicture> getAllCarEventPictures(int ownerId, int eventId) {
		List<CarEventPicture> carEventPictures = new ArrayList<CarEventPicture>();
		
		carEventPictureAdapter.open();
		carEventPictureAdapter.deleteAll(ownerId);
		
		String jsonString = request.send(context.getString(R.string.api_url) + 
				String.format("car_events_pictures/%s/json/%s", ownerId, eventId), null, HttpRequest.GET);
		
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
					//carEventPictureAdapter.insertCarEventPicture(carEventPicture);
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		carEventPictureAdapter.close();
		
		return carEventPictures;
	}
	
	private List<CarEventSound> getAllCarEventSounds(int ownerId, int eventId) {
		List<CarEventSound> carEventSounds = new ArrayList<CarEventSound>();
		
		carEventSoundAdapter.open();
		carEventSoundAdapter.deleteAll(ownerId);
		String jsonString = request.send(context.getString(R.string.api_url) + 
				String.format("car_events_sound/%s/json/%s", ownerId, eventId), null, HttpRequest.GET);
		
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
					//carEventSoundAdapter.insertCarEventSound(carEventSound);
				}
				
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
			
			carEventSoundAdapter.close();
		}
		carEventSoundAdapter.close();
		
		return carEventSounds;
	}
	
	public void setCameraImage(File cameraImage) {
		this.cameraImage = cameraImage;
	}
	
	public void setCameraImageThumb(File cameraImageThumb) {
		this.cameraImageThumb = cameraImageThumb;
	}
	
	public void updateCarEvents() {
		List<CarEvent> carEvents = carEventAdapter.getAllCarEvents(ownerId);
		MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, R.layout.list_mycar_events, carEvents);
		adapter.setDropDownViewResource(R.layout.list_mycar_events);
		mycar_events_list.setAdapter(adapter);
		
		adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(CarEvent carEvent) {
				activeCarEvent = carEvent;
				activeEventId = carEvent.getUid();
				((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
				car_events_flipper.showNext();
				mNewCarEvent = false;
			}
		});
	}
	
	public void updateCarEventsNotLoggedIn() {
		MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, R.layout.list_mycar_events, mCarEvents);
		adapter.setDropDownViewResource(R.layout.list_mycar_events);
		mycar_events_list.setAdapter(adapter);
		
		adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(CarEvent carEvent) {
				activeCarEvent = carEvent;
				activeEventId = carEvent.getUid();
				((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
				car_events_flipper.showNext();
				mNewCarEvent = false;
			}
		});
	}

	@Override
	public void load() {
		Helpers.createDirectory("viking/carevents");
		carEventPictureDirectory = Helpers.createDirectory("viking/carevents/picture");
		carEventSoundDirectory = Helpers.createDirectory("viking/carevents/sound");
		
		/*
		if(Helpers.isNetworkAvailable(context)) {
			((ViewFlipper) findViewById(R.id.car_event_main_flipper)).showNext();
			new Thread(new Runnable() {
				public void run() {
					if(((VikingApplication) context.getApplicationContext()).getUser() != null) {
						ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
					}
					final List<CarEvent> carEvents = getAllCarEvents(ownerId);
					handler.post(new Runnable() {
						public void run() { 
							MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, R.layout.list_mycar_events, carEvents);
							adapter.setDropDownViewResource(R.layout.list_mycar_events);
							mycar_events_list.setAdapter(adapter);
							
							adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
								@Override
								public void onItemClick(CarEvent carEvent) {
									activeCarEvent = carEvent;
									activeEventId = carEvent.getUid();
									((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
									car_events_flipper.showNext();
									mNewCarEvent = false;
								}
							});
							
							((ViewFlipper) findViewById(R.id.car_event_main_flipper)).showPrevious();
						}
					});
				}
			}).start();
		} else {
			carEventAdapter.openReadable();
			MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, 
					R.layout.list_mycar_events, carEventAdapter.getAllCarEvents(ownerId));
			adapter.setDropDownViewResource(R.layout.list_mycar_events);
			mycar_events_list.setAdapter(adapter);
			carEventAdapter.close();
			
			adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(CarEvent carEvent) {
					activeCarEvent = carEvent;
					
					activeEventId = carEvent.getUid();
					((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
					car_events_flipper.showNext();
					mNewCarEvent = false;
				}
			});
		}*/
		
		if(((VikingApplication) context.getApplicationContext()).getUser() != null) {
			ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
		}
		carEventAdapter.openReadable();
		List<CarEvent> carEvents = carEventAdapter.getAllCarEvents(ownerId);
		if(!Helpers.Constants.mIsLoggedIn) {
			carEvents = mCarEvents;
		}
		MyCarEventsAdapter adapter = new MyCarEventsAdapter(context, 
				R.layout.list_mycar_events, carEvents);
		adapter.setDropDownViewResource(R.layout.list_mycar_events);
		mycar_events_list.setAdapter(adapter);
		carEventAdapter.close();
		
		adapter.setOnItemClickListener(new MyCarEventsAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(CarEvent carEvent) {
				activeCarEvent = carEvent;
				
				activeEventId = carEvent.getUid();
				((TextView) findViewById(R.id.car_event_name)).setText(carEvent.getEvent());
				car_events_flipper.showNext();
				mNewCarEvent = false;
			}
		});
		
		
	}
	
	public static interface OnMyCarEventsCallback {
		public void onCarEventPictureClicked(CarEventPicture carEventPicture);
	}

	@Override
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
	public static List<CarEvent> getAllTempCarEvents() {
		return mCarEvents;
	}
	
	public static List<CarEventPicture> getAllTempCarEventPictures() {
		return mCarEventPictures;
	}
	
	public static List<CarEventSound> getAllTempCarEventSounds() {
		return mCarEventSounds;
	}
	
	public static void addTempCarEvent(CarEvent carEvent) {
		mCarEvents.add(carEvent);
	}
	
	public static void addTempCarEventPictures(CarEventPicture carEventPicture) {
		mCarEventPictures.add(carEventPicture);
	}
}
