 package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Car;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.adapter.CarImageAdapter;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.widget.TextView;
import android.widget.Gallery;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.text.Editable;
import android.text.TextWatcher;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MyCarInfo extends LinearLayout {
	private final String TAG = "VIKING";
	private User user;
	private Car car;
	private ViewFlipper car_image_flipper;
	private Gallery car_image_gallery;
	private EditText car_info_regno;
	private TextView car_info_eier;
	private TextView car_info_chassis_number;
	private TextView car_info_reg_year;
	private TextView car_info_reg_norway;
	private TextView car_info_brand_code;
	private TextView car_info_car_model;
	private TextView car_info_engine_performance;
	private TextView car_info_displacement;
	private TextView car_info_fuel_type;
	private TextView car_info_length;
	private TextView car_info_width;
	private TextView car_info_weight;
	private TextView car_info_total_weight;
	private TextView car_info_colour;
	private TextView car_info_co2_emission;
	private TextView car_info_std_tire_front;
	private TextView car_info_std_tire_back;
	private TextView car_info_driving_wheels;
	private TextView car_info_trailer_weight_with_brakes;
	private TextView car_info_trailer_weight_without_brakes;
	private ImageButton edit_shared_car;
	private DBCarAdapter carAdapter;
	private List<Car> cars;
	public static final List<Car> tempCars = new ArrayList<Car>();
	private Context context;
	private HttpRequest request;
	private Handler handler;
	private NavigationDots navigation_dots;
	private boolean mAddMode = false;
	private int mCurrentIndex = 0;

	public MyCarInfo(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.mycar_info, this);
		
		user = ((VikingApplication) context.getApplicationContext()).getUser();
		handler = new Handler();
		request = HttpRequest.getInstance();
		
		car_image_flipper = (ViewFlipper) findViewById(R.id.car_image_flipper);
		car_image_gallery = (Gallery) findViewById(R.id.car_images_gallery);
		car_info_regno = (EditText) findViewById(R.id.car_info_regno);
		car_info_eier = (TextView) findViewById(R.id.car_info_eier);
		car_info_chassis_number = (TextView) findViewById(R.id.car_info_chassis_number);
		car_info_reg_year = (TextView) findViewById(R.id.car_info_reg_year);
		car_info_reg_norway = (TextView) findViewById(R.id.car_info_reg_norway);
		car_info_brand_code = (TextView) findViewById(R.id.car_info_brand_code);
		car_info_car_model = (TextView) findViewById(R.id.car_info_car_model);
		car_info_engine_performance = (TextView) findViewById(R.id.car_info_engine_performance);
		car_info_displacement = (TextView) findViewById(R.id.car_info_displacement);
		car_info_fuel_type = (TextView) findViewById(R.id.car_info_fuel_type);
		car_info_length = (TextView) findViewById(R.id.car_info_length);
		car_info_width = (TextView) findViewById(R.id.car_info_width);
		car_info_weight = (TextView) findViewById(R.id.car_info_weight);
		car_info_total_weight = (TextView) findViewById(R.id.car_info_total_weight);
		car_info_colour = (TextView) findViewById(R.id.car_info_colour);
		car_info_co2_emission = (TextView) findViewById(R.id.car_info_co2_emission);
		car_info_std_tire_front = (TextView) findViewById(R.id.car_info_std_tire_front);
		car_info_std_tire_back = (TextView) findViewById(R.id.car_info_std_tire_back);
		car_info_driving_wheels = (TextView) findViewById(R.id.car_info_driving_wheels);
		car_info_trailer_weight_with_brakes = (TextView) findViewById(R.id.car_info_trailer_weight_with_brakes);
		car_info_trailer_weight_without_brakes = (TextView) findViewById(R.id.car_info_trailer_weight_without_brakes);
		edit_shared_car = (ImageButton) findViewById(R.id.edit_shared_car);
		navigation_dots = (NavigationDots) findViewById(R.id.navigation_dots);
		
		carAdapter = new DBCarAdapter(context);
		
		if(user != null) {
			carAdapter.openReadable();
			cars = carAdapter.getAllCars(user.getUid());
			carAdapter.close();
			
			navigation_dots.setDotsCount(cars.size());
			
			if(cars.size() > 0) {
				Car tempCar = new Car();
				tempCar.setUid(-1);
				cars.add(tempCar);
			}
			
			CarImageAdapter adapter = new CarImageAdapter(context, cars);
			car_image_gallery.setAdapter(adapter);
			car_image_gallery.setCallbackDuringFling(true);
			
			car = ((VikingApplication) context.getApplicationContext()).getActiveCar();
			if(car != null) {
				updateInfo(car);
			}
			
			if(car_image_gallery.getAdapter().getCount() == 0) {
				car_info_regno.setEnabled(true);
			}
		} else {
			cars = new ArrayList<Car>();
			Car tempCar = new Car();
			tempCar.setUid(-1);
			tempCars.add(tempCar);
			CarImageAdapter adapter = new CarImageAdapter(context, tempCars);
			car_image_gallery.setAdapter(adapter);
		}
		
		
		car_image_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
				final Car car;
				
				if(Helpers.Constants.mIsLoggedIn && Helpers.Constants.mFinishRequestingAllCars) {
					car = cars.get(position);
				} else {
					car = tempCars.get(position);
				}
				
				if(car.getUid() != -1) {
					mAddMode = false;
					((VikingApplication) context.getApplicationContext()).setActiveCar(car);
					updateInfo(car);
					car_info_regno.setEnabled(false);
					navigation_dots.setSelectedDot(position);
					mCurrentIndex = position;
					
					Log.i(TAG, "is shared: " + car.isShared());
					if(car.isShared()) {
						if(Helpers.Constants.mIsLoggedIn)
							edit_shared_car.setVisibility(View.VISIBLE);
						findViewById(R.id.edit_img_btn).setVisibility(View.GONE);
						findViewById(R.id.share_car_btn).setVisibility(View.GONE);
						findViewById(R.id.owner_info_btn).setVisibility(View.GONE);
					} else {
						if(Helpers.Constants.mIsLoggedIn)
							edit_shared_car.setVisibility(View.GONE);
						findViewById(R.id.edit_img_btn).setVisibility(View.VISIBLE);
						findViewById(R.id.share_car_btn).setVisibility(View.VISIBLE);
						findViewById(R.id.owner_info_btn).setVisibility(View.VISIBLE);
					}
				} else {
					car_info_regno.setText("");
					car_info_eier.setText("");
					car_info_chassis_number.setText("");
					car_info_reg_year.setText("");
					car_info_reg_norway.setText("");
					car_info_brand_code.setText("");
					car_info_car_model.setText("");
					car_info_engine_performance.setText("");
					car_info_displacement.setText("");
					car_info_fuel_type.setText("");
					car_info_length.setText("");
					car_info_width.setText("");
					car_info_weight.setText("");
					car_info_total_weight.setText("");
					car_info_colour.setText("");
					car_info_co2_emission.setText("");
					car_info_std_tire_front.setText("");
					car_info_std_tire_back.setText("");
					car_info_driving_wheels.setText("");
					car_info_trailer_weight_with_brakes.setText("");
					car_info_trailer_weight_without_brakes.setText("");
					car_info_regno.setEnabled(true);
					mAddMode = true;
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		car_info_regno.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().replace(" ", "").length() == 7 && car_info_regno.isEnabled() && mAddMode) {
					Log.i(TAG, "adding car");
					InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(car_info_regno.getWindowToken(), 0);
					addCar(s.toString());
					mAddMode = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
		
		car_info_regno.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					addCar(v.getText().toString());
				}
				return false;
			}
		});
		
		edit_shared_car.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alert = new AlertDialog.Builder(context)
									.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									})
									.setItems(new CharSequence[] {"Slett bil"}, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if(which == 0) {
												final int uid = ((VikingApplication) context.getApplicationContext()).getActiveCar().getSharedCarId();
												
												new Thread(new Runnable() {
													public void run() {
														synchronized(Helpers.lockObject) {
															if(deleteSharedCar(uid)) {
																int carUid = ((VikingApplication) context.getApplicationContext()).getActiveCar().getUid();
																carAdapter.delete(carUid);
																updateCarInfo(true);
															}
														}
													}
												}).start();
												
											}
										}
									})
									.create();
				alert.show();
			}
		});
	}
	
	private void addCar(final String carRegNo) {
		car_info_regno.setEnabled(false);
		if(!carRegNo.equals("")) {
			for(Car car: (Helpers.Constants.mIsLoggedIn)?cars:tempCars) {
				if(carRegNo.trim().toUpperCase().equals(car.getRegistrationNumber())) {
					car_info_regno.setText("");
					if(car_image_gallery.getAdapter().getCount() > 1) {
						car_image_gallery.setSelection(car_image_gallery.getAdapter().getCount() - 2);
					}
					Helpers.showMessage(context, "", "Registrering nummer allerede lagt til bil liste");
					return;
				}
			}
			if(Helpers.isNetworkAvailable(context)) {
				if(Helpers.Constants.mIsLoggedIn) {
					new Thread(new Runnable() {
						public void run() {
							synchronized(Helpers.lockObject) {
								final Car car = findCar(carRegNo);
								
								if(car.getRegistrationNumber() != null && !car.getRegistrationNumber().equals("")) {
									if(saveCar(car)) {
										mAddMode = false;
										cars = getAllCars(user.getUid());
										final int index = cars.size() - 1;
										mCurrentIndex = index;
										
										Car tempCar = new Car();
										tempCar.setUid(-1);
										cars.add(tempCar);
										((VikingApplication) context.getApplicationContext()).setActiveCar(cars.get(0));
										
										handler.post(new Runnable() {
											public void run() {
												CarImageAdapter adapter = new CarImageAdapter(context, cars);
												car_image_gallery.setAdapter(adapter);
												car_image_gallery.setSelection(index);
												navigation_dots.setDotsCount(cars.size() - 1);
												navigation_dots.setSelectedDot(0);
												updateInfo(car);
											}
										});
									}
								} else {
									handler.post(new Runnable() {
										public void run() {
											car_info_regno.setEnabled(true);
											Helpers.showMessage(context, "", "Registreringsnummer ikke funnet");
											car_info_regno.setText("");
										}
									});
								}
							}
						}
					}).start();
				} else {
					//((NotLoggedInCallback) context).onCarSaving();
					new Thread(new Runnable() {
						public void run() {
							final Car car = findCar(carRegNo);
							
							if(car.getRegistrationNumber() != null && !car.getRegistrationNumber().equals("")) {
								tempCars.remove(tempCars.size() - 1);
								tempCars.add(car);
								Car tempCar = new Car();
								tempCar.setUid(-1);
								tempCars.add(tempCar);
								mAddMode = false;
								
								handler.post(new Runnable() {
									public void run() {
										CarImageAdapter adapter = new CarImageAdapter(context, tempCars);
										car_image_gallery.setAdapter(adapter);
										if(tempCars.size() > 1) {
											((VikingApplication) context.getApplicationContext()).setActiveCar(tempCars.get(0));
											car_image_gallery.setSelection(tempCars.size() - 2);
										}
										navigation_dots.setDotsCount(tempCars.size() - 1);
										navigation_dots.setSelectedDot(0);
									}
								});
							} else {
								handler.post(new Runnable() {
									public void run() {
										car_info_regno.setEnabled(true);
										Helpers.showMessage(context, "", "Registration number not found");
										car_info_regno.setText("");
									}
								});
							}
						}
					}).start();
				}
			} else {
				Helpers.showMessage(context, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller pr�ve etter en tid.");
			}
		}
	}
	
	public void showCarImage() {
		car_image_flipper.setDisplayedChild(0);
	}
	
	public void showImageProgress() {
		car_image_flipper.setDisplayedChild(1);
	}
	
	public void removeInTempCars(Car car) {
		tempCars.remove(car);
		navigation_dots.setDotsCount(tempCars.size() - 1);
		navigation_dots.setSelectedDot(0);
	}
	
	public void updateCarInfo(Car car) {
		CarImageAdapter adapter = new CarImageAdapter(context, cars);
		car_image_gallery.setAdapter(adapter);
		if(adapter.getCount() > 0) {
			car_image_gallery.setSelection(0);
		}
		adapter.notifyDataSetChanged();
		navigation_dots.setDotsCount(cars.size());
		navigation_dots.setSelectedDot(0);
		updateInfo(car);
	}
	
	public void updateCarInfo(Car car, final List<Car> cars) {
		CarImageAdapter adapter = new CarImageAdapter(context, cars);
		car_image_gallery.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		updateInfo(car);
		navigation_dots.setDotsCount(cars.size() - 1);
		navigation_dots.setSelectedDot(0);
		
		this.cars = cars;
		
		car_image_gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
				final Car car = cars.get(position);
				
				if(car.getUid() != -1) {
					mAddMode = false;
					((VikingApplication) context.getApplicationContext()).setActiveCar(car);
					updateInfo(car);
					car_info_regno.setEnabled(false);
					navigation_dots.setSelectedDot(position);
					mCurrentIndex = position;
					
					if(car.isShared()) {
						if(Helpers.Constants.mIsLoggedIn)
							edit_shared_car.setVisibility(View.VISIBLE);
						findViewById(R.id.edit_img_btn).setVisibility(View.GONE);
						findViewById(R.id.share_car_btn).setVisibility(View.GONE);
						findViewById(R.id.owner_info_btn).setVisibility(View.GONE);
					} else {
						if(Helpers.Constants.mIsLoggedIn)
							edit_shared_car.setVisibility(View.GONE);
						findViewById(R.id.edit_img_btn).setVisibility(View.VISIBLE);
						findViewById(R.id.share_car_btn).setVisibility(View.VISIBLE);
						findViewById(R.id.owner_info_btn).setVisibility(View.VISIBLE);
					}
				} else {
					car_info_regno.setText("");
					car_info_eier.setText("");
					car_info_chassis_number.setText("");
					car_info_reg_year.setText("");
					car_info_reg_norway.setText("");
					car_info_brand_code.setText("");
					car_info_car_model.setText("");
					car_info_engine_performance.setText("");
					car_info_displacement.setText("");
					car_info_fuel_type.setText("");
					car_info_length.setText("");
					car_info_width.setText("");
					car_info_weight.setText("");
					car_info_total_weight.setText("");
					car_info_colour.setText("");
					car_info_co2_emission.setText("");
					car_info_std_tire_front.setText("");
					car_info_std_tire_back.setText("");
					car_info_driving_wheels.setText("");
					car_info_trailer_weight_with_brakes.setText("");
					car_info_trailer_weight_without_brakes.setText("");
					car_info_regno.setEnabled(true);
					mAddMode = true;
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	public void updateCarInfo(boolean moveToFirst) {
		if(user == null)
			user = ((VikingApplication) context.getApplicationContext()).getUser();
		carAdapter.openReadable();
		cars = carAdapter.getAllCars(user.getUid());
		carAdapter.close();
		
		if(cars.size() > 0) {
			((VikingApplication) context.getApplicationContext()).setActiveCar(cars.get(0));
		}
		
		Car tempCar = new Car();
		tempCar.setUid(-1);
		cars.add(tempCar);
		
		CarImageAdapter adapter = new CarImageAdapter(context, cars);
		car_image_gallery.setAdapter(adapter);
		if(moveToFirst)
			car_image_gallery.setSelection(0);
		else
			car_image_gallery.setSelection(mCurrentIndex);
		adapter.notifyDataSetChanged();
		navigation_dots.setDotsCount(cars.size() - 1);
		navigation_dots.setSelectedDot(0);
		
		if(car != null) {
			updateInfo(car);
		}
	}
	
	public void updateCarInfoNotLoggedIn() {
		CarImageAdapter adapter = new CarImageAdapter(context, tempCars);
		car_image_gallery.setAdapter(adapter);
		car_image_gallery.setSelection(0, true);
		
		if(tempCars.size() > 0 && tempCars.get(0).getUid() != -1) {
			updateInfo(tempCars.get(0));
		}
	}
	
	private void updateInfo(Car car) {
		car_info_regno.setText(car.getRegistrationNumber());
		car_info_eier.setText(car.getOwnername());
		car_info_chassis_number.setText(car.getChassisNumber());
		car_info_reg_year.setText(car.getCarRegYear());
		car_info_reg_norway.setText(car.getRegFirstTimeInNorway());
		car_info_brand_code.setText(car.getBrandCode());
		car_info_car_model.setText(car.getCarModel());
		car_info_engine_performance.setText(car.getEnginePerformance());
		car_info_displacement.setText(car.getDisplacement());
		car_info_fuel_type.setText(car.getFuelType());
		car_info_length.setText(car.getLength());
		car_info_weight.setText(car.getWeight() + " Kg");
		car_info_width.setText(car.getWidth());
		car_info_total_weight.setText(car.getTotalWeight());
		car_info_colour.setText(car.getColour());
		car_info_co2_emission.setText(car.getCo2Emission());
		car_info_std_tire_front.setText(car.getStandardTireFront());
		car_info_std_tire_back.setText(car.getStandardTireBack());
		car_info_driving_wheels.setText(car.getDrivingWheels());
		car_info_trailer_weight_with_brakes.setText(car.getTrailerWeightWithBrakes());
		car_info_trailer_weight_without_brakes.setText(car.getTrailerWeightWithoutBrakes());
	}
	
	public Car findCar(String registrationNumber) {
		Car car = new Car();
		String jsonString = request.send(context.getString(R.string.api_url) + 
				String.format("find_car/%s/json", registrationNumber), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONObject info = json.getJSONObject("cars");
				
				car.setUid(info.getInt("uid"));
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
				
				Log.i(TAG, "find car: " + jsonString);
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		return car;
	}
	
	private boolean saveCar(Car car) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		int ownerId = 0;
		
		if(user != null) {
			ownerId = user.getUid();
		}
		
		if(user != null) {
			queries.add(new BasicNameValuePair("owner_id", String.valueOf(ownerId)));
			queries.add(new BasicNameValuePair("ownername", user.getFirstname() + " " + user.getLastname()));
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
			queries.add(new BasicNameValuePair("std_tire_front", car.getStandardTireFront()));
			queries.add(new BasicNameValuePair("std_tire_back", car.getStandardTireBack()));
			queries.add(new BasicNameValuePair("driving_wheels", car.getDrivingWheels()));
			queries.add(new BasicNameValuePair("trailer_weight_with_brakes", car.getTrailerWeightWithBrakes()));
			queries.add(new BasicNameValuePair("trailer_weight_without_brakes", car.getTrailerWeightWithoutBrakes()));
			
			String result = request.send(context.getString(R.string.api_url) + "save_car", queries, HttpRequest.POST);
			
			if(result != null) {
				int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
				
				if(success == 1) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private List<Car> getAllCars(int ownerId) {
		List<Car> cars = new ArrayList<Car>();
		
		String jsonString = request.send(context.getString(R.string.api_url) + String.format("car/%s/json/" + System.currentTimeMillis(), ownerId), null, HttpRequest.GET);
		
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
					car.setStandardTireFront(info.getString("std_tire_front"));
					car.setStandardTireBack(info.getString("std_tire_back"));
					car.setDrivingWheels(info.getString("driving_wheels"));
					car.setTrailerWeightWithBrakes(info.getString("trailer_weight_with_brakes"));
					car.setTrailerWeightWithoutBrakes(info.getString("trailer_weight_without_brakes"));
					car.setFilename(info.getString("filename"));
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
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public static List<Car> getAllTempCars() {
		tempCars.remove(tempCars.size() - 1);
		return tempCars;
	}
	
	private boolean deleteSharedCar(int uid) {
		HttpRequest request = HttpRequest.getInstance();
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		
		String result = request.send(context.getString(R.string.api_url) + "delete_sharecar", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		return false;
	}
}
