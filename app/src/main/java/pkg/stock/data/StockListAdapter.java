package pkg.stock.data;

import java.util.List;

import ui.widget.UtilsText;
import utils.SysScreen;

import com.app.R;


import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class StockListAdapter extends android.widget.BaseAdapter{

	private android.content.Context mContext = null;
	private int mListItemLayoutId = 0;
	private List<StockItem> mItems;
	// X轴方向的偏移量  

    private java.util.ArrayList<android.view.View> mMovableViewList = null;
    public void setMovableViewList(java.util.ArrayList<android.view.View> movableViewList) {
    	mMovableViewList = movableViewList;
    }
	
	public StockListAdapter (android.content.Context context, int listItemLayoutId, List<StockItem> items) {
		mContext = context;
		mListItemLayoutId = listItemLayoutId;
		mItems = items;
	}

	public void updateListView(List<StockItem> items) {
		mItems = items;
		notifyDataSetChanged();
	}

	private java.util.List<StockDataQuote.InstantData> mStockDataQuotes = new java.util.ArrayList<StockDataQuote.InstantData> ();
	public java.util.List<StockDataQuote.InstantData> getStockDataQuotes() {
		if (null == mStockDataQuotes) {
			mStockDataQuotes = new java.util.ArrayList<StockDataQuote.InstantData> ();
		}
	    return mStockDataQuotes;	
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    private class StockListItemViewHolder {
    	private TextView mTextViewStockName = null;
    	private TextView mTextViewStockCode = null;
    	private TextView mTextViewPriceLastest = null;
    	private TextView mTextViewPriceOffsetRate = null;
    	private TextView mTextViewPriceOpen = null;
    	private TextView mTextViewPriceHigh = null;
    	private TextView mTextViewPriceLow = null;
    	private TextView mTextViewPricePreClose = null;
    	
    	private StockDataQuote.InstantData stockQuote = null;        
    }

	public void addStockDataQuote(StockDataQuote.InstantData quote) {
		getStockDataQuotes().add(quote);
	}

    public java.text.DecimalFormat getPriceFormatter() {
	    return new java.text.DecimalFormat("0.00");
    }

	private android.view.View buildListViewLayout() {  
		android.widget.LinearLayout result = new android.widget.LinearLayout(mContext); 
		StockListItemViewHolder holder = new StockListItemViewHolder(); 
		holder.stockQuote = new StockDataQuote.InstantData();
		addStockDataQuote(holder.stockQuote);

		android.widget.LinearLayout fixHeadLayout = new android.widget.LinearLayout(mContext);  
  
    	holder.mTextViewStockName = new android.widget.TextView(mContext);
    	holder.mTextViewStockName.setText("名称");  
    	holder.mTextViewStockName.setGravity(android.view.Gravity.CENTER);   
            //tx.setTextColor(idColor(R.color.font_content));
	        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
	    fixHeadLayout.addView(holder.mTextViewStockName, 0, new LayoutParams(200, SysScreen.dip2px(50)));  
	    result.addView(fixHeadLayout);  
	    
	    android.widget.LinearLayout movableLayout = new android.widget.LinearLayout(mContext);  
  
	    if (null != mMovableViewList) {
	    	mMovableViewList.add(movableLayout);
	    }
        holder.mTextViewPriceLastest = new android.widget.TextView(mContext);  
        holder.mTextViewPriceLastest.setText("最新价");  
        holder.mTextViewPriceLastest.setGravity(android.view.Gravity.CENTER);  
        //tx.setTextColor(getResources().getColor(R.color.font_content));
        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
        movableLayout.addView(holder.mTextViewPriceLastest, 200, SysScreen.dip2px(50));  

        holder.mTextViewPriceOffsetRate = new android.widget.TextView(mContext);  
        holder.mTextViewPriceOffsetRate.setText("涨跌幅");  
        holder.mTextViewPriceOffsetRate.setGravity(android.view.Gravity.CENTER);  
        //tx.setTextColor(getResources().getColor(R.color.font_content));
        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
        movableLayout.addView(holder.mTextViewPriceOffsetRate, 200, SysScreen.dip2px(50));  

        holder.mTextViewPriceHigh = new android.widget.TextView(mContext);  
        holder.mTextViewPriceHigh.setText("最高价");  
        holder.mTextViewPriceHigh.setGravity(android.view.Gravity.CENTER);  
        //tx.setTextColor(getResources().getColor(R.color.font_content));
        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
        movableLayout.addView(holder.mTextViewPriceHigh, 200, SysScreen.dip2px(50));  

        holder.mTextViewPriceLow = new android.widget.TextView(mContext);  
        holder.mTextViewPriceLow.setText("最低价");  
        holder.mTextViewPriceLow.setGravity(android.view.Gravity.CENTER);  
        //tx.setTextColor(getResources().getColor(R.color.font_content));
        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
        movableLayout.addView(holder.mTextViewPriceLow, 200, SysScreen.dip2px(50));  

        holder.mTextViewPriceOpen = new android.widget.TextView(mContext);  
        holder.mTextViewPriceOpen.setText("今开盘");  
        holder.mTextViewPriceOpen.setGravity(android.view.Gravity.CENTER);  
        //tx.setTextColor(getResources().getColor(R.color.font_content));
        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
        movableLayout.addView(holder.mTextViewPriceOpen, 200, SysScreen.dip2px(50));  

        holder.mTextViewPricePreClose = new android.widget.TextView(mContext);  
        holder.mTextViewPricePreClose.setText("昨收盘");  
        holder.mTextViewPricePreClose.setGravity(android.view.Gravity.CENTER);  
        //tx.setTextColor(getResources().getColor(R.color.font_content));
        //tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_middle));
        movableLayout.addView(holder.mTextViewPricePreClose, 200, SysScreen.dip2px(50));  
    
        result.addView(movableLayout); 
		result.setTag(holder); 
        return result;  
    }  

	private android.view.View buildListViewLayout2() {  
		android.view.View result = View.inflate(mContext, getListItemLayoutId(), null);
		StockListItemViewHolder holder = new StockListItemViewHolder();
		holder.stockQuote = new StockDataQuote.InstantData();
		
		addStockDataQuote(holder.stockQuote);
		
		holder.mTextViewStockName = (TextView) result.findViewById(R.id.item_stockname);
		holder.mTextViewStockCode = (TextView) result.findViewById(R.id.item_stockcode);
		holder.mTextViewPriceLastest = (TextView) result.findViewById(R.id.item_stockprice_lastest);
		holder.mTextViewPriceOffsetRate = (TextView) result.findViewById(R.id.item_stockprice_offsetrate);
		result.setTag(holder);
		return result;
	}
	
	@Override
	public View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
		if (convertView == null) {
			convertView = buildListViewLayout();
		}
		StockListItemViewHolder holder = (StockListItemViewHolder) convertView.getTag();			
		StockItem stockitem = mItems.get(position);
		if (null == stockitem.cacheStockQuote) {
			stockitem.cacheStockQuote = new StockDataQuote.InstantData();
			stockitem.cacheStockQuote.stockCode = stockitem.stockCode;
		}
		UtilsText.updateViewText(holder.mTextViewStockName, stockitem.stockName);
		UtilsText.updateViewText(holder.mTextViewStockCode, stockitem.stockCode);
		
		if (stockitem.stockCode.equals(holder.stockQuote.stockCode)) {
			stockitem.cacheStockQuote.assign(holder.stockQuote);
		} else {
			holder.stockQuote.clear();
			holder.stockQuote.stockCode = stockitem.stockCode;
			holder.stockQuote.assign(stockitem.cacheStockQuote);
		}		
		
		int color = mContext.getResources().getColor(R.color.black);
		java.text.DecimalFormat fmt = getPriceFormatter();
    	if (null != holder.stockQuote) {
    		if (0 < holder.stockQuote.price_lastest) {

    			UtilsText.updateViewText(holder.mTextViewPriceLastest, fmt.format(holder.stockQuote.price_lastest));
    			
    			if (holder.stockQuote.price_preclose == holder.stockQuote.price_lastest) {
    			} else {
        			if (holder.stockQuote.price_preclose < holder.stockQuote.price_lastest) {
        				color = mContext.getResources().getColor(R.color.red);
        			} else {
        				color = mContext.getResources().getColor(R.color.green);
        			}
    			}
    			UtilsText.updateViewText(holder.mTextViewPriceOffsetRate, holder.stockQuote.getPriceOffsetRate(fmt));	
    		} else {
    			UtilsText.updateViewText(holder.mTextViewPriceLastest, "--");	
    			UtilsText.updateViewText(holder.mTextViewPriceOffsetRate, "--");	
    		}
   			UtilsText.updateViewText(holder.mTextViewPriceHigh, "" + holder.stockQuote.price_high);
   			UtilsText.updateViewText(holder.mTextViewPriceLow, "" + holder.stockQuote.price_low);
   			UtilsText.updateViewText(holder.mTextViewPriceOpen, "" + holder.stockQuote.price_open);
   			UtilsText.updateViewText(holder.mTextViewPricePreClose, "" + holder.stockQuote.price_preclose);
		} else {
			UtilsText.updateViewText(holder.mTextViewPriceLastest, "--");
			UtilsText.updateViewText(holder.mTextViewPriceOffsetRate, "--");	
		}
		holder.mTextViewPriceLastest.setTextColor(color);
		holder.mTextViewPriceOffsetRate.setTextColor(color);
		return convertView;
	}
	
	private int getListItemLayoutId() {
		return mListItemLayoutId;
	}
}
