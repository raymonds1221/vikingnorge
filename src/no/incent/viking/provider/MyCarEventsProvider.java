package no.incent.viking.provider;

import no.incent.viking.db.DBHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyCarEventsProvider extends ContentProvider {
	private static final UriMatcher uriMatcher;
	private static final String AUTHORITY = "no.incent.viking.provider.MyCarEventsProvider";
	private static final String BASE_PATH = DBHelper.TABLE_CAR_EVENTS;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/carevents";
	public static final String CONTENT_ITEM_TUYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/carevent";
	public static final int MYCAREVENTS = 12;
	public static final int MYCAREVENT = 22;
	private DBHelper dbHelper;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, BASE_PATH, MYCAREVENTS);
		uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MYCAREVENT);
	}
	
	@Override
	public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
		if(uriMatcher.match(uri) != MYCAREVENTS)
			throw new IllegalStateException("delete Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int count = database.delete(DBHelper.TABLE_CAR_EVENTS, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return CONTENT_TYPE;
	}

	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		if(uriMatcher.match(uri) != MYCAREVENTS)
			throw new IllegalStateException("insert Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		long id = database.insert(DBHelper.TABLE_CAR_EVENTS, null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return false;
	}

	@Override
	public synchronized Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if(uriMatcher.match(uri) != MYCAREVENTS)
			throw new IllegalStateException("query Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(DBHelper.TABLE_CAR_EVENTS, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if(uriMatcher.match(uri) != MYCAREVENTS)
			throw new IllegalStateException("update Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int affectedRows = database.update(DBHelper.TABLE_CAR_EVENTS, values, selection, selectionArgs);
		
		return affectedRows;
	}

}
