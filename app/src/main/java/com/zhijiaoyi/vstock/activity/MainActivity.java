package com.zhijiaoyi.vstock.activity;

import android.os.Bundle;
import android.os.Message;
import android.widget.RadioGroup;

import com.app.R;
import com.app.activity.FragmentCamera;
import com.app.activity.FragmentWebView;
import com.zhijiaoyi.vstock.fragment.FragmentSettings;
import com.zhijiaoyi.vstock.fragment.FragmentStockDeal;
import com.zhijiaoyi.vstock.fragment.FragmentStockDealBuy;
import com.zhijiaoyi.vstock.fragment.FragmentStockDealCancel;
import com.zhijiaoyi.vstock.fragment.FragmentStockDealHold;
import com.zhijiaoyi.vstock.fragment.FragmentStockDealSale;
import com.zhijiaoyi.vstock.fragment.FragmentStockList;
import com.zhijiaoyi.vstock.fragment.FragmentStockQuote;
import com.zhijiaoyi.vstock.fragment.FragmentStockSettlement;
import com.zhijiaoyi.vstock.fragment.FragmentTest;

import base.app.BaseApp;
import base.app.BaseConsts;
import base.app.ManagerActivities;
import base.app.ManagerFactory;
import ui.activity.BaseFragmentActivity;

public class MainActivity extends BaseFragmentActivity {

    private RadioGroup fragmenttab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initview();//初始化ID
        setTab();//Tab选择监听
        createMessageLoopHandler();
        changeFragment(getFragmentStockQuote(), false);
    }

    private void setTab() {
        if (null != fragmenttab) {
            fragmenttab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.main_tab_1://
                            changeFragment(getFragmentStockList(), false);
                            break;
                        case R.id.main_tab_2://
                            changeFragment(getFragmentStockDeal(), false);
                            break;
                        case R.id.main_tab_3://
                            changeFragment(getFragmentStockQuote(), false);
                            break;
                        case R.id.main_tab_4://
                            changeFragment(getFragmentSettlement(), false);
                            break;
                        case R.id.main_tab_5://
                            changeFragment(getFragmentSettings(), false);
                            break;
                    }
                }
            });
        }
    }

    private void initview() {
        fragmenttab = (RadioGroup) findViewById(R.id.main_fragment_tab);
    }

    @Override
    protected boolean handleActivityMessage(Message msg) {
        switch (msg.what) {
            case BaseConsts.MSG_FRAGMENT_CHANGE:
                if ("quote".equals(msg.obj)) {
                    changeFragment(getFragmentStockQuote(), false);
                }
                return true;
        }
        return super.handleActivityMessage(msg);
    }

    protected FragmentStockQuote getFragmentStockQuote() {

        return new FragmentStockQuote();
    }

    protected FragmentStockList getFragmentStockList() {

        return new FragmentStockList();
    }

    protected FragmentStockSettlement getFragmentSettlement() {
        return new FragmentStockSettlement();
    }

    protected FragmentSettings getFragmentSettings() {
        return new FragmentSettings();
    }

    protected FragmentStockDeal getFragmentStockDeal() {
        return new FragmentStockDeal();
    }

    protected FragmentStockDealBuy getFragmentStockDealBuy() {
        return new FragmentStockDealBuy();
    }

    protected FragmentStockDealSale getFragmentStockDealSale() {
        return new FragmentStockDealSale();
    }

    protected FragmentStockDealCancel getFragmentStockDealCancel() {
        return new FragmentStockDealCancel();
    }

    protected FragmentStockDealHold getFragmentStockDealHold() {
        return new FragmentStockDealHold();
    }

    protected FragmentWebView getFragmentWebView() {
        return new FragmentWebView();
    }

    protected FragmentCamera getFragmentCamera() {
        return new FragmentCamera();
    }

    protected FragmentTest getFragmentTest() {
        return new FragmentTest();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_vstock_activity_main;
    }

    @Override
    protected int getFragmentId() {
        return R.id.main_content;
    }

    // 点击退出时记录时间
    private long mFirstBackPressTime = 0;

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - mFirstBackPressTime > 3000) {
            BaseApp.showToast("再按一次退出客户端");
            mFirstBackPressTime = secondTime;
        } else {
            ManagerActivities.getActivitiesManager(ManagerFactory.getManagerFactory()).AppExit(this);
        }
    }
}