package com.app.activity;

import ui.activity.BaseActivity;
import ui.widget.UtilsText;
import utils.*;

import com.app.R;

import base.app.*;
import base.app.user.UserLogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityUserLogin extends BaseActivity implements OnClickListener {

/*//
 * 通用页面 -- 用户模块 -- 登录
 * 
//*/
	private static String LOGTAG = ActivityUserLogin.class.getSimpleName();
	
	@Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(ManagerActivities.getLayoutId_Activity_User_Login());
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

    private boolean verifyLoginCondition(){
    	return true;
    }

    private void sendLoginRequest(){
    	try {
        	if (null != mProgressDialog) {
        	    mProgressDialog.cancel();
        		mProgressDialog = null;
        	}
    	} catch (Exception e) {    		
    	}
    	mProgressDialog = android.app.ProgressDialog.show(ActivityUserLogin.this, "", "登录中", true, false);    	
        (new Thread(){
			@Override  
			public void run(){
				//BaseApp.instance.sendBroadcast(BaseConsts.BROADCAST_USER_LOGIN, null, null);
				syncSendLoginRequest();
	        }
        }).start();
    }
    
    private void syncSendLoginRequest() {
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
					if (false) {
						doTest();
						return;
					} 						
					if (verifyLoginCondition()) {
						sendLoginRequest();						 
					}
				}
			});		
		}
		view = this.findViewById(R.id.btnCallRegister);
		if (null != view) {
			view.setOnClickListener(new android.view.View.OnClickListener(){
				@Override
				public void onClick(android.view.View v) {
					redirectToActivity(ActivityUserRegister.class);					
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
	
	private void doTest() {
		doTestDialog();
	}
	
	private android.app.AlertDialog mAlertDialog = null;
	private void doTestDialog() {
		/*//
		Context context = this;//getApplicationContext();
        View view = LayoutInflater.from(context).inflate(R.layout.pkg_vstock_dialog_stocklistitem_select, null);
        View btnview = null;
        btnview = view.findViewById(R.id.dlgbtn_1);
        if (null != btnview) btnview.setOnClickListener(this);
        btnview = view.findViewById(R.id.dlgbtn_2);
        if (null != btnview) btnview.setOnClickListener(this);
        btnview = view.findViewById(R.id.dlgbtn_3);
        if (null != btnview) btnview.setOnClickListener(this);
        btnview = view.findViewById(R.id.dlgbtn_4);
        if (null != btnview) btnview.setOnClickListener(this);
        btnview = view.findViewById(R.id.dlgbtn_5);
        if (null != btnview) btnview.setOnClickListener(this);
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);  
        builder.setTitle("智交易");
        builder.setView(view);  
        mAlertDialog = builder.create();
        mAlertDialog.show();
        //*/  
	}

	@Override
	public void onClick(View v) {	
		if (null != mAlertDialog) {
			mAlertDialog.dismiss();
			mAlertDialog = null;
		}
	}

	//  implements android.content.DialogInterface.OnClickListener
	//@Override
	//public void onClick(DialogInterface dialog, int which) {
	//}
}
