package com.zhijiaoyi.vstock.net;

import utils.*;

public class UtilsNetActions {

	private static String LOGTAG = UtilsNetActions.class.getSimpleName();
	
	  static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
	  static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
	  static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
	  
	public static String getSign(String call,String args){
		/**
		 * var CALLCLIENT_UA = "LOCAL_DEV_CLIENT";
		   var CALLCLIENT_SIGNKEY = "分配给子系统的签名密钥";  LOCAL_DEV_REQUEST_SIGN_KEY

		   //生成请求签名
		   var signKey = CALLCLIENT_UA + CALLCLIENT_SIGNKEY + CALLCLIENT_UA;
		   signKey = md5(signKey + call + signKey + args + signKey);
		 */
		String signKey="LOCAL_DEV_CLIENT" + "LOCAL_DEV_REQUEST_SIGN_KEY" + "LOCAL_DEV_CLIENT";
		signKey=signKey+call+signKey+args+signKey;

		return utils.StrUtils.MD5(signKey);
	}
	
	private String doAction(String action, String args) {
		Log.d(LOGTAG, " okhttp doaction:" + action + args);
		String result = null;
		net.okhttp.OkHttpClient client = new net.okhttp.OkHttpClient();
	    client.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS);
	    client.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS);
	    client.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS);

	    net.okhttp.Request request = null;
	    try {
			Log.d(LOGTAG, " okhttp begin ---------------------------");
			//RequestBody body = request.body();
			net.okhttp.RequestBody formBody = new net.okhttp.FormEncodingBuilder()
			  .add("call", action)
			  .add("args", args)
			  .add("sign", getSign(action, args))
			  .add("ua", Config.LOGIN_UA)
			  .build();
	        request = new net.okhttp.Request.Builder()
                .url(com.zhijiaoyi.vstock.net.Config.API_SERVER_URL)
	            .post(formBody)
	            .build();
			net.okhttp.Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				result = response.body().string();
				Log.d(LOGTAG, " okhttp:" + result);
			}
			Log.d(LOGTAG, " okhttp end ---------------------------");
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String doAction2(String action, String args) {
    	String result = null;
    	net.HttpUtils httpUtils = new net.HttpUtils();

        String sign = getSign(action, args);
		net.http.RequestParams reqparams=new net.http.RequestParams();
		reqparams.addBodyParameter("call", action);
		reqparams.addBodyParameter("args", args);
		reqparams.addBodyParameter("sign", sign);
		reqparams.addBodyParameter("ua", Config.LOGIN_UA);
		try {
			net.http.ResponseStream response = httpUtils.sendSync(
					net.http.Protocol.HttpMethod.POST, 
					com.zhijiaoyi.vstock.net.Config.API_SERVER_URL, reqparams);
			if (null != response) {
				result = response.readString();	
				Log.d(LOGTAG, " httputils:" + action + "/" + result);			
			}
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (net.http.HttpException e) {
			e.printStackTrace();
		}
		return result;		
	}
	
    public String doLogin(String username, String password) {

		Log.d(LOGTAG, " doLogin:" + username + "/" + password);
		
    	java.util.Map<String, String> map = new java.util.HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
		return doAction(Config.ACTION_LOGIN_CALL, new data.gson.Gson().toJson(map));
		/*//
		httpUtils.send(HttpMethod.POST, 
				com.zhijiaoyi.vstock.net.Config.API_SERVER_URL, reqparams, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
		});
		//*/
    }
}
