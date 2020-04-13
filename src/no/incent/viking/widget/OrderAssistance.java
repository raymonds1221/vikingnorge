package no.incent.viking.widget;

import no.incent.viking.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class OrderAssistance extends LinearLayout {
	private Context context;
	private final String[] causes = {"Kollisjon", "Sammenst¿t", "Tyveri", "Brann", "Innbrudd", "Utforj¿ring", "Glass", "H¾rverk"};
	private Spinner damage_cause;
	
	public OrderAssistance(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.order_assistance, this);
		this.context = context;
		
		initViews();
	}
	
	private void initViews() {
		damage_cause = (Spinner) findViewById(R.id.damage_cause);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, causes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		damage_cause.setAdapter(adapter);
	}
}
