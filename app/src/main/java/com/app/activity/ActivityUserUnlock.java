package com.app.activity;

import ui.activity.BaseActivity;
import ui.widget.UtilsText;

import com.app.R;

import base.app.*;
import base.app.user.UserLogin;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;

public class ActivityUserUnlock extends BaseActivity {

/*//
 * 通用页面 -- 用户模块 -- 用户解锁 (重登录)
 * 
//*/
	private static String LOGTAG = ActivityUserUnlock.class.getSimpleName();
	
	@Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ManagerActivities.getLayoutId_Activity_User_Unlock());
        createBroadcastReceiver();
        initViews();
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

	private void createBroadcastReceiver(){
        //注册广播接收器
		mActivityBroadcastReceiver =new BroadcastReceiver() {
		    @Override
		  	public void onReceive(Context context, Intent intent) {
		    	handleBroadcastReceive(context, intent);
		  	}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BaseConsts.BROADCAST_USER_LOGIN);
		intentFilter.addAction(BaseConsts.BROADCAST_USER_REGISTER);
		registerReceiver(mActivityBroadcastReceiver, intentFilter);
	}

	private void handleBroadcastReceive(Context context, Intent intent) {

    	boolean isEnterMain = false;
  		if (BaseConsts.BROADCAST_USER_LOGIN.equals(intent.getAction())) {
  			isEnterMain = true;
  		}
  		if (BaseConsts.BROADCAST_USER_REGISTER.equals(intent.getAction())) {
  			isEnterMain = true;
  		}
  		if (isEnterMain) {
  			BaseApp.core.getActivitiesManager().showMainActivity(this);
            BaseApp.instance.sendBroadcast(BaseConsts.BROADCAST_USER_ENTER, null, null);
            //BaseApp.core.getUserManager().downloadUserProfiles();
            //BaseApp.core.getUserManager().downloadUserAvatar();
            //this.clear();
            this.finish();
  		}
	}

    private boolean verifyUnlockCondition(){
    	return true;
    }

    private void sendUnlockRequest(){
    	/*//
    	try {
        	if (null != mProgressDialog)
        	    mProgressDialog.cancel();
    	} catch (Exception e) {    		
    	}
    	mProgressDialog = ProgressDialog.show(ActivityUserUnlock.this, "", "登录中", true, false);
    	//*/
        (new Thread(){
			@Override  
			public void run(){
				BaseApp.instance.sendBroadcast(BaseConsts.BROADCAST_USER_LOGIN, null, null);
				//syncSendUnlockRequest();
	        }
        }).start();    	
    }

    private void syncSendUnlockRequest() {
        base.app.user.UserLogin action = new base.app.user.UserLogin();
	    UserLogin.LoginResult loginret = action.doLogin("account", "password");
	    if (null != loginret) {
			if (loginret.isSuccess) {
				//登录成功
				if (null != loginret.account) {
					BaseApp.core.getUserManager().setLoginedActiveUserAccount(loginret.account);
					BaseApp.instance.sendBroadcast(BaseConsts.BROADCAST_USER_LOGIN, null, null);
				}
			} else {
				// 显示错误信息
				if (null != mBaseMsgHandler) {
					android.os.Message msg = new android.os.Message();
					msg.what = BaseConsts.MSG_SHOW_ERROR;
					msg.obj = loginret.errorMsg;
					mBaseMsgHandler.sendMessage(msg);
				}
			}
	    } else { 	
	    }    	
    }
    
	private void initViews() {
		android.view.View view = this.findViewById(R.id.btn_action);
		if (null != view) {
			UtilsText.updateViewText(view, "登录");	
			view.setOnClickListener(new android.view.View.OnClickListener(){
				@Override
				public void onClick(android.view.View v) {
					if (verifyUnlockCondition()) {
						sendUnlockRequest();						 
					}
				}
			});		
		}
		view = this.findViewById(R.id.btnCallChangeUser);
		if (null != view) {
			view.setOnClickListener(new android.view.View.OnClickListener(){
				@Override
				public void onClick(android.view.View v) {
					redirectToActivity(ActivityUserLogin.class);	
					ActivityUserUnlock.this.finish();
				}
			});		
		}
		view = this.findViewById(R.id.btnCallForgetPwd);
		if (null != view) {
			view.setOnClickListener(new android.view.View.OnClickListener(){
				@Override
				public void onClick(android.view.View v) {
					redirectToActivity(ActivityUserForgetPwd.class);
				}
			});
		}
	}
}
