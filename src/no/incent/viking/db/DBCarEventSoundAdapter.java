package no.incent.viking.db;

import no.incent.viking.pojo.CarEventSound;
import no.incent.viking.provider.CarEventSoundsProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public class DBCarEventSoundAdapter {
	private Context context;
	
	public DBCarEventSoundAdapter(Context context) {
		this.context = context;
	}
	
	public synchronized void open() {
	}
	
	public void close() {
	}
	
	public void insertCarEventSound(CarEventSound carEventSound) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, carEventSound.getUid());
		contentValues.put(DBHelper.COLUMN_OWNERID, carEventSound.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_EVENTID, carEventSound.getEventId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_NAME, carEventSound.getName());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME, carEventSound.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE, carEventSound.getFileType());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_PATH, carEventSound.getPath());
		
		context.getContentResolver().insert(CarEventSoundsProvider.CONTENT_URI, contentValues);
	}
	
	public void insertAll(List<CarEventSound> carEventSounds) {
		ContentValues[] contentValues = new ContentValues[carEventSounds.size()];
		int i = 0;
		
		for(CarEventSound carEventSound: carEventSounds) {
			contentValues[i] = new ContentValues();
			contentValues[i].put(DBHelper.COLUMN_UID, carEventSound.getUid());
			contentValues[i].put(DBHelper.COLUMN_OWNERID, carEventSound.getOwnerId());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_EVENTID, carEventSound.getEventId());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_SOUND_NAME, carEventSound.getName());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME, carEventSound.getFilename());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE, carEventSound.getFileType());
			contentValues[i].put(DBHelper.COLUMN_CAR_EVENT_SOUND_PATH, carEventSound.getPath());
			i++;
		}
		
		if(contentValues.length > 0)
			context.getContentResolver().bulkInsert(CarEventSoundsProvider.CONTENT_URI, contentValues);
	}
	
	public void updateCarEventSound(CarEventSound carEventSound) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, carEventSound.getUid());
		contentValues.put(DBHelper.COLUMN_OWNERID, carEventSound.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_EVENTID, carEventSound.getEventId());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_NAME, carEventSound.getName());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME, carEventSound.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE, carEventSound.getFileType());
		contentValues.put(DBHelper.COLUMN_CAR_EVENT_SOUND_PATH, carEventSound.getPath());
		
		context.getContentResolver().update(CarEventSoundsProvider.CONTENT_URI, contentValues,
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(carEventSound.getUid())});
	}
	
	public CarEventSound getCarEventSound(int uid) {
		CarEventSound carEventSound = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_EVENTID, DBHelper.COLUMN_CAR_EVENT_SOUND_NAME,
				DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME, DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE, DBHelper.COLUMN_CAR_EVENT_SOUND_PATH};
		Cursor cursor = context.getContentResolver().query(CarEventSoundsProvider.CONTENT_URI, columns,
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			carEventSound = new CarEventSound();
			carEventSound.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEventSound.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEventSound.setEventId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENTID)));
			carEventSound.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_NAME)));
			carEventSound.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME)));
			carEventSound.setFileType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE)));
			carEventSound.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_PATH)));
		}
		cursor.close();
		
		return carEventSound;
	}
	
	public List<CarEventSound> getAllCarEventSounds(int ownerId, int eventId) {
		List<CarEventSound> carEventSounds = new ArrayList<CarEventSound>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_EVENTID, DBHelper.COLUMN_CAR_EVENT_SOUND_NAME,
				DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME, DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE, DBHelper.COLUMN_CAR_EVENT_SOUND_PATH};
		Cursor cursor = context.getContentResolver().query(CarEventSoundsProvider.CONTENT_URI, columns,
				DBHelper.COLUMN_OWNERID + "=? AND " + DBHelper.COLUMN_CAR_EVENT_EVENTID + "=?", 
				new String[] {String.valueOf(ownerId), String.valueOf(eventId)}, null);
		
		while(cursor.moveToNext()) {
			CarEventSound carEventSound = new CarEventSound();
			carEventSound.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEventSound.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEventSound.setEventId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENTID)));
			carEventSound.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_NAME)));
			carEventSound.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_FILENAME)));
			carEventSound.setFileType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_FILETYPE)));
			carEventSound.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_SOUND_PATH)));
			carEventSounds.add(carEventSound);
		}
		cursor.close();
		
		return carEventSounds;
	}
	
	public void delete(int uid) {
		context.getContentResolver().delete(CarEventSoundsProvider.CONTENT_URI,
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)});
	}
	
	public void deleteAll(int ownerId) {
		context.getContentResolver().delete(CarEventSoundsProvider.CONTENT_URI,
				DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(ownerId)});
	}
}
