package ui.widget;

import utils.Log;

import android.annotation.SuppressLint;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;

public class WebChromeClientEx extends android.webkit.WebChromeClient {

	private static final String LOGTAG = WebChromeClientEx.class.getSimpleName();
	
	// WebChromeClient主要处理解析，
	// 渲染网页等浏览器做的事情，辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度
/*onCloseWindow(关闭WebView) 
　　onCreateWindow() 
　　onJsAlert (WebView上alert是弹不出来东西的，需要定制你的WebChromeClient处理弹出) 
　　onJsPrompt 
　　onJsConfirm 
　　onProgressChanged 
　　onReceivedIcon 
　　onReceivedTitle*/
	public int mProgressValue = 0;
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
    	mProgressValue = newProgress;
    	android.widget.ProgressBar progressbar = null;
    	progressbar = ((WebViewEx) view).showProcessBar();
    	Log.d(WebChromeClientEx.class.getSimpleName() + ".java", "onProgressChanged:" + mProgressValue);
        if (100 == newProgress) {
            // 网页加载完成
        	((WebViewEx) view).clearProcessShow();
        	view.invalidate();
        } else {
            // 加载中
        	if (null != progressbar) {
                if (android.view.View.GONE == progressbar.getVisibility())
                    progressbar.setVisibility(android.view.View.VISIBLE);
                progressbar.setProgress(newProgress);
        	}
        } 
        super.onProgressChanged(view, newProgress);
    }
    
    @Override
    public void onCloseWindow(WebView window) {
    	super.onCloseWindow(window);
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message,  
            JsResult result) {
        Log.d(LOGTAG, " onJsAlert");
		return true;  
    }
    /** 
     * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////” 
     */  
    @Override
    public boolean onJsConfirm(WebView view, String url, String message,  
            final JsResult result) {
        Log.d(LOGTAG, " onJsConfirm");
		return true;
    }
    
    @Override
    public boolean onJsPrompt(WebView view, String url, String message,  
            String defaultValue, final JsPromptResult result) { 
        Log.d(LOGTAG, " onJsPrompt");
		return true;
    }
    
    @Override  
    public void onRequestFocus(WebView view) {  
        Log.d(LOGTAG, WebChromeClientEx.class.getSimpleName() + " onRequestFocus");
        super.onRequestFocus(view);  
    }  
}
