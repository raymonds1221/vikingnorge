package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.MainMenuActivity;
import no.incent.viking.pojo.User;
import no.incent.viking.db.DBUserAdapter;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android.os.Handler;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OwnerInfo extends LinearLayout {
	private final String TAG = "VIKING";
	private Context context;
	private User user;
	private HttpRequest request;
	private EditText owner_info_firstname;
	private EditText owner_info_lastname;
	private EditText owner_info_email;
	private EditText owner_info_mobile;
	private EditText owner_info_address;
	private EditText owner_info_region;
	private DBUserAdapter userAdapter;
	private final Handler handler = new Handler();

	public OwnerInfo(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.owner_info, this);
		
		this.context = context;
		user = ((VikingApplication) context.getApplicationContext()).getUser();
		request = HttpRequest.getInstance();
		
		owner_info_firstname = (EditText) findViewById(R.id.owner_info_firstname);
		owner_info_lastname = (EditText) findViewById(R.id.owner_info_lastname);
		owner_info_email = (EditText) findViewById(R.id.owner_info_email);
		owner_info_mobile = (EditText) findViewById(R.id.owner_info_mobile);
		owner_info_address = (EditText) findViewById(R.id.owner_info_address);
		owner_info_region = (EditText) findViewById(R.id.owner_info_region);
		userAdapter = new DBUserAdapter(context);
		
		if(user != null) {
			owner_info_firstname.setText(user.getFirstname());
			owner_info_lastname.setText(user.getLastname());
			owner_info_email.setText(user.getEmail());
			owner_info_mobile.setText(user.getTelephone());
			owner_info_address.setText(user.getAddress());
			owner_info_region.setText(user.getArea());
		}
		
		findViewById(R.id.save_owner_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Helpers.isNetworkAvailable(context)) {
					new Thread(new Runnable() {
						public void run() {
							if(updateUser()) {
								handler.post(new Runnable() {
									public void run() {
										Helpers.showMessage(context, "", "Eier informajon har blitt oppdatert");
									}
								});
							}
						}
					}).start();
				}
			}
		});
	}
	
	private boolean updateUser() {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		String firstname = owner_info_firstname.getText().toString();
		String lastname = owner_info_lastname.getText().toString();
		String email = owner_info_email.getText().toString();
		String telephone = owner_info_mobile.getText().toString();
		String address = owner_info_address.getText().toString();
		String area = owner_info_region.getText().toString();
		
		if(!firstname.equals("") && !lastname.equals("") && !email.equals("")
				&& !telephone.equals("") && !address.equals("") && !area.equals("")) {
			queries.add(new BasicNameValuePair("owner_id", String.valueOf(user.getUid())));
			queries.add(new BasicNameValuePair("firstname", firstname));
			queries.add(new BasicNameValuePair("lastname", lastname));
			queries.add(new BasicNameValuePair("email", email));
			queries.add(new BasicNameValuePair("telephone", telephone));
			queries.add(new BasicNameValuePair("address", address));
			queries.add(new BasicNameValuePair("area", area));
			
			String result = request.send(context.getString(R.string.api_url) + "edit_owner", queries, HttpRequest.POST);
			
			if(result != null) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				
				try {
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document document = db.parse(new InputSource(new StringReader(result)));
					NodeList nl = document.getElementsByTagName("successful");
					
					int success = Integer.parseInt(nl.item(0).getFirstChild().getNodeValue());
					
					if(success == 1) {
						user.setFirstname(firstname);
						user.setLastname(lastname);
						user.setEmail(email);
						user.setTelephone(telephone);
						user.setAddress(address);
						user.setArea(area);
						((VikingApplication) context.getApplicationContext()).setUser(user);
						
						userAdapter.open();
						userAdapter.updateUser(user);
						userAdapter.close();
						
						((OnOwnerInfoCallback) context).onUpdateOwner(user);
						return true;
					}
				} catch(ParserConfigurationException ex) {
					Log.e(TAG, ex.getMessage());
				} catch(IOException ex) {
					Log.e(TAG, ex.getMessage());
				} catch(SAXException ex) {
					Log.e(TAG, ex.getMessage());
				}
			}
		}
		
		return false;
	}
	
	public interface OnOwnerInfoCallback {
		public void onUpdateOwner(User user);
	}
}
