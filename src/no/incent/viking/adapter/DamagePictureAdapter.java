package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.DamagePicture;
import no.incent.viking.util.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DamagePictureAdapter extends ArrayAdapter<DamagePicture> {
	private final String TAG = "VIKING";
	private Context context;
	private int layoutResourceId;
	private List<DamagePicture> damagePictures;
	
	public DamagePictureAdapter(Context context, int layoutResourceId, List<DamagePicture> damagePictures) {
		super(context, layoutResourceId, damagePictures);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.damagePictures = damagePictures;
	}
	
	private class ViewHolder {
		private ImageView damage_pic;
		private TextView picture_name;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		DamagePicture damagePicture = damagePictures.get(position);
		ViewHolder holder;
		
		if(view == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(layoutResourceId, null);
			holder.damage_pic = (ImageView) view.findViewById(R.id.damage_pic);
			holder.picture_name = (TextView) view.findViewById(R.id.picture_name);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if(damagePicture.getThumbFile() != null) {
			Bitmap bitmap = decodeFile(damagePicture.getThumbFile());
			bitmap = Helpers.resizeBitmap(context, bitmap, 120, 120);
			holder.damage_pic.setImageBitmap(bitmap);
		}
		
		holder.picture_name.setText(damagePicture.getName());
		
		return view;
	}
	
	private Bitmap decodeFile(File f) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeStream(new FileInputStream(f), null, options);
			final int requiredSize = 77;
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
