package ui.widget;

import utils.Log;
import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public class WebViewClientEx extends android.webkit.WebViewClient {
	
	private static final String LOGTAG = WebViewClientEx.class.getSimpleName();
/*WebViewClient是帮助WebView处理各种通知、请求事件的，具体来说包括： 

　　onLoadResource 
　　onPageStart 
　　onPageFinish 
　　onReceiveError //这个就是我们想要的方法
　　onReceivedHttpAuthRequest */
	/*//
    @Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
	  if (null == view) {
	  }
	  return super.shouldOverrideUrlLoading(view, url);
	}
	//*/

    @Override
    public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
    	super.onPageFinished(view, url);
    	((WebViewEx) view).clearProcessShow();
    }
    
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)    
    { 
        view.stopLoading();
        view.clearView();
        //Message msg = handler.obtainMessage();//发送通知，加入线程
        //msg.what=1;//通知加载自定义404页面
        //handler.sendMessage(msg);//通知发送！
    }
    //加载https时候，需要加入 下面代码
    //@Override
    //public void onReceivedSslError(WebView view, 
    //		SslErrorHandler handler, SslError error) {
    //   handler.proceed();  //接受所有证书
    //}
    @Override
    public void onReceivedSslError(WebView view, 
    		android.webkit.SslErrorHandler handler,
    		android.net.http.SslError error) {
        handler.proceed(); //接受所有证书
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    	// 拦截WebView加载网页
    	// 获取http请求的头，看是否包含所设置的flag
        //Log.d(LOGTAG, " shouldInterceptRequest:" + url);
        return super.shouldInterceptRequest(view, url); 
    }

}
