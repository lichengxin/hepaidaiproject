package com.hepaidai;

import net.HttpActDownloader;

import org.json.JSONObject;

import utils.Log;

import android.text.TextUtils;
import base.app.BaseApp;

public class AppUpdate extends base.BaseObj {

	private static final String LOGTAG = AppUpdate.class.getSimpleName();
	
	public static boolean checkIsHasUpdate (String AHttpResponse, base.app.AppUpdate.UpdateTask AUpdateTask) {
		if (null == AUpdateTask) 
			return false;
	    AUpdateTask.isNeedUpdateType = base.app.AppUpdate.UpdateType_NoUpdate;
		try {
		    org.json.JSONObject json_root = new org.json.JSONObject(AHttpResponse);
			if (null != json_root) {
				Log.d(LOGTAG, " checkIsHasUpdate:" + AHttpResponse);
				Log.d(LOGTAG, " AppName:" + BaseApp.EnglishName + "/" + AUpdateTask.currentVersion);
				int errorCode = json_root.getInt("status");
		        if (1 == errorCode) {
		        	if (!json_root.has("response"))
		        		return false;
		        	if (json_root.isNull("response"))
		        		return false;
		        	JSONObject response = null;
		        	try {
			        	response = json_root.getJSONObject("response");
		        	} catch (Exception e) {
		        		e.printStackTrace();
		        	}
					if (null == response)
						return false;		        		
				    if (response.has("version")) {
					    AUpdateTask.newVersion = response.getString("version");				    	
				    }
				    if (!TextUtils.isEmpty(AUpdateTask.newVersion)) {
				    	if (AUpdateTask.newVersion.equals(AUpdateTask.currentVersion)) {
				    		return false;
				    	}
				    }
				    if (response.has("download")) {
					    if (null == AUpdateTask.updateDownloadTask) {
					    	AUpdateTask.updateDownloadTask = new HttpActDownloader.Task();				    
					    }
					    AUpdateTask.updateDownloadTask.downloadUrl = response.getString("download");
					    if (TextUtils.isEmpty(AUpdateTask.updateDownloadTask.downloadUrl))
					    	return false;
				    } else {
				    	return false;
				    }		      
				    if (response.has("introduce")) {  	
				        AUpdateTask.newVersionDesc = response.getString("introduce");
						Log.d(LOGTAG, " Update NewVersion Desc:" + AUpdateTask.newVersionDesc);
				    }
				    if (response.has("is_force")) {
					    AUpdateTask.isForce = response.getInt("is_force");				    	
				    }
				    AUpdateTask.isNeedUpdateType = base.app.AppUpdate.UpdateType_NewVersion;
				    if (1 == AUpdateTask.isForce) {
					    AUpdateTask.isNeedUpdateType = base.app.AppUpdate.UpdateType_NewVersionForce;
				    }
					Log.d(LOGTAG, " Update Download Url:" + AUpdateTask.updateDownloadTask.downloadUrl);
				    AUpdateTask.updateDownloadTask.saveLocalFileName = BaseApp.EnglishName + AUpdateTask.newVersion + ".apk";
				    AUpdateTask.updateDownloadTask.saveLocalPath = "";
				    AUpdateTask.updateDownloadTask.saveLocalUrl = "";
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (base.app.AppUpdate.UpdateType_NewVersion == AUpdateTask.isNeedUpdateType)
			return true;
		if (base.app.AppUpdate.UpdateType_NewVersionForce == AUpdateTask.isNeedUpdateType)
			return true;
        return false;	
    }
}
