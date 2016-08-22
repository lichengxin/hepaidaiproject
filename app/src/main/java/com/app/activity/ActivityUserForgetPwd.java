package com.app.activity;


import ui.activity.BaseActivity;
import base.app.*;

/*//
 * 通用页面 -- 用户模块 -- 忘记密码
 * 
//*/
public class ActivityUserForgetPwd extends BaseActivity {

	private static String LOGTAG = ActivityUserForgetPwd.class.getSimpleName();
	
	@Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ManagerActivities.getLayoutId_Activity_User_ForgetPwd());
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
