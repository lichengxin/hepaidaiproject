package net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class UtilsNet {

	/**
	 * 判断是否有网络连接
	 */
	public static boolean isNetConnected(Context context){
		try{
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(null!=connectivity){
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if(null!=info && info.isConnected()){
					if(NetworkInfo.State.CONNECTED == info.getState()){
						return true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	// 网络控制
	public static void controlNetWork(Context context, boolean enabled)
	{
		controlWifi(context, enabled);
		controlMobileNetWork(context, enabled);
	}

	// wifi控制
	public static void controlWifi(Context context, boolean enabled)
	{
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
		{
			wifiManager.setWifiEnabled(enabled);
		} else
		{
			wifiManager.setWifiEnabled(enabled);
		}
	}

	// network控制
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void controlMobileNetWork(Context context, boolean enabled)
	{
		try
		{
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final java.lang.reflect.Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final java.lang.reflect.Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
		} catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
