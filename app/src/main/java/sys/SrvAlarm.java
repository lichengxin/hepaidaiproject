package sys;

import java.util.Calendar;
import java.util.Locale;

import utils.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.text.format.DateFormat;
import base.app.*;


/* 系统服务  alarm (闹钟) 控制类 */

/*  SystemClock.setCurrentTimeMillis(curMs);//需要Root权限
 * */

public class SrvAlarm {

	// Alarm是通过广播intent，所以BroadcastReceiver，Activity，Service都可以得到其intent，并进行处理
	
	// Alarm是在预定的时间触发Intent的，独立于应用程序的提醒用户的方式。
	// 当这个Alarm触发后，就会广播这个Intent，如果应用程序没有起启，就会
	// 启动这个应用程序，而不需要就用程序被打开或者处于活动状态。
	// 通过AlarmManager来管理所有的Alarm。
	
	// android.permission.SET_TIME
	// void setTime(long millis)
	
	// android.permission.SET_TIME_ZONE
	// void setTimeZone(String timeZone)

	// process属性，它规定了组件(activity, service, receiver等)所在的进程
	//  <receiver android:name=".RepeatingAlarm" android:process=":remote" />
	// process属性以一个冒号开头，进程名会在原来的进程名之后附加冒号之后的字符串作为新的进程名。
	//      当组件需要时，会自动创建这个进程。这个进程是应用私有的进程
	// process属性以小写字母开头，将会直接以属性中的这个名字作为进程名，这是一个全局进程
	//      这样的进程可以被多个不同应用中的组件共享
	private static final String LOGTAG = SrvAlarm.class.getSimpleName();
	
	public static class AlarmReceiverClass extends android.content.BroadcastReceiver  {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOGTAG, "AlarmReceiverClass:" + intent.getAction());
			BaseApp.showToast(LOGTAG + "AlarmReceiverClass:" + intent.getAction());
		}
	}
	
	public static long getAbsoluteAlarmTime(Context context, int AHour, int AMinute) {
		 // 设置警报时间         
        //将秒和毫秒设置为0  
		//calendar.set(java.util.Calendar.YEAR, 2015);
		//calendar.set(java.util.Calendar.MONTH, 11);
		//calendar.set(java.util.Calendar.DATE, 11);
		//calendar.set(java.util.Calendar.HOUR_OF_DAY, AHour);  
		//calendar.set(java.util.Calendar.MINUTE, AMinute);
		//calendar.set(java.util.Calendar.SECOND, 0);  
		//calendar.set(java.util.Calendar.MILLISECOND, 0);
		// 必须要 setTimeZone 否则不会提醒
		//java.util.TimeZone timezone = null;
		//timezone = java.util.TimeZone.getTimeZone("America/Los_Angeles");
		//timezone = java.util.TimeZone.getTimeZone("Asia/Shanghai");
		//timezone = java.util.TimeZone.getDefault();
		//Log.d(LOGTAG, "getDefault Time Zone:" + timezone.toString());
		//calendar.setTimeZone(timezone);
		java.util.Calendar calendar = java.util.Calendar.getInstance(); 
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));  
		Log.d(LOGTAG, "absolute alarm time1:" + DateFormat.format("yyyy-MM-dd hh:mm:ss", calendar));
		calendar.add(java.util.Calendar.SECOND, 5);
		//calendar.set(java.util.Calendar.HOUR_OF_DAY, 11);  
		//calendar.set(java.util.Calendar.MINUTE, 12);
		Log.d(LOGTAG, "absolute alarm time2:" + DateFormat.format("yyyy-MM-dd hh:mm:ss", calendar));
        long result = calendar.getTimeInMillis();
		 //calendar.add(java.util.Calendar.SECOND, 30);  
		//calendar.add(java.util.Calendar.SECOND, 5);
		//alarmtime = calendar.getTimeInMillis();
        //result = java.lang.System.currentTimeMillis();
        //java.util.Date t=new java.util.Date(); 
        //t.setTime(java.lang.System.currentTimeMillis() + 10 * 1000);
		return result;
	}

    public static String getFormattedTime(Context context, Calendar time) {
        //String skeleton = DateFormat.is24HourFormat(context) ? "EHm" : "Ehma";
        //String pattern = "yyyy-MM-dd HH:nn:ss";//DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        //DateFormat.getBestDateTimePattern(null, skeleton);
        return (String) DateFormat.format("yyyy-MM-dd hh:mm:ss", time);
    }
    
	public static long getAbsoluteAlarmTime(int ADelayMillis) {
		return System.currentTimeMillis() + ADelayMillis;
	}
	
	public static long getRelativeAlarmTime() {
		return 2000;
	}
	
	public static android.app.PendingIntent getAlarmPendingIntent(android.content.Context AContext, android.content.Intent AIntent) {
		/* pendingFlag
		   FLAG_CANCEL_CURRENT:如果当前系统中已经存在一个相同的PendingIntent对象，
		             那么就将先将已有的PendingIntent取消，然后重新生成一个PendingIntent对象。
        FLAG_NO_CREATE:如果当前系统中不存在相同的PendingIntent对象，系统 
                                    将不会创建该PendingIntent对象而是直接返回null。
        FLAG_ONE_SHOT:该PendingIntent只作用一次。在该PendingIntent对象通过send()方法触发过后，
              PendingIntent将自动调用cancel()进行销毁，那么如果你再调用send()方法的话，系统将会返回一个SendIntentException。
        FLAG_UPDATE_CURRENT:如果系统中有一个和你描述的PendingIntent对等的PendingInent，那么系统将
                          使用该PendingIntent对象，但是会使用新的Intent来更新之前PendingIntent中的Intent对象数据，例如更新Intent中的Extras
		 */
		int pendingFlag = android.app.PendingIntent.FLAG_UPDATE_CURRENT; //android.app.PendingIntent.FLAG_ONE_SHOT;
		pendingFlag = android.app.PendingIntent.FLAG_CANCEL_CURRENT;
		return android.app.PendingIntent.getBroadcast(AContext, 0, AIntent, pendingFlag);
		//return android.app.PendingIntent.getBroadcast(BaseApp.instance, 0, AIntent, pendingFlag);
	}
	
	/*试试在Service刚刚启动的时候就调用wakelock.acquire,因为alarmManager所持有的cpu唤醒时间是有限的
	 * 如果在这个有限的时间内，你所做的东西没有做完，那cpu就sleep了
	 * MIUI只要是第三方APP要用到 时间 的都会不准时
	 * */
	
	public static void setAlarm_relative(android.app.AlarmManager am, Context AContext, Intent intent) {
		// 相对时间
		// 相对时间 ELAPSED_REALTIME_WAKEUP
		// ELAPSED_REALTIME： 在指定的延时过后，发送广播，但不唤醒设备（闹钟在睡眠状态下不可用）
		//     如果在系统休眠时闹钟触发，它将不会被传递，直到下一次设备唤醒
		// ELAPSED_REALTIME_WAKEUP： 在指定的延时过后，发送广播，并唤醒设备（即使关机也会执行operation所对应的组件）
		// RTC： 指定当系统调用System.currentTimeMillis()方法返回的值与triggerAtTime
		//     相等时启动operation所对应的设备 在指定的时刻发送广播但不唤醒设备
		//     如果在系统休眠时闹钟触发，它将不会被传递，直到下一次设备唤醒（闹钟在睡眠状态下不可用）
		//android.app.PendingIntent pendingIntent = getAlarmPendingIntent(AContext, intent);
		//am.set(AlarmManager.POWER_OFF_WAKEUP, triggerAtTime + 100000, pendingIntent);
		//am.set(AlarmManager.ELAPSED_REALTIME, getRelativeAlarmTime(), getAlarmPendingIntent(AContext, intent));
		//am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, getRelativeAlarmTime(), getAlarmPendingIntent(AContext, intent));
	}
	
	public static void setAlarm_absolute(android.app.AlarmManager am, Context AContext, Intent intent) {
		// 绝对时间
		// 绝对时间 RTC   RTC_WAKEUP
		//final long time = System.currentTimeMillis();
		//long triggerAtTime = android.os.SystemClock.elapsedRealtime();
		//triggerAtTime = System.currentTimeMillis() - 1 * 100 * 1000 - 3 * 10 * 1000 - 1 * 1000;
		//triggerAtTime = System.currentTimeMillis() - 1 * 100 * 1000 - 3 * 10 * 1000 - 1 * 1000;
		
		//Log.d(LOGTAG, "SetAlarmTime 1:" + time + "/" + triggerAtTime);
		
		long alarmtime_absolute = 0;
		alarmtime_absolute = getAbsoluteAlarmTime(AContext, 14, 8);
		//alarmtime_absolute = getAbsoluteAlarmTime(5000);
		intent.putExtra("alarmtime", System.currentTimeMillis());
        //am.setTimeZone("GMT+08:00");
        //am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		//
		//am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
		//am.set(AlarmManager.RTC, time+5000, getAlarmPendingIntent(AContext, intent));  
		//am.set(AlarmManager.RTC, alarmtime_absolute, getAlarmPendingIntent(AContext, intent));
		am.set(AlarmManager.RTC_WAKEUP, alarmtime_absolute, getAlarmPendingIntent(AContext, intent));
	}
	
	public static void setAlarm (android.content.Context AContext) {
		android.content.Intent intent = null;
		Log.d(LOGTAG, "set alarm begin");
		//intent = new android.content.Intent(context, AlarmReceiverClass.class);
		intent = new android.content.Intent();
		intent.setAction(BaseConsts.BROADCAST_RECV_ALARM);
		
		android.app.AlarmManager am = null;
		//am = (android.app.AlarmManager) BaseApp.instance.getSystemService(android.content.Context.ALARM_SERVICE);
		am = (android.app.AlarmManager) AContext.getSystemService(android.content.Context.ALARM_SERVICE);
		if (null != am) {
			// set(int type，long startTime，PendingIntent pi)			
			// 只会警报一次  
			setAlarm_absolute(am, AContext, intent);
            // android.permission.SET_TIME_ZONE
			
			// 设置重复周期
			// setRepeating(int type，long startTime，long intervalTime，PendingIntent pi)
			// 设置不精确周期  setInexactRepeating
			// 设置精确周期 会重复警报多次 
			//am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 10*1000, pendingIntent); 
			// INTERVAL_DAY：      设置闹钟，间隔一天
			// INTERVAL_HALF_DAY：  设置闹钟，间隔半天
			// INTERVAL_FIFTEEN_MINUTES：设置闹钟，间隔15分钟
			// INTERVAL_HALF_HOUR：     设置闹钟，间隔半个小时
			// INTERVAL_HOUR：  设置闹钟，间隔一个小时
			 // 设置警报时间，除了用Calendar之外，还可以用  
			//long firstTime = android.os.SystemClock.elapsedRealtime();  
			//am.setRepeating(android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 5*1000, sender);  
		}
	}

	public void cancelAlarm (android.content.Context context) {
		android.content.Intent intent = new android.content.Intent(context, AlarmReceiverClass.class);  
		android.app.PendingIntent sender = android.app.PendingIntent.getBroadcast(context, 0, intent, 0);  
		android.app.AlarmManager am = (android.app.AlarmManager) context.getSystemService(android.content.Context.ALARM_SERVICE);
		if (null != am) {
		// 要取消这个警报，只要通过PendingIntent就可以做到  
		    am.cancel(sender);  
		}
	}
	
}
