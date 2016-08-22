package base.app;

import android.content.pm.PackageManager.NameNotFoundException;

public class Version extends base.BaseObj {
	
	public static int getVersionCode(String apkPackage)
	{
	    try
	    {
	    	if (android.text.TextUtils.isEmpty(apkPackage)) {
		        return BaseApp.instance.getPackageManager().getPackageInfo(BaseApp.instance.getPackageName(), 0).versionCode;	    		
	    	} else {
		        return BaseApp.instance.getPackageManager().getPackageInfo(apkPackage, 0).versionCode;	    		
	    	}
	    } catch (NameNotFoundException e)
	    {
	        e.printStackTrace();
	    }
	    return 0;
	}

	public static String getVersionName(String apkPackage)
	{
	    try
	    {
	    	if (android.text.TextUtils.isEmpty(apkPackage)) {
		    	return BaseApp.instance.getPackageManager().getPackageInfo(BaseApp.instance.getPackageName(), 0).versionName;
	    	} else {
		    	return BaseApp.instance.getPackageManager().getPackageInfo(apkPackage, 0).versionName;
	    	}
	    } catch (NameNotFoundException e)
	    {
	        e.printStackTrace();
	    }
	    return null;
	}
}
