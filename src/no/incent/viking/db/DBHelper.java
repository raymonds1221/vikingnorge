package no.incent.viking.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class DBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "viking.db";
	public static final int DATABASE_VERSION = 2;
	public static final String TABLE_USERS = "users";
	public static final String TABLE_CARS = "cars";
	public static final String TABLE_CAR_FILES = "carfiles";
	public static final String TABLE_TRAFFICS = "traffics";
	public static final String TABLE_CAR_ACCESS = "car_access";
	public static final String TABLE_CALL_TO_ACTIONS = "call_to_actions";
	public static final String TABLE_CAR_PHONES = "car_phones";
	public static final String TABLE_CAR_EVENTS = "car_events";
	public static final String TABLE_CAR_EVENT_PICTURES = "car_event_pictures";
	public static final String TABLE_CAR_EVENT_SOUNDS = "car_event_sounds";
	public static final String TABLE_PHONE_CATEGORIES = "phone_categories";
	public static final String TABLE_NEWS = "news";
	
	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_OWNERID = "owner_id";
	
	public static final String COLUMN_USER_FIRSTNAME = "firstname";
	public static final String COLUMN_USER_LASTNAME = "lastname";
	public static final String COLUMN_USER_EMAIL = "email";
	public static final String COLUMN_USER_ADDRESS = "address";
	public static final String COLUMN_USER_AREACODE = "area_code";
	public static final String COLUMN_USER_AREA = "area";
	public static final String COLUMN_USER_TELEPHONE = "telephone";
	public static final String COLUMN_USER_PASSWORD = "password";
	public static final String COLUMN_USER_CAR_REG_NO = "car_reg_no";
	public static final String COLUMN_USER_YEAR_OF_BIRTH = "year_of_birth";
	public static final String COLUMN_USER_COUNTRY = "country";
	public static final String COLUMN_USER_POSTBOX = "postbox";
	public static final String COLUMN_USER_GENDER = "gender";
	public static final String COLUMN_USER_STATUS = "status";
	
	public static final String COLUMN_CAR_OWNERNAME = "ownername";
	public static final String COLUMN_CAR_REGISTRATION_NUMBER = "registration_number";
	public static final String COLUMN_CAR_CHASSIS_NUMBER = "chassis_number";
	public static final String COLUMN_CAR_CARREGYEAR = "car_reg_year";
	public static final String COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY = "reg_first_time_in_norway";
	public static final String COLUMN_CAR_BRAND_CODE = "brand_code";
	public static final String COLUMN_CAR_CAR_MODEL = "car_model";
	public static final String COLUMN_CAR_ENGINE_PERFORMANCE = "engine_performance";
	public static final String COLUMN_CAR_DISPLACEMENT = "displacement";
	public static final String COLUMN_CAR_FUEL_TYPE = "fuel_type";
	public static final String COLUMN_CAR_LENGTH = "length";
	public static final String COLUMN_CAR_WIDTH = "width";
	public static final String COLUMN_CAR_WEIGHT = "weight";
	public static final String COLUMN_CAR_TOTAL_WEIGHT = "total_weight";
	public static final String COLUMN_CAR_COLOUR = "colour";
	public static final String COLUMN_CAR_CO2_EMISSION = "co2_emission";
	public static final String COLUMN_CAR_STANDARD_TIRE_FRONT = "std_tire_front";
	public static final String COLUMN_CAR_STANDARD_TIRE_BACK = "std_tire_back";
	public static final String COLUMN_CAR_DRIVING_WHEELS = "driving_wheels";
	public static final String COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES = "trailer_weight_with_brakes";
	public static final String COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES = "trailer_weight_without_brakes";
	public static final String COLUMN_CAR_FILENAME = "filename";
	public static final String COLUMN_CAR_PATH = "path";
	public static final String COLUMN_CAR_SHAREDCAR_ID = "sharedcar_id";
	public static final String COLUMN_CAR_IS_SHARED = "is_shared";
	
	public static final String COLUMN_CAR_FILE_NAME = "name";
	public static final String COLUMN_CAR_FILE_GALLERY = "gallery";
	public static final String COLUMN_CAR_FILE_FILENAME = "filename";
	public static final String COLUMN_CAR_FILE_PATH = "path";
	public static final String COLUMN_CAR_FILE_IS_DEFAULT = "is_default";
	
	public static final String COLUMN_TRAFFIC_ROAD_NAME = "road_name";
	public static final String COLUMN_TRAFFIC_AREA_NAME = "area_name";
	public static final String COLUMN_TRAFFIC_LATITUDE = "latitude";
	public static final String COLUMN_TRAFFIC_LONGITUDE = "longitude";
	public static final String COLUMN_TRAFFIC_OPTIONAL_TEXT = "optional_text";
	public static final String COLUMN_TRAFFIC_SHORT_TEXT = "short_text";
	public static final String COLUMN_TRAFFIC_START_TIME = "start_time";
	public static final String COLUMN_TRAFFIC_END_TIME = "end_time";
	public static final String COLUMN_TRAFFIC_TIMESTAMP = "timestamp";
	
	public static final String COLUMN_CAR_ACCESS_ROLE = "role";
	public static final String COLUMN_CAR_ACCESS_FULLNAME = "fullname";
	
	public static final String COLUMN_CALL_TO_ACTION_DESCRIPTION = "description";
	public static final String COLUMN_CALL_TO_ACTION_FILENAME = "filename";
	public static final String COLUMN_CALL_TO_ACTION_DEVICE = "device";
	public static final String COLUMN_CALL_TO_ACTION_DIMENSION = "dimension";
	public static final String COLUMN_CALL_TO_ACTION_PATH = "path";
	
	public static final String COLUMN_CAR_PHONE_CATEGORY = "category";
	public static final String COLUMN_CAR_PHONE_NAME = "name";
	public static final String COLUMN_CAR_PHONE_TELEPHONE = "telephone";
	public static final String COLUMN_CAR_PHONE_IS_DEFAULT = "is_default";
	
	public static final String COLUMN_CAR_EVENT_NAME = "name";
	public static final String COLUMN_CAR_EVENT_REGISTRATION = "registration";
	public static final String COLUMN_CAR_EVENT_EVENT = "event";
	public static final String COLUMN_CAR_EVENT_PLACE = "place";
	public static final String COLUMN_CAR_EVENT_DATETIME = "event_datetime";
	public static final String COLUMN_CAR_EVENT_NOTE = "note";
	public static final String COLUMN_CAR_EVENT_DATE_CREATED = "datecreated";
	
	public static final String COLUMN_CAR_EVENT_EVENTID = "event_id";
	public static final String COLUMN_CAR_EVENT_PICTURE_NAME = "name";
	public static final String COLUMN_CAR_EVENT_PICTURE_FILENAME = "filename";
	public static final String COLUMN_CAR_EVENT_PICTURE_FILETYPE = "filetype";
	public static final String COLUMN_CAR_EVENT_PICTURE_PATH = "path";
	
	public static final String COLUMN_CAR_EVENT_SOUND_NAME = "name";
	public static final String COLUMN_CAR_EVENT_SOUND_FILENAME = "filename";
	public static final String COLUMN_CAR_EVENT_SOUND_FILETYPE = "filetype";
	public static final String COLUMN_CAR_EVENT_SOUND_PATH = "path";
	
	public static final String COLUMN_PHONE_CATEGORY_CATEGORY = "category";
	public static final String COLUMN_PHONE_CATEGORY_NAME = "name";
	public static final String COLUMN_PHONE_CATEGORY_TELEPHONE = "telephone";
	
	public static final String COLUMN_NEWS_TITLE = "title";
	public static final String COLUMN_NEWS_URL = "url";
	public static final String COLUMN_NEWS_CREATION_DATE = "creation_date";
	public static final String COLUMN_NEWS_PUBLICATION_DATE = "publication_date";
	public static final String COLUMN_NEWS_AUTHOR = "author";
	public static final String COLUMN_NEWS_CATEGORY = "category";
	public static final String COLUMN_NEWS_SHORT_TEXT = "short_text";
	public static final String COLUMN_NEWS_FULL_TEXT = "full_text";
	public static final String COLUMN_NEWS_IMAGE = "image";
	public static final String COLUMN_NEWS_METADESC = "metadesc";
	public static final String COLUMN_NEWS_METAKEY = "metakey";
	public static final String COLUMN_NEWS_METADATA = "metadata";
	
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		String sql = "CREATE TABLE " + TABLE_USERS + "(" +
				COLUMN_UID + " INTEGER PRIMARY KEY, " +
				COLUMN_USER_FIRSTNAME + " TEXT, " +
				COLUMN_USER_LASTNAME + " TEXT, " +
				COLUMN_USER_EMAIL + " TEXT, " +
				COLUMN_USER_ADDRESS + " TEXT, " +
				COLUMN_USER_AREACODE + " TEXT, " +
				COLUMN_USER_AREA + " TEXT, " +
				COLUMN_USER_TELEPHONE + " TEXT, " +
				COLUMN_USER_PASSWORD + " TEXT, " +
				COLUMN_USER_CAR_REG_NO + " TEXT, " +
				COLUMN_USER_YEAR_OF_BIRTH + " TEXT, " +
				COLUMN_USER_COUNTRY + " TEXT, " +
				COLUMN_USER_POSTBOX + " TEXT, " +
				COLUMN_USER_GENDER + " TEXT, " +
				COLUMN_USER_STATUS + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CARS + "(" +
				COLUMN_UID + " INTEGER, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_OWNERNAME + " TEXT, " +
				COLUMN_CAR_REGISTRATION_NUMBER + " TEXT, " +
				COLUMN_CAR_CHASSIS_NUMBER + " TEXT, " +
				COLUMN_CAR_CARREGYEAR + " TEXT, " +
				COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY + " TEXT, " +
				COLUMN_CAR_BRAND_CODE + " TEXT, " +
				COLUMN_CAR_CAR_MODEL + " TEXT, " +
				COLUMN_CAR_ENGINE_PERFORMANCE + " TEXT, " +
				COLUMN_CAR_DISPLACEMENT + " TEXT, " +
				COLUMN_CAR_FUEL_TYPE + " TEXT, " +
				COLUMN_CAR_LENGTH + " TEXT, " +
				COLUMN_CAR_WIDTH + " TEXT, " +
				COLUMN_CAR_WEIGHT + " TEXT, " +
				COLUMN_CAR_TOTAL_WEIGHT + " TEXT, " +
				COLUMN_CAR_COLOUR + " TEXT, " +
				COLUMN_CAR_CO2_EMISSION + " TEXT, " +
				COLUMN_CAR_STANDARD_TIRE_FRONT + " TEXT," +
				COLUMN_CAR_STANDARD_TIRE_BACK + " TEXT," +
				COLUMN_CAR_DRIVING_WHEELS + " TEXT," +
				COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES + " TEXT," +
				COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES + " TEXT," +
				COLUMN_CAR_FILENAME + " TEXT, " +
				COLUMN_CAR_PATH + " TEXT, " +
				COLUMN_CAR_SHAREDCAR_ID + " INTEGER, " +
				COLUMN_CAR_IS_SHARED + " INTEGER);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CAR_FILES + "(" +
				COLUMN_UID + " INTEGER, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_FILE_NAME + " TEXT, " +
				COLUMN_CAR_FILE_GALLERY + " TEXT, " +
				COLUMN_CAR_FILE_FILENAME + " TEXT, " +
				COLUMN_CAR_FILE_PATH + " TEXT, " +
				COLUMN_CAR_FILE_IS_DEFAULT + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_TRAFFICS + "(" +
				COLUMN_UID + " INTEGER, " +
				COLUMN_TRAFFIC_ROAD_NAME + " TEXT, " +
				COLUMN_TRAFFIC_AREA_NAME + " TEXT, " +
				COLUMN_TRAFFIC_LATITUDE + " TEXT, " +
				COLUMN_TRAFFIC_LONGITUDE + " TEXT, " +
				COLUMN_TRAFFIC_OPTIONAL_TEXT + " TEXT, " +
				COLUMN_TRAFFIC_SHORT_TEXT + " TEXT, " +
				COLUMN_TRAFFIC_START_TIME + " TEXT, " +
				COLUMN_TRAFFIC_END_TIME + " TEXT, " +
				COLUMN_TRAFFIC_TIMESTAMP + " INTEGER);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CAR_ACCESS + "(" +
				COLUMN_UID + " INTEGER PRIMARY KEY, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_ACCESS_ROLE + " TEXT, " +
				COLUMN_CAR_ACCESS_FULLNAME + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CALL_TO_ACTIONS + "(" +
				COLUMN_UID + " INTEGER PRIMARY KEY, " +
				COLUMN_CALL_TO_ACTION_DESCRIPTION + " TEXT, " +
				COLUMN_CALL_TO_ACTION_FILENAME + " TEXT, " +
				COLUMN_CALL_TO_ACTION_DEVICE + " TEXT, " +
				COLUMN_CALL_TO_ACTION_DIMENSION + " TEXT, " +
				COLUMN_CALL_TO_ACTION_PATH + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CAR_PHONES + "(" +
				COLUMN_UID + " INTEGER, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_PHONE_CATEGORY + " TEXT, " +
				COLUMN_CAR_PHONE_NAME  + " TEXT, " +
				COLUMN_CAR_PHONE_TELEPHONE + " TEXT, " +
				COLUMN_CAR_PHONE_IS_DEFAULT + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CAR_EVENTS + "(" +
				COLUMN_UID + " INTEGER PRIMARY KEY, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_EVENT_NAME + " TEXT, " +
				COLUMN_CAR_EVENT_REGISTRATION + " TEXT, " +
				COLUMN_CAR_EVENT_EVENT + " TEXT, " +
				COLUMN_CAR_EVENT_PLACE + " TEXT, " +
				COLUMN_CAR_EVENT_DATETIME + " TEXT, " +
				COLUMN_CAR_EVENT_NOTE + " TEXT, " +
				COLUMN_CAR_EVENT_DATE_CREATED + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CAR_EVENT_PICTURES + "(" +
				COLUMN_UID + " INTEGER, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_EVENT_EVENTID + " INTEGER, " +
				COLUMN_CAR_EVENT_PICTURE_NAME + " TEXT, " +
				COLUMN_CAR_EVENT_PICTURE_FILENAME + " TEXT, " +
				COLUMN_CAR_EVENT_PICTURE_FILETYPE + " TEXT, " +
				COLUMN_CAR_EVENT_PICTURE_PATH + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_CAR_EVENT_SOUNDS + "(" +
				COLUMN_UID + " INTEGER, " +
				COLUMN_OWNERID + " INTEGER, " +
				COLUMN_CAR_EVENT_EVENTID + " INTEGER, " +
				COLUMN_CAR_EVENT_SOUND_NAME + " TEXT, " +
				COLUMN_CAR_EVENT_SOUND_FILENAME + " TEXT, " +
				COLUMN_CAR_EVENT_SOUND_FILETYPE + " TEXT, " +
				COLUMN_CAR_EVENT_SOUND_PATH + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_PHONE_CATEGORIES + "(" +
				COLUMN_UID + " INTEGER PRIMARY KEY, " +
				COLUMN_PHONE_CATEGORY_CATEGORY + " TEXT, " +
				COLUMN_PHONE_CATEGORY_NAME + " TEXT, " +
				COLUMN_PHONE_CATEGORY_TELEPHONE + " TEXT);";
		database.execSQL(sql);
		sql = "CREATE TABLE " + TABLE_NEWS + "(" +
				COLUMN_NEWS_TITLE + " TEXT, " +
				COLUMN_NEWS_URL + " TEXT, " + 
				COLUMN_NEWS_CREATION_DATE + " TEXT, " +
				COLUMN_NEWS_PUBLICATION_DATE + " TEXT, " +
				COLUMN_NEWS_CATEGORY + " TEXT, " +
				COLUMN_NEWS_SHORT_TEXT + " TEXT, " +
				COLUMN_NEWS_FULL_TEXT + " TEXT, " +
				COLUMN_NEWS_IMAGE + " TEXT, " +
				COLUMN_NEWS_METADESC + " TEXT, " +
				COLUMN_NEWS_METAKEY + " TEXT, " +
				COLUMN_NEWS_METADATA + " TEXT);";
		database.execSQL(sql);
	}
	
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_FILES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAFFICS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_ACCESS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_TO_ACTIONS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_PHONES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_EVENTS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_EVENT_PICTURES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR_EVENT_SOUNDS);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONE_CATEGORIES);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
		onCreate(database);
	}
}
