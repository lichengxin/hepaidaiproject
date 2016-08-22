package com.app.activity;

import java.util.ArrayList;
import java.util.List;

import pkg.stock.data.DBStock;
import pkg.stock.data.StockItem;
import pkg.stock.data.StockListAdapter;

import ui.activity.BaseFragment;
import ui.widget.UtilsListItem;
import ui.widget.WebViewClientEx;
import utils.Log;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.*;
import android.widget.*;
import base.app.BaseApp;

import com.app.R;
import com.zhijiaoyi.vstock.activity.ActivityListItem;

public class FragmentWebView extends BaseFragment implements OnClickListener {

	private static final String LOGTAG = FragmentWebView.class.getSimpleName();

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);		
		initViews();
		return mView;
	}

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_app_fragment_empty;
    }
    
    private ui.widget.WebViewEx mWebView = null;
    private ui.widget.WebViewClientEx mWebViewClient = null;

	protected void initViews() {
		initWebView();
	}
	
	protected void initWebView() {
		RelativeLayout mainLayout = (RelativeLayout) mView.findViewById(R.id.activity_main);
		if (null != mainLayout) {
			mWebView = new ui.widget.WebViewEx(getActivity());
			mainLayout.addView(mWebView, new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
			mWebView.loadUrl("http://baidu.com");
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
		    WebSettings settings = mWebView.getSettings();
			settings.setJavaScriptEnabled(true);
			// 判断页面加载过程
			mWebView.setWebChromeClient(new android.webkit.WebChromeClient() {
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
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
			// 不使用缓存
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
	}
	
	//改写物理按键——返回的逻辑
    //@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(mWebView.canGoBack())
            {
            	mWebView.goBack();//返回上一页面
                return true;
            }
            else
            {
                System.exit(0);//退出程序
            }
        }
        return false;
        //return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public void onClick(View v) {	
	}
}
