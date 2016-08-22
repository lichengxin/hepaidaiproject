package com.zhijiaoyi.vstock.fragment;

import pkg.stock.chart.*;
import pkg.stock.data.*;
import ui.activity.BaseFragment;
import utils.*;

import com.app.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import base.app.BaseApp;

import com.zhijiaoyi.vstock.App;

public class FragmentStockQuoteRealChart extends BaseFragment  {

	private static final String LOGTAG = FragmentStockQuoteRealChart.class.getSimpleName();

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);
		initViews();
		return mView;
	}

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_fragment_quote_realtime;
    }

    @Override
	public int getDisableViewPagerMaxY() {
    	if (null == mChartView) {
    		return 0;
    	} else {
    		return mChartView.getHeight();
    		
    	}
	}
	
    private ChartView mChartView = null;
	protected void initViews() {
		android.widget.RelativeLayout view = (android.widget.RelativeLayout) mView.findViewById(R.id.view_stockchart);
		if (null != view) {
			if (null == mChartView) {
				mChartView = new RealTimeChartView(this.getActivity());
				((RealTimeChartView) mChartView).setStockMinChartData(getStockDataReal_Min());
				view.addView(mChartView, new android.widget.RelativeLayout.LayoutParams(
						android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
						android.widget.RelativeLayout.LayoutParams.MATCH_PARENT
						));
				mChartView.setVisibility(View.VISIBLE);
			}
			(new DelayRefreshSyncTask()).execute("");
		}
	}

	protected class DelayRefreshSyncTask extends AsyncTask<String, Integer, String> {
		
		@Override
		protected String doInBackground(String... arg0) {
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
			(new RefreshStockRealQuoteSyncTask()).execute("");			
		}
	}
	// 实时数据

	private StockDataQuote.InstantData mStockQuoteInstantData = null; 
	
	private StockDataQuote.InstantData getStockQuoteInstantData() {
		if (null == mStockQuoteInstantData) {
			mStockQuoteInstantData = new StockDataQuote.InstantData(); 
		}
		return mStockQuoteInstantData;
	}
	
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
			//StockData_QQ.getStockData(FragmentStockQuote.mStockCode);
			StockDataQuote.InstantData instantData = getStockQuoteInstantData();
			String returnData = StockData_Sina.getStockInstantData(FragmentStockQuote.mStockCode);
			
			StockData_Sina.parseSingleStockInstantData(instantData, returnData);
			DataEntity_StockChartMinute minuteData = getStockDataReal_Min();
			if (0 == minuteData.price_preclose) {
				if (0 < instantData.price_preclose) {
				    minuteData.price_preclose = instantData.price_preclose;
				}
   			}
			if (0 < minuteData.price_preclose) {
				StockData_Sina.getStockMinuteData(minuteData, FragmentStockQuote.mStockCode);				
			}
			return null;
		}
		
		@Override  
	    protected void onPostExecute(String result) {
			mChartView.repaint();
		}
	}
}
