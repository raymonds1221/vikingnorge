package no.incent.viking.adapter;

import no.incent.viking.pojo.CarFile;
import no.incent.viking.util.Helpers;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.util.Log;

public class CarFilePictureViewAdapter extends BaseAdapter {
	private final String TAG = "VIKING";
	private Context context;
	private List<CarFile> carFiles;
	private WindowManager windowManager;
	
	public CarFilePictureViewAdapter(Context context, WindowManager windowManager, List<CarFile> carFiles) {
		this.context = context;
		this.windowManager = windowManager;
		this.carFiles = carFiles;
	}

	@Override
	public int getCount() {
		return carFiles.size();
	}

	@Override
	public Object getItem(int position) {
		return carFiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CarFile carFile = carFiles.get(position);
		ImageView imageView = new ImageView(context);
		
		if(carFile.getPath() != null) {
			DisplayMetrics metrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(metrics);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(carFile.getPath(), options);
			final int requiredSize = metrics.heightPixels;
			int scale = 1;
			
			while(options.outWidth/scale/2 >= requiredSize && options.outHeight/scale/2 >= requiredSize) {
				scale *= 2;
			}
			
			/*
			int scaleWidth = options.outWidth / metrics.widthPixels;
			int scaleHeight = options.outHeight / metrics.heightPixels;
			
			scale = (int) Math.min(scaleWidth, scaleHeight);*/
			
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(carFile.getPath(), options2);
			
			float scaleWidth = (float) metrics.widthPixels / options.outWidth;
			float scaleHeight = (float) metrics.heightPixels / options.outHeight;
			float scaleFactor = Math.min(scaleWidth, scaleHeight);
			
			int newWidth = Math.round(options.outWidth * scaleFactor);
			int newHeight = Math.round(options.outHeight * scaleFactor);
			
			bitmap = Helpers.resizeBitmap(context, bitmap, newWidth, newHeight);
			imageView.setImageBitmap(bitmap);
		}
		
		return imageView;
	}

}
