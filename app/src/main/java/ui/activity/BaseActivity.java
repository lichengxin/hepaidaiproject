package ui.activity;

import utils.*;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import base.app.*;


/*//
 * 所有  Activity 应当从  BaseActivity 或者 BaseFragmentActivity 继承下来
 * 
//*/
public abstract class BaseActivity extends android.app.Activity {

	private static String LOGTAG = BaseActivity.class.getSimpleName();
	private boolean mIsCreated = false;

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		BaseApp.core.getActivitiesManager().addActivity(this);
		mIsCreated = true;
	}

	@Override
	protected void onDestroy() {
		Log.d(LOGTAG + " " + BaseActivity.this.getClass().getSimpleName() + " onDestroy");
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
		if (null != mProgressDialog) {
			mProgressDialog.cancel();
			mProgressDialog = null;
		}
		super.onDestroy();
		BaseApp.core.getActivitiesManager().finishActivity(this);
		System.gc();
	}
	protected android.content.BroadcastReceiver mActivityBroadcastReceiver = null;
	protected String mActivityType = null;
	protected ProgressDialog mProgressDialog = null;	 

	public void showProgressDialog1(String message) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(BaseActivity.this);
		}
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}

	public void updateProgressDialog (boolean isShow, String message) {
		if (isShow) {
			if(mProgressDialog == null) {
				mProgressDialog = new ProgressDialog(this);
			}
			mProgressDialog.setMessage(message);
			mProgressDialog.show();
		} else {
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		}
	}
	
	public void cancelProgressDialog1() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

    public void cancelProgressDialog2(){
        try {
            if (null != mProgressDialog) {
            	mProgressDialog.cancel();	            	
            }
    	} catch (Exception e) {    		
    	}	    	
    }
    
    public void showProgressDialog2(Context context, String msg) {
    	cancelProgressDialog2();
    	mProgressDialog = ProgressDialog.show(context, "", msg, true, false);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		//BaseApp.core.getAnalysisManager().addActivityOnStartEvent(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//BaseApp.core.getAnalysisManager().addActivityOnStopEvent(this);
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mIsCreated)
		{
			mIsCreated = false;
			//initParams();
			initTitleParam();
		}
		//BaseApp.core.getAnalysisManager().addActivityOnResumeEvent(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//BaseApp.core.getAnalysisManager().addActivityOnPauseEvent(this);
	}

	public static final String APPINSTANCE= "instancestate";
	public static final String APPINSTANCE_DATA = "data";
	public class InstanceStateData {
		
	}
	
    public InstanceStateData mInstanceStateData = null;
    
//    @Override
//    protected void onSaveInstanceState(android.os.Bundle savedInstanceState){
//    	super.onSaveInstanceState(savedInstanceState);
//    	//doSaveInstanceState();
//	}
    
    protected void doSaveInstanceState() {
    	//SharedPreferences pref = BaseApp.instance.getSharedPreferences(APPINSTANCE, android.app.Activity.MODE_PRIVATE);
    	//if (null != pref) {
    	   	if (null != mInstanceStateData) {
    	   		//String data = (new Gson()).toJson(mInstanceStateData);
    	   		//Log.d(LOGTAG + " onSaveInstanceState "  + data);
    	   		//pref.edit().putString(APPINSTANCE_DATA, data).commit();
    		} else {
    			//pref.edit().putString(APPINSTANCE_DATA, "").commit();
    		}
    	   	//}
    }

	public synchronized static boolean isFastClick() {
		return isFastClick(500);
	}

	private static long mlastClickTime;
	public synchronized static boolean isFastClick(int timegap) {
		long time = System.currentTimeMillis();
		if (time - mlastClickTime < timegap) {
			return true;
		}
		mlastClickTime = time;
		return false;
	}
	
//    protected String getStoreInstanceStateData() {
//    	SharedPreferences pref = BaseApp.instance.getSharedPreferences(APPINSTANCE, android.app.Activity.MODE_PRIVATE);
//        if (null != pref) {
//            return pref.getString(APPINSTANCE_DATA, null);
//        }
//    	return null;
//    }
    
	public void redirectToActivity (Class destActivityClass) {
		Intent intent = new Intent();
		intent.setClass(this, destActivityClass);
		this.startActivity(intent);
	}	

	public void redirectToActivity (Class destActivityClass, String param) {
		Intent intent = new Intent();
		intent.setClass(this, destActivityClass);
		intent.putExtra(BaseConsts.EXTRA_ACTIVITY_PARAM, param);
		this.startActivity(intent);
	}
	
	public void redirectToActivity (Class destActivityClass, String title, String param) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(this, destActivityClass);
		intent.putExtra(BaseConsts.EXTRA_ACTIVITY_TITLE, title);
		intent.putExtra(BaseConsts.EXTRA_ACTIVITY_PARAM, param);
		this.startActivity(intent);
	}	

	public void redirectToActivity (Class destActivityClass, int requestCode) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(this, destActivityClass);
		this.startActivityForResult(intent, requestCode);
	}	

	protected String idStr(int id) {
		return getResources().getString(id);
	}

	protected int idColor(int id) {
		return getResources().getColor(id);
	}

	protected float idDimen(int id) {
		return getResources().getDimension(id);
	}	
	
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

	protected void initTitle(String title) {
		/*//
		TextView view = (TextView) findViewById(R.id.text_title);
		if (null != view) {
			view.setText(StrUtils.strNoNull(title));
			view.setFocusable(true);
			view.requestFocus();
			view.setFocusableInTouchMode(true);
		}
		//*/
	}
	
//	protected void initBackButton() {
//		View button = findViewById(R.id.btn_back);
//		if (null != button) {
//			button.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					BaseActivity.this.finish();
//				}
//			});
//			//button.setFocusable(true);
//			//button.requestFocus();
//			//button.setFocusableInTouchMode(true);
//		}
//	}

	protected void initMenuRightButton(String title) {
		/*//
		View view = findViewById(R.id.btn_menu_right);
		if (null == view) 
			return;
		if (TextUtils.isEmpty(title)) {
			view.setVisibility(View.GONE);
		} else {
			view.setOnClickListener(this);
			view.setVisibility(View.VISIBLE);
			try {
				if (Button.class.getSimpleName().equals(view.getClass().getSimpleName())) {
					((Button) view).setText(title);		
					return;
				}
				if (TextView.class.getSimpleName().equals(view.getClass().getSimpleName())) {
					((TextView) view).setText(title);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//*/
	}	
	
	protected void initTitleParam() {
		/*//
		String title = getIntent().getStringExtra(BaseConsts.EXTRA_TITLE);
		if (!TextUtils.isEmpty(title)) {
			initTitle(title);
		}
		//*/
	}
	
	protected void backButtonOnClick() {
		BaseActivity.this.finish();
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


}
