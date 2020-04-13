package no.incent.viking.db;

import no.incent.viking.pojo.CarFile;
import no.incent.viking.provider.MyCarFilesProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class DBCarFileAdapter {
	private Context context;
	//private DBHelper dbHelper;
	//private SQLiteDatabase database;
	
	public DBCarFileAdapter(Context context) {
		this.context = context;
		//dbHelper = new DBHelper(context);
	}
	
	public synchronized void open() {
		//database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		//database.close();
	}
	
	public void insertCarFile(CarFile carFile) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, carFile.getUid());
		contentValues.put(DBHelper.COLUMN_OWNERID, carFile.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_NAME, carFile.getName());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_GALLERY, carFile.getGallery());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_FILENAME, carFile.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_PATH, carFile.getPath());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT, carFile.getIsDefault());
		
		context.getContentResolver().insert(MyCarFilesProvider.CONTENT_URI, contentValues);
	}
	
	public void updateCarFile(CarFile carFile) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_OWNERID, carFile.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_NAME, carFile.getName());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_GALLERY, carFile.getGallery());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_FILENAME, carFile.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_PATH, carFile.getPath());
		contentValues.put(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT, carFile.getIsDefault());
		
		context.getContentResolver().update(MyCarFilesProvider.CONTENT_URI, contentValues, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(carFile.getUid())});
	}
	
	public CarFile getCarFile(int uid) {
		CarFile carFile = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_FILE_NAME, 
				DBHelper.COLUMN_CAR_FILE_GALLERY, DBHelper.COLUMN_CAR_FILE_FILENAME, DBHelper.COLUMN_CAR_FILE_PATH, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			carFile = new CarFile();
			carFile.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carFile.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carFile.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_NAME)));
			carFile.setGallery(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_GALLERY)));
			carFile.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_FILENAME)));
			carFile.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_PATH)));
			carFile.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT)));
		}
		cursor.close();
		
		return carFile;
	}
	
	public List<CarFile> getAllCarFiles(int ownerId) {
		List<CarFile> carFiles = new ArrayList<CarFile>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_FILE_NAME, 
				DBHelper.COLUMN_CAR_FILE_GALLERY, DBHelper.COLUMN_CAR_FILE_FILENAME, DBHelper.COLUMN_CAR_FILE_PATH, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(ownerId)}, null);
		
		while(cursor.moveToNext()) {
			CarFile carFile = new CarFile();
			
			carFile.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carFile.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carFile.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_NAME)));
			carFile.setGallery(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_GALLERY)));
			carFile.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_FILENAME)));
			carFile.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_PATH)));
			carFile.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT)));
			
			carFiles.add(carFile);
		}
		cursor.close();
		
		return carFiles;
	}
	
	public List<CarFile> getAllTempCarFiles() {
		List<CarFile> carFiles = new ArrayList<CarFile>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_FILE_NAME, 
				DBHelper.COLUMN_CAR_FILE_GALLERY, DBHelper.COLUMN_CAR_FILE_FILENAME, DBHelper.COLUMN_CAR_FILE_PATH, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_CAR_FILE_NAME + " not in (?,?,?,?)", new String[] {"Førerkort", "Vognkort", "Servicehefte", "Forsikringsbevis"}, null);
		while(cursor.moveToNext()) {
			CarFile carFile = new CarFile();
			
			carFile.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carFile.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carFile.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_NAME)));
			carFile.setGallery(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_GALLERY)));
			carFile.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_FILENAME)));
			carFile.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_PATH)));
			carFile.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT)));
			
			carFiles.add(carFile);
		}
		cursor.close();
		return carFiles;
	}
	
	public List<String> getAllGalleries(int ownerId) {
		String[] columns = {DBHelper.COLUMN_CAR_FILE_GALLERY};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(ownerId)}, null);
		List<String> galleries = new ArrayList<String>();

		while(cursor.moveToNext()) {
			galleries.add(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_GALLERY)));
		}
		cursor.close();
		
		return galleries;
	}
	
	public List<CarFile> getAllCarFilesByGallery(String gallery) {
		List<CarFile> carFiles = new ArrayList<CarFile>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_FILE_NAME, 
				DBHelper.COLUMN_CAR_FILE_GALLERY, DBHelper.COLUMN_CAR_FILE_FILENAME, DBHelper.COLUMN_CAR_FILE_PATH, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, DBHelper.COLUMN_CAR_FILE_GALLERY + "=?", new String[] {gallery}, null);
		
		while(cursor.moveToNext()) {
			CarFile carFile = new CarFile();
			
			carFile.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carFile.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carFile.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_NAME)));
			carFile.setGallery(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_GALLERY)));
			carFile.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_FILENAME)));
			carFile.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_PATH)));

			if(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT)) == null) {
				carFile.setIsDefault("");
			} else {
				carFile.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT)));
			}
			
			carFiles.add(carFile);
		}
		cursor.close();
		
		return carFiles;
	}
	
	public List<CarFile> getAllDefaultCarFile() {
		List<CarFile> carFiles = new ArrayList<CarFile>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_FILE_NAME, 
				DBHelper.COLUMN_CAR_FILE_GALLERY, DBHelper.COLUMN_CAR_FILE_FILENAME, DBHelper.COLUMN_CAR_FILE_PATH, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT + "='yes'", null, null);
		
		while(cursor.moveToNext()) {
			CarFile carFile = new CarFile();
			
			carFile.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carFile.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carFile.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_NAME)));
			carFile.setGallery(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_GALLERY)));
			carFile.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_FILENAME)));
			carFile.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_PATH)));
			carFile.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILE_IS_DEFAULT)));
			
			carFiles.add(carFile);
		}
		
		return carFiles;
	}
	
	public void delete(int uid) {
		context.getContentResolver().delete(MyCarFilesProvider.CONTENT_URI, 
				DBHelper.COLUMN_UID + "=?", new String[]{String.valueOf(uid)});
	}
	
	public void deleteAll(int ownerId) {
		context.getContentResolver().delete(MyCarFilesProvider.CONTENT_URI, null, null);
	}
	
	
	public void deleteGallery(String gallery) {
		context.getContentResolver().delete(MyCarFilesProvider.CONTENT_URI, DBHelper.COLUMN_CAR_FILE_GALLERY + "=?", new String[] {gallery});
	}
	
	public synchronized boolean hasDefaultFile() {
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarFilesProvider.CONTENT_URI, columns, DBHelper.COLUMN_CAR_FILE_IS_DEFAULT + "='yes'", null, null);
		
		if(cursor.moveToNext()) {
			return true;
		}
		
		cursor.close();
		
		return false;
	}
}
