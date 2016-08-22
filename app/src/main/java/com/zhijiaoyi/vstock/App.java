package com.zhijiaoyi.vstock;

import pkg.stock.data.DBStock;
import base.app.BaseApp;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class App extends base.app.BaseApp {

	private static final String LOGTAG = App.class.getSimpleName();
	@Override
	public void onCreate() {
		super.onCreate();
		OnAppStart();
		BaseApp.EnglishName = "zhijiaoyi";
    }
	
	void OnAppStart() {
	}
}
