package data;

import utils.Log;

public class JsonData extends base.BaseObj {
	
	private static final String LOGTAG = JsonData.class.getSimpleName();
// 在未知 json 数据结构的条件下遍历 json 数据结构
	public static void parseJsonData(String AJsonData) {
		try {
			org.json.JSONObject json_root = new org.json.JSONObject(AJsonData);
			if (null != json_root) {
				Log.d(LOGTAG, "parseJsonData:" + AJsonData);
				checkJsonObject(json_root);
			}
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void checkJsonObject(org.json.JSONObject AJsonObject) {
		// AJsonObject.keys()
		Log.d(LOGTAG, "checkJsonObject1:" + AJsonObject.toString());
		Log.d(LOGTAG, "checkJsonObject2:" + AJsonObject.length());
		java.util.Iterator<?> jsonKeys = AJsonObject.keys();
		org.json.JSONObject jsonObjectChild = null;
		org.json.JSONArray jsonArrayChild = null;
		String jsonkey = null;
		String jsonstr = null;
		boolean isHandled = false; 
		while(jsonKeys.hasNext()){//遍历JSONObject
			jsonkey = jsonKeys.next().toString();
			isHandled = false;
			Log.d(LOGTAG, "checkJsonObject3:" + jsonkey);	
			try {		
				jsonObjectChild = AJsonObject.getJSONObject(jsonkey);
				Log.d(LOGTAG, "checkJsonObject4");
				isHandled = true;
				if (null != jsonObjectChild) {
					checkJsonObject(jsonObjectChild);					
				}
			} catch (org.json.JSONException e) {
				//e.printStackTrace();
			}      
			if (!isHandled) {
				try {
					jsonArrayChild = AJsonObject.getJSONArray(jsonkey);
					Log.d(LOGTAG, "checkJsonObject5:" + jsonArrayChild.length());	
				} catch (org.json.JSONException e) {
					//e.printStackTrace();
				}
			}
			if (!isHandled) {
				try {
					jsonstr = AJsonObject.getString(jsonkey);
					Log.d(LOGTAG, "checkJsonObject6:" + jsonstr);	
				} catch (org.json.JSONException e) {
					//e.printStackTrace();
				}              
			}
        }    
		//AJsonObject.length();
	}
}
