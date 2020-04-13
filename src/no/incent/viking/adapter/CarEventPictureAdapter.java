package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.util.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class CarEventPictureAdapter extends ArrayAdapter<CarEventPicture> {
	private final String TAG = "VIKING";
	private Context context;
	private int layoutResourceId;
	private List<CarEventPicture> carEventPictures;
	
	public CarEventPictureAdapter(Context context, int layoutResourceId, List<CarEventPicture> carEventPictures) {
		super(context, layoutResourceId, carEventPictures);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.carEventPictures = carEventPictures;
	}
	
	private class ViewHolder {
		private ImageView event_picture;
		private TextView event_picture_name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		CarEventPicture carEventPicture = carEventPictures.get(position);
		
		if(v == null) {
			holder = new ViewHolder();
			
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(layoutResourceId, null);
			holder.event_picture = (ImageView) v.findViewById(R.id.event_picture);
			holder.event_picture_name = (TextView) v.findViewById(R.id.event_picture_name);
			
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		
		if(carEventPicture.getPath() != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(carEventPicture.getPath(), options);
			final int requiredSize = 70;
			int scale = 1;
			
			while(options.outWidth/scale/2 >= requiredSize && options.outHeight/scale/2 >= requiredSize) {
				scale *= 2;
			}
			
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(carEventPicture.getPath(), options2);
			holder.event_picture.setImageBitmap(bitmap);
		}
		
		holder.event_picture_name.setText(carEventPicture.getName());
		
		
		return v;
	}
	
	private Bitmap decodeFile(File f) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeStream(new FileInputStream(f), null, options);
			final int requiredSize = 70;
			int scale = 1;
			
			while(options.outWidth/scale/2 >= requiredSize && options.outHeight/scale/2 >= requiredSize) {
				scale *= 2;
			}
			
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, options2);
		} catch(IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
		return null;
	}
}
