package no.incent.viking.service;

import no.incent.viking.service.IVikingServiceCallback;
import no.incent.viking.pojo.User;

interface IVikingService {
	void requestCallToActions();
	void uploadRecords();
	void requestTraffics();
	void requestTrafficsByRadius(double latitude, double longitude);
	void requestNews();
	void requestPhoneCategories();
	void requestAllCars(int ownerId);
	void requestMyCarFiles(int ownerId);
	void requestMyCarPhones(int ownerId);
	void requestMyCarEvents(int ownerId);
	void saveUser(inout User user);
	void registerCallback(IVikingServiceCallback callback);
	void unregisterCallback(IVikingServiceCallback callback);
}