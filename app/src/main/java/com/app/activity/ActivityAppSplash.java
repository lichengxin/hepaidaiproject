package com.app.activity;


import ui.activity.BaseActivity;
import base.app.*;

/*//
 * 通用页面 -- 应用模块 -- 启动界面
 * 
 //*/
public class ActivityAppSplash extends BaseActivity {

	private static String LOGTAG = ActivityAppSplash.class.getSimpleName();

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ManagerActivities.getLayoutId_Activity_App_Splash());
		// 要注意事件
		// 必须在显示Splash的时候禁止BACK，MENU等事件，然后再在Splash结束后把它们重新启用。
		
		createMessageLoopHandler();
		if (null != mBaseMsgHandler) {
			mBaseMsgHandler.sendEmptyMessageDelayed(BaseConsts.MSG_RUN, 2000);			
		}
		// showDefaultAnimation();
	}

	@Override
	protected boolean handleActivityMessage(android.os.Message msg){
        switch (msg.what) {  
          case BaseConsts.MSG_RUN:
      		  showNextActivity();
     	      return true;
        }
		return super.handleActivityMessage(msg);
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

	// much easier to handle key events
	@Override
	public void onBackPressed() {
	}

	private void showDefaultAnimation() {
	}

	private void showTestActivity() {
		 //BaseApp.core.getActivitiesManager().showMainActivity(this);
		
		BaseApp.core.getActivitiesManager().showUserLoginActivity(this);
	}
	
	private void showNextActivity() {
		if (false) {
			// 测试方便跳转页面
			showTestActivity();
			finish();
			return;
		}
		boolean isShowNext = false;
		// 是否需要显示引导界面
		ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
		if (null != config) {
			if (0 == config.showAppGuide) {
				// **showAppGuideActivity();
				// **isShowNext = true;
			}
		}
		if (!isShowNext) {
			// 是否需要立即登录 , 是否可以直接进入主界面 后登录模式
			boolean isNeedLoginFirst = false;
			// *//
			if (isNeedLoginFirst) {
				base.app.user.UserAccount activeUser = null;
	            base.app.user.ManagerAppUser usermgr = BaseApp.core.getUserManager();
				if (null != usermgr) {
					usermgr.checkAutoLoginUserAccount();
					if (usermgr.isLogined()) {
						activeUser = usermgr.ActiveUser();
					}
				}
				if (null != activeUser) {
					if (activeUser.isLocked()) {
						BaseApp.core.getActivitiesManager().showUserUnlockActivity(this);
					} else {
						BaseApp.core.getActivitiesManager().showMainActivity(this);					
					}						
				} else {
					BaseApp.core.getActivitiesManager().showUserLoginActivity(this);						
				}
			} else {
				BaseApp.core.getActivitiesManager().showMainActivity(this);	
			}
			// */
		}
		this.finish();
	}
}
