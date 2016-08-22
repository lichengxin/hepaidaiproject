package base.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import utils.Log;
import utils.StrUtils;


/*//
 * BaseApp 管理 所有应用 通用的逻辑
 * 1. appMsgHandler 应用级别的消息循环处理
 * 2. getConfig 通用配置存储管理
 * 3. sendBoardcast 发送广播
 * 4. showToast 显示提示 无论是否在线程中 都没有影响
 * 5. instance 基础应用句柄
 * 6. core 基础工厂类扩展 (各种功能扩展通过工厂管理类的方式实现)
//*/
public class BaseApp extends android.app.Application {

	private static final String LOGTAG = BaseApp.class.getSimpleName();

	public static BaseApp instance = null;
	public static String AppName = null;
	public static String EnglishName = null;
	public static String ChineseName = null;
	
	public static ManagerFactory core = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		core = ManagerFactory.getManagerFactory();
		Thread.setDefaultUncaughtExceptionHandler(new sys.exception.LocalFileHandler(this));
    }

	private static Handler mAppMsgHandler = null;

	protected Handler appMsgHandler() {
		if (null != mAppMsgHandler)
			return mAppMsgHandler;
		Looper loop = Looper.getMainLooper();
		return mAppMsgHandler = new Handler(loop) {
			@Override
			public void handleMessage(Message msg) {
				handleAppMessage(msg);
				super.handleMessage(msg);
				// AppManager.handleAppMessage(msg);
			}
		};
	}

	private void handleAppMessage(Message msg) {
		switch (msg.what) {
		case BaseConsts.MSG_TOAST:
			Log.d(LOGTAG, " handleAppMessage " + msg.what);
			Toast.makeText(instance, (String) msg.obj, Toast.LENGTH_LONG).show();
			break;
		case BaseConsts.MSG_DOWNLOAD:
			Log.d(LOGTAG, " handleAppMessage MSG_DOWNLOAD");
			net.HttpActDownloader.handleDownloadMessage((net.HttpActDownloader.Task) msg.obj);
			break;
		case BaseConsts.MSG_APP_UPDATE:
			AppUpdate.handleUpdateMessage((AppUpdate.UpdateTask) msg.obj);
			break;
		default:
			break;
		}
	}

	private static ManagerAppConfig.AppConfig mConfig = null;
	public ManagerAppConfig.AppConfig getConfig() {
		if (null == mConfig)
			mConfig = ManagerAppConfig.load();
		return mConfig;
	}

	public static void sendAppMessage(int msg, Object obj) {
		Message msgobj = instance.appMsgHandler().obtainMessage();
		msgobj.obj = obj;
		msgobj.what = msg;
		msgobj.sendToTarget();
	}

	public void sendBroadcast(String MsgID, String extraName, String extraData) {
		if (TextUtils.isEmpty(MsgID))
			return;
		Intent intent = new Intent(MsgID);
		if (!TextUtils.isEmpty(extraName)) {
			intent.putExtra(extraName, extraData);
		}
		instance.sendBroadcast(intent);
	}

	public static void showToast(String message) {
		if (TextUtils.isEmpty(StrUtils.strNoNull(message)))
			return;
		Log.d(LOGTAG, "showToast:" + message);
		sendAppMessage(BaseConsts.MSG_TOAST, message);
	}
	
	public static void quitApp1() {
		android.os.Process.killProcess(android.os.Process.myPid());    //获取PID 
		System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
	}

	public static void quitApp2() {
		//系统会将，该包下的 ，所有进程，服务，全部杀掉，就可以杀干净了，要注意加上 
        //<uses-permission android:name=\"android.permission.RESTART_PACKAGES\"></uses-permission>
		android.app.ActivityManager am = (android.app.ActivityManager) instance.getSystemService(android.content.Context.ACTIVITY_SERVICE);
	    am.restartPackage(instance.getPackageName()); 
	}
	
	public static void quitApp3() {
		// Android的窗口类提供了历史栈，我们可以通过stack的原理来巧妙的实现，
		// 这里我们在A窗口打开B窗口时在Intent中直接加入标 志     Intent.FLAG_ACTIVITY_CLEAR_TOP，
		// 这样开启B时将会清除该进程空间的所有Activity
		Intent intent = new Intent(); 
		//intent.setClass(instance, QuotAppActivity.class); 
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置 
		instance.startActivity(intent);
		//接下来在B窗口中需要退出时直接使用finish方法即可全部退出
	}
	
	public static void quitApp4 () {
		try {
			BaseApp.core.getActivitiesManager().finishAllActivity();
			ActivityManager activityMgr= (ActivityManager) instance.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(instance.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}	
	}
	
	public static void quitApp() { 
		quitApp4();
	}
	
	public static boolean isNetworkAvailable() {
		android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivityManager) {
			// 获取NetworkInfo对象
			android.net.NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (android.net.NetworkInfo.State.CONNECTED == networkInfo[i].getState())
                    {
                        return true;
                    }
                }
            }
		}
		return false;
	}
	
	 /**
     * 获得版本名称
     */
    public String getVersionName(){
    	android.content.pm.PackageManager pm = this.getPackageManager();
    	if (null != pm) {
    		android.content.pm.PackageInfo packinfo;
			try {
				packinfo = pm.getPackageInfo("com.update.apk", 0);
	    		if (null != packinfo) {
	    			return packinfo.versionName;
	    		}
			} catch (android.content.pm.PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return null;
    }

    public void checkAppUpdate() {
    	if (null == mAppUpdate) {
    		mAppUpdate = new AppUpdate();
    		mAppUpdate.checkUpdate();
    	}
    }
    
    public void clearAppUpdate() {
    	mAppUpdate = null;
    }
    
    public static AppUpdate mAppUpdate = null;
}
