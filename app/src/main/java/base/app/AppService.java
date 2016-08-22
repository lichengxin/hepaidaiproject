package base.app;

import android.content.*;
import android.os.IBinder;

/*Service的启动有两种方式：
 * 本地服务 Local Service 用于应用程序内部
 *    以调用Context.startService()启动 
 * 远程服务 Remote Service 用于android系统内部的应用程序之间
 *    Context.bindService()方法建立，以调用 Context.unbindService()关闭。多个客户端可以绑定至同一个服务
 * 1. context.startService() 和
 *      context.startService()  ->onCreate()- >onStart()->Service running
        context.stopService()  ->onDestroy() ->Service stop  
 * 2. context.bindService()
 *      onCreate --> onBind(只一次，不可多次绑定) --> onUnbind --> onDestory 
 *      context.bindService()->onCreate()->onBind()->Service running
        onUnbind() -> onDestroy() ->Service stop
        服务的开机自启动
        这个可以通过在OnCreate加TARCE确认
        1. 开机启动的权限
        <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"></uses-permission>
        2. 
        <receiver android:name=".BootBroadcastReceiver">    
          <intent-filter><action android:name="android.intent.action.BOOT_COMPLETED" /></intent-filter>    
        </receiver> 
        
            可以通过android:priority = "1000"这个属性设置最高优先级，1000是最高值，如果数字越小则优先级越低
     <service android:name="com.dbjtech.acbxt.waiqin.UploadService"  
       android:enabled="true" >  
       <intent-filter android:priority="1000" >  
         <action android:name="com.dbjtech.myservice" />  
       </intent-filter>  
     </service>  
 * */
public class AppService extends android.app.Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 在运行onStartCommand后service进程被kill后，并且没有新的intent传递给它。
		// Service将移出开始状态，并且直到新的明显的方法（startService）调用才重新创建。
		// 因为如果没有传递任何未决定的intent那么service是不会启动，也就是期间onstartCommand不会接收到任何null的intent
		flags = START_NOT_STICKY;
		flags = START_REDELIVER_INTENT;
		
		// 运行onStartCommand后service进程被kill后，那将保留在开始状态，
		// 但是不保留那些传入的intent。不久后service就会再次尝试重新创建，
		// 因为保留在开始状态，在创建     service后将保证调用onstartCommand。
		// 如果没有传递任何开始命令给service，那将获取到null的intent
		flags = START_STICKY;
		// 手动返回START_STICKY，亲测当service因内存不足被kill，当内存又有的时候
		// service又被重新创建，比较不错，但是不能保证任何情况下都被重建，比如进程被干掉了
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void registerScreenOnReceiver() {
		/*在一个服务中，监听LCD唤醒[系统唤醒或者其他]*/ 
		IntentFilter ScreenFilter = new IntentFilter();  
		ScreenFilter.addAction(Intent.ACTION_SCREEN_ON);  
		registerReceiver(getAppReceiver(), ScreenFilter);
	}
	
	private BroadcastReceiver mAppReceiver = null;
	private BroadcastReceiver getAppReceiver() {
		if (null == mAppReceiver) {
			mAppReceiver = new BroadcastReceiver() {  
			    public void onReceive(Context context, Intent intent) {  
		            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {  
		            }
			    }
		    };  
		}
		return mAppReceiver;
	};
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		// 在service的onDestroy中要反注册这个receiver
		if (null != mAppReceiver) {
			unregisterReceiver(mAppReceiver);
		}
		if (mIsAutoRestartAfterKill) {
	        Intent intent = new Intent();  
	        intent.setClass(this, AppService.class); // D销毁时重新启动Service，与上面的 etAction比对      
			this.startService(intent);  
		}
	}
	protected boolean mIsAutoRestartAfterKill = false;
	@Override
	public void onStart(Intent intent, int startId){
	}
}
