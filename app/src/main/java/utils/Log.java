package utils;

import base.app.BaseApp;

import android.text.TextUtils;

public final class Log {

    /**
     * form util.Log;
     */
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int OFF = 10;
    public static int logLevel = VERBOSE;
    private static final String LOGTAG = BaseApp.EnglishName;

    private Log() {
    }

    public static int v(String msg) {
    	if(TextUtils.isEmpty(msg))
    	{
    		return v(LOGTAG, msg);
    	}
    	
    	return 0;
    }
    
    public static int v(String tag, String msg) {
    	if(logLevel<=VERBOSE)
    		return android.util.Log.v(tag, msg);
    	else
    		return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
       	if(logLevel<=VERBOSE)
       		return android.util.Log.v(tag, msg, tr);
    	else
    		return 0;
    }

    public static int d(String msg) {
    	return d(LOGTAG, msg);
    }

    public static int d(String tag, String msg) {
       	if(logLevel<=DEBUG)
       		return android.util.Log.d(tag, msg);
    	else
    		return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
       	if(logLevel<=DEBUG)
       		return android.util.Log.d(tag, msg, tr);
    	else
    		return 0;
    }

    public static int i(String msg) {
    	return i(LOGTAG, msg);
    }

    public static int i(String tag, String msg) {
      	if(logLevel<=INFO)
      		return android.util.Log.i(tag, msg);
    	else
    		return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
      	if(logLevel<=INFO)
      		return android.util.Log.i(tag, msg, tr);
    	else
    		return 0;
    }

    public static int w(String msg) {
    	return w(LOGTAG, msg);
    }

    public static int w(String tag, String msg) {
      	if(logLevel<=WARN)
      		return android.util.Log.w(tag, msg);
    	else
    		return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
      	if(logLevel<=WARN)
      		return android.util.Log.w(tag, msg, tr);
    	else
    		return 0;
    }

    public static boolean isLoggable(String tag, int level){
    	return android.util.Log.isLoggable(tag, level);
    }

    public static int w(String tag, Throwable tr) {
      	if(logLevel<=WARN)
      		return android.util.Log.w(tag, tr);
    	else
    		return 0;
    }
    
    public static int e(Exception ex) {
    	if(ex == null)
    		return -1;
    	
    	String msg = ex.getClass().getName() + ":" + ex.getMessage();
    	
    	return e(LOGTAG, msg);
    }

    public static int e(String msg) {
    	if(!TextUtils.isEmpty(msg))
    	{
    		return e(LOGTAG, msg);
    	}
    	
    	return e(LOGTAG, "Error Msg is null");
    }

    public static int e(String tag, String msg) {
      	if(logLevel<=ERROR)
      		return android.util.Log.e(tag, msg);
    	else
    		return 0;
   }

    public static int e(String tag, String msg, Throwable tr) {
      	if(logLevel<=ERROR)
      		return android.util.Log.e(tag, msg, tr);
    	else
    		return 0;
    }
    
    public static int e(Class c, String msg, Throwable tr) {
    	
      	if(logLevel<=ERROR)
      		return android.util.Log.e(c.getSimpleName(), msg, tr);
    	else
    		return 0;
    }

    public static int wtf(String msg) {
    	return wtf(LOGTAG, msg);
    }
    
    public static int wtf(String tag, String msg) {
      	if(logLevel<=ASSERT)
      		return android.util.Log.wtf(tag, msg);
    	else
    		return 0;
    }

   public static int wtf(String tag, Throwable tr) {
	   return wtf(tag, tr.getMessage(), tr);
   }

    public static int wtf(String tag, String msg, Throwable tr) {
      	if(logLevel<=ASSERT)
      		return android.util.Log.wtf(tag, msg, tr);
    	else
    		return 0;
    }

    public static String getStackTraceString(Throwable tr) {
   		return android.util.Log.getStackTraceString(tr);
     }

    public static int println(int priority, String tag, String msg) {
    	if(logLevel<=priority)
    		return android.util.Log.println(priority, tag, msg);
    	else
    		return 0;
    }
}
