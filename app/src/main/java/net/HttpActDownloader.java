package net;

import ui.widget.WebViewEx;
import utils.Log;
import android.text.TextUtils;
import base.app.BaseApp;
import base.app.BaseConsts;

public class HttpActDownloader {

	private static final String LOGTAG = HttpActDownloader.class.getSimpleName();

	public static final int DownloadTaskType_Unknown = 0;
	public static final int DownloadTaskType_File = DownloadTaskType_Unknown + 1;
	public static final int DownloadTaskType_Update = DownloadTaskType_File + 1;
	
	// process 过程
	public static final int DownloadTaskStatus_Unknown = 0;
	public static final int DownloadTaskStatus_Prepare = DownloadTaskStatus_Unknown + 1;
	public static final int DownloadTaskStatus_Downloading = DownloadTaskStatus_Prepare + 1;
	// result 结果
	public static final int DownloadTaskStatus_Success = DownloadTaskStatus_Downloading + 1;
	public static final int DownloadTaskStatus_ParamError = DownloadTaskStatus_Success + 1;
	public static final int DownloadTaskStatus_Fail = DownloadTaskStatus_ParamError + 1;
	
	public static class Task extends base.BaseObj {
		
		public int taskType;
		public int taskStatus;
		
		public String downloadUrl;
		public String saveLocalUrl;
		public String saveLocalPath;
		public String saveLocalFileName;
		public base.BaseObj refObject;
		
		public String ensureSaveLocalPath() {
	    	if (TextUtils.isEmpty(saveLocalPath)) {
	    	    if (!TextUtils.isEmpty(base.app.BaseConsts.APP_SITE())) {
	    		    saveLocalPath = base.app.BaseConsts.APP_SITE() + "/";
	    		    java.io.File destDir = new java.io.File(getSDCardPath()+"/" + saveLocalPath);  
	                if (!destDir.exists()) {  
	                  destDir.mkdirs();  
	                }  
	    	    }
	    	}
			return saveLocalPath;
		}
		
		public String checkSaveLocalUrl() {
			ensureSaveLocalPath();
	    	if (TextUtils.isEmpty(saveLocalUrl)) {
		    	if (!TextUtils.isEmpty(saveLocalFileName)) {
		    		saveLocalUrl = saveLocalPath + saveLocalFileName;
		    	}	    		
	    	}
			return saveLocalUrl;
		}
	} 
	
	public static Task download(String ADownloadUrl, String ASaveLocalFileName, String ASaveLocalPath) {
		Task result = new Task();
		Log.d(LOGTAG, " download :" + ADownloadUrl);
		result.downloadUrl = ADownloadUrl;
		result.saveLocalPath = ASaveLocalPath;
		result.saveLocalFileName = ASaveLocalFileName;
		result.checkSaveLocalUrl();
		(new DownloadTask()).execute(result);
		return result;
	}

	  /**  
	   * 获取外置SD卡路径  
	   *   
	   * @return  
	   */  
	public static String getSDCardPath() {  
	    return android.os.Environment.getExternalStorageDirectory().getPath();  
	}   
	    
	public static void handleDownloadMessage(final Task ATask) {
		if (null == ATask) 
			return;
		Log.d(LOGTAG, " handleDownloadMessage :" + ATask.downloadUrl);
        if (DownloadTaskStatus_Success == ATask.taskStatus) {
    		Log.d(LOGTAG, " handleDownloadMessage success:" + ATask.taskType);
    		if (DownloadTaskType_Update == ATask.taskType) {
    			if (null != ATask.refObject) {
    				base.app.AppUpdate.notifyUpdateDownloadStatusChange(ATask);
    			}
    		}
        }
	}
	
	public static void syncDownload(final Task ATask) {
		if (null == ATask) 
			return;
	    ATask.taskStatus = DownloadTaskStatus_Unknown;	
		ATask.checkSaveLocalUrl(); 
		if (TextUtils.isEmpty(ATask.saveLocalUrl)) 
			return;           		
		Log.d(LOGTAG, " syncDownload onResponse save url:" + ATask.saveLocalUrl);
        java.io.File file = new java.io.File(android.os.Environment.getExternalStorageDirectory(), 
        		ATask.saveLocalUrl);                    
        if (file.exists()) {
			Log.d(LOGTAG, " syncDownload update file exists !!!!");
            ATask.taskStatus = DownloadTaskStatus_Success;
    		BaseApp.instance.sendAppMessage(BaseConsts.MSG_DOWNLOAD, ATask);
    		return;        	
        }
		//创建okHttpClient对象
		net.okhttp.OkHttpClient httpClient = new net.okhttp.OkHttpClient();
		String url = ATask.downloadUrl;
		Log.d(LOGTAG, " syncDownload :" + url);
   	    String ua = null;
   	    if (!TextUtils.isEmpty(WebViewEx.mCustomUserAgent)) {
   	    	ua = WebViewEx.mCustomUserAgent;
   	    } else {
   	    	ua = "Mozilla/5.0 (Linux; Android 5.0.2; en-us) AppleWebKit/537.36 (KHTML, like Gecko) Version/45.0.2454.95 Mobile Safari/537.36;hpdapp";
   	    }
		final net.okhttp.Request httpRequest = new net.okhttp.Request.Builder()
            .url(url)
            .header("User-Agent", ua)
            .build();
        final net.okhttp.Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new net.okhttp.Callback()
        {

			@Override
			public void onFailure(net.okhttp.Request request, java.io.IOException e) {
				Log.d(LOGTAG, " syncDownload onFailure");
				if (null != e) {
					e.printStackTrace();					
				}
			}

			@Override
			public void onResponse(net.okhttp.Response response) throws java.io.IOException {
				// TODO Auto-generated method stub
				Log.d(LOGTAG, " syncDownload onResponse");
		        ATask.taskStatus = DownloadTaskStatus_Prepare;
                java.io.InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                java.io.FileOutputStream fos = null;
                java.io.File file = null;
                try
                {
                    is = response.body().byteStream();
                    android.os.Environment.getExternalStorageDirectory();
                    //file = new java.io.File(ATask.saveLocalUrl, ATask.downloadUrl);
    				Log.d(LOGTAG, " syncDownload onResponse save url:" + ATask.saveLocalUrl);
                    file = new java.io.File(android.os.Environment.getExternalStorageDirectory(), 
                    		ATask.saveLocalUrl);                    
                    fos = new java.io.FileOutputStream(file);
                    ATask.taskStatus = DownloadTaskStatus_Downloading;
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    ATask.taskStatus = DownloadTaskStatus_Success;
            		BaseApp.instance.sendAppMessage(BaseConsts.MSG_DOWNLOAD, ATask);
            		
    				Log.d(LOGTAG, " syncDownload OK");
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    //sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (java.io.IOException e)
                {
    		        ATask.taskStatus = DownloadTaskStatus_Fail;
                	e.printStackTrace();
                	//sendFailedStringCallback(response.request(), e, callback);
                } finally
                {
                    try
                    {
                        if (null != is) is.close();
                    } catch (java.io.IOException e)
                    {
                    	e.printStackTrace();
                    }
                    try
                    {
                        if (null != fos) fos.close();
                    } catch (java.io.IOException e)
                    {
                    	e.printStackTrace();
                    }
    		        if (DownloadTaskStatus_Fail == ATask.taskStatus) {
    		        	try {
    	                    file = new java.io.File(android.os.Environment.getExternalStorageDirectory(), 
    	                    		ATask.saveLocalUrl);
    	                    if (file.exists()) {
    	                    	file.delete();
    	                    }
    		        	} catch (Exception e) {
                        	e.printStackTrace();
    		        	}    		        	
    		        }
                }

            }
        });
	}
	
	protected static class DownloadTask extends android.os.AsyncTask<Task, Integer, String> {
		
		@Override
		protected String doInBackground(Task... params) {
			try {
				Thread.sleep(10);
				syncDownload(params[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override  
	    protected void onPostExecute(String result) {
		}
	}
	 public static abstract class ResultCallback<T>
	    {
		 java.lang.reflect.Type mType;

	        public ResultCallback()
	        {
	            mType = getSuperclassTypeParameter(getClass());
	        }

	        static java.lang.reflect.Type getSuperclassTypeParameter(Class<?> subclass)
	        {
	        	java.lang.reflect.Type superclass = subclass.getGenericSuperclass();
	            if (superclass instanceof Class)
	            {
	                throw new RuntimeException("Missing type parameter.");
	            }
	            java.lang.reflect.ParameterizedType parameterized = (java.lang.reflect.ParameterizedType) superclass;
	            return null;
	            //return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
	        }

	        public abstract void onError(net.okhttp.Request request, Exception e);
	        public abstract void onResponse(T response);
	    }

}
