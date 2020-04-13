package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.CarEvent;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.db.DBCarEventAdapter;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Handler;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.Html;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MyCarEventsAdapter extends ArrayAdapter<CarEvent> {
	private final String TAG = "VIKING";
	private List<CarEvent> carEvents;
	private Context context;
	private OnItemClickListener onItemClickListener;
	private HttpRequest request;
	private DBCarEventAdapter carEventAdapter;
	private final Handler handler = new Handler();

	public MyCarEventsAdapter(Context ctx, int layoutItemResourceId, List<CarEvent> carEvents) {
		super(ctx, layoutItemResourceId, carEvents);
		this.carEvents = carEvents;
		this.context = ctx;
		request = HttpRequest.getInstance();
		carEventAdapter = new DBCarEventAdapter(context);
	}

	public static class ViewHolder{
		public ImageView eventImg;
		public TextView eventText;
		public ImageButton delete_event;
		public ImageButton viewEventBtn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;
		final CarEvent carEvent = carEvents.get(position);
		if (v == null) {		
			LayoutInflater vi = 
				(LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = vi.inflate(R.layout.list_mycar_events, null);
			
			holder = new ViewHolder();
			holder.eventImg = (ImageView) v.findViewById(R.id.events_icon);
			holder.eventText = (TextView) v.findViewById(R.id.mycar_events_text);
			holder.delete_event = (ImageButton) v.findViewById(R.id.delete_event);
			holder.viewEventBtn = (ImageButton) v.findViewById(R.id.view_event_btn);
			
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();
		
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null)
					onItemClickListener.onItemClick(carEvent);
			}
		});
		
		final GestureDetector gestureDetector = new GestureDetector(new GestureListener(holder));
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date d = formatter.parse(carEvent.getDateCreated());
			formatter = new SimpleDateFormat("dd/MM/yy");
			String eventText = "Hendelse";
			
			if(carEvent.getEvent().equals("Veihjelp") || carEvent.getEvent().equals("Order Assistance")) {
				eventText = "Veihjelp";
			} else if(carEvent.getEvent().equals("Skademelding") || carEvent.getEvent().equals("Damage Report")) {
				eventText = "Skademelding";
			}
			
			holder.eventText.setText(eventText + " " + formatter.format(d));
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		
		
		holder.delete_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						if(deleteCarEvent(carEvent.getUid())) {
							carEventAdapter.openWritable();
							carEventAdapter.delete(carEvent.getUid());
							carEventAdapter.close();
							
							handler.post(new Runnable() {
								public void run() {
									carEvents.remove(carEvent);
									holder.delete_event.setVisibility(View.GONE);
									notifyDataSetChanged();
								}
							});
						}
					}
				}).start();
			}
		});
		return v;
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public static interface OnItemClickListener {
		public void onItemClick(CarEvent carEvent);
	}
	
	private class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		private ViewHolder holder;
		
		public GestureListener(ViewHolder holder) {
			this.holder = holder;
		}
		
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			float diffAbs = Math.abs(event1.getY() - event2.getY());
			float diff = event1.getX() - event2.getX();
			
			if(diffAbs > SWIPE_MAX_OFF_PATH)
				return false;
			
			if(diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(holder.delete_event.getVisibility() == View.GONE)
					holder.delete_event.setVisibility(View.VISIBLE);
				else
					holder.delete_event.setVisibility(View.GONE);
			} else if(-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(holder.delete_event.getVisibility() == View.GONE)
					holder.delete_event.setVisibility(View.VISIBLE);
				else
					holder.delete_event.setVisibility(View.GONE);
			}
			return true;
		}
	}
	
	public boolean deleteCarEvent(int uid) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		
		String result = request.send(context.getString(R.string.api_url) + "delete_car_event", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		return false;
	}
	
}