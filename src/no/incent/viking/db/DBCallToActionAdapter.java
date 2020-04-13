package no.incent.viking.db;

import no.incent.viking.pojo.CallToAction;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBCallToActionAdapter {
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	
	public DBCallToActionAdapter(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public synchronized void openWritable() {
		database = dbHelper.getWritableDatabase();
	}
	
	public synchronized void openReadable() {
		database = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		database.close();
	}
	
	public synchronized void insertCallToAction(CallToAction callToAction) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, callToAction.getUid());
		contentValues.put(DBHelper.COLUMN_CALL_TO_ACTION_DESCRIPTION, callToAction.getDescription());
		contentValues.put(DBHelper.COLUMN_CALL_TO_ACTION_FILENAME, callToAction.getFilename());
		contentValues.put(DBHelper.COLUMN_CALL_TO_ACTION_DEVICE, callToAction.getDevice());
		contentValues.put(DBHelper.COLUMN_CALL_TO_ACTION_DIMENSION, callToAction.getDimension());
		contentValues.put(DBHelper.COLUMN_CALL_TO_ACTION_PATH, callToAction.getPath());
		
		database.insert(DBHelper.TABLE_CALL_TO_ACTIONS, null, contentValues);
	}
	
	public synchronized CallToAction getCallToAction(int uid) {
		CallToAction callToAction = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_CALL_TO_ACTION_DESCRIPTION, DBHelper.COLUMN_CALL_TO_ACTION_FILENAME,
				DBHelper.COLUMN_CALL_TO_ACTION_DEVICE, DBHelper.COLUMN_CALL_TO_ACTION_DIMENSION, DBHelper.COLUMN_CALL_TO_ACTION_PATH};
		Cursor cursor = database.query(DBHelper.TABLE_CALL_TO_ACTIONS, columns, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null, null, null);
		
		if(cursor.moveToNext()) {
			callToAction = new CallToAction();
			callToAction.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			callToAction.setDescription(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CALL_TO_ACTION_DESCRIPTION)));
			callToAction.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CALL_TO_ACTION_FILENAME)));
			callToAction.setDevice(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CALL_TO_ACTION_DEVICE)));
			callToAction.setDimension(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CALL_TO_ACTION_DIMENSION)));
			callToAction.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CALL_TO_ACTION_PATH)));
		}
		
		cursor.close();
		return callToAction;
	}
	
	public synchronized void deleteAll() {
		database.delete(DBHelper.TABLE_CALL_TO_ACTIONS, null, null);
	}
}
