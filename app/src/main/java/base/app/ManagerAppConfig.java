package base.app;

import utils.*;
import android.content.SharedPreferences;

import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;


import data.gson.Gson;
import data.gson.reflect.TypeToken;
/**
 * 
 * @author gzh
 *
 */

/*//
 * 本地配置信息管理类，用于读取，保存全局配置到本地存储卡上
//*/
public class ManagerAppConfig  extends AbstractManager {
	
	private static final String LOGTAG = ManagerAppConfig.class.getSimpleName();
	
	public static final String APPCFG_NAME = "app";
	public static final String CFG_CONFIG = "appconfig";
	
	public ManagerAppConfig(ManagerFactory core) {
		super(core);
	}

	/**
	 * 本地初始化， 需要确保唯一执行, 在AppManager.OnAppStart中执行
	 */
	public static class AppConfig {
		
		private AppConfig() {
			//serverSetting = new ServerSettings();
		}
		
		//public ServerSettings serverSetting = new ServerSettings(); 
		public long lastUserId = 0;
		public String appUpdateIgnore = null;
		public String lastestDownloadUrl = null;
		public String lastestVersionName = null;
		public long lastestVersionCode = 0;	
		public int showAppGuide = 0;
		public long lastRegisterStep2UserId = 0;
		public long lastRegisterUserId = 0;
		public String lastestVersionBrief = null;
		public String lastDownloadUpdateFile = null;
		public String deviceId = "";
		public String channelId = "";
		public String IMEI = "";
		public String MAC = "";
		
		public int CurrentVersionCode = 0;
		public String CurrentVersionName = "";
		public String PackageName = "";
		
		//public AppVersionInfo NewVersion;
		
		/**
		 * 当前是否正在运行更新
		 */
		public boolean IsUpdateRunning;
		public String deviceToken;
	}

	/**
	 * 全局同步保存配置信息到配置文件
	 * @param info
	 * @return
	 */
	private static SharedPreferences getPref() {
		return BaseApp.instance.getSharedPreferences(APPCFG_NAME, android.app.Activity.MODE_PRIVATE);
	}
	
	public static AppConfig load() {
		AppConfig config = null;
		try {
			/*
			 * 此处为了兼容，主动向ApplicationData设置数据
			 */
			// 初始化本地服务器配置信息
			SharedPreferences pref = getPref();
			if (null != pref) {
				
				String strConfigJson = pref.getString(CFG_CONFIG, null);
				
				if(!TextUtils.isEmpty(strConfigJson))
				{
					Gson gson = new Gson();					
					config = gson.fromJson(strConfigJson, new TypeToken<AppConfig>(){}.getType());
				}
			}
		} catch (Exception ex) {
			Log.e(ex.getMessage());
		}
		
		if(null == config)
		{
			config = InitAppConfig();
		}
		
		//每次启动，都需要读取当前真实的安装信息
		config.IsUpdateRunning = false;
		if(TextUtils.isEmpty(config.deviceId)) {
			//config.deviceId = DeviceManager.GetDeviceID(BaseApp.instance);
		}

		try {
			PackageManager packagemgr = BaseApp.instance.getPackageManager();
			if (null != packagemgr) {
				config.PackageName = BaseApp.instance.getPackageName();

				if (TextUtils.isEmpty(config.channelId)) {
					config.channelId = AppChannel.getChannel(BaseApp.instance);
				}
				Log.d(LOGTAG, " channelId:" + config.channelId);
				
				PackageInfo packageInfo =packagemgr.getPackageInfo(config.PackageName, PackageManager.GET_CONFIGURATIONS);
				config.CurrentVersionCode = packageInfo.versionCode;
				config.CurrentVersionName = packageInfo.versionName;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return config;
	}
	
	private static AppConfig InitAppConfig()
	{
		AppConfig config = new AppConfig();
			config.lastUserId = 0;
			config.appUpdateIgnore = "";
			//config.serverSetting = new ServerSettings();
		return config;
	}

	public synchronized static boolean Save(AppConfig config){		
		Log.d("Save Local Config");
		SharedPreferences pref = getPref(); 
		if (null == pref)
			return false;
		
		Editor edit = pref.edit();
		if (null != edit) {
			try {
				// 初始化本地服务器配置信息
				Gson gson = new Gson();
				String strConfigJson = gson.toJson(config);
				
				if(!TextUtils.isEmpty(strConfigJson))
				{
					edit.putString(CFG_CONFIG, strConfigJson);
				}
				//提交
				edit.commit();			
				Log.d("Save Local Config Success");
				return true;
			}
			catch(Exception ex) {
				Log.d("Save Local Config Error : " + ex.getMessage());
			}
			return false;
		}	
		return false;
	}
}
