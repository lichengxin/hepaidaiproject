package base.app;

import java.util.Stack;

import ui.activity.*;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.zhijiaoyi.vstock.activity.*;
import com.app.R;
import com.hepaidai.activity.*;
import com.app.activity.ActivityAppAbout;
import com.app.activity.ActivityAppGuide;
import com.app.activity.ActivityUserFeedback;
import com.app.activity.ActivityUserForgetPwd;
import com.app.activity.ActivityUserLogin;
import com.app.activity.ActivityUserRegister;
import com.app.activity.ActivityUserUnlock;

/*//
 * ManagerActivities 管理 所有 Activity
 * 的生存周期 及 访问
//*/
public class ManagerActivities extends AbstractManager{

	private static final String LOGTAG = ManagerActivities.class.getSimpleName();
	
	private static Stack<Activity> mActivityStack;
	private static Stack<Activity> mDialogStack;
	
	private static ManagerActivities mInstance;
	
	private ManagerActivities(ManagerFactory core) {
		super(core);
	}
	/**
	 * 单一实例
	 */
	public static ManagerActivities getActivitiesManager(ManagerFactory core) {
		if(mInstance==null){
			mInstance=new ManagerActivities(core);
		}
		return mInstance;
	}
	/**
	 * 添加Activity到堆�?
	 */
	public void addActivity(Activity activity){
		if(mActivityStack==null){
			mActivityStack=new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}
	
	public void addDialogActivity(Activity activity){
		if(mDialogStack==null){
			mDialogStack=new Stack<Activity>();
		}
		mDialogStack.add(activity);
	}
	/**
	 * 获取当前Activity（堆栈中�?后一个压入的�?
	 */
	public Activity currentActivity(){
		Activity activity=mActivityStack.lastElement();
		return activity;
	}
	/**
	 * 结束当前Activity（堆栈中�?后一个压入的�?
	 */
	public void finishActivity(){
		Activity activity=mActivityStack.lastElement();
		finishActivity(activity);
	}
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			mActivityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : mActivityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	/**
	 * 结束�?有Activity
	 */
	public void finishAllActivity(){
		for (int i = 0, size = mActivityStack.size(); i < size; i++){
            if (null != mActivityStack.get(i)){
            	mActivityStack.get(i).finish();
            }
	    }
		mActivityStack.clear();
	}
	/**
	 * �?出应用程�?
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}
	}

	public void showActivity(Context context, String activityClassName) {
		if (BaseConsts.ACTIVITY_MAIN.equals(activityClassName)) {
			showMainActivity(context);
			return;
		}
	}

	public void redirectToActivity (Context context, Class destActivityClass) {
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(context, destActivityClass);
		context.startActivity(intent);
	}	
	
	public void showUserUnlockActivity(Context context) {
		redirectToActivity(context, ActivityUserUnlock.class);		
	}

	public void showUserLoginActivity(Context context) {
		redirectToActivity(context, ActivityUserLogin.class);
	}

	public void showUserRegisterActivity(Context context) {
		redirectToActivity(context, ActivityUserRegister.class);
	}

	public void showUserForgetPwdActivity(Context context) {
		// redirectToActivity(context, UserInfoActivity.class);
		// redirectToActivity(context, UserPwdChangeActivity.class);
		redirectToActivity(context, ActivityUserForgetPwd.class);
	}
	
	public void showUserFeedBackActivity(Context context) {
		redirectToActivity(context, ActivityUserFeedback.class);
	}

	public void showAppAboutActivity(Context context) {
		redirectToActivity(context, ActivityAppAbout.class);
	}

	public void showAppGuideActivity(Context context) {
		redirectToActivity(context, ActivityAppGuide.class);
	}
	
	public void showMainActivity(Context context) {
		android.content.Intent intent = new android.content.Intent(
				android.content.Intent.ACTION_MAIN);
		// intent.setClass(getApplication(), MainActivity.class); 
		//intent.setClass(context, MainActivity.class);
		
		intent.setClass(context, com.hepaidai.activity.MainActivity.class);
		
		intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setFlags(android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		// intent.setFlags(android.content.Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		context.startActivity(intent);
		// BaseApp.instance.sendBroadcast(GlobleConsts.BROADCAST_USER_ENTER,
		// null);
		// overridePendingTransition must be called AFTER finish() or
		// startActivity, or it won't work.
		// overridePendingTransition(R.anim.activity_in, R.anim.splash_out);
		//}
	}

	public static int getLayoutId_Activity_App_Splash() {
		return R.layout.pkg_app_activity_splash;
	}

	public static int getLayoutId_Activity_App_About() {
	    return R.layout.pkg_app_activity_about;
	}

	public static int getLayoutId_Dialog_App_Update() {
	    return R.layout.pkg_app_dialog_update;
	}
	
	public static int getLayoutId_Activity_User_FeedBack() {
	    return R.layout.pkg_app_activity_user_feedback;
	}

	public static int getLayoutId_Activity_User_ForgetPwd() {
	    return R.layout.pkg_app_activity_user_forgetpwd;
	}

	public static int getLayoutId_Activity_User_Login() {
	    return R.layout.pkg_app_activity_user_login;
	}

	public static int getLayoutId_Activity_User_Register() {
	    return R.layout.pkg_app_activity_user_reg;
	}

	public static int getLayoutId_Activity_User_Unlock() {
	    return R.layout.pkg_app_activity_user_unlock;
	}
}
