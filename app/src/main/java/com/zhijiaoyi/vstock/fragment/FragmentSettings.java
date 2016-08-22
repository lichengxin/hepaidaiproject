package com.zhijiaoyi.vstock.fragment;

import ui.activity.BaseFragment;
import ui.widget.UtilsListItem;

import com.app.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import base.app.BaseApp;

import com.zhijiaoyi.vstock.activity.ActivityListItem;

public class FragmentSettings extends BaseFragment implements OnClickListener {

	private static final String LOGTAG = FragmentSettings.class.getSimpleName();

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);		
		initViews();
		return mView;
	}

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_fragment_settings;
    }

	protected void initViews() {
		View view = null;
		view = UtilsListItem.initListItemText(mView, R.id.item_myinfo, "我的信息", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_myaccount, "我的账户", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_protocol, "相关协议", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_message, "消息通知", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_myservice, "我的客服", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_feedback, "意见反馈", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_settings, "设置", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_logout, "登出", "", 0);
		if (null != view ) view.setOnClickListener(this);
		view = UtilsListItem.initListItemText(mView, R.id.item_exit, "退出应用", "", 0);
		if (null != view ) view.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {	
		switch (v.getId()) {
	        case R.id.item_myinfo:
	            //com.base.sys.SrvAlarm.setAlarm(getActivity());
	        	//redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_MyInfo);
	        	break;
	        case R.id.item_myaccount:
	        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_MyAccount);
	        	break;
	        case R.id.item_protocol:
	        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_Protocol);
	        	break;
	        case R.id.item_message:
	        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_Message);
	        	break;
	        case R.id.item_myservice:
	        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_MyService);
	        	break;
	        case R.id.item_feedback:
	        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_Feedback);
	        	break;
	        case R.id.item_settings:
	        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_Config);
	        	break;
	        case R.id.item_logout:
	        	doLogout();
	        	this.getActivity().finish();
	        	break;
	        case R.id.item_exit:
	        	BaseApp.quitApp();
	        	break;
		}


	}
	
	private void doLogout() {
		BaseApp.core.getUserManager().activeUserLogout();
    	//redirectToActivity(ActivityUserLogin.class);
		//this.getActivity.finish();
	}
}
