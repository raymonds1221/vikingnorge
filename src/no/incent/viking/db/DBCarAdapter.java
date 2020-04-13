package no.incent.viking.db;

import no.incent.viking.pojo.Car;
import no.incent.viking.provider.CarProvider;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public class DBCarAdapter {
	private Context context;
	
	public DBCarAdapter(Context context) {
		this.context = context;
	}
	
	public void openWritable() {
	}
	
	public void openReadable() {
	}
	
	public void close() {
	}
	
	public void insertCar(Car car) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_UID, car.getUid());
		contentValues.put(DBHelper.COLUMN_OWNERID, car.getOwnerId());
		contentValues.put(DBHelper.COLUMN_CAR_OWNERNAME, car.getOwnername());
		contentValues.put(DBHelper.COLUMN_CAR_REGISTRATION_NUMBER, car.getRegistrationNumber());
		contentValues.put(DBHelper.COLUMN_CAR_CHASSIS_NUMBER, car.getChassisNumber());
		contentValues.put(DBHelper.COLUMN_CAR_CARREGYEAR, car.getCarRegYear());
		contentValues.put(DBHelper.COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY, car.getRegFirstTimeInNorway());
		contentValues.put(DBHelper.COLUMN_CAR_BRAND_CODE, car.getBrandCode());
		contentValues.put(DBHelper.COLUMN_CAR_CAR_MODEL, car.getCarModel());
		contentValues.put(DBHelper.COLUMN_CAR_ENGINE_PERFORMANCE, car.getEnginePerformance());
		contentValues.put(DBHelper.COLUMN_CAR_DISPLACEMENT, car.getDisplacement());
		contentValues.put(DBHelper.COLUMN_CAR_FUEL_TYPE, car.getFuelType());
		contentValues.put(DBHelper.COLUMN_CAR_LENGTH, car.getLength());
		contentValues.put(DBHelper.COLUMN_CAR_WIDTH, car.getWidth());
		contentValues.put(DBHelper.COLUMN_CAR_WEIGHT, car.getWeight());
		contentValues.put(DBHelper.COLUMN_CAR_TOTAL_WEIGHT, car.getTotalWeight());
		contentValues.put(DBHelper.COLUMN_CAR_COLOUR, car.getColour());
		contentValues.put(DBHelper.COLUMN_CAR_CO2_EMISSION, car.getCo2Emission());
		contentValues.put(DBHelper.COLUMN_CAR_FILENAME, car.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_PATH, car.getPath());
		contentValues.put(DBHelper.COLUMN_CAR_SHAREDCAR_ID, car.getSharedCarId());
		contentValues.put(DBHelper.COLUMN_CAR_IS_SHARED, car.isShared());
		contentValues.put(DBHelper.COLUMN_CAR_STANDARD_TIRE_FRONT, car.getStandardTireFront());
		contentValues.put(DBHelper.COLUMN_CAR_STANDARD_TIRE_BACK, car.getStandardTireBack());
		contentValues.put(DBHelper.COLUMN_CAR_DRIVING_WHEELS, car.getDrivingWheels());
		contentValues.put(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES, car.getTrailerWeightWithBrakes());
		contentValues.put(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES, car.getTrailerWeightWithoutBrakes());
		context.getContentResolver().insert(CarProvider.CONTENT_URI, contentValues);
	}
	
	public void updateCar(Car car) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DBHelper.COLUMN_CAR_OWNERNAME, car.getOwnername());
		contentValues.put(DBHelper.COLUMN_CAR_REGISTRATION_NUMBER, car.getRegistrationNumber());
		contentValues.put(DBHelper.COLUMN_CAR_CHASSIS_NUMBER, car.getChassisNumber());
		contentValues.put(DBHelper.COLUMN_CAR_CARREGYEAR, car.getCarRegYear());
		contentValues.put(DBHelper.COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY, car.getRegFirstTimeInNorway());
		contentValues.put(DBHelper.COLUMN_CAR_BRAND_CODE, car.getBrandCode());
		contentValues.put(DBHelper.COLUMN_CAR_CAR_MODEL, car.getCarModel());
		contentValues.put(DBHelper.COLUMN_CAR_ENGINE_PERFORMANCE, car.getEnginePerformance());
		contentValues.put(DBHelper.COLUMN_CAR_DISPLACEMENT, car.getDisplacement());
		contentValues.put(DBHelper.COLUMN_CAR_FUEL_TYPE, car.getFuelType());
		contentValues.put(DBHelper.COLUMN_CAR_LENGTH, car.getLength());
		contentValues.put(DBHelper.COLUMN_CAR_WIDTH, car.getWidth());
		contentValues.put(DBHelper.COLUMN_CAR_WEIGHT, car.getWeight());
		contentValues.put(DBHelper.COLUMN_CAR_TOTAL_WEIGHT, car.getTotalWeight());
		contentValues.put(DBHelper.COLUMN_CAR_COLOUR, car.getColour());
		contentValues.put(DBHelper.COLUMN_CAR_CO2_EMISSION, car.getCo2Emission());
		contentValues.put(DBHelper.COLUMN_CAR_STANDARD_TIRE_FRONT, car.getStandardTireFront());
		contentValues.put(DBHelper.COLUMN_CAR_STANDARD_TIRE_BACK, car.getStandardTireBack());
		contentValues.put(DBHelper.COLUMN_CAR_DRIVING_WHEELS, car.getDrivingWheels());
		contentValues.put(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES, car.getTrailerWeightWithBrakes());
		contentValues.put(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES, car.getTrailerWeightWithoutBrakes());
		contentValues.put(DBHelper.COLUMN_CAR_FILENAME, car.getFilename());
		contentValues.put(DBHelper.COLUMN_CAR_PATH, car.getPath());
		contentValues.put(DBHelper.COLUMN_CAR_SHAREDCAR_ID, car.getSharedCarId());
		contentValues.put(DBHelper.COLUMN_CAR_IS_SHARED, car.isShared());
		
		context.getContentResolver().update(CarProvider.CONTENT_URI, contentValues, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(car.getUid())});
	}
	
	public Car getCar(int uid) {
		Car car = null;
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_OWNERNAME, DBHelper.COLUMN_CAR_REGISTRATION_NUMBER,
						DBHelper.COLUMN_CAR_CHASSIS_NUMBER, DBHelper.COLUMN_CAR_CARREGYEAR, DBHelper.COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY, DBHelper.COLUMN_CAR_BRAND_CODE,
						DBHelper.COLUMN_CAR_CAR_MODEL, DBHelper.COLUMN_CAR_ENGINE_PERFORMANCE, DBHelper.COLUMN_CAR_DISPLACEMENT, DBHelper.COLUMN_CAR_FUEL_TYPE, DBHelper.COLUMN_CAR_LENGTH,
						DBHelper.COLUMN_CAR_WIDTH, DBHelper.COLUMN_CAR_WEIGHT, DBHelper.COLUMN_CAR_TOTAL_WEIGHT, DBHelper.COLUMN_CAR_COLOUR, DBHelper.COLUMN_CAR_CO2_EMISSION,
						DBHelper.COLUMN_CAR_STANDARD_TIRE_FRONT, DBHelper.COLUMN_CAR_STANDARD_TIRE_BACK, DBHelper.COLUMN_CAR_DRIVING_WHEELS, DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES, DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES,
						DBHelper.COLUMN_CAR_FILENAME, DBHelper.COLUMN_CAR_PATH, DBHelper.COLUMN_CAR_SHAREDCAR_ID, DBHelper.COLUMN_CAR_IS_SHARED};
		Cursor cursor = context.getContentResolver().query(CarProvider.CONTENT_URI, columns, DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)}, null);
		
		if(cursor.moveToNext()) {
			car = new Car();
			car.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			car.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			car.setOwnername(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_OWNERNAME)));
			car.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_REGISTRATION_NUMBER)));
			car.setChassisNumber(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CHASSIS_NUMBER)));
			car.setCarRegYear(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CARREGYEAR)));
			car.setRegFirstTimeInNorway(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY)));
			car.setBrandCode(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_BRAND_CODE)));
			car.setCarModel(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CAR_MODEL)));
			car.setEnginePerformance(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_ENGINE_PERFORMANCE)));
			car.setDisplacement(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_DISPLACEMENT)));
			car.setFuelType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FUEL_TYPE)));
			car.setLength(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_LENGTH)));
			car.setWidth(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_WIDTH)));
			car.setWeight(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_WEIGHT)));
			car.setTotalWeight(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_TOTAL_WEIGHT)));
			car.setColour(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_COLOUR)));
			car.setCo2Emission(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CO2_EMISSION)));
			car.setStandardTireFront(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_STANDARD_TIRE_FRONT)));
			car.setStandardTireBack(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_STANDARD_TIRE_BACK)));
			car.setDrivingWheels(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_DRIVING_WHEELS)));
			car.setTrailerWeightWithBrakes(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES)));
			car.setTrailerWeightWithoutBrakes(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES)));
			car.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILENAME)));
			car.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PATH)));
			car.setSharedCarId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_SHAREDCAR_ID)));
			car.setShared(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_IS_SHARED)) > 0);
		}
		cursor.close();
		
		return car;
	}
	
	public List<Car> getAllCars(int ownerId) {
		List<Car> cars = new ArrayList<Car>();
		String[] columns = {DBHelper.COLUMN_UID, DBHelper.COLUMN_OWNERID, DBHelper.COLUMN_CAR_OWNERNAME, DBHelper.COLUMN_CAR_REGISTRATION_NUMBER,
				DBHelper.COLUMN_CAR_CHASSIS_NUMBER, DBHelper.COLUMN_CAR_CARREGYEAR, DBHelper.COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY, DBHelper.COLUMN_CAR_BRAND_CODE,
				DBHelper.COLUMN_CAR_CAR_MODEL, DBHelper.COLUMN_CAR_ENGINE_PERFORMANCE, DBHelper.COLUMN_CAR_DISPLACEMENT, DBHelper.COLUMN_CAR_FUEL_TYPE, DBHelper.COLUMN_CAR_LENGTH,
				DBHelper.COLUMN_CAR_WIDTH, DBHelper.COLUMN_CAR_WEIGHT, DBHelper.COLUMN_CAR_TOTAL_WEIGHT, DBHelper.COLUMN_CAR_COLOUR, DBHelper.COLUMN_CAR_CO2_EMISSION,
				DBHelper.COLUMN_CAR_STANDARD_TIRE_FRONT, DBHelper.COLUMN_CAR_STANDARD_TIRE_BACK, DBHelper.COLUMN_CAR_DRIVING_WHEELS, DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES, DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES,
				DBHelper.COLUMN_CAR_FILENAME, DBHelper.COLUMN_CAR_PATH, DBHelper.COLUMN_CAR_SHAREDCAR_ID, DBHelper.COLUMN_CAR_IS_SHARED};
		Cursor cursor = context.getContentResolver().query(CarProvider.CONTENT_URI, columns, null, null, null);
		
		while(cursor.moveToNext()) {
			Car car = new Car();
			car.setUid(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_UID)));
			car.setOwnerId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_OWNERID)));
			car.setOwnername(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_OWNERNAME)));
			car.setRegistrationNumber(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_REGISTRATION_NUMBER)));
			car.setChassisNumber(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CHASSIS_NUMBER)));
			car.setCarRegYear(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CARREGYEAR)));
			car.setRegFirstTimeInNorway(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_REG_FIRST_TIME_IN_NORWAY)));
			car.setBrandCode(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_BRAND_CODE)));
			car.setCarModel(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CAR_MODEL)));
			car.setEnginePerformance(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_ENGINE_PERFORMANCE)));
			car.setDisplacement(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_DISPLACEMENT)));
			car.setFuelType(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FUEL_TYPE)));
			car.setLength(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_LENGTH)));
			car.setWidth(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_WIDTH)));
			car.setWeight(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_WEIGHT)));
			car.setTotalWeight(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_TOTAL_WEIGHT)));
			car.setColour(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_COLOUR)));
			car.setCo2Emission(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_CO2_EMISSION)));
			car.setStandardTireFront(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_STANDARD_TIRE_FRONT)));
			car.setStandardTireBack(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_STANDARD_TIRE_BACK)));
			car.setDrivingWheels(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_DRIVING_WHEELS)));
			car.setTrailerWeightWithBrakes(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITH_BRAKES)));
			car.setTrailerWeightWithoutBrakes(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_TRAILER_WEIGHT_WITHOUT_BRAKES)));
			car.setFilename(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_FILENAME)));
			car.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CAR_PATH)));
			car.setSharedCarId(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_SHAREDCAR_ID)));
			car.setShared(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CAR_IS_SHARED)) > 0);
			
			cars.add(car);
		}
		cursor.close();
		
		return cars;
	}
	
	public void delete(int uid) {
		context.getContentResolver().delete(CarProvider.CONTENT_URI, 
				DBHelper.COLUMN_UID + "=?", new String[] {String.valueOf(uid)});
	}
	
	public void deleteAll(int ownerId) {
		context.getContentResolver().delete(CarProvider.CONTENT_URI, null, null);
	}
	
	public void deleteAllShared() {
		context.getContentResolver().delete(CarProvider.CONTENT_URI, DBHelper.COLUMN_CAR_IS_SHARED + "=1", null);
	}
}
