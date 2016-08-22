package base.app;

import ui.widget.WebViewEx;
import utils.Log;
import net.HttpActDownloader;
import android.text.TextUtils;

public class AppUpdate {

	private static final String LOGTAG = AppUpdate.class.getSimpleName();
	private static String UrlCheckUpdate() {
		return BaseConsts.APP_APIROOT() + "/app/version";			
		//return BaseConsts.APP_APIROOT();
	};
	
	// 更新任务
	final public static int UpdateType_Unknown = 0;
	final public static int UpdateType_NoUpdate = 1;
	final public static int UpdateType_NewVersion = 2; // 有新版本
	final public static int UpdateType_NewVersionForce = 3; // 有新版本强制更新
	
	final public static int UpdateStep_Begin = 0;
	final public static int UpdateStep_CheckUpdate = UpdateStep_Begin + 1;
	final public static int UpdateStep_ConfirmUpdate = UpdateStep_CheckUpdate + 1;
	final public static int UpdateStep_ConfirmDownload = UpdateStep_ConfirmUpdate + 1;
	final public static int UpdateStep_DownloadUpdate = UpdateStep_ConfirmDownload + 1;
	final public static int UpdateStep_ConfirmInstall = UpdateStep_DownloadUpdate + 1;
	final public static int UpdateStep_Install = UpdateStep_ConfirmInstall + 1;
	final public static int UpdateStep_End = UpdateStep_Install + 1;
	
	public static class UpdateTask extends base.BaseObj {
		public int updateStep = 0;
		public int stepResult = 0;
		public String newVersion = null;
		public String currentVersion = null;
		public String checkVersionUrl = null;
		
		public int isNeedUpdateType = UpdateType_Unknown;
		public int isForce = 0;
		// 更新下载任务
		public String newVersionDesc = null;
		public net.HttpActDownloader.Task updateDownloadTask = null;
		
		public void setStep(int AStep) {
			if (updateStep != AStep) {
				updateStep = AStep;
				stepResult = 0;
			}
		}
		//String downloadUrl;
		//String localSaveUrl;
	}

	public static void installUpdate(UpdateTask AUpdateTask) {
		Log.d(LOGTAG, " installUpdate1:" + AUpdateTask.updateDownloadTask.saveLocalUrl); 
		Log.d(LOGTAG, " installUpdate2:" + AUpdateTask.updateDownloadTask.saveLocalFileName); 
		Log.d(LOGTAG, " installUpdate3:" + AUpdateTask.updateDownloadTask.saveLocalPath);
		
		  
		if (!android.text.TextUtils.isEmpty(AUpdateTask.updateDownloadTask.saveLocalUrl)) {
			java.io.File apkfile = new java.io.File(android.os.Environment.getExternalStorageDirectory(), 
					AUpdateTask.updateDownloadTask.saveLocalUrl);
			
			//File file = new File("/storage/sdcard0/Manual/test.pdf");
			//java.io.File file = new java.io.File(AUpdateTask.updateDownloadTask.saveLocalUrl);
            if(apkfile.exists()){
    			Log.d(LOGTAG, " installUpdate begin install:" + AUpdateTask.updateDownloadTask.saveLocalUrl);
    		    android.content.Intent installIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
    		    //android.net.Uri installUri = android.net.Uri.fromFile(
    		    //		new java.io.File(android.os.Environment.getExternalStorageDirectory(), 
    		    //				AUpdateTask.updateDownloadTask.saveLocalUrl));
    		    installIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK); 
    		    android.net.Uri installUri = android.net.Uri.parse("file://" + apkfile.toString());
    		    installIntent.setDataAndType(installUri, "application/vnd.android.package-archive");	    
    		    base.app.BaseApp.instance.startActivity(installIntent);
    		    android.os.Process.killProcess(android.os.Process.myPid());
            }
		}
	}
	
	public static UpdateTask mCurrentUpdateTask = null;
	
	public static void notifyUpdateDownloadStatusChange(HttpActDownloader.Task ADownloadTask) {
		// 通知下载更新 状态变化
		Log.d(LOGTAG, " notifyUpdateDownloadStatusChange:" + ADownloadTask.downloadUrl);
		if (null == ADownloadTask) 
			return;
		if (null == ADownloadTask.refObject) 
			return;
		UpdateTask updateTask = (UpdateTask) ADownloadTask.refObject;
		ADownloadTask.refObject = null;
		if (HttpActDownloader.DownloadTaskStatus_Success == ADownloadTask.taskStatus) {
			updateTask.setStep(UpdateStep_ConfirmInstall);
			BaseApp.instance.sendAppMessage(BaseConsts.MSG_APP_UPDATE, updateTask);
		}
	}

	public static void showConfirmUpdateDialog1(UpdateTask AUpdateTask) {
		if (null == AUpdateTask) 
			return;
		Log.d(LOGTAG, " showConfirmUpdateDialog1");
		mCurrentUpdateTask = AUpdateTask;
		android.app.Activity currentActivity = 
				BaseApp.instance.core.getActivitiesManager().currentActivity();
		if (null == currentActivity)
			return;
		android.content.Intent intent = new android.content.Intent();
		intent.setClass(currentActivity, com.app.activity.DialogAppUpdate.class);
		//android.os.Bundle bundle = new android.os.Bundle();   
		//bundle.putString("DataKey", edittext.getText().toString());//给 bundle 写入数据   
		//intent.putExtras(bundle);  
		currentActivity.startActivityForResult(intent, 0);
	}
	
	private static void showConfirmUpdateDialog3(UpdateTask AUpdateTask) {
		if (null == AUpdateTask) 
			return;
		android.content.Context context = BaseApp.core.getActivitiesManager().currentActivity();
		if (null == context) {
			context = BaseApp.instance;
		}
		if (null == context) {
			return;
		}
		mCurrentUpdateTask = AUpdateTask; 
		android.app.AlertDialog.Builder dialogbuilder = new android.app.AlertDialog.Builder(context);
		dialogbuilder.setTitle("更新提示");//设置对话框标题
		if (TextUtils.isEmpty(AUpdateTask.newVersionDesc)) {
			if (1 == AUpdateTask.isForce) {
				dialogbuilder.setMessage("发现新版本 " + AUpdateTask.newVersion + " 请更新后继续使用");//设置显示的内容	
			} else {
				dialogbuilder.setMessage("发现新版本 " + AUpdateTask.newVersion);//设置显示的内容	
			}			
		} else {
			dialogbuilder.setMessage(AUpdateTask.newVersionDesc);//设置显示的内容				
		}  
	    dialogbuilder.setPositiveButton("确定",new android.content.DialogInterface.OnClickListener() {//添加确定按钮  
	         @Override  
	         public void onClick(android.content.DialogInterface dialog, int which) {//确定按钮的响应事件
	        	mCurrentUpdateTask.setStep(UpdateStep_Install);
            	BaseApp.instance.sendAppMessage(BaseConsts.MSG_APP_UPDATE, null);
	         }  	  
	     });
	    dialogbuilder.setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {//添加返回按钮  
	         @Override  
	         public void onClick(android.content.DialogInterface dialog, int which) {//响应事件
	        	 if (null != mCurrentUpdateTask) {
	        		 if (1 == mCurrentUpdateTask.isForce) {
	        			 // 强制更新 退出应用
	        			 BaseApp.quitApp();
	        		 } else {
			        	 //ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
			        	 //config.appUpdateIgnore = mCurrentUpdateTask.newVersion;
	        			 //ManagerAppConfig.Save(config);
	        		 }
	        	 }	        	 
	        	 dialog.dismiss();
	         }  
	    });
	    dialogbuilder.show();//在按键响应事件中显示此对话框  
	}
	
	public static void handleUpdateMessage(UpdateTask AUpdateTask) {
		UpdateTask updateTask = AUpdateTask;
		if (null == updateTask)
			updateTask = mCurrentUpdateTask;
		if (null == updateTask)
			return;
		//showConfirmUpdateDialog1(updateTask);
		//if (true)
		//return;
		Log.d(LOGTAG, " handleUpdateMessage:" + updateTask.updateStep + " / " + updateTask.isNeedUpdateType);
		if (UpdateStep_CheckUpdate == updateTask.updateStep) { // 有新版本
			if (UpdateType_NoUpdate == updateTask.isNeedUpdateType)
				return;
			if (UpdateType_NewVersion == updateTask.isNeedUpdateType) { // 有新版本
	        	ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
	        	if (null != config.appUpdateIgnore) {
		        	if (config.appUpdateIgnore.equals(updateTask.newVersion))
		        		return;
	        	}
				updateTask.updateDownloadTask.taskType = net.HttpActDownloader.DownloadTaskType_Update;
				updateTask.updateDownloadTask.refObject = updateTask;
				updateTask.setStep(UpdateStep_ConfirmUpdate); 
			}
			if (UpdateType_NewVersionForce == updateTask.isNeedUpdateType) { // 有新版本 强制更新
				updateTask.updateDownloadTask.taskType = net.HttpActDownloader.DownloadTaskType_Update;
				updateTask.updateDownloadTask.refObject = updateTask;
				updateTask.setStep(UpdateStep_ConfirmUpdate);
			}
		}
		if (UpdateStep_ConfirmUpdate == updateTask.updateStep) {
			showConfirmUpdateDialog1(updateTask);
		}
		if (UpdateStep_ConfirmDownload == updateTask.updateStep) {
			
		}
		if (UpdateStep_DownloadUpdate == updateTask.updateStep) {
			net.HttpActDownloader.syncDownload(updateTask.updateDownloadTask);
		}
		if (UpdateStep_ConfirmInstall == updateTask.updateStep) {
			//if (UpdateType_NewVersion == updateTask.isNeedUpdateType) { // 有新版本
			//	showConfirmUpdateDialog1(updateTask);
			//}
			//if (UpdateType_NewVersionForce == updateTask.isNeedUpdateType) { // 有新版本 强制更新
			//	showConfirmUpdateDialog1(updateTask);
				//installUpdate(updateTask);
			//}
			updateTask.setStep(UpdateStep_Install);
		}
		if (UpdateStep_Install == updateTask.updateStep) {
			updateTask.setStep(UpdateStep_End);
			installUpdate(updateTask);
		}
		if (UpdateStep_End == updateTask.updateStep) {
		}
	}
	
	private void syncCheckUpdate(UpdateTask AUpdateTask) {
		if (null == AUpdateTask)
			return;
		if (TextUtils.isEmpty(AUpdateTask.currentVersion)) {
			AUpdateTask.currentVersion = Version.getVersionName(null);
		}
		String url = UrlCheckUpdate();
		Log.d(LOGTAG, " syncCheckUpdate:" + url);
		//创建okHttpClient对象
		net.okhttp.OkHttpClient httpClient = new net.okhttp.OkHttpClient();
		//创建一个Request
		net.okhttp.FormEncodingBuilder httpPostBuilder = new net.okhttp.FormEncodingBuilder();

		Log.d(LOGTAG, " checkUpdate versionName:" + (new base.app.Version()).getVersionName(null));
		Log.d(LOGTAG, " checkUpdate versionCode:" + (new base.app.Version()).getVersionCode(null));
		String ver = (new base.app.Version()).getVersionName(null);
		httpPostBuilder.add("curVersion", ver);
		Log.d(LOGTAG, " ver:" + ver);
		url = url + "?" + "curVersion=" + ver;
   	    ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
   	    String appchannel = config.channelId;
		Log.d(LOGTAG, " appchannel:" + appchannel);
   	    if (!TextUtils.isEmpty(appchannel)) {
   			httpPostBuilder.add("marketChanel", appchannel);
   	   	    url = url + "&" + "marketChanel=" + appchannel;   	    	
   	    };
   	    
   	    String ua = null;
   	    if (!TextUtils.isEmpty(WebViewEx.mCustomUserAgent)) {
   	    	ua = WebViewEx.mCustomUserAgent;
   	    } else {
   	    	ua = "Mozilla/5.0 (Linux; Android 5.0.2; en-us) AppleWebKit/537.36 (KHTML, like Gecko) Version/45.0.2454.95 Mobile Safari/537.36;hpdapp";
   	    }
		Log.d(LOGTAG, " user-agent:" + ua);
		final net.okhttp.Request httpRequest = new net.okhttp.Request.Builder()
		                .url(url)
		                .header("User-Agent", ua)
		                .post(httpPostBuilder.build())
		                .build();
		//new call
		net.okhttp.Call httpCall = httpClient.newCall(httpRequest); //请求加入调度
		// 请求加入调度
		/*//
		httpCall.enqueue(new com.base.net.okhttp.Callback()
        {
            @Override
            public void onFailure(com.base.net.okhttp.Request request, java.io.IOException e)
            {
            }

            @Override
            public void onResponse(final com.base.net.okhttp.Response response) throws java.io.IOException
            {
            	final String httpResponse = response.body().string();
				Log.d(LOGTAG, " okhttp1:" + httpResponse);
            }
        }); 
		//*/
		net.okhttp.Response response = null;
		try {
			response = httpCall.execute();
			if (response.isSuccessful()) {
				String httpResponse = response.body().string();
				Log.d(LOGTAG, " okhttp1:" + httpResponse);
				if (com.hepaidai.AppUpdate.checkIsHasUpdate(httpResponse, AUpdateTask)) {
					// notify has new version need update
					// 发现存在版本更新
					Log.d(LOGTAG, " checkIsHasUpdate" + AUpdateTask.updateDownloadTask.downloadUrl);
					if (null != AUpdateTask.updateDownloadTask) {
						if (!android.text.TextUtils.isEmpty(AUpdateTask.updateDownloadTask.downloadUrl)) {
							// 先下载再提示 ???
							AUpdateTask.updateDownloadTask.checkSaveLocalUrl();
							if (android.text.TextUtils.isEmpty(AUpdateTask.updateDownloadTask.saveLocalUrl)) {
								//AUpdateTask.updateDownloadTask.saveLocalUrl = 
										//android.os.Environment.getExternalStorageDirectory();
							}
							AUpdateTask.setStep(UpdateStep_CheckUpdate);
		            		BaseApp.instance.sendAppMessage(BaseConsts.MSG_APP_UPDATE, AUpdateTask);
							//installUpdate(AUpdateTask);
						}
					}
				}
			} else {
				Log.d(LOGTAG, " okhttp fail");
			}         
		} catch (java.io.IOException e1) {
			e1.printStackTrace();
		}
	}

	protected class CheckUpdateTask extends android.os.AsyncTask<UpdateTask, Integer, String> {
		
		@Override
		protected String doInBackground(UpdateTask... params) {
			try {
				Log.d("CheckUpdateTask");
				//if (true) {
				//	return null;					
				//}
				Thread.sleep(10);
				syncCheckUpdate(params[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override  
	    protected void onPostExecute(String result) {
			mUpdateTask = null;
			base.app.BaseApp.instance.clearAppUpdate();
		}
	}

	private static UpdateTask mUpdateTask = null;
	public UpdateTask getUpdateTask () {
		return mUpdateTask;
	}
	
	public void clearUpdateTask () {
		mUpdateTask = null;
	}
	
	public void checkUpdate() {	
		if (null == mUpdateTask) {
			mUpdateTask = new UpdateTask();
			(new CheckUpdateTask()).execute(mUpdateTask);		
		}
	}
	
	/*File file = new File(Environment.getExternalStorageDirectory(), "balabala.mp4");

RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

RequestBody requestBody = new MultipartBuilder()
     .type(MultipartBuilder.FORM)
     .addPart(Headers.of(
          "Content-Disposition", 
              "form-data; name=\"username\""), 
          RequestBody.create(null, "张鸿洋"))
     .addPart(Headers.of(
         "Content-Disposition", 
         "form-data; name=\"mFile\"; 
         filename=\"wjd.mp4\""), fileBody)
     .build();

Request request = new Request.Builder()
    .url("http://192.168.1.103:8080/okHttpServer/fileUpload")
    .post(requestBody)
    .build();*/
}
