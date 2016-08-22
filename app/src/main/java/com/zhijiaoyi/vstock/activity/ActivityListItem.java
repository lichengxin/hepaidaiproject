package com.zhijiaoyi.vstock.activity;

import ui.activity.BaseActivity;
import ui.widget.UtilsListItem;
import android.view.View;
import android.view.View.OnClickListener;
import base.app.BaseConsts;

import com.app.R;
import com.zhijiaoyi.vstock.*;

public class ActivityListItem extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityType = getIntent().getStringExtra(BaseConsts.EXTRA_ACTIVITY_PARAM);
		initViews();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	protected void initViews() {
		View view = null;		
		if (null != mActivityType) {
			if (mActivityType.equals(Consts.TagActivity_Setting_MyInfo)) {
		        setContentView(R.layout.pkg_vstock_activity_myinfo);	

				view = UtilsListItem.initListItemText(this, R.id.item_mymobile, "手机号", "", 0);
				if (null != view ) view.setOnClickListener(this);
				view = UtilsListItem.initListItemText(this, R.id.item_myaccount, "操盘账号", "", 0);
				if (null != view ) view.setOnClickListener(this);
				view = UtilsListItem.initListItemText(this, R.id.item_registertime, "注册时间", "", 0);
				if (null != view ) view.setOnClickListener(this);
			}
			if (mActivityType.equals(Consts.TagActivity_Setting_MyAccount)) {
		        setContentView(R.layout.pkg_vstock_activity_myaccount);	

				view = UtilsListItem.initListItemText(this, R.id.item_myaccount1, "积分账户", "", 0);
				if (null != view ) view.setOnClickListener(this);

				view = UtilsListItem.initListItemText(this, R.id.item_myaccount2, "实盘账户", "", 0);
				if (null != view ) view.setOnClickListener(this);
			}
			if (mActivityType.equals(Consts.TagActivity_Setting_Protocol)) {
		        setContentView(R.layout.pkg_vstock_activity_protocol);	

				view = UtilsListItem.initListItemText(this, R.id.item_protocol1, "协议1", "", 0);
				if (null != view ) view.setOnClickListener(this);

				view = UtilsListItem.initListItemText(this, R.id.item_protocol2, "协议2", "", 0);
				if (null != view ) view.setOnClickListener(this);		        
			}
			if (mActivityType.equals(Consts.TagActivity_Setting_Message)) {
		        setContentView(R.layout.pkg_vstock_activity_viewmsgs);	

				view = UtilsListItem.initListItemText(this, R.id.item_msg1, "消息1", "", 0);
				if (null != view ) view.setOnClickListener(this);

				view = UtilsListItem.initListItemText(this, R.id.item_msg2, "消息2", "", 0);
				if (null != view ) view.setOnClickListener(this);	
			}
			if (mActivityType.equals(Consts.TagActivity_Setting_Config)) {
		        setContentView(R.layout.pkg_vstock_activity_settings);	


				view = UtilsListItem.initListItemText(this, R.id.item_changepwd, "修改交易密码", "", 0);
				if (null != view ) view.setOnClickListener(this);

				view = UtilsListItem.initListItemText(this, R.id.item_about, "关于", "", 0);
				if (null != view ) view.setOnClickListener(this);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
          case R.id.item_mymobile:
        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_MyInfo);
        	break;
          case R.id.item_myaccount:
        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_MyInfo);
        	break;
          case R.id.item_registertime:
        	redirectToActivity(ActivityListItem.class, com.zhijiaoyi.vstock.Consts.TagActivity_Setting_MyInfo);
        	break;
		}
	}
}
