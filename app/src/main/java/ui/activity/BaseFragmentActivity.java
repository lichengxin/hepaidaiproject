package ui.activity;

import utils.Log;
import base.app.BaseApp;
import base.app.BaseConsts;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.Toast;

/*//
 * 所有  Activity 应当从  BaseActivity 或者 BaseFragmentActivity 继承下来
 * 
//*/
public abstract class BaseFragmentActivity extends FragmentActivity {

	private static String LOGTAG = BaseFragmentActivity.class.getSimpleName();
    protected FragmentManager mFragmentManager;
    
    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BaseApp.core.getActivitiesManager().addActivity(this);
        if (null == savedInstanceState) {
            //tabsBuyBtn.setChecked(true);
        }
        mFragmentManager = getSupportFragmentManager();
        //group.setOnCheckedChangeListener(this);

        //changeFragment(new TabsBuyFragment(), false);
    }

	@Override
	protected void onDestroy() {
		Log.d(LOGTAG + " " + BaseFragmentActivity.this.getClass().getSimpleName() + " onDestroy");
		try {
		    if (null != mActivityBroadcastReceiver) {
		    	unregisterReceiver(mActivityBroadcastReceiver);
		    	mActivityBroadcastReceiver = null;
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null !=mBaseMsgHandler) {
			mBaseMsgHandler = null;
		}
		super.onDestroy();
		//BaseApp.showToast("on Destroy:" + this.getClass().getSimpleName());
		BaseApp.core.getActivitiesManager().finishActivity(this);
	}
	protected android.content.BroadcastReceiver mActivityBroadcastReceiver = null;

	public void callCloseActivity(){
		 if (null != mBaseMsgHandler)
			 mBaseMsgHandler.sendEmptyMessage(BaseConsts.MSG_ACTIVITY_CLOSE);
	}

	protected Handler mBaseMsgHandler = null;	
	public Handler getBaseMsgHandler() {
		return mBaseMsgHandler;
	}

	public void sendMessage(int msg, Object obj){
		createMessageLoopHandler();
		if (null != mBaseMsgHandler) {
			Message msgobj = mBaseMsgHandler.obtainMessage();
			msgobj.obj = obj;  
			msgobj.what = msg;  
			msgobj.sendToTarget();
		}
	}
	
	protected void createMessageLoopHandler(){
		if (null != mBaseMsgHandler)
			return;
		Looper loop = Looper.getMainLooper();  
		mBaseMsgHandler = new Handler(loop){  
		    @Override  
		    public void handleMessage(Message msg) {  
		        super.handleMessage(msg);  
		        handleActivityMessage(msg);
		    }  
		};  		
	}

	protected boolean handleActivityMessage(Message msg){
        switch (msg.what) {  
            case BaseConsts.MSG_TOAST:        	  
      	        Toast.makeText(this, (String)msg.obj, Toast.LENGTH_LONG).show();
      	        return true;
             case BaseConsts.MSG_ACTIVITY_CLOSE:
        	     finish();
       	        return true;
        }
		return false;
	}

	@Override
	public void onBackPressed() {
		// 按下回退键
		super.onBackPressed(); 
	}
	
	protected abstract int getLayoutId();
    protected abstract int getFragmentId();

    protected BaseFragment mSelectedFragment = null;
    public void setSelectedFragment(BaseFragment fragment) {
    	mSelectedFragment = fragment;
    }
    
    public void changeFragment(BaseFragment fragment, boolean isAddToBackStack) {
        
    	int fragmentId = getFragmentId();
    	if (0 != fragmentId) {
            Bundle bundle = new Bundle();
            //bundle.putString("stockcode",stockcode);
            //if(null !=mUserHoldData){
            //    bundle.putSerializable("userHoldData", this.mUserHoldData);
            //}
            fragment.setArguments(bundle);
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(fragmentId, fragment);
            if (isAddToBackStack) {
            	// 也就是将Fragment加入到回退栈
                transaction.addToBackStack(null);
            }
            //fragment事务，你可以应用动画。在commit()之前调用setTransition()
            //transaction.setTransition();
            transaction.commit();
            //**transaction.commitAllowingStateLoss();
    	}
    }

}
