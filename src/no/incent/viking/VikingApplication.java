package no.incent.viking;

import no.incent.viking.pojo.User;
import no.incent.viking.pojo.Car;

import java.util.List;
import java.util.ArrayList;

import android.app.Application;

public class VikingApplication extends Application {
	private User user;
	private Car activeCar;
	private List<Car> cars = new ArrayList<Car>();

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<Car> getCars() {
		return cars;
	}
	
	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
	
	public Car getActiveCar() {
		return activeCar;
	}
	
	public void setActiveCar(Car activeCar) {
		this.activeCar = activeCar;
	}
}
