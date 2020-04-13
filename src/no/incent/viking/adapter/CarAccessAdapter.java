package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.CarAccess;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;

import java.util.List;
import java.util.ArrayList;

import android.os.Handler;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class CarAccessAdapter extends ArrayAdapter<CarAccess> {
	private Context context;
	private List<CarAccess> carAccesses;
	private final Handler handler = new Handler();

	public CarAccessAdapter(Context context, int layoutId, List<CarAccess> carAccesses) {
		super(context, layoutId, carAccesses);
		this.context = context;
		this.carAccesses = carAccesses;
	}
	
	private class ViewHolder {
		public TextView access_role;
		public TextView access_name;
		public ImageButton delete_user;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CarAccess carAccess = carAccesses.get(position);
		final ViewHolder holder;
		View view = convertView;
		
		if(view == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.list_car_access, null);
			
			holder.access_role = (TextView) view.findViewById(R.id.access_role);
			holder.access_name = (TextView) view.findViewById(R.id.access_name);
			holder.delete_user = (ImageButton) view.findViewById(R.id.delete_user);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.access_role.setText(carAccess.getDescription());
		holder.access_name.setText(carAccess.getName());
		
		if(position == 0) {
			view.setBackgroundResource(R.drawable.table_top);
		} else if(position == carAccesses.size() - 1) {
			view.setBackgroundResource(R.drawable.table_bottom);
		} else {
			view.setBackgroundResource(R.drawable.table_mid);
		}
		
		final GestureDetector gestureDetector = new GestureDetector(new GestureListener(holder));
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		holder.delete_user.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						if(deleteSharedCar(carAccess.getUid())) {
							carAccesses.remove(carAccess);
							
							handler.post(new Runnable() {
								public void run() {
									holder.delete_user.setVisibility(View.GONE);
									notifyDataSetChanged();
								}
							});
						}
					}
				}).start();
			}
		});
		return view;
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
				if(holder.delete_user.getVisibility() == View.VISIBLE)
					holder.delete_user.setVisibility(View.GONE);
				else
					holder.delete_user.setVisibility(View.VISIBLE);
			} else if(-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(holder.delete_user.getVisibility() == View.VISIBLE)
					holder.delete_user.setVisibility(View.GONE);
				else
					holder.delete_user.setVisibility(View.VISIBLE);
			}
			return true;
		}
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
