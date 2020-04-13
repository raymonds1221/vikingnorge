package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.Car;
import no.incent.viking.pojo.User;
import no.incent.viking.db.DBCarAdapter;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.Helpers;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.os.Handler;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

public class EUControl extends LinearLayout {
	private final String TAG = "VIKING";
	private Context context;
	private HttpRequest request;
	private EditText license_plate;
	private TextView eu_date;
	private TextView eu_month;
	private TextView eu_year;
	private String[] months = {"Oktober", "Januar", "Februar", "Mars", "April", "Mai", "Juni",
								"November", "August", "September"};
	private DBCarAdapter carAdapter;
	private List<Car> cars;
	private int ownerId;
	private User user;
	private Car car;
	private int position = 0;
	private Handler handler;
	
	public EUControl(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.eu_control, this);
		this.context = context;
		
		request = HttpRequest.getInstance();
		user = ((VikingApplication) context.getApplicationContext()).getUser();
		handler = new Handler();
		
		if(user != null) {
			ownerId = user.getUid();
		}
		
		license_plate = (EditText) findViewById(R.id.license_plate);
		eu_date = (TextView) findViewById(R.id.eu_date);
		eu_month = (TextView) findViewById(R.id.eu_month);
		eu_year = (TextView) findViewById(R.id.eu_year);
		
		carAdapter = new DBCarAdapter(context);
		carAdapter.openReadable();
		cars = carAdapter.getAllCars(ownerId);
		carAdapter.close();
		
		if(cars.size() > 0 && user != null) {
			car = cars.get(0);
			initEUControl(car);
		}
		
		findViewById(R.id.change_car_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(user != null) {
					position++;
					
					if(position == cars.size()) {
						position = 0;
					}
					
					initEUControl(cars.get(position));
				}
			}
		});
		
		license_plate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {	
					new Thread(new Runnable() {
						public void run() {
							final Car car = findCar(license_plate.getText().toString());
							handler.post(new Runnable() {
								public void run() {
									if(car != null) {
										initEUControl(car);
									} else {
										Helpers.showMessage(context, "", "Registreringsnummer ikke funnet");
									}
								}
							});
						}
					}).start();
				}
				return false;
			}
		});
	}
	
	public void initEUControl(Car car) {
		String day = "00";
		String month = "";
		String year = "";
		
		if(car.getRegistrationNumber() != null && !car.getRegistrationNumber().equals("")) {
			String lastDigitString = String.valueOf(car.getRegistrationNumber().charAt(car.getRegistrationNumber().length() - 1));
			
			if(isNumeric(lastDigitString)) {
				int lastDigit = Integer.parseInt(lastDigitString);
				month = months[lastDigit];
				
				switch(lastDigit) {
					case 0:
	                    day = "31";
	                    break;
	                case 1:
	                    day = "31";
	                    break;
	                case 2:
	                    day = "28";
	                    break;
	                case 3:
	                    day = "31";
	                    break;
	                case 4:
	                    day = "30";
	                    break;
	                case 5:
	                    day = "31";
	                    break;
	                case 6:
	                    day = "30";
	                    break;
	                case 7:
	                    day = "30";
	                    break;
	                case 8:
	                    day = "31";
	                    break;
	                case 9:
	                    day = "30";
	                    break;
				}
			}
			
			if(!car.getCarRegYear().equals("") && isNumeric(car.getCarRegYear())) {
				int eu_control_year = 0;
				Calendar calendar = Calendar.getInstance();
				int presentYear = calendar.get(Calendar.YEAR);
				
				if(isNumeric(car.getWeight())) {
					int weight = Integer.parseInt(car.getWeight());
					
					if(weight < 3500) {
						eu_control_year = Integer.parseInt(car.getCarRegYear()) + 4;
						
						while(eu_control_year < presentYear) {
							eu_control_year += 2;
						}
					} else {
						eu_control_year = Integer.parseInt(car.getCarRegYear()) + 2;
						
						while(eu_control_year < presentYear) {
							eu_control_year += 1;
						}
					}
					
					year = String.valueOf(eu_control_year);
				}
			}
			
			license_plate.setText(car.getRegistrationNumber());
			eu_date.setText(day.charAt(0) + "  " + day.charAt(1));
			eu_month.setText(month);
			eu_year.setText(year);
		}
	}
	
	public Car findCar(String registrationNumber) {
		Car car = null;
		String jsonString = request.send(context.getString(R.string.api_url) + 
				String.format("find_car/%s/json", registrationNumber), null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONObject info = json.getJSONObject("cars");
				
				int success = Integer.parseInt(info.getString("success"));
				
				if(success == 1) {
					car = new Car();
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
				}
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		}
		return car;
	}
	
	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	private boolean isNumeric(String input) {
		return Pattern.matches("[0-9]*", input);
	}
}
