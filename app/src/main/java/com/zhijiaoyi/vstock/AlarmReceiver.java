package com.zhijiaoyi.vstock;

import utils.*;
import base.app.*;


public class AlarmReceiver extends android.content.BroadcastReceiver  {
//public class AlarmReceiver extends com.base.app.BaseAlarmReceiver {
	
	// <receiver android:name=".AlarmReceiver" android:process=":remote"></receiver>
	// com.android.alarm.permission.SET_ALARM
	private static final String LOGTAG = AlarmReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(android.content.Context context, android.content.Intent intent) {
		 String action = intent.getAction();
		 Log.d(LOGTAG, AlarmReceiver.class.getName() + " onrecv:" + action);
		 long alarmtime = intent.getLongExtra("alarmtime", 0);
		 long currentime = System.currentTimeMillis();
		 BaseApp.showToast(AlarmReceiver.class.getName() +  " onrecv:" + 
			(currentime - alarmtime) + "/" +
			alarmtime +
		     " action:" + action);
		 //super.onReceive(context, intent);
		 //int id = intent.getIntExtra("id", -1);  
	     //long alarmtime= intent.getLongExtra("alarm_time", -1);  
	}
}
