package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.CarFile;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.db.DBCarFileAdapter;

import java.util.List;
import java.util.ArrayList;
import java.net.URLDecoder;

import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MyCarFilesAdapter extends ArrayAdapter<String> {
	private final String TAG = "VIKING";
	private List<String> galleries;
	private Context context;
	private OnItemClickListener onItemClickListener;
	private DBCarFileAdapter carFileAdapter;
	private final Handler handler = new Handler();

	public MyCarFilesAdapter(Context ctx, int layoutItemResourceId, List<String> galleries) {
		super(ctx, layoutItemResourceId, galleries);
		this.galleries = galleries;
		this.context = ctx;
		carFileAdapter = new DBCarFileAdapter(ctx);
	}

	public static class ViewHolder{
		public ImageView thumb;
		public TextView fileText;
		public ImageButton viewBtn;
		public ImageButton delete_files;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ViewHolder holder;
		final String gallery = galleries.get(position);
		
		if (v == null) {		
			LayoutInflater vi = 
				(LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = vi.inflate(R.layout.list_mycar_files, null);
			
			holder = new ViewHolder();
			holder.thumb = (ImageView) v.findViewById(R.id.mycar_files_thumb);
			holder.fileText = (TextView) v.findViewById(R.id.mycar_files_text);
			holder.viewBtn = (ImageButton) v.findViewById(R.id.view_btn);
			holder.delete_files = (ImageButton) v.findViewById(R.id.delete_files);
			
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();
		
		
		carFileAdapter.open();
		List<CarFile> carFiles = carFileAdapter.getAllCarFilesByGallery(gallery);
		carFileAdapter.close();
		
		if(carFiles.size() > 0) {
			CarFile carFile = carFiles.get(0);
			if(carFile.getIsDefault().equals("yes")) {
				if(carFiles.size() > 1) {
					carFile = carFiles.get(1);
				}
			}
			if(carFile.getPath() != null) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(carFile.getPath(), options);
				final int requiredSize = 200;
				int scale = 1;
				
				while(options.outWidth/scale/2 >= requiredSize && options.outHeight/scale/2 >= requiredSize) {
					scale *= 2;
				}
				
				BitmapFactory.Options options2 = new BitmapFactory.Options();
				options2.inSampleSize = scale;
				Bitmap bitmap = BitmapFactory.decodeFile(carFile.getPath(), options2);
				holder.thumb.setImageBitmap(Helpers.resizeBitmap(context, bitmap, 100, 80));
			}
			
			String file = carFile.getName();
			for(CarFile cf: carFiles) {
				if(cf.getIsDefault().equals("yes")) {
					file = cf.getName();
					break;
				}
			}
			if (file != null) {
				holder.fileText.setText(URLDecoder.decode(file));
			}
		}
		
		final GestureDetector gestureDetector = new GestureDetector(new GestureListener(holder, gallery));
		v.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemClickListener != null) {
					onItemClickListener.onItemClick(gallery);
				}
			}
		});
		
		holder.delete_files.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						if(Helpers.Constants.mIsLoggedIn) {
							Log.i(TAG, "gallery: " + gallery);
							if(deleteCarFileGallery(gallery)) {
								carFileAdapter.deleteGallery(gallery);
								galleries.remove(gallery);
								
								handler.post(new Runnable() {
									public void run() {
										holder.delete_files.setVisibility(View.GONE);
										holder.viewBtn.setVisibility(View.VISIBLE);
										notifyDataSetChanged();
									}
								});
							}
						} else {
							carFileAdapter.deleteGallery(gallery);
							galleries.remove(gallery);
							
							handler.post(new Runnable() {
								public void run() {
									holder.delete_files.setVisibility(View.GONE);
									holder.viewBtn.setVisibility(View.VISIBLE);
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
		public void onItemClick(String gallery);
	}
	
	private class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 200;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		private ViewHolder holder;
		private String gallery;
		
		public GestureListener(ViewHolder holder, String gallery) {
			this.holder = holder;
			this.gallery = gallery;
		}
		
		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			float diffAbs = Math.abs(event1.getY() - event2.getY());
			float diff = event1.getX() - event2.getX();
			
			if(diffAbs > SWIPE_MAX_OFF_PATH)
				return false;
			
			if(diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(!gallery.equals("Forerkort") && !gallery.equals("Vognkort") 
						&& !gallery.equals("Servicehefte") && !gallery.equals("Forsikringsbevis")) {
					if(holder.delete_files.getVisibility() == View.VISIBLE) {
						holder.delete_files.setVisibility(View.GONE);
						holder.viewBtn.setVisibility(View.VISIBLE);
					} else {
						holder.delete_files.setVisibility(View.VISIBLE);
						holder.viewBtn.setVisibility(View.GONE);
					}
				}
			} else if(-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(!gallery.equals("Forerkort") && !gallery.equals("Vognkort") 
						&& !gallery.equals("Servicehefte") && !gallery.equals("Forsikringsbevis")) {
					if(holder.delete_files.getVisibility() == View.VISIBLE) {
						holder.delete_files.setVisibility(View.GONE);
						holder.viewBtn.setVisibility(View.VISIBLE);
					} else {
						holder.delete_files.setVisibility(View.VISIBLE);
						holder.viewBtn.setVisibility(View.GONE);
					}
				}
			}
			return true;
		}
	}
	
	public boolean deleteCarFileGallery(String gallery) {
		HttpRequest request = HttpRequest.getInstance();
		int ownerId = 0;
		
		if(((VikingApplication)context.getApplicationContext()).getUser() != null) {
			ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
		}
		List<NameValuePair> queries = new ArrayList<NameValuePair>();
		queries.add(new BasicNameValuePair("gallery", gallery));
		queries.add(new BasicNameValuePair("owner_id", String.valueOf(ownerId)));
		
		String result = request.send(context.getString(R.string.api_url) + "delete_car_file_gallery", queries, HttpRequest.POST);
		Log.i(TAG, "delete gallery result: " + result);
		if(result != null) {
			int success = Integer.parseInt(Helpers.parseXMLNode(result, "successful"));
			
			if(success == 1) {
				Log.i(TAG, "car file gallery deleted.");
				return true;
			}
		}
		return false;
	}
}