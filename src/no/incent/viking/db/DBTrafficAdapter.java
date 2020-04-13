	package no.incent.viking.db;

import no.incent.viking.pojo.Traffic;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBTrafficAdapter {
	private DBHelper dbHelper;
	private SQLiteDatabase database;
	
	public DBTrafficAdapter(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public synchronized void openReadable() {
		database = dbHelper.getReadableDatabase();
	}
	
	public synchronized void openWriteable() {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		database.close();
	}
	
	public synchronized void insertTraffic(Traffic traffic) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(DBHelper.COLUMN_UID, traffic.getRoadId());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_ROAD_NAME, traffic.getRoadName());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_AREA_NAME, traffic.getAreaName());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_LATITUDE, traffic.getLatitude());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_LONGITUDE, traffic.getLongitude());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT, traffic.getOptionalText());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, traffic.getShortText());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_START_TIME, traffic.getStartTime());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_END_TIME, traffic.getEndTime());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_TIMESTAMP, traffic.getTimestamp());
		
		database.insertOrThrow(DBHelper.TABLE_TRAFFICS, null, contentValues);
	}
	
	public synchronized void insertAll(List<Traffic> traffics) {
		database.beginTransaction();
		
		try {
			for(Traffic traffic: traffics) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(DBHelper.COLUMN_UID, traffic.getRoadId());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_ROAD_NAME, traffic.getRoadName());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_AREA_NAME, traffic.getAreaName());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_LATITUDE, traffic.getLatitude());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_LONGITUDE, traffic.getLongitude());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT, traffic.getOptionalText());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, traffic.getShortText());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_START_TIME, traffic.getStartTime());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_END_TIME, traffic.getEndTime());
				contentValues.put(DBHelper.COLUMN_TRAFFIC_TIMESTAMP, traffic.getTimestamp());
				
				database.insert(DBHelper.TABLE_TRAFFICS, null, contentValues);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}
	
	public synchronized void updateTraffic(Traffic traffic) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(DBHelper.COLUMN_TRAFFIC_ROAD_NAME, traffic.getRoadName());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_AREA_NAME, traffic.getAreaName());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_LATITUDE, traffic.getLatitude());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_LONGITUDE, traffic.getLongitude());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT, traffic.getOptionalText());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, traffic.getShortText());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_START_TIME, traffic.getStartTime());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_END_TIME, traffic.getEndTime());
		contentValues.put(DBHelper.COLUMN_TRAFFIC_TIMESTAMP, traffic.getTimestamp());
		
		database.update(DBHelper.TABLE_TRAFFICS, contentValues, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(traffic.getRoadId())});
	}
	
	public synchronized Traffic getTraffic(int uid) {
		Traffic traffic = null;
		
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_AREA_NAME,
							DBHelper.COLUMN_TRAFFIC_LATITUDE, DBHelper.COLUMN_TRAFFIC_LONGITUDE, DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT,
							DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, DBHelper.COLUMN_TRAFFIC_START_TIME, DBHelper.COLUMN_TRAFFIC_END_TIME, DBHelper.COLUMN_TRAFFIC_TIMESTAMP};
		Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null, null, null);
		
		if(cursor.moveToNext()) {
			traffic = new Traffic();
			traffic.setRoadId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			traffic.setRoadName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_ROAD_NAME)));
			traffic.setAreaName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_AREA_NAME)));
			traffic.setLatitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LATITUDE)));
			traffic.setLongitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LONGITUDE)));
			traffic.setOptionalText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT)));
			traffic.setShortText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT)));
			traffic.setStartTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_START_TIME)));
			traffic.setEndTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_END_TIME)));
			traffic.setTimestamp(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_TIMESTAMP)));
		}
		cursor.close();
		
		return traffic;
	}
	
	public synchronized List<Traffic> getAllTraffics(int limit) {
		List<Traffic> traffics = new ArrayList<Traffic>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_AREA_NAME,
				DBHelper.COLUMN_TRAFFIC_LATITUDE, DBHelper.COLUMN_TRAFFIC_LONGITUDE, DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT,
				DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, DBHelper.COLUMN_TRAFFIC_START_TIME, DBHelper.COLUMN_TRAFFIC_END_TIME, DBHelper.COLUMN_TRAFFIC_TIMESTAMP};
		String lim = (limit > 0)?String.valueOf(limit):null;
		Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, null, null, null, null, null, lim);

		while(cursor.moveToNext()) {
			Traffic traffic = new Traffic();
			traffic.setRoadId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			traffic.setRoadName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_ROAD_NAME)));
			traffic.setAreaName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_AREA_NAME)));
			traffic.setLatitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LATITUDE)));
			traffic.setLongitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LONGITUDE)));
			traffic.setOptionalText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT)));
			traffic.setShortText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT)));
			traffic.setStartTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_START_TIME)));
			traffic.setEndTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_END_TIME)));
			traffic.setTimestamp(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_TIMESTAMP)));
			
			traffics.add(traffic);
		}
		cursor.close();
		
		return traffics;
	}
	
	public synchronized void deleteAll() {
		database.delete(DBHelper.TABLE_TRAFFICS, null, null);
	}
	
	public synchronized String[] getAreaNameGroup(String search) {
		String[] areaNames = null;
		
		String selection = null;
		
		if(search != null) {
			/*selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search  + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT + " LIKE '%" + search + "%'";*/
			selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search  + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%'";
		}
		
		String[] columns = {DBHelper.COLUMN_TRAFFIC_AREA_NAME, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_START_TIME};
		Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, selection, null, 
				DBHelper.COLUMN_TRAFFIC_AREA_NAME, null, DBHelper.COLUMN_TRAFFIC_AREA_NAME);
		areaNames = new String[cursor.getCount()];
		
		int i=0;
		while(cursor.moveToNext()) {
			areaNames[i] = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_AREA_NAME));
			i++;
		}
		cursor.close();
		
		return areaNames;
	}
	
	public synchronized String[] getRoadNameGroup(String search) {
		String[] roadNames = null;
		
		String selection = null;
		
		if(search != null) {
			/*selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search  + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT + " LIKE '%" + search + "%'";*/
			selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search  + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%'";
		}
		
		String[] columns = {DBHelper.COLUMN_TRAFFIC_AREA_NAME, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_START_TIME};
		Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, selection, null, 
				DBHelper.COLUMN_TRAFFIC_ROAD_NAME, null, DBHelper.COLUMN_TRAFFIC_ROAD_NAME);
		roadNames = new String[cursor.getCount()];
		
		int i=0;
		while(cursor.moveToNext()) {
			roadNames[i] = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_ROAD_NAME));
			i++;
		}
		cursor.close();
		
		return roadNames;
	}
	
	public synchronized String[] getTimeGroup(String search) {
		String[] times = null;
		
		String selection = null;
		
		if(search != null) {
			/*selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search  + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT + " LIKE '%" + search + "%'";*/
			selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search  + "%' OR " +
					DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%'";
		}
		
		String[] columns = {DBHelper.COLUMN_TRAFFIC_AREA_NAME, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_START_TIME};
		Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, selection, null, 
				DBHelper.COLUMN_TRAFFIC_START_TIME, null, DBHelper.COLUMN_TRAFFIC_TIMESTAMP + " DESC");
		times = new String[cursor.getCount()];
		
		int i=0;
		while(cursor.moveToNext()) {
			times[i] = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_START_TIME));
			i++;
		}
		cursor.close();
		
		return times;
	}
	
	public synchronized List<List<Traffic>> getAllTrafficsByAreaName(String search) {
		List<List<Traffic>> traffics = new ArrayList<List<Traffic>>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_AREA_NAME,
				DBHelper.COLUMN_TRAFFIC_LATITUDE, DBHelper.COLUMN_TRAFFIC_LONGITUDE, DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT,
				DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, DBHelper.COLUMN_TRAFFIC_START_TIME, DBHelper.COLUMN_TRAFFIC_END_TIME};
		String selection = "";
		
		for(String areaName: getAreaNameGroup(search)) {
			List<Traffic> traffic_list = new ArrayList<Traffic>();
			
			selection = DBHelper.COLUMN_TRAFFIC_AREA_NAME + "='" + areaName + "'";
			
			if(search != null) {
				selection += " AND (" + DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search + "%' OR " +
							DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%' OR " +
							DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT + " LIKE '%" + search + "%')";
			}
			
			Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, selection, null, null, null, DBHelper.COLUMN_TRAFFIC_TIMESTAMP + " DESC");
			
			while(cursor.moveToNext()) {
				Traffic traffic = new Traffic();
				traffic.setRoadId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
				traffic.setRoadName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_ROAD_NAME)));
				traffic.setAreaName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_AREA_NAME)));
				traffic.setLatitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LATITUDE)));
				traffic.setLongitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LONGITUDE)));
				traffic.setOptionalText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT)));
				traffic.setShortText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT)));
				traffic.setStartTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_START_TIME)));
				traffic.setEndTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_END_TIME)));
				
				traffic_list.add(traffic);
			}
			traffics.add(traffic_list);
			cursor.close();
		}
		return traffics;
	}
	
	public synchronized List<List<Traffic>> getAllTrafficsByRoadName(String search) {
		List<List<Traffic>> traffics = new ArrayList<List<Traffic>>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_AREA_NAME,
				DBHelper.COLUMN_TRAFFIC_LATITUDE, DBHelper.COLUMN_TRAFFIC_LONGITUDE, DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT,
				DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, DBHelper.COLUMN_TRAFFIC_START_TIME, DBHelper.COLUMN_TRAFFIC_END_TIME};
		String selection = "";
		
		for(String roadName: getRoadNameGroup(search)) {
			List<Traffic> traffic_list = new ArrayList<Traffic>();
			
			selection = DBHelper.COLUMN_TRAFFIC_ROAD_NAME + "='" + roadName + "'";
			
			if(search != null) {
				selection += " AND (" + DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '" + search + "' OR " +
							DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%' OR " +
							DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT + " LIKE '%" + search + "%')";
			}
			
			Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, selection, null, null, null, DBHelper.COLUMN_TRAFFIC_TIMESTAMP + " DESC");
			
			while(cursor.moveToNext()) {
				Traffic traffic = new Traffic();
				traffic.setRoadId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
				traffic.setRoadName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_ROAD_NAME)));
				traffic.setAreaName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_AREA_NAME)));
				traffic.setLatitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LATITUDE)));
				traffic.setLongitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LONGITUDE)));
				traffic.setOptionalText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT)));
				traffic.setShortText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT)));
				traffic.setStartTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_START_TIME)));
				traffic.setEndTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_END_TIME)));
				
				traffic_list.add(traffic);
			}
			traffics.add(traffic_list);
			cursor.close();
		}
		
		return traffics;
	}
	
	public synchronized List<List<Traffic>> getAllTrafficsByTime(String search) {
		List<List<Traffic>> traffics = new ArrayList<List<Traffic>>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_TRAFFIC_ROAD_NAME, DBHelper.COLUMN_TRAFFIC_AREA_NAME,
				DBHelper.COLUMN_TRAFFIC_LATITUDE, DBHelper.COLUMN_TRAFFIC_LONGITUDE, DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT,
				DBHelper.COLUMN_TRAFFIC_SHORT_TEXT, DBHelper.COLUMN_TRAFFIC_START_TIME, DBHelper.COLUMN_TRAFFIC_END_TIME};
		
		String selection = "";
		
		for(String time: getTimeGroup(search)) {
			List<Traffic> traffic_list = new ArrayList<Traffic>();
			
			selection = DBHelper.COLUMN_TRAFFIC_START_TIME + "='" + time + "'";
			
			if(search != null) {
				selection += " AND (" + DBHelper.COLUMN_TRAFFIC_AREA_NAME + " LIKE '%" + search + "%' OR " +
							DBHelper.COLUMN_TRAFFIC_ROAD_NAME + " LIKE '%" + search + "%' OR " +
							DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT + " LIKE '%" + search + "%')";
			}
			
			Cursor cursor = database.query(DBHelper.TABLE_TRAFFICS, columns, selection, null, null, null, null);
			
			while(cursor.moveToNext()) {
				Traffic traffic = new Traffic();
				traffic.setRoadId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
				traffic.setRoadName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_ROAD_NAME)));
				traffic.setAreaName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_AREA_NAME)));
				traffic.setLatitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LATITUDE)));
				traffic.setLongitude(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_LONGITUDE)));
				traffic.setOptionalText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_OPTIONAL_TEXT)));
				traffic.setShortText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_SHORT_TEXT)));
				traffic.setStartTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_START_TIME)));
				traffic.setEndTime(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TRAFFIC_END_TIME)));
				
				traffic_list.add(traffic);
			}
			traffics.add(traffic_list);
			cursor.close();
		}
		
		return traffics;
	}
}
