package base.app;

public class BaseConsts {
	
	public final static String BROADCAST_RECV_ALARM = "android.intent.action.ALARM_RECEIVER";
	public final static String BROADCAST_SERVICE = "android.intent.action.APP_SERVICE";	
	
	public final static String EXTRA_ACTIVITY_TYPE = "activity.type";
	
	public final static String EXTRA_ACTIVITY_TITLE = "activity.title";
	public final static String EXTRA_ACTIVITY_PARAM = "activity.param";
	public final static String EXTRA_ACTIVITY_PARAM2 = "activity.param2";
	// 用户进入主界面  用户可能不经过 登录直接进入
	public static final String BROADCAST_USER_LOGIN = "com.user.logined"; 
	public static final String BROADCAST_USER_REGISTER = "com.user.register";
	public static final String BROADCAST_USER_LOGOUT = "com.user.logout";
	public static final String BROADCAST_USER_REQUEST_LOGOUT = "com.user.request.logout";
	public static final String BROADCAST_USER_ENTER = "com.user.enter";

	public static final int MSG_RUN = 1000;
	
	public static final int MSG_APP_UPDATE = 1011;

	public static final int MSG_DOWNLOAD = 1021;
	
	public static final int MSG_TOAST = 1101;	
	public static final int MSG_ACTIVITY_REFRESH = 1201;
	public static final int MSG_ACTIVITY_CLOSE = 1501;
	public static final int MSG_SUCCESS = 2000;
	

	public static final int MSG_FRAGMENT_CHANGE = 3001;
	
	public static final int MSG_SHOW_ERROR = 5001;
	
	public static final String ACTIVITY_MAIN = "activity.main";

	private static int RUNMODE_TEST = 1;
	private static int RUNMODE_DEBUG = 2;
	private static int RUNMODE_RELEASE = 3;
	
	//private static int RUNNINGMODE = RUNMODE_TEST;
	//private static int RUNNINGMODE = RUNMODE_DEBUG;
	private static int RUNNINGMODE = RUNMODE_RELEASE;
	
	//public static final String APP_SITE = "hepaidai.com";
	//public static final String APP_SITE = "www.hepaidai.com";
	//public static final String WWW_APP_SITE = "www.hepaidai.com";
	public static final String APP_SITE_DEBUG = "192.168.10.81";
	public static final String APP_SITE_TEST = "www.test.hepaidai.org";
	//public static final String APP_SITE_TEST = "test.hepaidai.org";
	//public static final String APP_SITE_TEST = "192.168.40.131";
	public static final String APP_SITE_RELEASE = "www.hepaidai.com";
	
	public static final String APP_APIROOT_DEBUG = "http://" + APP_SITE_DEBUG;
	public static final String APP_APIROOT_TEST = "http://" + APP_SITE_TEST;
	public static final String APP_APIROOT_RELEASE = "http://" + APP_SITE_RELEASE;
	
	public static String APP_SITE() {
		if (RUNMODE_RELEASE == RUNNINGMODE) {
		    return APP_SITE_RELEASE;
		}
		if (RUNMODE_TEST == RUNNINGMODE) {
			return APP_SITE_TEST;
		}
		if (RUNMODE_DEBUG == RUNNINGMODE) {
		    return APP_SITE_DEBUG;
		}
		return APP_SITE_RELEASE;
	}

	public static String APP_APIROOT() {
		if (RUNMODE_RELEASE == RUNNINGMODE) {
		    return APP_APIROOT_RELEASE;
		}
		if (RUNMODE_TEST == RUNNINGMODE) {
			return APP_APIROOT_TEST;
		}
		if (RUNMODE_DEBUG == RUNNINGMODE) {
		    return APP_APIROOT_DEBUG;
		}
		return APP_APIROOT_RELEASE;
	}
}
