package com.zhijiaoyi.vstock.fragment;

import ui.activity.BaseFragment;

import com.app.R;

import android.os.Bundle;

public class FragmentStockDealSale extends BaseFragment  {

	private static final String LOGTAG = FragmentStockDealSale.class.getSimpleName();

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);
		initViews();
		return mView;
	}

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_fragment_stockdealsale;
    }

	protected void initViews() {
		
	}
}
