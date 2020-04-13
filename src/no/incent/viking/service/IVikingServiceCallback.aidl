package no.incent.viking.service;

interface IVikingServiceCallback {
	void onFinishRequestingCallToActions();
	void onFinishRequestingTraffics();
	void onFinishRequestingNews();
	void onFinishRequestingPhoneCategories();
	void onFinishRequestingAllCars();
	void onFinishRequestingCarFiles();
	void onFinishRequestingCarPhones();
	void onFinishRequestingCarEvents();
	void onFinishSavingUser();
}