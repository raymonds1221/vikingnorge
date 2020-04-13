package no.incent.viking;

import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.PreferenceWrapper;
import no.incent.viking.pojo.User;
import no.incent.viking.db.DBUserAdapter;
import no.incent.viking.service.VikingService;
import no.incent.viking.service.IVikingService;
import no.incent.viking.service.IVikingServiceCallback;

import java.util.List;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.widget.EditText;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.flurry.android.FlurryAgent;

public class Register extends BaseActivity {
	private EditText field_sign_mobile;
	private EditText field_sign_pass;
	private EditText field_sign_email;
	private ProgressDialog progressDialog;
	private DBUserAdapter userAdapter;
	private PreferenceWrapper prefsWrapper;
	private IVikingService mVikingService;
	
	@Override
	protected int contentView() {
		return R.layout.register;
	}

	@Override
	protected void initialize() {
		field_sign_mobile = (EditText) findViewById(R.id.field_sign_mobile);
		field_sign_pass = (EditText) findViewById(R.id.field_sign_pass);
		field_sign_email = (EditText) findViewById(R.id.field_sign_email);
		
		field_sign_mobile.setTypeface(Helpers.getArialFont(this));
		field_sign_pass.setTypeface(Helpers.getArialFont(this));
		field_sign_email.setTypeface(Helpers.getArialFont(this));
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Please wait...");
		
		userAdapter = new DBUserAdapter(this);
		prefsWrapper = new PreferenceWrapper(this);
		
		Intent intent = new Intent(this, VikingService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
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

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void onRegisterClicked(View view) {
		if(Helpers.isNetworkAvailable(this)) {
			final String telephone = field_sign_mobile.getText().toString().trim();
			final String password = field_sign_pass.getText().toString().trim();
			final String email = field_sign_email.getText().toString().trim();
			
			if(!telephone.equals("") && !password.equals("") && !email.equals("")) {
				progressDialog.show();
				
				new Thread(new Runnable() {
					public void run() {
						List<NameValuePair> queries = new ArrayList<NameValuePair>();
						queries.add(new BasicNameValuePair("telephone", telephone));
						queries.add(new BasicNameValuePair("password", password));
						queries.add(new BasicNameValuePair("email", email));
						
						final String result = request.send(getString(R.string.api_url) + "signup", queries, HttpRequest.POST);
						
						if(result != null) {
							int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
							
							if(success == 1) {
								int ownerId = Integer.parseInt(Helpers.parseXMLNode(result, "owner_id"));
								User user = new User();
								user.setUid(ownerId);
								user.setTelephone(telephone);
								user.setPassword(password);
								
								userAdapter.insertUser(user);
								((VikingApplication) getApplicationContext()).setUser(user);
								prefsWrapper.setPreferenceBooleanValue(PreferenceWrapper.IS_LOGGED_IN, true);
								prefsWrapper.setPreferenceIntValue(PreferenceWrapper.OWNER_ID, user.getUid());
								
								try {
									mVikingService.uploadRecords();
								} catch (RemoteException ex) {
									Log.e(TAG, ex.getMessage());
								}
								
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Helpers.Constants.mIsLoggedIn = true;
										setResult(Helpers.Constants.RESULT_REGISTERED);
										finish();
									}
								});
								
								/*
								try {
									mVikingService.saveUser(user);
								} catch (RemoteException ex) {
									Log.e(TAG, ex.getMessage());
								}*/
							} else {
								handler.post(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										String message = Helpers.parseXMLNode(result, "message");
										
										if(message != null && !message.equals("")) {
											Helpers.showMessage(Register.this, "Obs!", message);
										}
									}
								});
							}
						} else {
							handler.post(new Runnable() {
								public void run() {
									progressDialog.dismiss();
									Helpers.showMessage(Register.this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
								}
							});
						}
					}
				}).start();
			} else {
				Helpers.showMessage(this, "", "Du må fylle ut alle feltene.");
			}
		} else {
			Helpers.showMessage(this, "No Network Connection", "Network utilgjengelig. Kontroller nettverksinnstillingene eller prøve etter en tid.");
		}
	}
	
	public void onCancelClicked(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mVikingService = IVikingService.Stub.asInterface(service);
			try {
				mVikingService.registerCallback(callback);
			} catch(RemoteException ex) {
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
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog.dismiss();
					Helpers.Constants.mIsLoggedIn = true;
					setResult(Helpers.Constants.RESULT_REGISTERED);
					finish();
				}
			});
		}
		
		@Override
		public void onFinishRequestingTraffics() throws RemoteException {}
		@Override
		public void onFinishRequestingPhoneCategories() throws RemoteException {}
		@Override
		public void onFinishRequestingNews() throws RemoteException {}
		@Override
		public void onFinishRequestingCarPhones() throws RemoteException {}
		@Override
		public void onFinishRequestingCarFiles() throws RemoteException {}
		@Override
		public void onFinishRequestingCarEvents() throws RemoteException {}
		@Override
		public void onFinishRequestingCallToActions() throws RemoteException {}
		@Override
		public void onFinishRequestingAllCars() throws RemoteException {}
	};
}
