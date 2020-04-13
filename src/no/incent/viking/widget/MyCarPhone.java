package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.CarPhone;
import no.incent.viking.pojo.PhoneCategory;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.MyCarEntity;
import no.incent.viking.util.NotLoggedInCallback;
import no.incent.viking.db.DBCarPhoneAdapter;
import no.incent.viking.db.DBPhoneCategoryAdapter;
import no.incent.viking.adapter.MyCarPhoneAdapter;

import java.util.List;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.os.Handler;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.EditText;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MyCarPhone extends MyCarEntity {
	private final String TAG = "VIKING";
	private final Handler handler = new Handler();
	private Context context;
	private HttpRequest request;
	private DBCarPhoneAdapter carPhoneAdapter;
	private DBPhoneCategoryAdapter phoneCategoryAdapter;
	private ListView mycar_phone_list;
	private int ownerId = 0;
	private EditText phone_name;
	private EditText phone_number;
	private boolean phoneCategorySelected = false;
	private PhoneCategory phoneCategory;
	private List<CarPhone> carPhones;
	public static List<CarPhone> tempCarPhones;

	public MyCarPhone(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.mycar_phone, this);
		
		if(((VikingApplication) context.getApplicationContext()).getUser() != null){
			ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
		}
		
		this.context = context;
		request = HttpRequest.getInstance();
		carPhoneAdapter = new DBCarPhoneAdapter(context);
		phoneCategoryAdapter = new DBPhoneCategoryAdapter(context);
		mycar_phone_list = (ListView) findViewById(R.id.mycar_phone_list);
		phone_name = (EditText) findViewById(R.id.phone_name);
		phone_number = (EditText) findViewById(R.id.phone_number);
		
		findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = phone_name.getText().toString();
				String telephone = phone_number.getText().toString();
				
				if(Helpers.Constants.mIsLoggedIn) {
					if(!name.equals("") && !telephone.equals("")) {
						final CarPhone carPhone = new CarPhone();
						carPhone.setOwnerId(ownerId);
						
						if(phoneCategorySelected && phoneCategory != null) {
							carPhone.setCategory(phoneCategory.getCategory());
							carPhone.setName(name);
							carPhone.setTelephone(telephone);
						} else {
							carPhone.setName(name);
							carPhone.setTelephone(telephone);
						}
						
						carPhone.setIsDefault("");
						
						new Thread(new Runnable() {
							public void run() {
								synchronized(Helpers.lockObject) {
									if(saveCarPhone(carPhone)) {
										carPhoneAdapter.openReadable();
										carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
										carPhoneAdapter.close();
										
										handler.post(new Runnable() {
											public void run() {
												MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, 
														R.layout.list_mycar_phone, carPhones);
												adapter.setDropDownViewResource(R.layout.list_mycar_phone);
												mycar_phone_list.setAdapter(adapter);
												phoneCategorySelected = false;
												phone_name.setText("");
												phone_number.setText("");
												
												adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
													@Override
													public void onItemClick(CarPhone carPhone) {
														MyCarPhone.this.onItemClick(carPhone);
													}
												});
											}
										});
									}
								}
							}
						}).start();
					} else {
						Helpers.showMessage(context, "", "Please enter name and telephone number");
					}
				} else {
					//((NotLoggedInCallback) context).onCarSaving();
					CarPhone carPhone = new CarPhone();
					carPhone.setName(name);
					carPhone.setTelephone(telephone);
					carPhone.setIsDefault("");
					tempCarPhones.add(carPhone);
					MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, 
							R.layout.list_mycar_phone, tempCarPhones);
					adapter.setDropDownViewResource(R.layout.list_mycar_phone);
					mycar_phone_list.setAdapter(adapter);
					phone_name.setText("");
					phone_number.setText("");
					
					adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
						@Override
						public void onItemClick(CarPhone carPhone) {
							MyCarPhone.this.onItemClick(carPhone);
						}
					});
				}
			}
		});
		
		if(!Helpers.Constants.mIsLoggedIn)
			tempCarPhones = getAllTempDefaultCarPhones();
	}
	
	public synchronized List<CarPhone> getAllCarPhones(int ownerId) {
		List<CarPhone> carPhones = new ArrayList<CarPhone>();
		String jsonString = request.send(context.getString(R.string.api_url) + 
				String.format("car_phones/%s/json", ownerId), null, HttpRequest.GET);
		
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
					cp.setName("Veihjelp");
					cp.setTelephone("06000");
					cp.setIsDefault("yes");
					carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-2);
					cp.setOwnerId(ownerId);
					cp.setName("Ambulanse");
					cp.setTelephone("113");
					cp.setIsDefault("yes");
					carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-3);
					cp.setOwnerId(ownerId);
					cp.setName("Politi");
					cp.setTelephone("112");
					cp.setIsDefault("yes");
					carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-4);
					cp.setOwnerId(ownerId);
					cp.setName("Brann");
					cp.setTelephone("110");
					cp.setIsDefault("yes");
					carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-5);
					cp.setOwnerId(ownerId);
					cp.setName("Verksted");
					cp.setIsDefault("yes");
					carPhoneAdapter.insertCarPhone(cp);
					
					cp = new CarPhone();
					cp.setUid(-6);
					cp.setOwnerId(ownerId);
					cp.setName("Forsikring");
					cp.setIsDefault("yes");
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
					
					carPhoneAdapter.insertCarPhone(carPhone);
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
		
		return carPhones;
	}
	
	private List<CarPhone> getAllTempDefaultCarPhones() {
		List<CarPhone> defaultCarPhones = new ArrayList<CarPhone>();
		
		CarPhone cp = new CarPhone();
		cp.setUid(-1);
		cp.setOwnerId(ownerId);
		cp.setName("Veihjelp");
		cp.setTelephone("06000");
		cp.setIsDefault("yes");
		carPhoneAdapter.insertCarPhone(cp);
		defaultCarPhones.add(cp);
		
		cp = new CarPhone();
		cp.setUid(-2);
		cp.setOwnerId(ownerId);
		cp.setName("Ambulanse");
		cp.setTelephone("113");
		cp.setIsDefault("yes");
		carPhoneAdapter.insertCarPhone(cp);
		defaultCarPhones.add(cp);
		
		cp = new CarPhone();
		cp.setUid(-3);
		cp.setOwnerId(ownerId);
		cp.setName("Politi");
		cp.setTelephone("112");
		cp.setIsDefault("yes");
		carPhoneAdapter.insertCarPhone(cp);
		defaultCarPhones.add(cp);
		
		cp = new CarPhone();
		cp.setUid(-4);
		cp.setOwnerId(ownerId);
		cp.setName("Brann");
		cp.setTelephone("110");
		cp.setIsDefault("yes");
		carPhoneAdapter.insertCarPhone(cp);
		defaultCarPhones.add(cp);
		
		cp = new CarPhone();
		cp.setUid(-5);
		cp.setOwnerId(ownerId);
		cp.setName("Verksted");
		cp.setIsDefault("yes");
		carPhoneAdapter.insertCarPhone(cp);
		defaultCarPhones.add(cp);
		
		cp = new CarPhone();
		cp.setUid(-6);
		cp.setOwnerId(ownerId);
		cp.setName("Forsikring");
		cp.setIsDefault("yes");
		carPhoneAdapter.insertCarPhone(cp);
		defaultCarPhones.add(cp);
		
		return defaultCarPhones;
	}
	
	private boolean saveCarPhone(CarPhone carPhone) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(carPhone.getOwnerId())));
		queries.add(new BasicNameValuePair("category", carPhone.getCategory()));
		queries.add(new BasicNameValuePair("name", carPhone.getName()));
		queries.add(new BasicNameValuePair("telephone", carPhone.getTelephone()));
		
		String result = request.send(context.getString(R.string.api_url) + "save_car_phones", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				int uid = Integer.parseInt(Helpers.parseXMLNode(result, "uid"));
				
				for(CarPhone cp: carPhoneAdapter.getAllDefaultCarPhones()) {
					cp.setOwnerId(carPhone.getOwnerId());
					carPhoneAdapter.updateCarPhone(cp);
				}
				
				carPhone.setUid(uid);
				
				carPhoneAdapter.openWritable();
				carPhoneAdapter.insertCarPhone(carPhone);
				carPhoneAdapter.close();
				
				if(carPhones != null) {
					carPhones.add(carPhone);
				}
				return true;
			}
		}
		
		return false;
	}
	
	private boolean updateCarPhone(CarPhone carPhone) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(carPhone.getUid())));
		queries.add(new BasicNameValuePair("category", carPhone.getCategory()));
		queries.add(new BasicNameValuePair("name", carPhone.getName()));
		queries.add(new BasicNameValuePair("telephone", carPhone.getTelephone()));
		
		String result = request.send(context.getString(R.string.api_url) + "edit_car_phone", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private void onItemClick(final CarPhone carPhone) {
		if(carPhone.getIsDefault().equals("yes") && !carPhone.getName().equals("Veihjelp") 
				&& !carPhone.getName().equals("Ambulanse") && !carPhone.getName().equals("Politi") 
				&& !carPhone.getName().equals("Brann")) {
			List<PhoneCategory> phoneCategories;
			
			phoneCategoryAdapter.open();
			phoneCategories = phoneCategoryAdapter.getAllCarPhonesByCategory(carPhone.getName());
			phoneCategoryAdapter.close();
			
			final String[] names = new String[phoneCategories.size()];
			final String[] telephones = new String[phoneCategories.size()];
			
			int i=0;
			for(PhoneCategory phoneCategory: phoneCategories) {
				names[i] = phoneCategory.getName();
				telephones[i] = phoneCategory.getTelephone();
				i++;
			}
			
			AlertDialog alert = new AlertDialog.Builder(context)
							.setCancelable(true)
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
							.setItems(names, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if(Helpers.Constants.mIsLoggedIn) {
										carPhone.setTelephone(telephones[which]);
										
										new Thread(new Runnable() {
											public void run() {
												if(updateCarPhone(carPhone)) {
													carPhoneAdapter.openWritable();
													carPhoneAdapter.updateCarPhone(carPhone);
													carPhoneAdapter.close();
													carPhoneAdapter.openReadable();
													carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
													carPhoneAdapter.close();
													
													handler.post(new Runnable() {
														public void run() {
															synchronized(Helpers.lockObject) {
																MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, 
																		R.layout.list_mycar_phone, carPhones);
																mycar_phone_list.setAdapter(adapter);
																
																adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
																	@Override
																	public void onItemClick(CarPhone carPhone) {
																		MyCarPhone.this.onItemClick(carPhone);
																	}
																});
															}
														}
													});
												}
											}
										}).start();
									} else {
										//((NotLoggedInCallback) context).onCarSaving();
										carPhone.setTelephone(telephones[which]);
										carPhoneAdapter.updateCarPhone(carPhone);
										MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, 
												R.layout.list_mycar_phone, tempCarPhones);
										mycar_phone_list.setAdapter(adapter);
										
										adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
											@Override
											public void onItemClick(CarPhone carPhone) {
												MyCarPhone.this.onItemClick(carPhone);
											}
										});
									}
								}
							}).create();
			alert.show();
		}
	}

	@Override
	public void load() {
		/*if(Helpers.isNetworkAvailable(context)) {
			new Thread(new Runnable() {
				public void run() {
					if(Helpers.Constants.mIsLoggedIn) {
						if(((VikingApplication) context.getApplicationContext()).getUser() != null) {
							ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
						}
						final List<CarPhone> carPhones = getAllCarPhones(ownerId);
						
						handler.post(new Runnable() {
							public void run() {
								MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context,
										R.layout.list_mycar_phone, carPhones);
								adapter.setDropDownViewResource(R.layout.list_mycar_phone);
								mycar_phone_list.setAdapter(adapter);
								
								adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
									@Override
									public void onItemClick(CarPhone carPhone) {
										MyCarPhone.this.onItemClick(carPhone);
									}
								});
							}
						});
					} else {
						handler.post(new Runnable() {
							public void run() {
								tempCarPhones = getAllTempDefaultCarPhones();
								MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, R.layout.list_mycar_phone, tempCarPhones);
								mycar_phone_list.setAdapter(adapter);
								
								adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
									@Override
									public void onItemClick(CarPhone carPhone) {
										MyCarPhone.this.onItemClick(carPhone);
									}
								});
							}
						});
					}
				}
			}).start();
		} else {
			carPhoneAdapter.openReadable();
			carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
			MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, 
					R.layout.list_mycar_phone, carPhones);
			adapter.setDropDownViewResource(R.layout.list_mycar_phone);
			mycar_phone_list.setAdapter(adapter);
			carPhoneAdapter.close();
			
			adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(CarPhone carPhone) {
					MyCarPhone.this.onItemClick(carPhone);
				}
			});
		}*/
		
		if(Helpers.isNetworkAvailable(context)) {
			if(Helpers.Constants.mIsLoggedIn) {
				if(((VikingApplication) context.getApplicationContext()).getUser() != null) {
					ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
				}
				final List<CarPhone> carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
				
				handler.post(new Runnable() {
					public void run() {
						MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context,
								R.layout.list_mycar_phone, carPhones);
						adapter.setDropDownViewResource(R.layout.list_mycar_phone);
						mycar_phone_list.setAdapter(adapter);
						
						adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
							@Override
							public void onItemClick(CarPhone carPhone) {
								MyCarPhone.this.onItemClick(carPhone);
							}
						});
					}
				});
			} else {
				handler.post(new Runnable() {
					public void run() {
						MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, R.layout.list_mycar_phone, tempCarPhones);
						mycar_phone_list.setAdapter(adapter);
						
						adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
							@Override
							public void onItemClick(CarPhone carPhone) {
								MyCarPhone.this.onItemClick(carPhone);
							}
						});
					}
				});
			}
		} else {
			carPhoneAdapter.openReadable();
			carPhones = carPhoneAdapter.getAllCarPhones(ownerId);
			MyCarPhoneAdapter adapter = new MyCarPhoneAdapter(context, 
					R.layout.list_mycar_phone, carPhones);
			adapter.setDropDownViewResource(R.layout.list_mycar_phone);
			mycar_phone_list.setAdapter(adapter);
			carPhoneAdapter.close();
			
			adapter.setOnItemClick(new MyCarPhoneAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(CarPhone carPhone) {
					MyCarPhone.this.onItemClick(carPhone);
				}
			});
		}
	}

	@Override
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
	public static List<CarPhone> getAllTempCarPhones() {
		List<CarPhone> returnCarPhones = new ArrayList<CarPhone>();
		
		for(CarPhone cp: tempCarPhones) {
			if(!cp.getName().equals("Veihjelp") && !cp.getName().equals("Ambulanse") && !cp.getName().equals("Politi") 
					&& !cp.getName().equals("Brann") && !cp.getName().equals("Verksted") && !cp.getName().equals("Forsikring")) {
				returnCarPhones.add(cp);
			}
		}
		
		return returnCarPhones;
	}
}
