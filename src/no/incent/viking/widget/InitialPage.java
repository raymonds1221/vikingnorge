package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.adapter.NewsItemAdapter;
import no.incent.viking.db.DBTrafficAdapter;
import no.incent.viking.db.DBCallToActionAdapter;
import no.incent.viking.pojo.CallToAction;
import no.incent.viking.pojo.Traffic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class InitialPage extends LinearLayout {
	private Context context;
	private ListView top_news_list;
	private DBTrafficAdapter trafficAdapter;
	private DBCallToActionAdapter callToActionAdapter;
	private ImageView call_to_action;
	private CallToAction callToAction;
	
	public InitialPage(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.initialpage, this);
		this.context = context;
		
		top_news_list = (ListView) findViewById(R.id.top_news_list);
		
		trafficAdapter = new DBTrafficAdapter(context);
		
		callToActionAdapter = new DBCallToActionAdapter(context);
		callToActionAdapter.openReadable();
		callToAction = callToActionAdapter.getCallToAction(8);
		callToActionAdapter.close();
		
		call_to_action = (ImageView) findViewById(R.id.call_to_action);
		
		if(callToAction != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(callToAction.getPath());
			call_to_action.setImageBitmap(bitmap);
		}
		
		updateTrafficList();
	}
	
	public void updateTrafficList() {
		trafficAdapter.openReadable();
		NewsItemAdapter adapter = new NewsItemAdapter(context, R.layout.list_topnews, trafficAdapter.getAllTraffics(3));
		top_news_list.setAdapter(adapter);
		trafficAdapter.close();
		
		adapter.setOnItemClick(new NewsItemAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Traffic traffic) {
				((OnInitialPageCallback) context).onTrafficClick(traffic);
			}
		});
	}
	
	public static interface OnInitialPageCallback {
		public void onTrafficClick(Traffic traffic);
	}
}
