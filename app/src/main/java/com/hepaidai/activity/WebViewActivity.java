package com.hepaidai.activity;

import ui.activity.*;
import ui.widget.WebChromeClientEx;
import ui.widget.WebViewClientEx;
import utils.*;
import base.app.*;

import com.app.R;

import android.app.Activity;
import android.os.*;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class WebViewActivity extends BaseActivity {

	private static final String LOGTAG = WebViewActivity.class.getSimpleName();
    private static final String APP_CACAHE_DIRNAME = "/webcache";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pkg_hepaidai_activity_webview);
        Log.d(LOGTAG, WebViewActivity.class.getSimpleName() + " OnCreate");
        initWebView();
        if (null != mWebView) {
    		mWebView.loadUrl("http://www.hepaidai.com");        	
        }
    }

    private android.webkit.WebView mWebView = null;
    //private com.base.uiwidget.WebViewEx mWebView = null;
    private ui.widget.WebViewClientEx mWebViewClient = null;


	protected void initWebView() {
		RelativeLayout layoutWebView = (RelativeLayout) this.findViewById(R.id.layout_webview);
		if (null != layoutWebView) {
			//mWebView = new com.base.uiwidget.WebViewEx(this);
			mWebView = new android.webkit.WebView(this);
			layoutWebView.addView(mWebView, new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
	        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
			mWebView.setWebViewClient(new WebViewClientEx(){
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	                view.loadUrl(url);
	                return true;
	            }
	        });
			// 如果访问的页面中有Javascript，则webview必须设置支持Javascript
		    WebSettings websettings = mWebView.getSettings();
			websettings.setJavaScriptEnabled(true);
			// 判断页面加载过程
			mWebView.setWebChromeClient(new WebChromeClientEx() {
	            @Override
	            public void onProgressChanged(WebView view, int newProgress) {
	                if (newProgress == 100) {
	                    // 网页加载完成
	                } else {
	                    // 加载中
	                }

	            }
	        });
			// 优先使用缓存
			websettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			// 不使用缓存
			websettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			//自适应屏幕
			/*
			 * LayoutAlgorithm是一个枚举用来控制页面的布局，有三个类型：
            1.NARROW_COLUMNS：可能的话使所有列的宽度不超过屏幕宽度
            2.NORMAL：正常显示不做任何渲染
            3.SINGLE_COLUMN：把所有内容放大webview等宽的一列中
            */
			websettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			websettings.setJavaScriptEnabled(true);
	      //设置加载进来的页面自适应手机屏幕 
			websettings.setUseWideViewPort(true);
			websettings.setLoadWithOverviewMode(true);
  	        //隐藏webview缩放按钮
			websettings.setBuiltInZoomControls(false);
	         
			websettings.setRenderPriority(RenderPriority.HIGH);
			websettings.setBlockNetworkImage(false);
			websettings.setAllowFileAccess(true);
			websettings.setCacheMode(WebSettings.LOAD_DEFAULT);
	        // 开启 DOM storage API 功能
			websettings.setDomStorageEnabled(true);
	        //开启 database storage API 功能
			websettings.setDatabaseEnabled(true);
	        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
	        //设置数据库缓存路径
	        websettings.setDatabasePath(cacheDirPath);
	        //设置  Application Caches 缓存目录
	        websettings.setAppCachePath(cacheDirPath);
	        //开启 Application Caches 功能
	        websettings.setAppCacheEnabled(true);			
		}
	}
	
}