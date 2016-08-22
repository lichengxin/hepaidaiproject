package com.hepaidai;

import base.app.BaseApp;

public class App extends base.app.BaseApp {

	private static final String LOGTAG = App.class.getSimpleName();
	@Override
	public void onCreate() {
		super.onCreate();
		OnAppStart();
		BaseApp.EnglishName = "hepaidai";//App.class.getSimpleName();
    }
	
	void OnAppStart() {
	}
}
