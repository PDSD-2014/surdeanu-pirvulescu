package ro.pirvulescusurdeanu.septica.controllers;

import android.app.Activity;

public class MainController {
	private static MainController instance;
	private Activity currentActivity;
	
	private MainController() {
		
	}
	
	public static MainController getInstance() {
		if (instance == null) {
			instance = new MainController();
		}
		return instance;
	}
	
	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}
	
	public Activity getCurrentActivity() {
		return currentActivity;
	}
 }
