package no.incent.viking.provider;

import no.incent.viking.db.DBHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CarEventSoundsProvider extends ContentProvider {
	private static UriMatcher uriMatcher;
	private static final String AUTHORITY = "no.incent.viking.provider.CarEventSoundsProvider";
	private static final String BASE_PATH = DBHelper.TABLE_CAR_EVENT_SOUNDS;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/car_event_sounds";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/car_event_sound";
	private static final int CAR_EVENT_SOUNDS = 52;
	private static final int CAR_EVENT_SOUND_ID = 53;
	private DBHelper dbHelper;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, BASE_PATH, CAR_EVENT_SOUNDS);
		uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CAR_EVENT_SOUND_ID);
	}
			
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if(uriMatcher.match(uri) != CAR_EVENT_SOUNDS)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int count = database.delete(DBHelper.TABLE_CAR_EVENT_SOUNDS, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return CONTENT_TYPE;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if(uriMatcher.match(uri) != CAR_EVENT_SOUNDS)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		long id = database.insert(DBHelper.TABLE_CAR_EVENT_SOUNDS, null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if(uriMatcher.match(uri) != CAR_EVENT_SOUNDS)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(DBHelper.TABLE_CAR_EVENT_SOUNDS, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if(uriMatcher.match(uri) != CAR_EVENT_SOUNDS)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int affectedRows = database.update(DBHelper.TABLE_CAR_EVENT_SOUNDS, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return affectedRows;
	}

}
