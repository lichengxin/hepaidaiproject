package com.zhijiaoyi.vstock;

import android.content.*;

public class StockService extends android.app.Service  {

	@Override
	public android.os.IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		//super.onStart(intent, startId);
	}
}
