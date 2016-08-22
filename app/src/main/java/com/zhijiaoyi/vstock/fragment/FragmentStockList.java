package com.zhijiaoyi.vstock.fragment;

import com.app.R;

import java.util.ArrayList;
import java.util.List;

import pkg.stock.data.*;

import ui.activity.BaseFragment;
import ui.activity.BaseFragmentActivity;
import ui.widget.UtilsText;
import utils.*;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout.LayoutParams;

import base.app.BaseConsts;

/*TAB1*/
public class FragmentStockList extends BaseFragment {

    private static final String LOGTAG = FragmentStockList.class.getSimpleName();
    private StockListAdapter mAdapter = null;
    private ListView mStockListView = null;
    private List<StockItem> mStockList = new ArrayList<StockItem>();
    private EditText edtSearch;

    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        initViews();
        return mView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_fragment_stocklist;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler = new android.os.Handler() {
            public void handleMessage(android.os.Message msg) {
                if (1 == msg.what) {
                    (new RefreshStocksQuoteSyncTask()).execute("");
                }
            }

            ;
        };
        new Thread(new RefreshStockListQuote()).start();
    }

    protected Context getContext() {
        return FragmentStockList.this.getActivity();
    }

    @Override
    public void onPause() {
        mHandler = null;
        super.onPause();
    }

    protected void initViews() {
        fillAllDBStockItem();
        edtSearch = (EditText) mView.findViewById(R.id.edit_stock_search);
        if (null != edtSearch) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable edit) {
                    (new SearchStockSyncTask()).execute(edit.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
        initStockListView();
        initTitleButtons();
    }

    protected void fillAllDBStockItem() {
        DBStock dbstock = DBStock.getInstance();
        int count = dbstock.getStockItemCount();
        for (int i = 0; i < count; i++) {
            mStockList.add(dbstock.getStockItem(i));
        }
    }

    protected void initTitleButtons() {
        View view = null;
        view = mView.findViewById(R.id.btn_stock_self);
        if (null != view) {
            view.setOnClickListener(getStockClassTabOnClickListener());
            setActiveStockListTab(view);
        }
        view = mView.findViewById(R.id.btn_stock_history);
        if (null != view) view.setOnClickListener(getStockClassTabOnClickListener());
        view = mView.findViewById(R.id.btn_stock_hot);
        if (null != view) view.setOnClickListener(getStockClassTabOnClickListener());
    }

    private android.view.View.OnClickListener mStockClassTabOnClickListener = null;

    private android.view.View.OnClickListener getStockClassTabOnClickListener() {
        if (null == mStockClassTabOnClickListener) {
            mStockClassTabOnClickListener = new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_stock_history:
                            setActiveStockListTab(v);
                            break;
                        case R.id.btn_stock_hot:
                            setActiveStockListTab(v);
                            break;
                        case R.id.btn_stock_self:
                            setActiveStockListTab(v);
                            break;
                    }
                }
            };
        }
        return mStockClassTabOnClickListener;
    }

    private View mActiveStockListTab = null;

    protected void setActiveStockListTab(View view) {
        if (null == view)
            return;
        //((android.widget.TextView)view).setTextColor(00000000);
        TextView textview = null;
        if (null != mActiveStockListTab) {
            //mActiveStockListTab.setBackgroundColor(0);
            mActiveStockListTab.setBackgroundResource(R.drawable.shape_border);
            textview = (TextView) mActiveStockListTab.findViewById(R.id.btn_text);
            if (null != textview) {
                textview.setTextColor(idColor(R.color.white));
            }
        }
        mActiveStockListTab = view;
        mActiveStockListTab.setBackgroundColor(idColor(R.color.white));
        textview = (TextView) mActiveStockListTab.findViewById(R.id.btn_text);
        if (null != textview) {
            textview.setTextColor(idColor(R.color.LightSteelBlue));
        }
    }

    protected void initStockListView() {
        android.widget.RelativeLayout rootLayout = (android.widget.RelativeLayout) mView.findViewById(R.id.listview_stock);
        if (null != rootLayout) {
            ListViewStockList listview = new ListViewStockList(getContext());
            rootLayout.addView(listview);
            if (null != mStockListView) {
                mAdapter = new StockListAdapter();
                mStockListView.setAdapter(mAdapter);
                mStockListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        doStockListViewOnClick(position);
                    }
                });
                mStockListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {
                        showStockItemSelectDialog();
                        return true;
                    }
                });
            }
        }
    }

    private android.view.View.OnClickListener mDialogOnClickListener = null;

    private android.view.View.OnClickListener getDialogOnClickListener() {
        if (null == mDialogOnClickListener) {
            mDialogOnClickListener = new android.view.View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dlgbtn_1:
                            ((BaseFragmentActivity) getActivity()).sendMessage(BaseConsts.MSG_FRAGMENT_CHANGE, "quote");
                            break;
                        case R.id.dlgbtn_2:
                            break;
                        case R.id.dlgbtn_3:
                            break;
                        case R.id.dlgbtn_4:
                            break;
                        case R.id.dlgbtn_5:
                            break;
                    }
                    if (null != mAlertDialog) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                }
            };
        }
        return mDialogOnClickListener;
    }

    private android.app.AlertDialog mAlertDialog = null;

    public void showStockItemSelectDialog() {
        Context context = getContext();//getApplicationContext();
        View view = LayoutInflater.from(context).inflate(R.layout.pkg_vstock_dialog_stocklistitem_select, null);
        View btnview = null;
        btnview = view.findViewById(R.id.dlgbtn_1);
        if (null != btnview) btnview.setOnClickListener(getDialogOnClickListener());
        btnview = view.findViewById(R.id.dlgbtn_2);
        if (null != btnview) btnview.setOnClickListener(getDialogOnClickListener());
        btnview = view.findViewById(R.id.dlgbtn_3);
        if (null != btnview) btnview.setOnClickListener(getDialogOnClickListener());
        btnview = view.findViewById(R.id.dlgbtn_4);
        if (null != btnview) btnview.setOnClickListener(getDialogOnClickListener());
        btnview = view.findViewById(R.id.dlgbtn_5);
        if (null != btnview) btnview.setOnClickListener(getDialogOnClickListener());

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("智交易");
        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    //====================================================================
    // stock list related
    //====================================================================
    private String[] mFixLeftListColumnsText = {"名称"};
    private int[] mFixLeftListColumnsWidth = {SysScreen.dip2px(80)};

    private android.view.View mLayoutMovable = null;
    private android.view.View mLayoutMovableParent = null;

    public float mStartX = 0;
    public int mMoveOffsetX = 0;
    public int mFixX = 0;
    private int mTitleHeight = SysScreen.dip2px(20);
    int columnMovablePriceLastest = 0;
    int columnMovablePriceOffsetRate = 1;
    int columnMovablePriceHigh = 2;
    int columnMovablePriceLow = 3;
    int columnMovablePriceOpen = 4;
    int columnMovablePriceClose = 5;
    private String[] mMovableListColumnsText = {"最新价", "涨跌幅", "最高价", "最低价", "开盘价", "收盘价"};
    private int[] mMovableListColumnsWidth = {
            SysScreen.dip2px(70),
            SysScreen.dip2px(70),
            SysScreen.dip2px(70),
            SysScreen.dip2px(70),
            SysScreen.dip2px(70),
            SysScreen.dip2px(70)};

    private int mMovableTotalWidth = 0;

    private int MovableTotalWidth() {
        if (0 == mMovableTotalWidth) {
            for (int i = 0; i < mMovableListColumnsWidth.length; i++) {
                mMovableTotalWidth = mMovableTotalWidth + mMovableListColumnsWidth[i];
            }
        }
        return mMovableTotalWidth;
    }

    private android.widget.TextView addListItemTextView(String headerName, int AWidth, android.widget.LinearLayout AParent) {
        android.widget.TextView result = new android.widget.TextView(getContext());
        result.setText(headerName);
        result.setGravity(android.view.Gravity.CENTER);
        result.setTextColor(idColor(R.color.font_content));
        result.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, idDimen(R.dimen.font_middle));
        AParent.addView(result, AWidth, SysScreen.dip2px(mTitleHeight));
        return result;
    }

    private android.widget.TextView addListHeaderTextView(String headerName, int AWidth, android.widget.LinearLayout AParent) {
        android.widget.TextView result = new android.widget.TextView(getContext());
        result.setText(headerName);
        result.setGravity(android.view.Gravity.CENTER);
        result.setTextColor(idColor(R.color.font_content));
        result.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, idDimen(R.dimen.font_middle));
        AParent.addView(result, AWidth, SysScreen.dip2px(mTitleHeight));
        return result;
    }

    public class ListViewStockList extends android.widget.RelativeLayout {

        public ListViewStockList(Context context) {
            super(context);
            init();
        }

        public ListViewStockList(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ListViewStockList(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        /**
         * 列表头的高和宽
         */
        private android.widget.LinearLayout mLayoutTitleMovable;
        private float mStartX = 0;
        private int mMoveOffsetX = 0;
        private int mFixX = 0;

        protected void init() {
            // ListView可移动区域
            android.widget.LinearLayout relativeLayout = new android.widget.LinearLayout(getContext());
            relativeLayout.setOrientation(android.widget.LinearLayout.VERTICAL);

            relativeLayout.addView(buildHeadLayout());
            relativeLayout.addView(buildMoveableListView());

            this.addView(relativeLayout, new
                    android.widget.RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));
        }

        private android.view.View buildHeadLayout() {
            android.widget.LinearLayout headLayout = new android.widget.LinearLayout(getContext());

            android.widget.LinearLayout fixHeadLayout = new android.widget.LinearLayout(getContext());

            addListHeaderTextView(mFixLeftListColumnsText[0], mFixLeftListColumnsWidth[0], fixHeadLayout);
            headLayout.addView(fixHeadLayout);

            mLayoutTitleMovable = new android.widget.LinearLayout(getContext());

            for (int i = 0; i < mMovableListColumnsText.length; i++) {
                addListHeaderTextView(mMovableListColumnsText[i], mMovableListColumnsWidth[i], mLayoutTitleMovable);
            }
            headLayout.addView(mLayoutTitleMovable);
            return headLayout;
        }

        private android.view.View buildMoveableListView() {
            android.widget.LinearLayout relativeLayout = new android.widget.LinearLayout(getContext());
            mStockListView = new android.widget.ListView(getContext());
            mStockListView.setCacheColorHint(00000000);
            relativeLayout.addView(mStockListView);
            return relativeLayout;
        }

        @Override
        public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
            switch (ev.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    mStartX = ev.getX();
                    break;
                case android.view.MotionEvent.ACTION_MOVE:
                    int offsetX = (int) Math.abs(ev.getX() - mStartX);
                    if (offsetX > 30) {
                        return true;
                    } else {
                        return false;
                    }
                case android.view.MotionEvent.ACTION_UP:
                    actionUP();
                    break;
                default:
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent ev) {
            switch (ev.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    mStartX = ev.getX();
                    return true;
                case android.view.MotionEvent.ACTION_MOVE:
                    int offsetX = (int) Math.abs(ev.getX() - mStartX);
                    if (offsetX > 30) {
                        mMoveOffsetX = (int) (mStartX - ev.getX() + mFixX);
                        if (0 > mMoveOffsetX) {
                            mMoveOffsetX = 0;
                        } else {
                            if ((mLayoutTitleMovable.getWidth() + mMoveOffsetX) > MovableTotalWidth()) {
                                mMoveOffsetX = MovableTotalWidth() - mLayoutTitleMovable.getWidth();
                            }
                        }
                        mLayoutTitleMovable.scrollTo(mMoveOffsetX, 0);
                        Log.d(LOGTAG, " scroll list ");
                        if (null != mMovableViewList) {
                            Log.d(LOGTAG, " scroll list child:" + mMovableViewList.size());
                            for (int i = 0; i < mMovableViewList.size(); i++) {
                                mMovableViewList.get(i).scrollTo(mMoveOffsetX, 0);
                            }
                        }
                        // mLayoutMovable.scrollTo(mOffsetX, 0);
                    }
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    mFixX = mMoveOffsetX; // mFixX + (int) ((int) ev.getX() - mStartX)
                    actionUP();
                    break;

                default:
                    break;
            }
            return super.onTouchEvent(ev);
        }

        private void actionUP() {
            if (mFixX < 0) {
                mFixX = 0;
                mLayoutTitleMovable.scrollTo(0, 0);
                if (null != mMovableViewList) {
                    for (int i = 0; i < mMovableViewList.size(); i++) {
                        mMovableViewList.get(i).scrollTo(0, 0);
                    }
                }
            } else {
                if ((mLayoutTitleMovable.getWidth() + Math.abs(mFixX)) > MovableTotalWidth()) {
                    mLayoutTitleMovable.scrollTo(MovableTotalWidth() - mLayoutTitleMovable.getWidth(), 0);
                    if (null != mMovableViewList) {
                        for (int i = 0; i < mMovableViewList.size(); i++) {
                            mMovableViewList.get(i).scrollTo(MovableTotalWidth() - mLayoutTitleMovable.getWidth(), 0);
                        }
                    }
                }
            }
        }
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

    private java.util.ArrayList<android.view.View> mMovableViewList = new java.util.ArrayList();

    public void addStockDataQuote(StockDataQuote.InstantData quote) {
        getStockDataQuotes().add(quote);
    }

    public class StockListAdapter extends android.widget.BaseAdapter {

        @Override
        public int getCount() {
            return mStockList.size();
        }

        @Override
        public Object getItem(int position) {
            return mStockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private android.view.View buildListViewLayout() {
            Log.d(LOGTAG, "buildListViewLayout");
            android.widget.LinearLayout result = new android.widget.LinearLayout(getContext());
            StockListItemViewHolder holder = new StockListItemViewHolder();
            holder.stockQuote = new StockDataQuote.InstantData();
            addStockDataQuote(holder.stockQuote);

            android.widget.LinearLayout fixHeadLayout = new android.widget.LinearLayout(getContext());

            android.widget.RelativeLayout fixColumn1Layout = new android.widget.RelativeLayout(getContext());

            holder.mTextViewStockName = new android.widget.TextView(getContext());
            holder.mTextViewStockName.setTextColor(idColor(R.color.font_content));
            holder.mTextViewStockName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, idDimen(R.dimen.font_middle));
            fixColumn1Layout.addView(holder.mTextViewStockName,
                    new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            holder.mTextViewStockCode = new android.widget.TextView(getContext());
            holder.mTextViewStockCode.setTextColor(idColor(R.color.font_content));
            holder.mTextViewStockCode.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, idDimen(R.dimen.font_small));
            android.widget.RelativeLayout.LayoutParams layoutParams = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = SysScreen.dip2px(18);
            fixColumn1Layout.addView(holder.mTextViewStockCode, layoutParams);

            fixHeadLayout.addView(fixColumn1Layout, 0, new LayoutParams(mFixLeftListColumnsWidth[0], SysScreen.dip2px(50)));
            result.addView(fixHeadLayout);

            android.widget.LinearLayout movableLayout = new android.widget.LinearLayout(getContext());

            if (null != mMovableViewList) {
                Log.d(LOGTAG, "buildListViewLayout add movableLayout");
                mMovableViewList.add(movableLayout);
            }
            android.widget.TextView textview = null;
            for (int i = 0; i < mMovableListColumnsText.length; i++) {
                textview = addListItemTextView(mMovableListColumnsText[i], mMovableListColumnsWidth[i], movableLayout);
                if (columnMovablePriceLastest == i) holder.mTextViewPriceLastest = textview;
                if (columnMovablePriceOffsetRate == i) holder.mTextViewPriceOffsetRate = textview;
                if (columnMovablePriceHigh == i) holder.mTextViewPriceHigh = textview;
                if (columnMovablePriceLow == i) holder.mTextViewPriceLow = textview;
                if (columnMovablePriceOpen == i) holder.mTextViewPriceOpen = textview;
                if (columnMovablePriceClose == i) holder.mTextViewPricePreClose = textview;
            }

            result.addView(movableLayout);
            result.setTag(holder);
            return result;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = buildListViewLayout();
            }
            StockListItemViewHolder holder = (StockListItemViewHolder) convertView.getTag();
            StockItem stockitem = mStockList.get(position);
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

            int color = getResources().getColor(R.color.black);
            if (null != holder.stockQuote) {
                if (0 < holder.stockQuote.price_lastest) {

                    UtilsText.updateViewText(holder.mTextViewPriceLastest, StockPrice.getPackedPriceText(holder.stockQuote.price_lastest));

                    if (holder.stockQuote.price_preclose == holder.stockQuote.price_lastest) {
                    } else {
                        if (holder.stockQuote.price_preclose < holder.stockQuote.price_lastest) {
                            color = getResources().getColor(R.color.red);
                        } else {
                            color = getResources().getColor(R.color.green);
                        }
                    }
                    UtilsText.updateViewText(holder.mTextViewPriceOffsetRate, holder.stockQuote.getPriceOffsetRate(StockPrice.getPackedPriceFormatter()));
                } else {
                    UtilsText.updateViewText(holder.mTextViewPriceLastest, "--");
                    UtilsText.updateViewText(holder.mTextViewPriceOffsetRate, "--");
                }
                UtilsText.updateViewText(holder.mTextViewPriceHigh, StockPrice.getPackedPriceText(holder.stockQuote.price_high));
                UtilsText.updateViewText(holder.mTextViewPriceLow, StockPrice.getPackedPriceText(holder.stockQuote.price_low));
                UtilsText.updateViewText(holder.mTextViewPriceOpen, StockPrice.getPackedPriceText(holder.stockQuote.price_open));
                UtilsText.updateViewText(holder.mTextViewPricePreClose, StockPrice.getPackedPriceText(holder.stockQuote.price_preclose));
            } else {
                UtilsText.updateViewText(holder.mTextViewPriceLastest, "--");
                UtilsText.updateViewText(holder.mTextViewPriceOffsetRate, "--");
            }
            holder.mTextViewPriceLastest.setTextColor(color);
            holder.mTextViewPriceOffsetRate.setTextColor(color);
            return convertView;
        }
    }

    private Handler mHandler = null;

    class RefreshStockListQuote implements Runnable {
        @Override
        public void run() {
            while (null != mHandler) {
                try {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("thread error...");
                }
            }
        }
    }


    private List<StockDataQuote.InstantData> mStockDataQuotes = new ArrayList<StockDataQuote.InstantData>();

    public List<StockDataQuote.InstantData> getStockDataQuotes() {
        if (null == mStockDataQuotes) {
            mStockDataQuotes = new java.util.ArrayList<StockDataQuote.InstantData>();
        }
        return mStockDataQuotes;
    }

    protected class RefreshStocksQuoteSyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            if (false)
                return null;
            List<StockDataQuote.InstantData> stockquotes = getStockDataQuotes();
            if (0 < stockquotes.size()) {
                List<String> stockcodes = new ArrayList<String>();
                for (int i = 0; i < stockquotes.size(); i++) {
                    stockcodes.add(stockquotes.get(i).stockCode);
                }
                String returnData = StockData_Sina.getStocksInstantData(stockcodes);
                StockData_Sina.parseStockInstantDatas(stockquotes, returnData);
                return "1";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null == result)
                return;
            mAdapter.notifyDataSetChanged();
        }
    }
    /*选择耗时操作*/
    protected class SearchStockSyncTask extends AsyncTask<String, Integer, String> {

        private List<StockItem> mSearchStockList = null;
        private List<StockItem> mSearchStockList2 = null;

        @Override
        protected String doInBackground(String... arg0) {
            String keyword = arg0[0].toLowerCase();
            if (null != keyword) {
                keyword = keyword.trim();
            }
            if (TextUtils.isEmpty(keyword)) {
                return null;
            }
            keyword = keyword.toUpperCase();
            mSearchStockList = new ArrayList<StockItem>();
            mSearchStockList2 = new ArrayList<StockItem>();
            DBStock.getInstance().searchStock(keyword, mSearchStockList, mSearchStockList2);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mStockList.clear();
            if (null != mSearchStockList) {
                for (int i = 0; i < mSearchStockList.size(); i++) {
                    mStockList.add(mSearchStockList.get(i));
                }
            } else {
                fillAllDBStockItem();
            }
            if (null != mSearchStockList2) {
                for (int i = 0; i < mSearchStockList2.size(); i++) {
                    mStockList.add(mSearchStockList2.get(i));
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    protected void doStockListViewOnClick(int position) {
        StockItem stockitem = mStockList.get(position - mStockListView.getHeaderViewsCount());
        if (null != stockitem) {
            FragmentStockQuote.mStockCode = stockitem.stockCode;
            BaseFragmentActivity activity = (BaseFragmentActivity) this.getActivity();
            activity.sendMessage(BaseConsts.MSG_FRAGMENT_CHANGE, "quote");
        }
    }
}
