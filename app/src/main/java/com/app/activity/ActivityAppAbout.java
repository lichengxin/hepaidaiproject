package com.app.activity;


import ui.activity.BaseActivity;
import base.app.*;

/*//
 * 通用页面 -- 应用模块 -- 关于界面
 * 
//*/
public class ActivityAppAbout extends BaseActivity {

	private static String LOGTAG = ActivityAppAbout.class.getSimpleName();
	
	@Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ManagerActivities.getLayoutId_Activity_App_About());
        //--------------------------------------------------------------------------
        //setBaseTitle(idStr(R.string.action_aboutapp));
        // 初始�? �?出动画的参数 没有参数可能无法动画效果
    	//initCloseAnimationParam();
		//initBackButton();  
		initViews();
		initTitle("关于软件");
        //--------------------------------------------------------------------------
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	private void initViews() {
			//contentView = (TextView)findViewById(R.id.txtAboutContent);
			//AppConfig config = RenMai.mInstance.getConfig();
			ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
			String ver = "当前版本 " + config.CurrentVersionName;

			if(android.text.TextUtils.equals(config.channelId , "debug") || 
			   android.text.TextUtils.equals(config.channelId , "dev") ){
				ver += " " + config.channelId + "/" + config.deviceId;
			}
			
			//int verCode = config.getVersionCode();
			//TextView txtAboutVersion = (TextView)findViewById(R.id.txtAboutVersion);			
			//if (null != txtAboutVersion) {
			//	txtAboutVersion.setText(ver);
			//}
	}
}
