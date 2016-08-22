package com.zhijiaoyi.vstock.fragment;

import com.app.R;
import java.util.ArrayList;
import java.util.List;

import pkg.stock.data.StockItem;
import pkg.stock.data.StockListAdapter;
import ui.activity.BaseFragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class FragmentTest extends BaseFragment implements OnClickListener {

	private static final String LOGTAG = FragmentTest.class.getSimpleName();

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);		
		initViews();
		return mView;
	}

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_app_fragment_test;
    }

	private StockListAdapter mAdapter;
	private List<StockItem> mStockList = new ArrayList<StockItem>();
	private ui.widget.ListViewEx mStockListView = null;
	
	protected void initViews() {
	}

	@Override
	public void onClick(View v) {	
	}
}
