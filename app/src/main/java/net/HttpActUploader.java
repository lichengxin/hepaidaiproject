package net;

import android.os.AsyncTask;

public class HttpActUploader {
	
	public static class Task {
		public String uploadUrl;
		public String localFileUrl;
	} 
	
	public static Task download(String AUploadUrl, String ALocalFileUrl) {
		Task result = new Task();
		result.uploadUrl = AUploadUrl;
		result.localFileUrl = ALocalFileUrl;
		(new DownloadTask()).execute(result);
		return result;
	}
	
	protected static void syncUpload(Task ATask) {		
	}
	
	protected static class DownloadTask extends AsyncTask<Task, Integer, String> {
		
		@Override
		protected String doInBackground(Task... params) {
			try {
				Thread.sleep(10);
				syncUpload(params[0]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override  
	    protected void onPostExecute(String result) {
		}
	}
	
}
