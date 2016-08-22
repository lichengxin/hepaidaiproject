package com.app.activity;

import ui.activity.BaseDialogActivity;

import com.app.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import base.app.*;

public class DialogAppUpdate extends BaseDialogActivity {

	private static String LOGTAG = DialogAppUpdate.class.getSimpleName();
	
	@Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置点击空白处 不退出
        setFinishOnTouchOutside(false);  

        setContentView(ManagerActivities.getLayoutId_Dialog_App_Update());
		initViews();
	}

	private void initViews() {
		initButtonPositive();
		initButtonNegative();
		initTextView_Step1();
	}
	
	private boolean initTextView_Step1() {
		Log.d(LOGTAG, " initTextView ");
		if (null == AppUpdate.mCurrentUpdateTask) 
			return false;
		Log.d(LOGTAG, " initTextView 1 ");
		TextView textview = (TextView) this.findViewById(R.id.textview);
		if (null == textview)
			return false;
		if (null != AppUpdate.mCurrentUpdateTask) {
			if (!TextUtils.isEmpty(AppUpdate.mCurrentUpdateTask.newVersionDesc)) {
				textview.setText(AppUpdate.mCurrentUpdateTask.newVersionDesc);
				return true;
			}
		}
		textview.setText("您是否更新合拍贷V" + AppUpdate.mCurrentUpdateTask.newVersion + "版本?");		
		return true;
	}

	private void initView_Step_HintWifi() {
		Log.d(LOGTAG, " initView_Step_HintWifi ");
		if (null == AppUpdate.mCurrentUpdateTask) 
			return;
		TextView textview = (TextView) this.findViewById(R.id.textview);
		if (null != textview)
  		  textview.setText("您在非wifi环境下载,将会产生流浪费用是否继续下载?");
		textview = (TextView) this.findViewById(R.id.title);
		if (null != textview)
   		    textview.setText("网络提醒");
	}
	
	private void initButtonPositive() {
		View button = this.findViewById(R.id.buttonPositive);
		if (null == button)
			return; 
		Log.d(LOGTAG, "Button Positive Set");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(LOGTAG, "Button Positive Click");
				if (null != AppUpdate.mCurrentUpdateTask) {
					if (AppUpdate.UpdateStep_ConfirmUpdate == AppUpdate.mCurrentUpdateTask.updateStep) {
						AppUpdate.mCurrentUpdateTask.setStep(AppUpdate.UpdateStep_ConfirmDownload);
						if (!net.Connect.isWifiConnected()) {
							initView_Step_HintWifi();			
							return;
						}
					}
					if (AppUpdate.UpdateStep_ConfirmDownload == AppUpdate.mCurrentUpdateTask.updateStep) {
						DialogAppUpdate.this.setResult(RESULT_OK, null);
		                DialogAppUpdate.this.finish();
		                
						AppUpdate.mCurrentUpdateTask.setStep(AppUpdate.UpdateStep_DownloadUpdate);
	            		BaseApp.instance.sendAppMessage(BaseConsts.MSG_APP_UPDATE, AppUpdate.mCurrentUpdateTask);
		                return;
					}
				}
				DialogAppUpdate.this.setResult(RESULT_OK, null);
                DialogAppUpdate.this.finish();
			}			
		});
	}
	
	private void initButtonNegative() {
		View button = this.findViewById(R.id.buttonNegative);
		if (null == button)
			return;
		Log.d(LOGTAG, "Button Negative Set");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        //Intent i = new Intent(DialogAppUpdate.this, MainActivity.class);  
				//Bundle b = new Bundle();  
				//b.putString("str", input);  
				//i.putExtras(b);  
				//DialogAppUpdate.this.setResult(RESULT_OK, i);  
				Log.d(LOGTAG, "Button Negative Click");
				if (null != AppUpdate.mCurrentUpdateTask) {
					AppUpdate.mCurrentUpdateTask.setStep(AppUpdate.UpdateStep_End);
				}
				DialogAppUpdate.this.setResult(RESULT_CANCELED, null);
                DialogAppUpdate.this.finish();  
			}			
		});
	}
}
