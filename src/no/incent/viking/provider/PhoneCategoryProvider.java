package no.incent.viking.provider;

import no.incent.viking.db.DBHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentResolver;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PhoneCategoryProvider extends ContentProvider {
	private static UriMatcher uriMatcher;
	private static final String AUTHORITY = "no.incent.viking.provider.PhoneCategoryProvider";
	private static final String BASE_PATH = DBHelper.TABLE_PHONE_CATEGORIES;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/phonecategories";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/phonecategory";
	public static final int PHONECATEGORIES = 15;
	public static final int PHONECATEGORY_ID = 25;
	private DBHelper dbHelper;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, BASE_PATH, PHONECATEGORIES);
		uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PHONECATEGORY_ID);
	}
	
	@Override
	public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
		if(uriMatcher.match(uri) != PHONECATEGORIES)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int count = database.delete(DBHelper.TABLE_PHONE_CATEGORIES, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return CONTENT_TYPE;
	}

	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		if(uriMatcher.match(uri) != PHONECATEGORIES)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		long id = database.insert(DBHelper.TABLE_PHONE_CATEGORIES, null, values);
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
		if(uriMatcher.match(uri) != PHONECATEGORIES)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(DBHelper.TABLE_PHONE_CATEGORIES, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		if(uriMatcher.match(uri) != PHONECATEGORIES)
			throw new IllegalArgumentException("Unknown URI: " + uri);
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		int affectedRows = database.update(DBHelper.TABLE_PHONE_CATEGORIES, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return affectedRows;
	}

}
