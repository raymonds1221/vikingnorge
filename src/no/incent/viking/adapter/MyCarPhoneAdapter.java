package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.CarPhone;
import no.incent.viking.db.DBCarPhoneAdapter;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.Helpers;

import java.util.List;
import java.util.ArrayList;

import android.os.Handler;
import android.content.Intent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.net.Uri;
import android.text.Html;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MyCarPhoneAdapter extends ArrayAdapter<CarPhone> {
	private List<CarPhone> carPhones;;
	private Context context;
	private OnItemClickListener onItemClickListener;
	private DBCarPhoneAdapter carPhoneAdapter;
	private HttpRequest request;
	private final Handler handler = new Handler();

	public MyCarPhoneAdapter(Context ctx, int layoutItemResourceId, List<CarPhone> carPhones) {
		super(ctx, layoutItemResourceId, carPhones);
		this.carPhones = carPhones;
		this.context = ctx;
		carPhoneAdapter = new DBCarPhoneAdapter(ctx);
		request = HttpRequest.getInstance();
	}

	public static class ViewHolder{
		public TextView phoneDesc;
		public TextView phoneNo;
		public ImageButton callBtn;
		public ImageButton delete_phone;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;
		final CarPhone carPhone = carPhones.get(position);
		if (v == null) {		
			LayoutInflater vi = 
				(LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = vi.inflate(R.layout.list_mycar_phone, null);
			
			holder = new ViewHolder();
			holder.phoneDesc = (TextView) v.findViewById(R.id.mycar_phone_desc);
			holder.phoneNo = (TextView) v.findViewById(R.id.mycar_phone_number);
			holder.callBtn = (ImageButton) v.findViewById(R.id.call_btn);
			holder.delete_phone = (ImageButton) v.findViewById(R.id.delete_phone);
			
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		holder.phoneDesc.setText(Html.fromHtml(carPhone.getName()));
		holder.phoneNo.setText(carPhone.getTelephone());
		
		holder.callBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + carPhone.getTelephone()));
				context.startActivity(intent);
			}
		});
		
		holder.delete_phone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!carPhone.getIsDefault().equals("yes")) {
					new Thread(new Runnable() {
						public void run() {
							if(deleteCarPhone(carPhone.getUid())) {
								carPhoneAdapter.openWritable();
								carPhoneAdapter.delete(carPhone.getUid());
								carPhoneAdapter.close();
								
								carPhones.remove(carPhone);
								handler.post(new Runnable() {
									public void run() {
										MyCarPhoneAdapter.this.notifyDataSetChanged();
										holder.phoneNo.setVisibility(View.VISIBLE);
										holder.delete_phone.setVisibility(View.GONE);
									}
								});
							}
						}
					}).start();
				}
			}
		});
		
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null)
					onItemClickListener.onItemClick(carPhone);
			}
		});
		
		final GestureDetector gestureDetector = new GestureDetector(new GestureListener(holder));
		
		v.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		return v;
	}
	
	public void setOnItemClick(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	public static interface OnItemClickListener {
		public void onItemClick(CarPhone carPhone);
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
				if(holder.phoneNo.getVisibility() == View.GONE) {
					holder.phoneNo.setVisibility(View.VISIBLE);
					holder.delete_phone.setVisibility(View.GONE);
				} else {
					holder.phoneNo.setVisibility(View.GONE);
					holder.delete_phone.setVisibility(View.VISIBLE);
				}
			} else if(-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(holder.phoneNo.getVisibility() == View.GONE) {
					holder.phoneNo.setVisibility(View.VISIBLE);
					holder.delete_phone.setVisibility(View.GONE);
				} else {
					holder.phoneNo.setVisibility(View.GONE);
					holder.delete_phone.setVisibility(View.VISIBLE);
				}
			}
			return true;
		}
	}
	
	private boolean deleteCarPhone(int uid) {
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		
		String result = request.send(context.getString(R.string.api_url) + "delete_car_phone", queries, HttpRequest.POST);
		
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1)
				return true;
		}
		
		return false;
	}
}