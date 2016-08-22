package com.hepaidai;

import android.os.AsyncTask;

import java.io.IOException;

import base.app.BaseConsts;
import data.JsonData;
import utils.Log;


public class UserLogin extends base.BaseObj {

	private static final String LOGTAG = UserLogin.class.getSimpleName();
	private static final String UrlLogin = BaseConsts.APP_APIROOT() + "/login";
	
	private static void syncUserLogin() {
		//创建okHttpClient对象
		net.okhttp.OkHttpClient httpClient = new net.okhttp.OkHttpClient();
		//创建一个Request
		net.okhttp.FormEncodingBuilder httpPostBuilder = new net.okhttp.FormEncodingBuilder();   
		httpPostBuilder.add("keywords", "litwrd");
		httpPostBuilder.add("password", "littles");
		httpPostBuilder.add("src", "app");
		
		final net.okhttp.Request httpRequest = new net.okhttp.Request.Builder()
		                .url(UrlLogin)
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
				parseUserLoginHttpResponse(response.body().string());
			} else {
			}         
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void parseUserLoginHttpResponse(String httpResponse) {
		try {
			JsonData.parseJsonData(httpResponse);
			org.json.JSONObject json_root = new org.json.JSONObject(httpResponse);
			Log.d(LOGTAG, "parseUserLoginHttpResponse:" + httpResponse);
			String errorCode = null;
			if (null != json_root) {
				errorCode = json_root.getString("errorCode"); 
				org.json.JSONObject json_result = json_root.getJSONObject("result");
				if (null != json_result) {
					Log.d(LOGTAG, ":" + httpResponse);					
				}
				/*{"result":{"url":"","app_login_token":null},
				 * "errorCode":"0","errorMessage":"","status":"OK"}*/
				/*{"result":[],"errorCode":"3",
				 * "errorMessage":"\u8bf7\u60a8\u8f93\u5165\u5bc6\u7801","status":"ERROR"}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static class UserLoginTask extends AsyncTask<String, Integer, String> {
		
		@Override
		protected String doInBackground(String... arg0) {
			try {
				Thread.sleep(10);
				syncUserLogin();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override  
	    protected void onPostExecute(String result) {
		}
	}
	
	public static void userLogin() {	
		(new UserLoginTask()).execute("");		
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
