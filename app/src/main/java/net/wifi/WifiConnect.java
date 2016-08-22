package net.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.*;

// 创建 wifi 热点
public class WifiConnect {
	private WifiManager mWifiManager;  
    private WifiInfo mWifiInfo;
    private Context mContext;
    private android.net.wifi.WifiManager.WifiLock mWifiLock;
    // 扫描出的网络连接列表
    private java.util.List<WifiConfiguration> mWifiConfigurations;

    public static final int WIFI_CONNECTED = 0x01;  
    public static final int WIFI_CONNECT_FAILED = 0x02;  
    public static final int WIFI_CONNECTING = 0x03;  
    
    public int isWifiContected(Context context) {  
        ConnectivityManager connectivityManager = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo wifiNetworkInfo = connectivityManager  
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
          
        //Log.v(TAG, "isConnectedOrConnecting = " + wifiNetworkInfo.isConnectedOrConnecting());  
        //Log.d(TAG, "wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());  
        if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR  
                || wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING) {  
            return WIFI_CONNECTING;  
        } else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED) {  
            return WIFI_CONNECTED;  
        } else {  
        	//Log.d(TAG, "getDetailedState() == " + wifiNetworkInfo.getDetailedState());  
            return WIFI_CONNECT_FAILED;  
        }  
    }  
      
	 private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {  
		  
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	            // TODO Auto-generated method stub  
	            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {  
	                //Log.d(TAG, "RSSI changed");  
	                  
	                //有可能是正在获取，或者已经获取了  
	            	//Log.d(TAG, " intent is " + WifiManager.RSSI_CHANGED_ACTION);  
	                  
	                if (isWifiContected(mContext) == WIFI_CONNECTED) {  
	                    //stopTimer();  
	                	//onNotifyWifiConnected();  
	                	//unRegister();  
	                } else if (isWifiContected(mContext) == WIFI_CONNECT_FAILED) {  
	                	//stopTimer();  
	                	//closeWifi();  
	                	//onNotifyWifiConnectFailed();  
	                	//unRegister();  
	                } else if (isWifiContected(mContext) == WIFI_CONNECTING) {  
	                      
	                }  
	            }  
	        }  
	    };  
	      
}
