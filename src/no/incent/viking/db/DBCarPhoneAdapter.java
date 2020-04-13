package no.incent.viking.db;

import no.incent.viking.pojo.CarPhone;
import no.incent.viking.provider.MyCarPhoneProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBCarPhoneAdapter {
	private final String TAG = "VIKING";
	private Context context;
	//private DBHelper dbHelper;
	//private SQLiteDatabase database;
	
	public DBCarPhoneAdapter(Context context) {
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
	
	public void insertCarPhone(CarPhone carPhone) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DBHelper.COLUMN_UID, carPhone.getUid());
			contentValues.put(DBHelper.COLUMN_OWNERID, carPhone.getOwnerId());
			contentValues.put(DBHelper.COLUMN_CAR_PHONE_CATEGORY, carPhone.getCategory());
			contentValues.put(DBHelper.COLUMN_CAR_PHONE_NAME, carPhone.getName());
			contentValues.put(DBHelper.COLUMN_CAR_PHONE_TELEPHONE, carPhone.getTelephone());
			contentValues.put(DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT, carPhone.getIsDefault());
			
			context.getContentResolver().insert(MyCarPhoneProvider.CONTENT_URI, contentValues);
		} catch(SQLiteException ex) {
			Log.e(TAG, ex.getMessage());
		} catch(IllegalStateException ex) {
			Log.e(TAG, ex.getMessage());
		}
	}
	
	public void updateCarPhone(CarPhone carPhone) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_OWNERID, carPhone.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_PHONE_CATEGORY, carPhone.getCategory());
		contentValues.put(DBHelper.COLUMN_CAR_PHONE_NAME, carPhone.getName());
		contentValues.put(DBHelper.COLUMN_CAR_PHONE_TELEPHONE, carPhone.getTelephone());
		contentValues.put(DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT, carPhone.getIsDefault());
		
		context.getContentResolver().update(MyCarPhoneProvider.CONTENT_URI, contentValues, 
					DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(carPhone.getUid())});
	}
	
	public CarPhone getCarPhone(int uid) {
		CarPhone carPhone = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_PHONE_CATEGORY,
				DBHelper.COLUMN_CAR_PHONE_NAME, DBHelper.COLUMN_CAR_PHONE_TELEPHONE, DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarPhoneProvider.CONTENT_URI, columns, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			carPhone = new CarPhone();
			carPhone.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carPhone.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carPhone.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_CATEGORY)));
			carPhone.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_NAME)));
			carPhone.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_TELEPHONE)));
			carPhone.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT)));
		}
		cursor.close();
		
		return carPhone;
	}
	
	public List<CarPhone> getAllCarPhones(int ownerId) {
		List<CarPhone> carPhones = new ArrayList<CarPhone>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_PHONE_CATEGORY,
				DBHelper.COLUMN_CAR_PHONE_NAME, DBHelper.COLUMN_CAR_PHONE_TELEPHONE, DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarPhoneProvider.CONTENT_URI, columns, DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(ownerId)}, null);
		
		while(cursor.moveToNext()) {
			CarPhone carPhone = new CarPhone();
			carPhone.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carPhone.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carPhone.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_CATEGORY)));
			carPhone.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_NAME)));
			carPhone.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_TELEPHONE)));
			carPhone.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT)));
			
			carPhones.add(carPhone);
		}
		cursor.close();
		
		return carPhones;
	}
	
	public List<CarPhone> getAllDefaultCarPhones() {
		List<CarPhone> carPhones = new ArrayList<CarPhone>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_PHONE_CATEGORY,
				DBHelper.COLUMN_CAR_PHONE_NAME, DBHelper.COLUMN_CAR_PHONE_TELEPHONE, DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarPhoneProvider.CONTENT_URI, columns, DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT + "='yes'", null, null);
		
		while(cursor.moveToNext()) {
			CarPhone carPhone = new CarPhone();
			carPhone.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			carPhone.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			carPhone.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_CATEGORY)));
			carPhone.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_NAME)));
			carPhone.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_TELEPHONE)));
			carPhone.setIsDefault(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT)));
			
			carPhones.add(carPhone);
		}
		cursor.close();
		return carPhones;
	}
	
	public boolean hasDefaultCarPhones() {
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT};
		Cursor cursor = context.getContentResolver().query(MyCarPhoneProvider.CONTENT_URI, columns, DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT + "='yes'", null, null);
		
		if(cursor.moveToNext()) {
			return true;
		}
		
		cursor.close();
		
		return false;
	}
	
	public void delete(int uid) {
		context.getContentResolver().delete(MyCarPhoneProvider.CONTENT_URI, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)});
	}
	
	public void deleteAll(int ownerId) {
		context.getContentResolver().delete(MyCarPhoneProvider.CONTENT_URI, DBHelper.COLUMN_OWNERID + "=" + ownerId + " AND " + DBHelper.COLUMN_CAR_PHONE_IS_DEFAULT + "=''", null);
	}
}