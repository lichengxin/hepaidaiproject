package com.app.activity;


import ui.activity.BaseActivity;
import base.app.*;

public class ActivityUserRegister extends BaseActivity {

/*//
 * 通用页面 -- 用户模块 -- 注册
 * 
//*/
	private static String LOGTAG = ActivityUserRegister.class.getSimpleName();
	
	@Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(ManagerActivities.getLayoutId_Activity_User_Register());
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
	}
}
