package no.incent.viking;

import no.incent.viking.util.HttpRequest;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.maps.MapActivity;

public abstract class BaseActivity extends MapActivity {
	protected final String TAG = "VIKING";
	protected final Handler handler = new Handler();
	protected HttpRequest request;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(contentView());
		
		request = HttpRequest.getInstance();
		initialize();
	}
	
	protected abstract int contentView();
	protected abstract void initialize();
}
