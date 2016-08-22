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
public class WifiAp {
	
	private WifiManager mWifiManager = null;
	private android.content.Context mContext = null;
	
	public WifiAp(Context context) {  
	    mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}
    
   public static final int TYPE_NO_PASSWD = 0x11;  
   public static final int TYPE_WEP = 0x12;  
   public static final int TYPE_WPA = 0x13;  

    private String mSSID = "";  
    private String mPasswd = "";  
	public void startWifiAp(String ssid, String passwd) {  
	    mSSID = ssid;  
	    mPasswd = passwd;  
	          
	    if (mWifiManager.isWifiEnabled()) {  
	        mWifiManager.setWifiEnabled(false);  
	    }   
	 /*         
	    startWifiAp();  
	          
	        MyTimerCheck timerCheck = new MyTimerCheck() {  
	              
	            @Override  
	            public void doTimerCheckWork() {  
	                // TODO Auto-generated method stub  
	                  
	                if (isWifiApEnabled(mWifiManager)) {  
	                    Log.v(TAG, "Wifi enabled success!");  
	                    this.exit();  
	                } else {  
	                    Log.v(TAG, "Wifi enabled failed!");  
	                }  
	            }  
	  
	            @Override  
	            public void doTimeOutWork() {  
	                // TODO Auto-generated method stub  
	                this.exit();  
	            }  
	        };  
	        timerCheck.start(15, 1000);  
	//*/
	}

	public void startWifiAp() {  
		java.lang.reflect.Method method1 = null;  
	    try {  
	        method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",  
	                    WifiConfiguration.class, boolean.class);  
	        WifiConfiguration netConfig = new WifiConfiguration();  
	  
	        netConfig.SSID = mSSID;  
	        netConfig.preSharedKey = mPasswd;  
	  
	        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
	        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);  
	        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
	        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
	        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
	        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);  
	        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
	        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
	        method1.invoke(mWifiManager, netConfig, true);  
	  
	    } catch (IllegalArgumentException e) {  
	        e.printStackTrace();  
	    } catch (IllegalAccessException e) {  
	        e.printStackTrace();  
	    } catch (java.lang.reflect.InvocationTargetException e) {  
	        e.printStackTrace();  
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (NoSuchMethodException e) {  
	        e.printStackTrace();  
	    }  
	}  

    private WifiConfiguration IsExsits(String SSID) {  
        java.util.List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
        for (WifiConfiguration existingConfig : existingConfigs) {  
            if (existingConfig.SSID.equals("\"" + SSID + "\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {  
                return existingConfig;  
            }  
        }  
        return null;  
    }  
      
	 public WifiConfiguration createWifiInfo(String SSID, String password, int type) {  
         
	        //Log.v(TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);  
	          
	        WifiConfiguration config = new WifiConfiguration();  
	        config.allowedAuthAlgorithms.clear();  
	        config.allowedGroupCiphers.clear();  
	        config.allowedKeyManagement.clear();  
	        config.allowedPairwiseCiphers.clear();  
	        config.allowedProtocols.clear();  
	        config.SSID = "\"" + SSID + "\"";  
	  
	        WifiConfiguration tempConfig = this.IsExsits(SSID);  
	        if (tempConfig != null) {  
	            mWifiManager.removeNetwork(tempConfig.networkId);  
	        }  
	          
	        // 分为三种情况：1没有密码2用wep加密3用wpa加密  
	        if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS  
	            config.wepKeys[0] = "";  
	            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
	            config.wepTxKeyIndex = 0;  
	              
	        } else if (type == TYPE_WEP) {  //  WIFICIPHER_WEP   
	            config.hiddenSSID = true;  
	            config.wepKeys[0] = "\"" + password + "\"";  
	            config.allowedAuthAlgorithms  
	                    .set(WifiConfiguration.AuthAlgorithm.SHARED);  
	            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
	            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
	            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
	            config.allowedGroupCiphers  
	                    .set(WifiConfiguration.GroupCipher.WEP104);  
	            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
	            config.wepTxKeyIndex = 0;  
	        } else if (type == TYPE_WPA) {   // WIFICIPHER_WPA  
	            config.preSharedKey = "\"" + password + "\"";  
	            config.hiddenSSID = true;  
	            config.allowedAuthAlgorithms  
	                    .set(WifiConfiguration.AuthAlgorithm.OPEN);  
	            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
	            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
	            config.allowedPairwiseCiphers  
	                    .set(WifiConfiguration.PairwiseCipher.TKIP);  
	            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
	            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
	            config.allowedPairwiseCiphers  
	                    .set(WifiConfiguration.PairwiseCipher.CCMP);  
	            config.status = WifiConfiguration.Status.ENABLED;  
	        }   
	          
	        return config;  
	    }  
	 
    private static boolean isWifiApEnabled(WifiManager wifiManager) {  
        try {  
        	java.lang.reflect.Method method = wifiManager.getClass().getMethod("isWifiApEnabled");  
            method.setAccessible(true);  
            return (Boolean) method.invoke(wifiManager);  
  
        } catch (NoSuchMethodException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return false;  
    }  
  
	 private static void closeWifiAp(WifiManager wifiManager) {  
	        if (isWifiApEnabled(wifiManager)) {  
	            try {  
	            	java.lang.reflect.Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");  
	                method.setAccessible(true);  
	  
	                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);  
	  
	                java.lang.reflect.Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);  
	                method2.invoke(wifiManager, config, false);  
	            } catch (NoSuchMethodException e) {  
	                e.printStackTrace();  
	            } catch (IllegalArgumentException e) {  
	                e.printStackTrace();  
	            } catch (IllegalAccessException e) {  
	                e.printStackTrace();  
	            } catch (java.lang.reflect.InvocationTargetException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
	  
}
