package no.incent.viking.adapter;

import no.incent.viking.R;
import no.incent.viking.pojo.Car;
import no.incent.viking.util.Helpers;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Gallery;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CarImageAdapter extends BaseAdapter {
	private Context context;
	private List<Car> cars;
	
	public CarImageAdapter(Context context, List<Car> cars) {
		this.context = context;
		this.cars = cars;
	}
	
	@Override
	public int getCount() {
		return cars.size();
	}

	@Override
	public Object getItem(int position) {
		return cars.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Car car = cars.get(position);
		Bitmap bitmap = null;
		
		ImageView car_image = new ImageView(context);
		if(car.getPath() != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			BitmapFactory.decodeFile(car.getPath(),options);
			final int requiredSize = 500;
			int scale = 1;
			
			while(options.outWidth/scale/2 >= requiredSize && options.outHeight/scale/2 >= requiredSize) {
				scale *= 2;
			}
			
			BitmapFactory.Options options2 = new BitmapFactory.Options();
			options2.inSampleSize = scale;
			options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory.decodeFile(car.getPath(), options2);
			bitmap = Helpers.resizeBitmap(context, bitmap, 500, 220);
		} else {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car_img_frame);
		}
		car_image.setImageBitmap(bitmap);
		car_image.setScaleType(ImageView.ScaleType.FIT_XY);
		car_image.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT, Gallery.LayoutParams.FILL_PARENT));
		return car_image;
	}

	
}
