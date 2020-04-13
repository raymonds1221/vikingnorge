package no.incent.viking.db;

import no.incent.viking.pojo.PhoneCategory;
import no.incent.viking.provider.PhoneCategoryProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBPhoneCategoryAdapter {
	private Context context;
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	
	public DBPhoneCategoryAdapter(Context context) {
		this.context = context;
		//dbHelper = new DBHelper(context);
	}
	
	public synchronized void open() {
		//database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		//database.close();
	}
	
	public void insertPhoneCategory(PhoneCategory phoneCategory) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, phoneCategory.getUid());
		contentValues.put(DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY, phoneCategory.getCategory());
		contentValues.put(DBHelper.COLUMN_PHONE_CATEGORY_NAME, phoneCategory.getName());
		contentValues.put(DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE, phoneCategory.getTelephone());
		
		context.getContentResolver().insert(PhoneCategoryProvider.CONTENT_URI, contentValues);
	}
	
	public void insertAll(List<PhoneCategory> phoneCategories) {
		ContentValues[] contentValues = new ContentValues[phoneCategories.size()];
		
		int i = 0;
		for(PhoneCategory phoneCategory: phoneCategories) {
			contentValues[i] = new ContentValues();
			contentValues[i].put(DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY, phoneCategory.getCategory());
			contentValues[i].put(DBHelper.COLUMN_PHONE_CATEGORY_NAME, phoneCategory.getName());
			contentValues[i].put(DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE, phoneCategory.getTelephone());
			i++;
		}
		context.getContentResolver().bulkInsert(PhoneCategoryProvider.CONTENT_URI, contentValues);
	}
	
	public void updatePhoneCategory(PhoneCategory phoneCategory) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY, phoneCategory.getCategory());
		contentValues.put(DBHelper.COLUMN_PHONE_CATEGORY_NAME, phoneCategory.getName());
		contentValues.put(DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE, phoneCategory.getTelephone());
		
		context.getContentResolver().update(PhoneCategoryProvider.CONTENT_URI, contentValues, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(phoneCategory.getUid())});
	}
	
	public PhoneCategory getPhoneCategory(int uid) {
		PhoneCategory phoneCategory = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY,
				DBHelper.COLUMN_PHONE_CATEGORY_NAME, DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE};
		Cursor cursor = context.getContentResolver().query(PhoneCategoryProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			phoneCategory = new PhoneCategory();
			phoneCategory.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			phoneCategory.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY)));
			phoneCategory.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_NAME)));
			phoneCategory.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE)));
		}
		cursor.close();
		
		return phoneCategory;
	}
	
	public List<PhoneCategory> getAllPhoneCategories() {
		List<PhoneCategory> phoneCategories = new ArrayList<PhoneCategory>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY,
				DBHelper.COLUMN_PHONE_CATEGORY_NAME, DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE};
		Cursor cursor = context.getContentResolver().query(PhoneCategoryProvider.CONTENT_URI, columns, null, null, null);
		
		while(cursor.moveToNext()) {
			PhoneCategory phoneCategory = new PhoneCategory();
			phoneCategory.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			phoneCategory.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY)));
			phoneCategory.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_NAME)));
			phoneCategory.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE)));
			phoneCategories.add(phoneCategory);
		}
		cursor.close();
		
		return phoneCategories;
	}
	
	public List<PhoneCategory> getAllCarPhonesByCategory(String category) {
		List<PhoneCategory> phoneCategories = new ArrayList<PhoneCategory>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY,
				DBHelper.COLUMN_PHONE_CATEGORY_NAME, DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE};
		Cursor cursor = context.getContentResolver().query(PhoneCategoryProvider.CONTENT_URI, columns, 
				DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY + " LIKE '%" + category + "%'", null, null);
		
		while(cursor.moveToNext()) {
			PhoneCategory phoneCategory = new PhoneCategory();
			phoneCategory.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			phoneCategory.setCategory(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_CATEGORY)));
			phoneCategory.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_NAME)));
			phoneCategory.setTelephone(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE_CATEGORY_TELEPHONE)));
			phoneCategories.add(phoneCategory);
		}
		cursor.close();
		
		return phoneCategories;
	}
	
	public void delete(int uid) {
		context.getContentResolver().delete(PhoneCategoryProvider.CONTENT_URI, 
				DBHelper.COLUMN_OWNERID + "=?", new String[] {String.valueOf(uid)});
	}
	
	public void deleteAll() {
		context.getContentResolver().delete(PhoneCategoryProvider.CONTENT_URI, null, null);
	}
}
