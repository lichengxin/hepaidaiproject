package net;

import base.app.BaseApp;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class Connect {
	
    public static boolean isConnected() { 
	    boolean flag = false;  
	    //得到网络连接信息  
	    ConnectivityManager manager = (ConnectivityManager) BaseApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    //去进行判断网络是否连接 
	    if (null != manager) {
		    if (null != manager.getActiveNetworkInfo()) {  
		        flag = manager.getActiveNetworkInfo().isAvailable();  
		    }  
	    }
	    return flag;  
	}  
    
    /** 
     * 网络已经连接，然后去判断是wifi连接还是GPRS连接 
     * 设置一些自己的逻辑调用 
     */  
    public static boolean isWifiConnected(){ 
    	ConnectivityManager manager = (ConnectivityManager) BaseApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if (null != manager) {
            State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (null != wifi) {
                //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！  
                return (wifi == State.CONNECTED || wifi == State.CONNECTING); 
            }
    	}
    	return false;
    } 
    
    public static boolean isGPRSConnected(){
    	ConnectivityManager manager = (ConnectivityManager) BaseApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if (null != manager) {
            State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (null != gprs) {
                return (gprs == State.CONNECTED || gprs == State.CONNECTING);            	
            }
    	}
    	return false;
    }
}
