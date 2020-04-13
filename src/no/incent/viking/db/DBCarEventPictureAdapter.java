package no.incent.viking.db;

import no.incent.viking.pojo.CarEventPicture;
import no.incent.viking.provider.CarEventPicturesProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBCarEventPictureAdapter {
	private final String TAG = "VIKING";
	private Context context;
	
	public DBCarEventPictureAdapter(Context context) {
		this.context = context;
	}
	
	public synchronized void open() {
	}
	
	public void close() {
	}
	
	public void insertCarEventPicture(CarEventPicture carEventPicture) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, carEventPicture.getUid());
		contentValues.put(DBHelper.COLUMN_OWNERID, carEventPicture.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_EVENTID, carEventPicture.getEventId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME, carEventPicture.getName());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME, carEventPicture.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE, carEventPicture.getFileType());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH, carEventPicture.getPath());
		
		context.getContentResolver().insert(CarEventPicturesProvider.CONTENT_URI, contentValues);
	}
	
	public void insertAll(List<CarEventPicture> carEventPictures) {
		ContentValues[] contentValues = new ContentValues[carEventPictures.size()];
		int i = 0;
		
		for(CarEventPicture carEventPicture: carEventPictures) {
			contentValues[i] = new ContentValues();
			contentValues[i].put(DBHelper.COLUMN_UID, carEventPicture.getUid());
			contentValues[i].put(DBHelper.COLUMN_OWNERID, carEventPicture.getOwnerId());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_EVENTID, carEventPicture.getEventId());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME, carEventPicture.getName());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME, carEventPicture.getFilename());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE, carEventPicture.getFileType());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH, carEventPicture.getPath());
			i++;
		}
		
		if(contentValues.length > 0) {
			context.getContentResolver().bulkInsert(CarEventPicturesProvider.CONTENT_URI, contentValues);
		}
	}
	
	public void updateCarEventPicture(CarEventPicture carEventPicture) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, carEventPicture.getUid());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_EVENTID, carEventPicture.getEventId());
		contentValues.put(DBHelper.COLUMN_OWNERID, carEventPicture.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME, carEventPicture.getName());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME, carEventPicture.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE, carEventPicture.getFileType());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH, carEventPicture.getPath());
		
		context.getContentResolver().update(CarEventPicturesProvider.CONTENT_URI, contentValues, 
				DBHelper.COLUMN_UID + "=?", new String[]{String.valueOf(carEventPicture.getUid())});
	}
	
	public CarEventPicture getCarEventPicture(int uid) {
		CarEventPicture carEventPicture = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_EVENTID, DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME,
				DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME, DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE, DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH};
		Cursor cursor = context.getContentResolver().query(CarEventPicturesProvider.CONTENT_URI, columns, DBHelper.COLUMN_UID + "=?", new String[]{String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			carEventPicture = new CarEventPicture();
			carEventPicture.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEventPicture.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEventPicture.setEventId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENTID)));
			carEventPicture.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME)));
			carEventPicture.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME)));
			carEventPicture.setFileType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE)));
			carEventPicture.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH)));
		}
		cursor.close();
		
		return carEventPicture;
	}
	
	public CarEventPicture getCarEventPicture(String name) {
		CarEventPicture carEventPicture = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_EVENTID, DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME,
				DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME, DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE, DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH};
		Cursor cursor = context.getContentResolver().query(CarEventPicturesProvider.CONTENT_URI, columns, DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME + "=?", new String[] {name}, null);
		
		if(cursor.moveToNext()) {
			carEventPicture = new CarEventPicture();
			carEventPicture.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEventPicture.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEventPicture.setEventId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENTID)));
			carEventPicture.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME)));
			carEventPicture.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME)));
			carEventPicture.setFileType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE)));
			carEventPicture.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH)));
		}
		cursor.close();
		
		return carEventPicture;
	}
	
	public List<CarEventPicture> getAllCarEventPictures(int ownerId, int eventId) {
		List<CarEventPicture> carEventPictures = new ArrayList<CarEventPicture>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_EVENTID, DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME,
				DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME, DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE, DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH};
		Cursor cursor = context.getContentResolver().query(CarEventPicturesProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_OWNERID + "=? AND " + DBHelper.COLUMN_CAR_EVENT_EVENTID + "=?",
				new String[]{String.valueOf(ownerId), String.valueOf(eventId)}, null);
		
		while (cursor.moveToNext()) {
			CarEventPicture carEventPicture = new CarEventPicture();
			carEventPicture.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEventPicture.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEventPicture.setEventId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENTID)));
			carEventPicture.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_NAME)));
			carEventPicture.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILENAME)));
			carEventPicture.setFileType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_FILETYPE)));
			carEventPicture.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PICTURE_PATH)));
			
			carEventPictures.add(carEventPicture);
		}
		cursor.close();
		
		return carEventPictures;
	}
	
	public void delete(int uid) {
		context.getContentResolver().delete(CarEventPicturesProvider.CONTENT_URI, 
				DBHelper.COLUMN_UID + "=?", new String[]{String.valueOf(uid)});
	}
	
	public void deleteAll(int ownerId) {
		context.getContentResolver().delete(CarEventPicturesProvider.CONTENT_URI,
				DBHelper.COLUMN_OWNERID + "=?", new String[]{String.valueOf(ownerId)});
	}
}
