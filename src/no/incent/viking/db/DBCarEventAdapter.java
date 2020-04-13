package no.incent.viking.db;

import no.incent.viking.pojo.CarEvent;
import no.incent.viking.provider.MyCarEventsProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBCarEventAdapter {
	private final String TAG = "VIKING";
	private Context context;
	
	public DBCarEventAdapter(Context context) {
		//dbHelper = new DBHelper(context);
		this.context = context;
	}
	
	public void openWritable() {
		//database = dbHelper.getWritableDatabase();
	}
	
	public void openReadable() {
		//database = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		//database.close();
	}
	
	public void insertCarEvent(CarEvent carEvent) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.COLUMN_UID, carEvent.getUid());
			contentValues.put(DBHelper.COLUMN_OWNERID, carEvent.getOwnerId());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_NAME, carEvent.getName());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_REGISTRATION, carEvent.getRegistration());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_EVENT, carEvent.getEvent());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_PLACE, carEvent.getPlace());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_DATETIME, carEvent.getDateTime());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_NOTE, carEvent.getNote());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_DATE_CREATED, carEvent.getDateCreated());
			
			context.getContentResolver().insert(MyCarEventsProvider.CONTENT_URI, contentValues);
		} catch(SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IllegalStateException ex) {
			Log.e(TAG, ex.getMessage());
		}
		
	}
	
	public void updateCarEvent(CarEvent carEvent) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_NAME, carEvent.getName());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_REGISTRATION, carEvent.getRegistration());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_EVENT, carEvent.getEvent());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_PLACE, carEvent.getPlace());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_DATETIME, carEvent.getDateTime());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_NOTE, carEvent.getNote());
			contentValues.put(DBHelper.COLUMN_CAR_EVENT_DATE_CREATED, carEvent.getDateCreated());
			
			context.getContentResolver().update(MyCarEventsProvider.CONTENT_URI, contentValues, 
						DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(carEvent.getUid())});
		} catch(SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IllegalStateException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
	
	public CarEvent getCarEvent(int uid) {
		CarEvent carEvent = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_NAME, DBHelper.COLUMN_CAR_EVENT_REGISTRATION,
				DBHelper.COLUMN_CAR_EVENT_EVENT, DBHelper.COLUMN_CAR_EVENT_PLACE, DBHelper.COLUMN_CAR_EVENT_DATETIME, DBHelper.COLUMN_CAR_EVENT_NOTE, DBHelper.COLUMN_CAR_EVENT_DATE_CREATED};
		Cursor cursor = context.getContentResolver().query(MyCarEventsProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			carEvent = new CarEvent();
			carEvent.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEvent.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEvent.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_NAME)));
			carEvent.setRegistration(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_REGISTRATION)));
			carEvent.setEvent(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENT)));
			carEvent.setPlace(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PLACE)));
			carEvent.setDateTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_DATETIME)));
			carEvent.setNote(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_NOTE)));
			carEvent.setDateCreated(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_DATE_CREATED)));
		}
		cursor.close();
		return carEvent;
	}
	
	public synchronized List<CarEvent> getAllCarEvents(int ownerId) {
		List<CarEvent> carEvents = new ArrayList<CarEvent>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_EVENT_NAME, DBHelper.COLUMN_CAR_EVENT_REGISTRATION,
				DBHelper.COLUMN_CAR_EVENT_EVENT, DBHelper.COLUMN_CAR_EVENT_PLACE, DBHelper.COLUMN_CAR_EVENT_DATETIME, DBHelper.COLUMN_CAR_EVENT_NOTE, DBHelper.COLUMN_CAR_EVENT_DATE_CREATED};
		Cursor cursor = context.getContentResolver().query(MyCarEventsProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(ownerId)}, null);
		
		while(cursor.moveToNext()) {
			CarEvent carEvent = new CarEvent();
			carEvent.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carEvent.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carEvent.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_NAME)));
			carEvent.setRegistration(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_REGISTRATION)));
			carEvent.setEvent(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_EVENT)));
			carEvent.setPlace(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_PLACE)));
			carEvent.setDateTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_DATETIME)));
			carEvent.setNote(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_NOTE)));
			carEvent.setDateCreated(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_EVENT_DATE_CREATED)));
			
			carEvents.add(carEvent);
		}
		cursor.close();
		
		return carEvents;
	}
	
	public synchronized void delete(int uid) {
		try {
			context.getContentResolver().delete(MyCarEventsProvider.CONTENT_URI, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)});
		} catch(SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IllegalStateException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
	
	public synchronized void deleteAll(int ownerId) {
		try {
			context.getContentResolver().delete(MyCarEventsProvider.CONTENT_URI, DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(ownerId)});
		} catch(SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IllegalStateException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
}
