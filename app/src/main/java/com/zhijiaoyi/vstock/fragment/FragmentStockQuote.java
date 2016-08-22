package com.zhijiaoyi.vstock.fragment;

import pkg.stock.chart.*;
import pkg.stock.data.*;
import ui.activity.BaseFragment;
import ui.widget.ViewPagerEx;
import utils.*;

import com.app.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import base.app.BaseApp;

import com.zhijiaoyi.vstock.App;

import java.util.ArrayList;

public class FragmentStockQuote extends BaseFragment {

    private static final String LOGTAG = FragmentStockQuote.class.getSimpleName();
    public static String mStockCode = "300460";
    private ViewPagerEx mPager;
    private ArrayList<BaseFragment> mFragmentList = null;

    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        initViews();
        return mView;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_fragment_stockquote;
    }

    protected void initViews() {
        mPager = (ViewPagerEx) mView.findViewById(R.id.viewpager);
        initViewPager();
    }

    protected void initViewPager() {
        if (null != mPager) {
            mPager.setBackgroundColor(idColor(R.color.transparent));
            mFragmentList = new ArrayList<BaseFragment>();
            BaseFragment fragmentItem = null;
            fragmentItem = new FragmentStockQuoteRealChart();
            mFragmentList.add(fragmentItem);
            fragmentItem = new FragmentStockQuoteDayChart();
            mFragmentList.add(fragmentItem);
            Log.d(LOGTAG, "mPager.mDisableScrollMaxY:" + mPager.mDisableScrollMaxY);

            //mPager.setScanScroll(false);
            mPager.setOffscreenPageLimit(2);

            //给ViewPager设置适配器

            mPager.setAdapter(new FragmentViewPagerFragmentAdapter(getChildFragmentManager()));
            mPager.setCurrentItem(0);//设置当前显示标签页为第一页  
            mPager.setOnPageChangeListener(new ViewPageChangeListener());//页面变化时的监听器
        }
    }

    public class FragmentViewPagerFragmentAdapter extends FragmentPagerAdapter {
        public FragmentViewPagerFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    public class ViewPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

            BaseFragment fragmentItem = mFragmentList.get(arg0);
            mPager.mDisableScrollMaxY = fragmentItem.getDisableViewPagerMaxY();
        }

        @Override
        public void onPageSelected(int arg0) {
            //Log.d(LOGTAG, "onPageSelected:" + arg0);
        }
    }

    private ChartView mChartView = null;

    protected void initViews2() {
        android.widget.RelativeLayout view = (android.widget.RelativeLayout) mView.findViewById(R.id.view_stockchart);
        if (null != view) {
            if (null == mChartView) {
                //mChartView = new RealTimeChartView(this.getActivity());
                mChartView = new DayChartView(this.getActivity());
                view.addView(mChartView, new android.widget.RelativeLayout.LayoutParams(
                        android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
                        android.widget.RelativeLayout.LayoutParams.MATCH_PARENT
                ));
                mChartView.setVisibility(View.VISIBLE);
            }
            if (mChartView.getClass().getSimpleName().equals(RealTimeChartView.class.getSimpleName())) {
                (new DelayRefreshSyncTask()).execute("1");
            }
            if (mChartView.getClass().getSimpleName().equals(DayChartView.class.getSimpleName())) {
                StockDataStore_Day stockdata = getStockDataStore_Day();
                if (null != stockdata) {
                    ((DayChartView) mChartView).setDataSource(stockdata);
                }
                (new DelayRefreshSyncTask()).execute("2");
            }
        }
    }

    protected class DelayRefreshSyncTask extends AsyncTask<String, Integer, String> {
        private String mRefreshType = null;

        @Override
        protected String doInBackground(String... arg0) {
            mRefreshType = arg0[0];
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mChartView.repaint();
            if ("1".equals(mRefreshType)) {
                (new RefreshStockRealQuoteSyncTask()).execute("");
            }
            if ("2".equals(mRefreshType)) {
                (new RefreshStockDayQuoteSyncTask()).execute("");
            }
        }
    }

    // 实时数据
    private DataEntity_StockChartMinute mStockChartMinData = null;

    protected DataEntity_StockChartMinute getStockDataReal_Min() {
        if (null == mStockChartMinData)
            mStockChartMinData = new DataEntity_StockChartMinute();
        return mStockChartMinData;
    }

    // 刷新实时行情数据
    protected class RefreshStockRealQuoteSyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            //StockData_QQ.getStockData(mStockCode);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ((RealTimeChartView) mChartView).setStockMinChartData(mStockChartMinData);
        }
    }

    // 日线数据
    private StockDataStore_Day mStockChartDayData = null; //new StockDataStore_Day("");

    protected StockDataStore_Day getStockDataStore_Day() {
        if (null == mStockChartDayData) {
            mStockChartDayData = new StockDataStore_Day();
            mStockChartDayData.loadStock(mStockCode);
        }
        return mStockChartDayData;
    }

    // 刷新日线数据
    protected class RefreshStockDayQuoteSyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {
            StockData_163.getStockDayData(getStockDataStore_Day());
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mChartView.repaint();
        }
    }
}
