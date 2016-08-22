package com.hepaidai.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.app.R;

import base.app.BaseApp;
import base.app.BaseConsts;
import base.app.ManagerActivities;
import base.app.ManagerAppConfig;
import base.app.ManagerFactory;
import base.app.Version;
import ui.activity.BaseActivity;
import ui.widget.WebChromeClientEx;
import ui.widget.WebViewClientEx;
import ui.widget.WebViewEx;
import utils.Log;

public class MainActivity extends BaseActivity {

	private static final String LOGTAG = MainActivity.class.getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //View viewToLoad = LayoutInflater.from(this.getParent()).inflate(R.layout.pkg_hepaidai_activity_main, null);
        //this.setContentView(viewToLoad);
        
        setContentView(R.layout.pkg_hepaidai_activity_main);
        Log.d(LOGTAG, MainActivity.class.getSimpleName() + " OnCreate");
        createMessageLoopHandler();

        initGuide();   
        initWebView();
   	    loadWebViewUrl();  	
   	    initSplash();
        //hideGuide();
   	    //hideSplash();
        //base.app.BaseApp.instance.showToast(config.channelId);
   	    /*//
   	    String channel = AppChannel.getChannel(this);
   	    if (TextUtils.isEmpty(channel)) {
   		    channel = "no channel";
   	    }
        base.app.BaseApp.instance.showToast(channel);
   	    //*/
        base.app.BaseApp.instance.checkAppUpdate();
        //com.hepaidai.UserLogin.userLogin();
        //this.redirectToActivity(com.app.activity.ActivityAppGuide.class);
    }

    // 点击退出时记录时间
    private long mFirstBackPressTime = 0;
    @Override
    public void onBackPressed()
    {
    	long secondTime = System.currentTimeMillis();
        if (secondTime - mFirstBackPressTime > 3000)
        {
            BaseApp.showToast("再按一次退出客户端");
            mFirstBackPressTime = secondTime;// ����firstTime
        } else
        {
            ManagerActivities.getActivitiesManager(ManagerFactory.getManagerFactory()).AppExit(this);
        }
    }
    
    private WebViewEx mWebView = null;
    //private com.base.uiwidget.WebViewEx mWebView = null;

    private void loadLocalErrorWebPage() {
    	Log.d(LOGTAG, " loadLocalErrorWebPage ");
    	mWebView.loadUrl("file:///android_asset/webpage/errorPage.html");    	
    }

    @Override
	protected boolean handleActivityMessage(Message msg){
		return super.handleActivityMessage(msg);
	}
	
    protected void loadWebViewUrl() {

        if (null == mWebView) 
        	return;
    	//Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
    	//additionalHttpHeaders.put("app_token", "");
    	//mWebView.loadUrl("http://www.hepaidai.com", additionalHttpHeaders);
    	if (!net.Connect.isConnected()) {
    		loadLocalErrorWebPage();
    	} else {
            String url = null;
       	    ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();   
            if (TextUtils.isEmpty(config.channelId)) {
                url = "http://" + BaseConsts.APP_SITE() + "/" ;            	
            } else {
            	url = "http://" + BaseConsts.APP_SITE() + "?" + "appchannel=" + config.channelId;
            }
            Log.d(LOGTAG, " loadWebViewUrl:" + url);
            mWebView.loadUrl(url);        		
    	}	                
        //loadLocalErrorWebPage();
        //mWebView.loadUrl("http://www.test.hepaidai.org");
        //mWebView.loadUrl("http://192.168.10.81/");
        //mWebView.loadUrl("file:///android_asset/webpage/errorPage.html"); 
    }
    
	protected void initWebView() {
		RelativeLayout layoutWebView = (RelativeLayout) this.findViewById(R.id.layout_webview);
		if (null != layoutWebView) {
			if (null == this.getParent()) {
				mWebView = new ui.widget.WebViewEx(this);
				Log.d(LOGTAG, " new WebView self ");
			} else {
				mWebView = new ui.widget.WebViewEx(this.getParent());
	            Log.d(LOGTAG, " new WebView parent ");
			}
			//mWebView = new android.webkit.WebView(this);
			layoutWebView.addView(mWebView, new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT));
	        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
			updateWebViewSettings();
			mWebView.setWebViewClient(new WebViewClientEx(){   
				@Override
	            public void onReceivedError(WebView view, int errorCode,
                        String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理�?                    loadLocalErrorWebPage();
                } 
				
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	            	//Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
	            	//additionalHttpHeaders.put("app_token", "");
	            	//view.loadUrl(url, additionalHttpHeaders);
	            	//view.loadUrl(url);
	            	//Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
	            	//additionalHttpHeaders.put("app_token", "");
	            	//view.loadUrl(url, additionalHttpHeaders);
	            	//com.base.app.BaseApp.showToast("loading:" + url);
	            	//得到网络连接信息  
	            	if (!net.Connect.isConnected()) {
	            		loadLocalErrorWebPage();
                    	return true;
	            	}	     
	            	Log.d(MainActivity.class.getSimpleName() + ".java", "shouldOverrideUrlLoading:" + url);
	            	if (!TextUtils.isEmpty(url)) {
	            		
	            		Log.d(MainActivity.class.getSimpleName() + ".java", "shouldOverrideUrlLoading:" + url.indexOf("tel:"));
		            	if (0 == url.toLowerCase().indexOf("tel:")) {
		            		String phonenum = url.substring(4);
		            		// tel:4006800088
	            			Log.d(MainActivity.class.getSimpleName() + ".java", "shouldOverrideUrlLoading tel" + phonenum);
	    	            	if (!TextUtils.isEmpty(phonenum)) {   
	    	                    Intent intent = new Intent();  
	    	                    //intent.setAction(Intent.ACTION_CALL); // 直接拨打
	    	                    intent.setAction(Intent.ACTION_DIAL); // 进入拨号界面
	    	                    //url:统一资源定位符  
	    	                    //uri:统一资源标示符（更广）  
	    	                    intent.setData(Uri.parse(url));  
	    	                    //开启系统拨号器  
	    	                    startActivity(intent);  
	    	            	}
		            		return true;
		            	}
	            		if (0 < url.indexOf("logout")) {
	            			//com.base.app.BaseApp.showToast("logout");
	            			Log.d(MainActivity.class.getSimpleName() + ".java", "shouldOverrideUrlLoading: logout");
	            			((WebViewEx)view).clearWebViewCache();
	            		}
	            	}
	            	String userAgent = ((WebViewEx)view).getCustomUserAgent();
	            	//BaseApp.showToast("userAgent:" + userAgent);
	            	if (!TextUtils.isEmpty(userAgent)) {
		            	view.getSettings().setUserAgentString(userAgent);	            		
	            	}
	            	view.loadUrl(url);
	                return true;
	            }
	        });
			// 如果访问的页面中有Javascript，则webview必须设置支持Javascript
		    //WebSettings websettings = mWebView.getSettings();
			// 判断页面加载过程
			mWebView.setWebChromeClient(new WebChromeClientEx() {
			    @Override
			    public void onProgressChanged(WebView view, int newProgress) {
			    	// 这个有点意�?			    	// 有时�?居然显示部分 ???
			    	if (90 < newProgress) {
			    		hideSplash();
			    	}
			    	super.onProgressChanged(view, newProgress);
			    }				
			});
		}
	}

	public class ViewPagerAdapter extends PagerAdapter{  
	    public java.util.List<View> views; //界面列表 
	    //底部小点图片  
	    public int enteringIndex = 0;
	    public int currentIndex = 0;
	    
	      
	    public void setCurrentIndex(int index) {
	    	setDotIndex(currentIndex, index);
	    	currentIndex = index;
	    }
	    
	    public ViewPagerAdapter (java.util.List<View> views){  
	        this.views = views;  
	        setDotIndex(0, 0);
	    }  
	  
	    //销毁arg1位置的界�? 
	    @Override  
	    public void destroyItem(View arg0, int arg1, Object arg2) {  
	        ((ViewPager) arg0).removeView(views.get(arg1));       
	    }  
	  
	    @Override  
	    public void finishUpdate(View arg0) {}  
	  
	    //获得当前界面�? 
	    @Override  
	    public int getCount() {  
	        if (null == views) 
	        	return 0;  
	        return views.size();	          
	    }  
	      
	  
	    //初始化arg1位置的界�? 
	    @Override  
	    public Object instantiateItem(View arg0, int arg1) {  
	        ((ViewPager) arg0).addView(views.get(arg1), 0);  
	        return views.get(arg1);  
	    }  
	  
	    //判断是否由对象生成界�? 
	    @Override  
	    public boolean isViewFromObject(View arg0, Object arg1) {  
	        return (arg0 == arg1);  
	    }  
	  
	    @Override  
	    public void restoreState(Parcelable arg0, ClassLoader arg1) {}  
	  
	    @Override  
	    public Parcelable saveState() {  
	        return null;  
	    }  
	  
	    @Override  
	    public void startUpdate(View arg0) {}  
	}

    public android.widget.ImageView[] mImage_Dots = null;
	protected void initDots (int dotcount) {
		android.widget.LinearLayout layout_viewpager_dots = (android.widget.LinearLayout) 
				findViewById(R.id.layout_viewpager_dots);
	    if (null == layout_viewpager_dots) 
	    	return;
		mImage_Dots = new android.widget.ImageView[dotcount]; 
      //循环取得小点图片  
        for (int i = 0; i < dotcount; i++) {
        	android.widget.ImageView image_dot = new android.widget.ImageView(this);
        	image_dot.setPadding(20, 20, 20, 20);
        	mImage_Dots[i] = image_dot; 
        	layout_viewpager_dots.addView(image_dot);
        	image_dot.setImageResource(R.drawable.img_viewpager_dot);
    	}		
	}

	protected void setDotIndex (int oldIndex, int newIndex) {
		if (null == mImage_Dots)
			return;
		mImage_Dots[oldIndex].setImageResource(R.drawable.img_viewpager_dot);
		mImage_Dots[newIndex].setImageResource(R.drawable.img_viewpager_dot_active);
	}
	
	public void initGuide() {
	    View guideView = null; 
	    guideView = MainActivity.this.findViewById(R.id.layout_guide);
    	if (null == guideView) 
    		return; 
    	ViewPager viewpager = (ViewPager)MainActivity.this.findViewById(R.id.viewpager);
   	    ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig(); 
        Log.d(LOGTAG, " showAppGuide:" + config.showAppGuide);
   	    if (0 == config.showAppGuide) {  
   	        config.showAppGuide = 1;
   	        ManagerAppConfig.Save(config);
   	    	if (View.VISIBLE != guideView.getVisibility()) {
   	    		guideView.setVisibility(View.VISIBLE);
   	    	}
   	    	if (View.VISIBLE != viewpager.getVisibility()) {
   	    		viewpager.setVisibility(View.VISIBLE);
   	    	}
   	    } else {
   	    	if (null != viewpager) {
   	   	    	if (View.VISIBLE != viewpager.getVisibility()) {
   	   	    		viewpager.setVisibility(View.GONE);
   	   	    	}
   	    	}
   	    	if (View.GONE != guideView.getVisibility()) {
   	    		guideView.setVisibility(View.GONE);
   	    	}
	    	return;
   	    }   	
    	java.util.List<View> guideviews = new java.util.ArrayList<View> ();

    	android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
    	View pageview = null;
    	android.widget.ImageView imageview = null;
    	pageview = inflater.inflate(R.layout.pkg_app_layout_guide_item, null);
    	imageview = (android.widget.ImageView) pageview.findViewById(R.id.imageview);
    	imageview.setBackgroundResource(R.drawable.app_welcome_guide1);
    	guideviews.add(pageview);

    	pageview = inflater.inflate(R.layout.pkg_app_layout_guide_item, null);
    	imageview = (android.widget.ImageView) pageview.findViewById(R.id.imageview);
    	imageview.setBackgroundResource(R.drawable.app_welcome_guide2);
    	guideviews.add(pageview);

    	pageview = inflater.inflate(R.layout.pkg_app_layout_guide_item, null);
    	imageview = (android.widget.ImageView) pageview.findViewById(R.id.imageview);
    	imageview.setBackgroundResource(R.drawable.app_welcome_guide3);
    	guideviews.add(pageview);

    	pageview = inflater.inflate(R.layout.pkg_app_layout_guide_item, null);
    	imageview = (android.widget.ImageView) pageview.findViewById(R.id.imageview);
    	imageview.setBackgroundResource(R.drawable.app_welcome_guide4);
    	View button = pageview.findViewById(R.id.button);
    	if (null != button) {
    		button.setVisibility(View.VISIBLE);
    		button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hideGuide();
				}
    		});
    	}
    	guideviews.add(pageview);
    	
    	initDots(guideviews.size());
    	mViewpagerAdapter = new ViewPagerAdapter(guideviews);
		viewpager.setAdapter(mViewpagerAdapter);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (1 == arg0) {
					/* 默示正在滑动  */
				}
				if (2 == arg0) {
					/*滑动完毕 */				
				}
				if (0 == arg0) {
					/* 默示什么都没做 */
				}
		    }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
		    }

			@Override
			public void onPageSelected(int arg0) {
				mViewpagerAdapter.setCurrentIndex(arg0);
			}
		});

		viewpager.setPageTransformer(true, new PageTransformer() {			
			@Override
			public void transformPage(View view, float position) {
				int pageWidth=view.getWidth();
				int pageHeight=view.getHeight();
				
				if(position<-1){
					view.setAlpha(0);
				}else if (position<=1){
					float scaleFactor=Math.max(MIN_SCALE, 1-Math.abs(position));
					float vertMargin = pageHeight * (1 - scaleFactor) / 2;
		            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
		            if (position < 0) {
		                view.setTranslationX(horzMargin - vertMargin / 2);
		            } else {
		                view.setTranslationX(-horzMargin + vertMargin / 2);
		            }

		            view.setScaleX(scaleFactor);
		            view.setScaleY(scaleFactor);
		            view.setAlpha(MIN_ALPHA +
		                    (scaleFactor - MIN_SCALE) /
		                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));
				}else { 
		            view.setAlpha(0);
		        }
			}
		});
	}

	private ViewPagerAdapter mViewpagerAdapter = null;
	private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.8f;

    private TranslateAnimation mHideGuideAnimation = null;
	public void hideGuide() {
	    View guideView = null; 
	    guideView = MainActivity.this.findViewById(R.id.layout_guide);
    	if (null == guideView) 
    		return;
    	if (View.GONE == guideView.getVisibility()) 
    		return;
    	//guideView.setVisibility(View.GONE);
    	if (null != mHideGuideAnimation) 
    		return;
    	mHideGuideAnimation = new TranslateAnimation(0.0f, -guideView.getWidth(), 0.0f,0.0f);
    	mHideGuideAnimation.setDuration(400);
    	mHideGuideAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				View guideView = MainActivity.this.findViewById(R.id.layout_guide);
				if (null == guideView) 
					return;
				guideView.setVisibility(View.GONE);
				mHideGuideAnimation = null;
		    	guideView = null;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
    	}); 
    	guideView.startAnimation(mHideGuideAnimation);
	}
	

	public class HideAnimation extends TranslateAnimation{

		public View hideView = null;
		
		public HideAnimation(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public HideAnimation(float fromXDelta, float toXDelta,
				float fromYDelta, float toYDelta) {
			super(fromXDelta, toXDelta, fromYDelta, toYDelta);
		}

		public HideAnimation(int fromXType, float fromXValue, int toXType,
				float toXValue, int fromYType, float fromYValue, int toYType,
				float toYValue) {
			super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType,
					toYValue);
		}		
	}

	protected void initSplash() {
		android.widget.TextView textview = (android.widget.TextView) MainActivity.this.findViewById(R.id.textview);
		if (null == textview) 
			return;
		textview.setText("V " + Version.getVersionName(null));
	}
	
    private TranslateAnimation mHideSplashAnimation = null;
	public void hideSplash() {
	    View splashView = null; 
	    splashView = MainActivity.this.findViewById(R.id.layout_splash);
    	if (null == splashView) 
    		return;
    	if (View.GONE == splashView.getVisibility()) 
    		return;
    	if (null != mHideSplashAnimation) 
    		return;
		mHideSplashAnimation = new TranslateAnimation(0.0f, -splashView.getWidth(), 0.0f,0.0f);
		mHideSplashAnimation.setDuration(400);
		mHideSplashAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				View splashView = MainActivity.this.findViewById(R.id.layout_splash);
				if (null == splashView) 
					return;
				splashView.setVisibility(View.GONE);
		    	mHideSplashAnimation = null;
		    	splashView = null;
				// show update dialog test
				//android.content.Intent i = new android.content.Intent(
						//		MainActivity.this, com.app.activity.DialogAppUpdate.class);
					//MainActivity.this.startActivityForResult(i, 0);  
			}

			@Override
			public void onAnimationRepeat(Animation animation) {									
			}				    			
		});
		splashView.startAnimation(mHideSplashAnimation);
	}

    public void updateWebViewSettings() {
		//自适应屏幕
		/*
		 * LayoutAlgorithm是一个枚举用来控制页面的布局，有三个类型�?        1.NARROW_COLUMNS：可能的话使所有列的宽度不超过屏幕宽度
        2.NORMAL：正常显示不做任何渲�?        3.SINGLE_COLUMN：把所有内容放大webview等宽的一列中
        */
    	Log.d(MainActivity.class.getSimpleName() + ".java", "updateWebViewSettings");
    	mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
    	mWebView.getSettings().setJavaScriptEnabled(true);
    	mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//允许js弹出窗口
      //设置加载进来的页面自适应手机屏幕 
    	mWebView.getSettings().setUseWideViewPort(true);
    	mWebView.getSettings().setLoadWithOverviewMode(true);
	        //隐藏webview缩放按钮
		//mWebView.getSettings().setSupportZoom(false);
    	mWebView.getSettings().setSupportZoom(true);
    	mWebView.getSettings().setBuiltInZoomControls(false);
    	
        //系统在大字体条件�?显示错乱
		// 设置默认的字体大小，默认�?6，有效值区间在1-72之间
    	mWebView.getSettings().setDefaultFontSize(16);
    	mWebView.getSettings().setTextSize(TextSize.NORMAL);
         
    	mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
    	mWebView.getSettings().setBlockNetworkImage(false);
        // 开�?DOM storage API 功能
    	mWebView.getSettings().setDomStorageEnabled(true);
    	mWebView.getSettings().setAppCacheMaxSize(1024 * 1024);
    	mWebView.getSettings().setAllowFileAccess(true);
    	mWebView.getSettings().setAllowContentAccess(true);
    	mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
    	mWebView.getSettings().setLoadWithOverviewMode(true);
    	mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		// 不使用缓�?    	//mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// 优先使用缓存
    	//mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    	mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
/*
1.LOAD_CACHE_ONLY //不使用网络，只读取本地缓存数�?
2.LOAD_DEFAULT //根据cache-control决定是否从网络上取数据�?
3.LOAD_CACHE_NORMAL //API level 17中已经废�? 从API level 11开始作用同LOAD_DEFAULT模式 
4.LOAD_NO_CACHE //不使用缓存，只从网络获取数据 
5.LOAD_CACHE_ELSE_NETWORK //只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
*/
    	// 避免保存密码 对话�?    	
    	if (Build.VERSION.SDK_INT <= 18) {
    		mWebView.getSettings().setSavePassword(false);
    	}
    	//mWebView.getSettings().setSavePassword(false);
    	//mWebView.getSettings().setSaveFormData(false);
    	
		//mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //开�?database storage API 功能
    	mWebView.getSettings().setDatabaseEnabled(true);
		
        String cacheDirPath = getFilesDir().getAbsolutePath() + WebViewEx.WEB_CACAHE_DIRNAME;
        
        //设置数据库缓存路�?        mWebView.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        //开�?Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);
        
        //String user_agent = mWebView.getSettings().getUserAgentString() + ";hpdapp";
        //mWebView.getSettings().setUserAgentString(user_agent);
        if (TextUtils.isEmpty(mWebView.getCustomUserAgent())) {
            mWebView.SetCustomUserAgent(mWebView.getSettings().getUserAgentString() + ";hpdapp");
            //com.base.app.BaseApp.showToast("user_agent:" + user_agent);
            mWebView.getSettings().setUserAgentString(mWebView.getCustomUserAgent());
        }
        //user_agent = mWebView.getSettings().getUserAgentString();
        //com.base.app.BaseApp.showToast("user_agent:" + user_agent);
        /* 开启硬件加速后webview有可能会出现闪烁的问题，解决方法是在webview中设�?
         * 把webview 中的硬件加速关闭。设置LAYER_TYPE_SOFTWARE后会把当前view转为bitmap保存
         * 这样就不能开多个webview，否则会报out of memory*/
        
        //mWebView.setScaleX();
        //mWebView.setScaleY();
        mWebView.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null);			
        //mWebView.addJavascriptInterface(new WebAppInterface(), "ExitAndroid");
        //mWebView.setBackgroundColor(android.graphics.Color.parseColor("#000000")); //ok ����������
        //mWebView.setBackgroundColor(0x000000);//��������
    }
    
    public class WebAppInterface {

        public WebAppInterface() {
        }

        @JavascriptInterface //sdk17版本以上加上注解
        public void LogOut() {
        }
    }

}