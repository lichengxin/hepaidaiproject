package base.app;

import utils.*;
import android.content.*;

public class AlarmReceiver extends BroadcastReceiver  {
	
	// 每一个 AlarmReceiver 放着各自的 app 命名空间内
	
	// <receiver android:name=".AlamrReceiver" android:process=":remote"></receiver>
	// com.android.alarm.permission.SET_ALARM
	private static final String LOGTAG = AlarmReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		 String action = intent.getAction();
		 Log.d(LOGTAG, base.app.AlarmReceiver.class.getName() +  " onrecv:" + action);
		 BaseApp.showToast(base.app.AlarmReceiver.class.getName() + " onrecv:" +action);
		 //int id = intent.getIntExtra("id", -1);  
	     //long alarmtime= intent.getLongExtra("alarm_time", -1);  
	}
}
