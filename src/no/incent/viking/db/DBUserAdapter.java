package no.incent.viking.db;

import no.incent.viking.pojo.User;
import no.incent.viking.provider.UserProvider;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public class DBUserAdapter {
	private Context context;
	
	public DBUserAdapter(Context context) {
		this.context = context;
	}
	
	public synchronized void open() {
	}
	
	public void close() {
	}
	
	public void insertUser(User user) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(DBHelper.COLUMN_UID, user.getUid());
		contentValues.put(DBHelper.COLUMN_USER_FIRSTNAME, user.getFirstname());
		contentValues.put(DBHelper.COLUMN_USER_LASTNAME, user.getLastname());
		contentValues.put(DBHelper.COLUMN_USER_EMAIL, user.getEmail());
		contentValues.put(DBHelper.COLUMN_USER_ADDRESS, user.getAddress());
		contentValues.put(DBHelper.COLUMN_USER_AREACODE, user.getAreaCode());
		contentValues.put(DBHelper.COLUMN_USER_AREA, user.getArea());
		contentValues.put(DBHelper.COLUMN_USER_TELEPHONE, user.getTelephone());
		contentValues.put(DBHelper.COLUMN_USER_PASSWORD, user.getPassword());
		contentValues.put(DBHelper.COLUMN_USER_CAR_REG_NO, user.getCarRegNo());
		contentValues.put(DBHelper.COLUMN_USER_YEAR_OF_BIRTH, user.getYearOfBirth());
		contentValues.put(DBHelper.COLUMN_USER_COUNTRY, user.getCountry());
		contentValues.put(DBHelper.COLUMN_USER_POSTBOX, user.getPostbox());
		contentValues.put(DBHelper.COLUMN_USER_GENDER, user.getGender());
		contentValues.put(DBHelper.COLUMN_USER_STATUS, user.getStatus());
		
		context.getContentResolver().insert(UserProvider.CONTENT_URI, contentValues);
	}
	
	public void updateUser(User user) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(DBHelper.COLUMN_USER_FIRSTNAME, user.getFirstname());
		contentValues.put(DBHelper.COLUMN_USER_LASTNAME, user.getLastname());
		contentValues.put(DBHelper.COLUMN_USER_EMAIL, user.getEmail());
		contentValues.put(DBHelper.COLUMN_USER_ADDRESS, user.getAddress());
		contentValues.put(DBHelper.COLUMN_USER_AREACODE, user.getAreaCode());
		contentValues.put(DBHelper.COLUMN_USER_AREA, user.getArea());
		contentValues.put(DBHelper.COLUMN_USER_TELEPHONE, user.getTelephone());
		contentValues.put(DBHelper.COLUMN_USER_PASSWORD, user.getPassword());
		contentValues.put(DBHelper.COLUMN_USER_CAR_REG_NO, user.getCarRegNo());
		contentValues.put(DBHelper.COLUMN_USER_YEAR_OF_BIRTH, user.getYearOfBirth());
		contentValues.put(DBHelper.COLUMN_USER_COUNTRY, user.getCountry());
		contentValues.put(DBHelper.COLUMN_USER_POSTBOX, user.getPostbox());
		contentValues.put(DBHelper.COLUMN_USER_GENDER, user.getGender());
		contentValues.put(DBHelper.COLUMN_USER_STATUS, user.getStatus());
		
		context.getContentResolver().update(UserProvider.CONTENT_URI, contentValues, 
				DBHelper.COLUMN_UID + "=?", new String[]{String.valueOf(user.getUid())});
	}
	
	public User getUser(int uid) {
		User user = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_USER_FIRSTNAME, DBHelper.COLUMN_USER_LASTNAME,
				DBHelper.COLUMN_USER_EMAIL, DBHelper.COLUMN_USER_ADDRESS, DBHelper.COLUMN_USER_AREACODE, DBHelper.COLUMN_USER_AREA,
				DBHelper.COLUMN_USER_TELEPHONE, DBHelper.COLUMN_USER_PASSWORD, DBHelper.COLUMN_USER_CAR_REG_NO, DBHelper.COLUMN_USER_YEAR_OF_BIRTH, 
				DBHelper.COLUMN_USER_COUNTRY, DBHelper.COLUMN_USER_POSTBOX, DBHelper.COLUMN_USER_GENDER, DBHelper.COLUMN_USER_STATUS};
		Cursor cursor = context.getContentResolver().query(UserProvider.CONTENT_URI, columns, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			user = new User();
			user.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			user.setFirstname(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_FIRSTNAME)));
			user.setLastname(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_LASTNAME)));
			user.setEmail(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_EMAIL)));
			user.setAddress(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_ADDRESS)));
			user.setAreaCode(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_AREACODE)));
			user.setArea(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_AREA)));
			user.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_TELEPHONE)));
			user.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_PASSWORD)));
			user.setCarRegNo(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_CAR_REG_NO)));
			user.setYearOfBirth(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_YEAR_OF_BIRTH)));
			user.setCountry(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_COUNTRY)));
			user.setPostbox(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_POSTBOX)));
			user.setGender(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_GENDER)));
			user.setStatus(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_STATUS)));
		}
		cursor.close();
		
		return user;
	}
	
	public User getUser(String telephone, String password) {
		User user = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_USER_FIRSTNAME, DBHelper.COLUMN_USER_LASTNAME,
				DBHelper.COLUMN_USER_EMAIL, DBHelper.COLUMN_USER_ADDRESS, DBHelper.COLUMN_USER_AREACODE, DBHelper.COLUMN_USER_AREA,
				DBHelper.COLUMN_USER_TELEPHONE, DBHelper.COLUMN_USER_PASSWORD, DBHelper.COLUMN_USER_CAR_REG_NO, DBHelper.COLUMN_USER_YEAR_OF_BIRTH, 
				DBHelper.COLUMN_USER_COUNTRY, DBHelper.COLUMN_USER_POSTBOX, DBHelper.COLUMN_USER_GENDER, DBHelper.COLUMN_USER_STATUS};
		Cursor cursor = context.getContentResolver().query(UserProvider.CONTENT_URI, columns, DBHelper.COLUMN_USER_TELEPHONE + "=? AND " + DBHelper.COLUMN_USER_PASSWORD + "=?", 
								new String[] {telephone, password}, null);
		
		if(cursor.moveToNext()) {
			user = new User();
			user.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			user.setFirstname(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_FIRSTNAME)));
			user.setLastname(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_LASTNAME)));
			user.setEmail(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_EMAIL)));
			user.setAddress(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_ADDRESS)));
			user.setAreaCode(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_AREACODE)));
			user.setArea(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_AREA)));
			user.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_TELEPHONE)));
			user.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_PASSWORD)));
			user.setCarRegNo(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_CAR_REG_NO)));
			user.setYearOfBirth(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_YEAR_OF_BIRTH)));
			user.setCountry(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_COUNTRY)));
			user.setPostbox(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_POSTBOX)));
			user.setGender(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_GENDER)));
			user.setStatus(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USER_STATUS)));
		}
		cursor.close();
		
		return user;
	}
}
