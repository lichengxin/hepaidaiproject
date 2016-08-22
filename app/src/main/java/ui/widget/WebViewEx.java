package ui.widget;

import java.util.Map;

import utils.Log;


import android.annotation.SuppressLint;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.TextSize;

public class WebViewEx extends android.webkit.WebView {

	private static final String LOGTAG = WebViewEx.class.getSimpleName();
	
    public static final String WEB_CACAHE_DIRNAME = "/webcache";
	//*/
	private android.content.Context mContext = null;
	
	public WebViewEx(android.content.Context context) {
		super(context);
		mContext = context;
	}
	
	public WebViewEx(android.content.Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public WebViewEx(android.content.Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

    @Override
    public void destroy() {
        //BrowserSettings.getInstance().stopManagingSettings(getSettings());
        super.destroy();
    }
	/*
	public WebViewEx(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
	}
	//*/
	WebChromeClientEx mWebChromeClient = null;
    @Override
    public void setWebChromeClient(WebChromeClient client) {
    	Log.d(LOGTAG, "setWebChromeClient");
        mWebChromeClient = (WebChromeClientEx) client;
        super.setWebChromeClient(mWebChromeClient);
    }

    public void setWebChromeClientEx(WebChromeClientEx client) {
    	Log.d(LOGTAG, "setWebChromeClientEx");
        mWebChromeClient = (WebChromeClientEx) client;
        super.setWebChromeClient(mWebChromeClient);
    }
    
    WebViewClientEx mWebViewClient = null;
    @Override
    public void setWebViewClient(WebViewClient client) {
        mWebViewClient = (WebViewClientEx) client;
        Log.d(LOGTAG, "setWebViewClient");
        super.setWebViewClient(mWebViewClient);
    }

    public void setWebViewClientEx(WebViewClientEx client) {
        mWebViewClient = (WebViewClientEx) client;
        Log.d(LOGTAG, "setWebViewClientEx");
        super.setWebViewClient(mWebViewClient);
    }
    
    android.graphics.Bitmap mLastWebPageBmp = null;
    android.graphics.Canvas mLastWebPageCanvas = null;

    private android.widget.ProgressBar mProgressbar = null;
    public android.widget.ProgressBar showProcessBar() {
    	if (null == mProgressbar) {
    		Log.d(LOGTAG, "showProcessBar Create ProcessBar");
            mProgressbar = new android.widget.ProgressBar(
            		mContext, null, android.R.attr.progressBarStyleHorizontal);
            //mProgressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3, 0, 0));
            mProgressbar.setLayoutParams(
            		new LayoutParams(LayoutParams.MATCH_PARENT, 6, 0, 0)
            		);
            this.addView(mProgressbar);
    	}
        return mProgressbar;
    }
    
    private android.app.ProgressDialog mProgressDialog = null;
    public android.app.ProgressDialog getProcessDialog() {
    	return mProgressDialog;
    }

    public void clearProcessShow() {
    	if (null != mProgressDialog) {
    		mProgressDialog.dismiss();
        	mProgressDialog = null;
    	}
    	if (null != mProgressbar) {
    		mProgressbar.setVisibility(android.view.View.GONE);
    	}
    }
    
    public void showProcessDialog() {
    	mProgressDialog = android.app.ProgressDialog.show(mContext, null,"页面加载中，请稍后.."); 
    }
    
    @Override
    public void loadUrl(String url) {
    	super.loadUrl(url); 
    	//showProcessDialog();
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
    	super.loadUrl(url, additionalHttpHeaders);
    }
    
    @Override
    protected void onDraw(android.graphics.Canvas c) {
    	if (null == c) {
            super.onDraw(c);    		
    	} else {
        	//super.onDraw(c);
    		//if (true)
    		//	return;
    		if (100 == mWebChromeClient.mProgressValue) {
                super.onDraw(c);  
                if (null == mLastWebPageBmp) {
                	mLastWebPageBmp = android.graphics.Bitmap.createBitmap(
                			this.getWidth(), 
                			this.getHeight(),
                			android.graphics.Bitmap.Config.ARGB_8888);
                    mLastWebPageCanvas = new android.graphics.Canvas(mLastWebPageBmp);
                }                
                super.onDraw(mLastWebPageCanvas);
        		//c.drawBitmap(mLastWebPageBmp, 0, 0, null);  
        	} else {
                if (null == mLastWebPageBmp) {
                    super.onDraw(c);                    	
                } else {
            		c.drawBitmap(mLastWebPageBmp, 0, 0, null);
                    //super.onDraw(c);                 	
                }
        	}
    	}    		
        //}
    }
    private int clearCacheFolder(java.io.File dir, long numDays) {        
        int deletedFiles = 0;       
        if (dir!= null && dir.isDirectory()) {           
            try {              
                for (java.io.File child:dir.listFiles()) {  
                    if (child.isDirectory()) {            
                        deletedFiles += clearCacheFolder(child, numDays);        
                    }  
                    if (child.lastModified() < numDays) {   
                        if (child.delete()) {                 
                            deletedFiles++;         
                        }  
                    }  
                }           
            } catch(Exception e) {     
                e.printStackTrace();  
            }   
        }     
        return deletedFiles;   
    }  
    
    public void clearWebViewCache() {
    	Log.d(LOGTAG, "clearLocalCacheData");
    	this.clearCache(true);
    	this.clearHistory();
    	android.webkit.CookieSyncManager.createInstance(mContext);
    	android.webkit.CookieSyncManager.getInstance().startSync();
    	android.webkit.CookieManager.getInstance().removeSessionCookie();
    	android.webkit.CookieManager.getInstance().removeAllCookie();
    	clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
    	android.webkit.CookieSyncManager.getInstance().sync();  
    	
    	//mContext.getApplicationContext().deleteDatabase("webView2.db");
    	//清除缓存的有效方法  
        //this.loadDataWithBaseURL(null, "","text/html", "utf-8", null);
    	//清理Webview缓存数据库
    	//try { 
    		//mContext.getApplicationContext().deleteDatabase("webview.db");
    		//mContext.getApplicationContext().deleteDatabase("webviewCache.db"); 
    	//} catch (Exception e) {
    	//  e.printStackTrace(); 
    	//}
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    public static String mCustomUserAgent = null;
    
    public String getCustomUserAgent() {
    	return mCustomUserAgent;
    } 

    public void SetCustomUserAgent(String userAgent) {
    	mCustomUserAgent = userAgent;
    	//mCustomUserAgent = "Mozilla/5.0 (Linux; Android 5.0.2; en-us) AppleWebKit/537.36 (KHTML, like Gecko) Version/45.0.2454.95 Mobile Safari/537.36;hpdapp";
    }
    
    @Override
    public boolean showContextMenuForChild(android.view.View originalView) {
        return false;
    }

	@SuppressLint("NewApi") 
    private void updateSettings() {
		//自适应屏幕
		/*
		 * LayoutAlgorithm是一个枚举用来控制页面的布局，有三个类型：
        1.NARROW_COLUMNS：可能的话使所有列的宽度不超过屏幕宽度
        2.NORMAL：正常显示不做任何渲染
        3.SINGLE_COLUMN：把所有内容放大webview等宽的一列中
        */
		this.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		this.getSettings().setJavaScriptEnabled(true);
      //设置加载进来的页面自适应手机屏幕 
		this.getSettings().setUseWideViewPort(true);
		this.getSettings().setLoadWithOverviewMode(true);
	        //隐藏webview缩放按钮
		//mWebView.getSettings().setSupportZoom(false);
		this.getSettings().setSupportZoom(true);
		this.getSettings().setBuiltInZoomControls(false);
        //系统在大字体条件下 显示错乱
		// 设置默认的字体大小，默认为16，有效值区间在1-72之间
		this.getSettings().setDefaultFontSize(16);
		this.getSettings().setTextSize(TextSize.NORMAL);
         
		this.getSettings().setRenderPriority(RenderPriority.HIGH);
		this.getSettings().setBlockNetworkImage(false);
        // 开启 DOM storage API 功能
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setAppCacheMaxSize(1024 * 1024);
		this.getSettings().setAllowFileAccess(true);
		// 不使用缓存
		//mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// 优先使用缓存
		this.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //开启 database storage API 功能
		this.getSettings().setDatabaseEnabled(true);
		
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + WebViewEx.WEB_CACAHE_DIRNAME;
        
        //设置数据库缓存路径
        this.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        this.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        this.getSettings().setAppCacheEnabled(true);	        
        /* 开启硬件加速后webview有可能会出现闪烁的问题，解决方法是在webview中设置 
         * 把webview 中的硬件加速关闭。设置LAYER_TYPE_SOFTWARE后会把当前view转为bitmap保存
         * 这样就不能开多个webview，否则会报out of memory*/
        
        //mWebView.setScaleX();
        //mWebView.setScaleY();
        this.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null);			
        //mWebView.addJavascriptInterface(new WebAppInterface(), "ExitAndroid");
        //mWebView.setBackgroundColor(android.graphics.Color.parseColor("#000000")); //ok 不会闪黑屏
        //mWebView.setBackgroundColor(0x000000);//会闪黑屏
    }
}
