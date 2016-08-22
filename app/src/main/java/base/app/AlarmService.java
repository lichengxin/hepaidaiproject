package base.app;

import android.content.*;

// <uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
/* Android中的进程是托管的，当系统进程空间紧张的时候，
 * 会依照优先级自动进行进程的回收。Android将进程分为6个等级,它们按优先级顺序由高到低依次是:
   1.前台进程( FOREGROUND_APP)
   2.可视进程(VISIBLE_APP )
   3. 次要服务进程(SECONDARY_SERVER )
   4.后台进程 (HIDDEN_APP)
   5.内容供应节点(CONTENT_PROVIDER)
   6.空进程(EMPTY_APP)
   当service运行在低内存的环境时，将会kill掉一些存在的进程。因此进程的优先级将会很重要
   可以使用startForeground 将service放到前台状态。这样在低内存时被kill的几率会低一些*/
public class AlarmService  extends android.app.Service {

	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return mBinder;
	}

	private android.os.IBinder mBinder = new android.os.Binder(){		
	};	

	private android.app.NotificationManager mNofifyMgr;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mNofifyMgr = (android.app.NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}
	
	// service +broadcast  方式，就是当service走ondestory的时候，发送一个自定义的广播，当收到广播的时候，重新启动service
	@Override
	public void onDestroy() {
		// onDestroy方法里重启service
		stopForeground(true);  
        Intent intent = new Intent(BaseConsts.BROADCAST_SERVICE);  
        sendBroadcast(intent);
        
        Intent sevice = new Intent(this, AlarmService.class);  
        this.startService(sevice);  
        super.onDestroy();  		
	}
	
	//@Override
	public void onStartCommand() {
		
	}
}
