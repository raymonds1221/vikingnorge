package no.incent.viking.widget;

import no.incent.viking.R;
import no.incent.viking.VikingApplication;
import no.incent.viking.pojo.CarFile;
import no.incent.viking.util.Helpers;
import no.incent.viking.util.HttpRequest;
import no.incent.viking.util.MyCarEntity;
import no.incent.viking.db.DBCarFileAdapter;
import no.incent.viking.adapter.MyCarFilesAdapter;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.ByteArrayOutputStream;

import android.os.Handler;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.ViewFlipper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MyCarFiles extends MyCarEntity {
	private final String TAG = "VIKING";
	private final Handler handler = new Handler();
	private Context context;
	private HttpRequest request;
	private static DBCarFileAdapter carFileAdapter;
	private ListView mycar_files_list;
	private int ownerId = 0;
	private ViewFlipper mycar_files_list_flipper;
	private List<String> galleries;
	private File carFileDirectory;

	public MyCarFiles(final Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.mycar_files, this);
		
		if(((VikingApplication) context.getApplicationContext()).getUser() != null){
			ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
		}
		
		this.context = context;
		request = HttpRequest.getInstance();
		carFileAdapter = new DBCarFileAdapter(context);
		mycar_files_list = (ListView) findViewById(R.id.mycar_files_list);
		mycar_files_list_flipper = (ViewFlipper) findViewById(R.id.mycar_files_list_flipper);
		
		if(!Helpers.Constants.mIsLoggedIn)
			saveAllDefaultTempCarFiles();
	}
	
	private List<CarFile> getAllCarFiles(int ownerId) {
		List<CarFile> carFiles = new ArrayList<CarFile>();
		String url  = context.getString(R.string.api_url) + String.format("car_files/%s/json/%s/2/" + System.currentTimeMillis(), ownerId, System.currentTimeMillis());
		String jsonString = request.send(url, null, HttpRequest.GET);
		
		if(jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				JSONArray car_files = json.getJSONArray("car_files");
				
				carFileAdapter.open();
				carFileAdapter.deleteAll(ownerId);
				
				if(!carFileAdapter.hasDefaultFile()) {
					CarFile carFile = new CarFile();;
					carFile.setUid(-1);
					carFile.setName("Førerkort");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Forerkort");
					
					Drawable d = context.getResources().getDrawable(R.drawable.vognkort);
					Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					File f = new File(carFileDirectory, "vognkort.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
					
					carFile = new CarFile();
					carFile.setUid(-2);
					carFile.setName("Vognkort");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Vognkort");
					
					d = context.getResources().getDrawable(R.drawable.forerkort);
					bitmap = ((BitmapDrawable) d).getBitmap();
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					f = new File(carFileDirectory, "forerkort.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
					
					carFile = new CarFile();
					carFile.setUid(-3);
					carFile.setName("Servicehefte");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Servicehefte");
					
					d = context.getResources().getDrawable(R.drawable.servicehefte);
					bitmap = ((BitmapDrawable) d).getBitmap();
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					f = new File(carFileDirectory, "servicehefte.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
					
					carFile = new CarFile();
					carFile.setUid(-4);
					carFile.setName("Forsikringsbevis");
					carFile.setOwnerId(ownerId);
					carFile.setGallery("Forsikringsbevis");
					
					d = context.getResources().getDrawable(R.drawable.forsikringsbevis);
					bitmap = ((BitmapDrawable) d).getBitmap();
					baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					f = new File(carFileDirectory, "forsikringsbevis.png");
					Helpers.saveFile(f, baos.toByteArray(), true);
					carFile.setPath(f.getAbsolutePath());
					carFile.setIsDefault("yes");
					carFileAdapter.insertCarFile(carFile);
				} else {
					for(CarFile carFile: carFileAdapter.getAllDefaultCarFile()) {
						carFile.setOwnerId(ownerId);
						carFileAdapter.updateCarFile(carFile);
					}
				}
				
				for(int i=0;i<car_files.length();i++) {
					JSONObject file = car_files.getJSONObject(i).getJSONObject("file");
					CarFile carFile = new CarFile();
					carFile.setUid(file.getInt("uid"));
					carFile.setOwnerId(file.getInt("owner_id"));
					carFile.setName(file.getString("name"));
					carFile.setGallery(file.getString("gallery"));
					carFile.setFilename(file.getString("filename"));
					byte[] data = request.send(carFile.getFilename());
					
					String filename = Helpers.getLastPathSegment(carFile.getFilename());
					File f = new File(carFileDirectory, filename);
					
					if(filename != null && f != null && Helpers.isValidDownloadFile(filename)) {
						Helpers.saveFile(f, data, true);
						carFile.setPath(f.getAbsolutePath());
					}
					
					carFile.setIsDefault("");
					carFileAdapter.insertCarFile(carFile);
				}
				carFiles = carFileAdapter.getAllCarFiles(ownerId);
				carFileAdapter.close();
			} catch(JSONException ex) {
				Log.e(TAG, ex.getMessage());
			}
		} else {
			carFileAdapter.open();
			carFiles = carFileAdapter.getAllCarFiles(ownerId);
			carFileAdapter.close();
		}
		
		return carFiles;
	}
	
	public void saveAllDefaultTempCarFiles() {
		carFileDirectory = Helpers.createDirectory("viking/carfiles");
		
		CarFile carFile = new CarFile();;
		carFile.setUid(-1);
		carFile.setName("Førerkort");
		carFile.setOwnerId(ownerId);
		carFile.setGallery("Forerkort");
		
		Drawable d = context.getResources().getDrawable(R.drawable.vognkort);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		File f = new File(carFileDirectory, "vognkort.png");
		Helpers.saveFile(f, baos.toByteArray(), true);
		carFile.setPath(f.getAbsolutePath());
		carFile.setIsDefault("yes");
		carFileAdapter.insertCarFile(carFile);
		
		carFile = new CarFile();
		carFile.setUid(-2);
		carFile.setName("Vognkort");
		carFile.setOwnerId(ownerId);
		carFile.setGallery("Vognkort");
		
		d = context.getResources().getDrawable(R.drawable.forerkort);
		bitmap = ((BitmapDrawable) d).getBitmap();
		baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		f = new File(carFileDirectory, "forerkort.png");
		Helpers.saveFile(f, baos.toByteArray(), true);
		carFile.setPath(f.getAbsolutePath());
		carFile.setIsDefault("yes");
		carFileAdapter.insertCarFile(carFile);
		
		carFile = new CarFile();
		carFile.setUid(-3);
		carFile.setName("Servicehefte");
		carFile.setOwnerId(ownerId);
		carFile.setGallery("Servicehefte");
		
		d = context.getResources().getDrawable(R.drawable.servicehefte);
		bitmap = ((BitmapDrawable) d).getBitmap();
		baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		f = new File(carFileDirectory, "servicehefte.png");
		Helpers.saveFile(f, baos.toByteArray(), true);
		carFile.setPath(f.getAbsolutePath());
		carFile.setIsDefault("yes");
		carFileAdapter.insertCarFile(carFile);
		
		carFile = new CarFile();
		carFile.setUid(-4);
		carFile.setName("Forsikringsbevis");
		carFile.setOwnerId(ownerId);
		carFile.setGallery("Forsikringsbevis");
		
		d = context.getResources().getDrawable(R.drawable.forsikringsbevis);
		bitmap = ((BitmapDrawable) d).getBitmap();
		baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		f = new File(carFileDirectory, "forsikringsbevis.png");
		Helpers.saveFile(f, baos.toByteArray(), true);
		carFile.setPath(f.getAbsolutePath());
		carFile.setIsDefault("yes");
		carFileAdapter.insertCarFile(carFile);
	}
	
	public void updateCarFileList() {
		carFileAdapter.open();
		galleries = groupCarFiles(carFileAdapter.getAllCarFiles(ownerId));
		MyCarFilesAdapter adapter = new MyCarFilesAdapter(context, 
				R.layout.list_mycar_files, galleries);
		adapter.setDropDownViewResource(R.layout.list_mycar_files);
		mycar_files_list.setAdapter(adapter);
		carFileAdapter.close();
		
		adapter.setOnItemClickListener(new MyCarFilesAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(String gallery) {
				((OnMyCarFilesCallback) context).onCarFileClick(gallery);
			}
		});
	}
	
	public static interface OnMyCarFilesCallback {
		public void onCarFileClick(String carFile);
	}
	
	private List<String> groupCarFiles(List<CarFile> carFiles) {
		List<String> galleries = new ArrayList<String>();
		
		for(CarFile carFile: carFiles) {
			if(!galleries.contains(carFile.getGallery())) {
				galleries.add(carFile.getGallery());
			}
		}
		
		return galleries;
	}

	@Override
	public void load() {
		carFileDirectory = Helpers.createDirectory("viking/carfiles");
		
		/*
		if(Helpers.isNetworkAvailable(context)) {
			mycar_files_list_flipper.showNext();
			new Thread(new Runnable() {
				public void run() {
					if(((VikingApplication) context.getApplicationContext()).getUser() != null) {
						ownerId = ((VikingApplication) context.getApplicationContext()).getUser().getUid();
					}
					galleries = groupCarFiles(getAllCarFiles(ownerId));
					
					handler.post(new Runnable() {
						public void run() {
							MyCarFilesAdapter adapter = new MyCarFilesAdapter(context,
									R.layout.list_mycar_files, galleries);
							adapter.setDropDownViewResource(R.layout.list_mycar_files);
							mycar_files_list.setAdapter(adapter);
							mycar_files_list_flipper.showPrevious();
							
							adapter.setOnItemClickListener(new MyCarFilesAdapter.OnItemClickListener() {
								@Override
								public void onItemClick(String gallery) {
									((OnMyCarFilesCallback) context).onCarFileClick(gallery);
								}
							});
						}
					});
				}
			}).start();
		} else {
			carFileAdapter.open();
			galleries = groupCarFiles(carFileAdapter.getAllCarFiles(ownerId));
			MyCarFilesAdapter adapter = new MyCarFilesAdapter(context,
					R.layout.list_mycar_files, galleries);
			adapter.setDropDownViewResource(R.layout.list_mycar_files);
			adapter.notifyDataSetChanged();
			
			adapter.setOnItemClickListener(new MyCarFilesAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(String gallery) {
					((OnMyCarFilesCallback) context).onCarFileClick(gallery);
				}
			});
			mycar_files_list.setAdapter(adapter);
			carFileAdapter.close();
		}*/
		
		carFileAdapter.open();
		galleries = groupCarFiles(carFileAdapter.getAllCarFiles(ownerId));
		MyCarFilesAdapter adapter = new MyCarFilesAdapter(context,
				R.layout.list_mycar_files, galleries);
		adapter.setDropDownViewResource(R.layout.list_mycar_files);
		adapter.notifyDataSetChanged();
		
		adapter.setOnItemClickListener(new MyCarFilesAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(String gallery) {
				((OnMyCarFilesCallback) context).onCarFileClick(gallery);
			}
		});
		
		mycar_files_list.setAdapter(adapter);
		carFileAdapter.close();
	}

	@Override
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
	public static List<CarFile> getAllTempCarFiles() {
		return carFileAdapter.getAllTempCarFiles();
	}
}
