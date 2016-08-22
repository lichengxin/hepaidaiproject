package com.zhijiaoyi.vstock.fragment;

import java.text.*;

import pkg.stock.chart.*;
import pkg.stock.chart.DayChartView.StockCandleChartData;
import pkg.stock.data.*;

import ui.activity.BaseFragment;
import ui.widget.UtilsText;
import utils.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import base.app.BaseApp;

import com.app.R;
import com.zhijiaoyi.vstock.App;

public class FragmentStockQuoteDayChart extends BaseFragment  {

	private static final String LOGTAG = FragmentStockQuoteDayChart.class.getSimpleName();

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);
		initViews();
		return mView;
	}

	private android.support.v4.view.ViewPager mPager = null;  

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_fragment_quote_daychart;
    }

    @Override
	public int getDisableViewPagerMaxY() {
    	if (null == mChartView) {
    		return 0;
    	} else {
    		return mChartView.getHeight();
    		
    	}
	}

	public int colorPriceOffset0() {
		return idColor(R.color.white);
	}

	public int colorPriceOffsetUp() {
		return idColor(R.color.red);
	}

	public int colorPriceOffsetDown() {
		return idColor(R.color.green);
	}
	
    private DecimalFormat mDisplayPriceFmt =new DecimalFormat("0.00");
    private SimpleDateFormat mDisplayDateFmt = new SimpleDateFormat("yyyyMMdd");
    private ChartView mChartView = null;
	protected void initViews() {
		android.widget.RelativeLayout view = (android.widget.RelativeLayout) mView.findViewById(R.id.view_stockchart);
		if (null != view) {
			if (null == mChartView) {
				mChartView = new DayChartView(this.getActivity());
				view.addView(mChartView, new android.widget.RelativeLayout.LayoutParams(
						android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
						android.widget.RelativeLayout.LayoutParams.MATCH_PARENT
						));
				mChartView.mChartCoordNotifier = new DayChartCoordNotifier();
				mChartView.setVisibility(View.VISIBLE);
			}
			StockDataStore_Day stockdata = getStockDataStore_Day();
			if (null != stockdata) {
				((DayChartView) mChartView).setDataSource(stockdata);
			}
			(new DelayRefreshSyncTask()).execute("2");
		}
	}

	public class DayChartCoordNotifier extends ChartCoordNotifier {
		
		private void updatePriceView(int resId, int price, int preprice) {
			View view = mView.findViewById(resId);
			if (null != view) {
				UtilsText.updateViewText(view, mDisplayPriceFmt.format((double)price / 1000));
				if (0 < preprice) {
					int color = colorPriceOffset0();
					if (price != preprice) {
						if (price > preprice) {
							color = colorPriceOffsetUp();
						} else {
							color = colorPriceOffsetDown();						
						}
					}
					((TextView) view).setTextColor(color);
				}
			}
		}
		
		@Override
		public void OnShow(ChartData AChartData, int AChartDataIndex, int ACursorIndex) {
			DayChartView.StockCandleChartData chartData = (DayChartView.StockCandleChartData) AChartData;
			View view = null;
			view = mView.findViewById(R.id.txt_date);
			UtilsText.updateViewText(view, mDisplayDateFmt.format(DelphiUtils.DelphiTime2JavaTime(chartData.price_date[AChartDataIndex])));
			view = mView.findViewById(R.id.txt_volume);
			UtilsText.updateViewText(view, mDisplayPriceFmt.format((double)chartData.volume[AChartDataIndex] / 1000000) + "万");
			view = mView.findViewById(R.id.txt_amount);
			UtilsText.updateViewText(view, mDisplayPriceFmt.format((double)chartData.amount[AChartDataIndex] / 100000000) + "亿");
			
			int pricepreclose = 0;
			if (0 < AChartDataIndex) {
				pricepreclose = chartData.price_close[AChartDataIndex - 1];
			} else {
				pricepreclose = chartData.firstPreClosePrice;
			}
			
			updatePriceView(R.id.txt_quote_close, chartData.price_close[AChartDataIndex], pricepreclose);
			updatePriceView(R.id.txt_quote_open, chartData.price_open[AChartDataIndex], pricepreclose);
			updatePriceView(R.id.txt_quote_high, chartData.price_high[AChartDataIndex], pricepreclose);
			updatePriceView(R.id.txt_quote_low, chartData.price_low[AChartDataIndex], pricepreclose);
			
			if (0 < pricepreclose) {
				int offset = 0;
				int color = colorPriceOffset0();
				offset = chartData.price_close[AChartDataIndex] - pricepreclose;
				if (0 != offset) {
					if (0 < offset) {
						color = colorPriceOffsetUp();
					} else {
						color = colorPriceOffsetDown();
					}
				}
				view = mView.findViewById(R.id.txt_quote_priceoffset);
				UtilsText.updateViewText(view, mDisplayPriceFmt.format((double)offset / 1000));
				((TextView) view).setTextColor(color);

				view = mView.findViewById(R.id.txt_quote_priceoffsetrate);
				UtilsText.updateViewText(view, mDisplayPriceFmt.format((double)(offset * 100) / pricepreclose) + "%");
				((TextView) view).setTextColor(color);
			}
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
			(new RefreshStockDayQuoteSyncTask()).execute("");	
		}
	}
	// 日线数据
	private StockDataStore_Day mStockChartDayData = null; //new StockDataStore_Day("");
	protected StockDataStore_Day getStockDataStore_Day() {
		if (null == mStockChartDayData) {
			mStockChartDayData = new StockDataStore_Day();
			mStockChartDayData.loadStock(FragmentStockQuote.mStockCode);
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
