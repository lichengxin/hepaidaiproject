package com.app.activity;

import java.util.ArrayList;

import ui.activity.BaseActivity;
import utils.*;

import com.app.R;


import base.app.*;


import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/*//
 * 通用页面 -- 应用模块 -- 引导界面
 * 欢迎向导
 * 
//*/
public class ActivityAppGuide extends BaseActivity implements OnPageChangeListener {

	private static String LOGTAG = ActivityAppGuide.class.getSimpleName();
	private ArrayList<android.view.View> mPageViews;
	private android.view.LayoutInflater mInflater;
	private android.widget.LinearLayout mGroups;
	private android.widget.FrameLayout mMain;
	private ViewPager mViewPager;
	private android.widget.ImageView[] mImageViews;

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		

   	    ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
   	    config.showAppGuide = 1;
   	    ManagerAppConfig.Save(config);
   	    
		setContentView(R.layout.pkg_app_activity_guide);
		// initViews();
		mInflater = android.view.LayoutInflater.from(this);
		mPageViews = new ArrayList<android.view.View>();
		/*//
		mPageViews.add(mInflater.inflate(R.layout.layout_app_guide1, null));
		mPageViews.add(mInflater.inflate(R.layout.layout_app_guide2, null));
		mPageViews.add(mInflater.inflate(R.layout.layout_app_guide3, null));

		mMain = (FrameLayout) mInflater.inflate(R.layout.activity_app_guide, null);
		mGroups = (LinearLayout) mMain.findViewById(R.id.viewGroup);
		mViewPager = (ViewPager) mMain.findViewById(R.id.guidePages);
		mImageViews = new ImageView[mPageViews.size()];
		//*/
		//android.view.ViewGroup.LayoutParams layoutparams = null;
		if (true)
			return;
		android.widget.LinearLayout.LayoutParams layoutparams = null;
		for (int i = 0; i < mPageViews.size(); i++) {
			android.widget.ImageView iv = new android.widget.ImageView(this);
			//layoutparams = new android.view.ViewGroup.LayoutParams(20, 20);
			//layoutparams = new LinearLayout.LayoutParams(20, 20);
			layoutparams = new android.widget.LinearLayout.LayoutParams(
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 
					android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			if  (0 != i) {
			    layoutparams.setMargins(SysScreen.dip2px(20), 0,  0,  0);
			}
			iv.setLayoutParams(layoutparams);
			/*//
			mImageViews[i] = iv;
			if (0 == i) {
				mImageViews[i].setBackgroundResource(R.drawable.dot_blue);
			} else {
				mImageViews[i].setBackgroundResource(R.drawable.dot_gray);
			}
			mGroups.addView(mImageViews[i]);
			//*/
		}
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(this);
		setContentView(mMain);
	}

	private PagerAdapter mPageAdapter = new PagerAdapter() {
		@Override
		public void startUpdate(android.view.View arg0) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public boolean isViewFromObject(android.view.View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(android.view.View arg0, int arg1) {
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public void finishUpdate(android.view.View arg0) {}

		@Override
		public void destroyItem(android.view.View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	private void initViews() {
	}

	private boolean mIsScrolled = false;
	
	@Override
	public void onPageScrollStateChanged(int state) {
		switch (state) {		
		    case ViewPager.SCROLL_STATE_DRAGGING:			
		    	mIsScrolled = false;			
		    	break;		
		    case ViewPager.SCROLL_STATE_SETTLING:			
		    	mIsScrolled = true;			
		    	break;		
		    case ViewPager.SCROLL_STATE_IDLE:			
		    	if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1 && !mIsScrolled) {
		    		ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
			    	if (null != config) {
			    		config.showAppGuide = 1;
			    		ManagerAppConfig.Save(config);    		
			    	}
			    	/*//
					AppUserManager usermgr = App.core.getUserManager();
					if (null != usermgr) {
						usermgr.checkAutoLoginUserAccount();
						if (usermgr.isLogined()) {
							showMainActivity();
						} else {
				    		redirectToActivity(UserLoginActivity.class);				
						}       
					}
					//*/
		    		ActivityAppGuide.this.finish();			
		    	}			
		    	mIsScrolled = true;			
		    	break;		
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		/*//
		for (int i = 0; i < mPageViews.size(); i++) {
			mImageViews[position].setBackgroundResource(R.drawable.dot_blue);
			if (position != i) {
				mImageViews[i].setBackgroundResource(R.drawable.dot_gray);
			}
		}
		//*/
	}

}
