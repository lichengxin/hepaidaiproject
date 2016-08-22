package ui.widget;

import pkg.stock.data.StockListAdapter;
import utils.Log;
import utils.SysScreen;
import base.app.BaseApp;

import com.app.R;

import android.content.Context;
import android.util.AttributeSet;

public class ListViewEx extends android.widget.RelativeLayout {

	//*/
	protected Context mContext = null;
	public ListViewEx(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public ListViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public ListViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	//*/
	/**  
     * 列表头的高和宽  
     */  
    private int mTitleHeight = 50;  
    private int mTitleWidth = 200;  
    /**  
     * 可滚动和不可滚动列头的名称  
     */  
    private String[] mTitleMovableStr = { "最新价", "涨跌幅", "最高价", "最低价", "今开盘", "昨收盘"};  
    private String[] mTitleFixStr = {"名称" };  
  
    private android.widget.ListView mListView;  
  
    private java.util.ArrayList<android.view.View> mMovableViewList = new java.util.ArrayList();  
      
	protected void init() {
		 // ListView可移动区域  
        android.widget.LinearLayout relativeLayout = new android.widget.LinearLayout(mContext);  
        relativeLayout.setOrientation(android.widget.LinearLayout.VERTICAL);  

        relativeLayout.addView(buildHeadLayout());  
        relativeLayout.addView(buildMoveableListView()); 
        
        this.addView(relativeLayout, new 
        		android.widget.RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,  
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));  
	}

    private android.view.View mLayoutTitleMovable;  
  
	private android.view.View buildHeadLayout() {  
		android.widget.LinearLayout headLayout = new android.widget.LinearLayout(mContext);  

		android.widget.LinearLayout fixHeadLayout = new android.widget.LinearLayout(mContext);  
	    for (int i = 0; i < mTitleFixStr.length; i++) {  
	       	android.widget.TextView tx = new android.widget.TextView(mContext);  
	        tx.setText(mTitleFixStr[i]);  
	        tx.setGravity(android.view.Gravity.CENTER);   
            tx.setTextColor(getResources().getColor(R.color.font_content));
            tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 
            		getResources().getDimension(R.dimen.font_middle));
	        fixHeadLayout.addView(tx, i, new LayoutParams(mTitleWidth, SysScreen.dip2px(mTitleHeight)));  
	    }  
		headLayout.addView(fixHeadLayout);  
    	mLayoutTitleMovable = new android.widget.LinearLayout(mContext);  
        for (int i = 0; i < mTitleMovableStr.length; i++) {  
  
        	android.widget.TextView tx = new android.widget.TextView(mContext);  
            tx.setText(mTitleMovableStr[i]);  
            tx.setGravity(android.view.Gravity.CENTER);  
            tx.setTextColor(getResources().getColor(R.color.font_content));
            tx.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, 
            		getResources().getDimension(R.dimen.font_middle));
            ((android.widget.LinearLayout) mLayoutTitleMovable).addView(tx, mTitleWidth, SysScreen.dip2px(mTitleHeight));  
        }    
        headLayout.addView(mLayoutTitleMovable);  
        return headLayout;  
    }  

	private android.view.View buildMoveableListView() {  
		android.widget.LinearLayout relativeLayout = new android.widget.LinearLayout(mContext);  
        mListView = new android.widget.ListView(mContext);  
        mListView.setCacheColorHint(00000000);  
        relativeLayout.addView(mListView);  
        return relativeLayout;    
    }  

	private StockListAdapter mInternalAdapter = null;
	
	public void setAdapter(  
			StockListAdapter movableAdapter) {  
	        // listViewFix.setAdapter(fixAdapter);  
		    mInternalAdapter = movableAdapter;
		    mInternalAdapter.setMovableViewList(this.mMovableViewList);
	        mListView.setAdapter(movableAdapter);  
	          
	}

	private float mStartX = 0;  
    private int mMoveOffsetX = 0;
    private int mFixX = 0;
    
    //onTouchEvent：触发触摸事件
    //onInterceptTouchEvent：触发拦截触摸事件
    // ViewGroup里的onInterceptTouchEvent默认返回值是false，这样touch事件会传递到View控件
    
    @Override  
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {  
  
        switch (ev.getAction()) {  
        case android.view.MotionEvent.ACTION_DOWN:  
            mStartX = ev.getX();  
            Log.e("TEST", "中断按下x= " + ev.getX());  
            break;  
        case android.view.MotionEvent.ACTION_MOVE:  
            Log.e("TEST", "中断移动x= " + ev.getX());  
            int offsetX = (int) Math.abs(ev.getX() - mStartX);  
            if (offsetX > 30) {  
                return true;  
            } else {  
                return false;  
            }  
  
        case android.view.MotionEvent.ACTION_UP:  
            Log.e("TEST", "中断抬起x= " + ev.getX());  
            actionUP();  
            break;  
        default:  
            break;  
        }  
        return super.onInterceptTouchEvent(ev);  
    }  
    
    private static boolean isInitShow = false;
    
    @Override  
    public boolean onTouchEvent(android.view.MotionEvent ev) {  
        switch (ev.getAction()) {  
        case android.view.MotionEvent.ACTION_DOWN:  
            mStartX = ev.getX();  
            Log.e("TEST", "移动按下x= " + ev.getX());  
            return true;  
        case android.view.MotionEvent.ACTION_MOVE:  
            int offsetX = (int) Math.abs(ev.getX() - mStartX);  
            if (offsetX > 30) {  
                Log.e("TEST", "移动偏移" + offsetX);  
                mMoveOffsetX = (int) (mStartX - ev.getX() + mFixX);
                if (0 > mMoveOffsetX) {
                	mMoveOffsetX = 0;
                } else {
                    if ((mLayoutTitleMovable.getWidth() + mMoveOffsetX) > MovableHeaderWidth()) {  
                    	mMoveOffsetX = MovableHeaderWidth() - mLayoutTitleMovable.getWidth();
                    }
                }
                mLayoutTitleMovable.scrollTo(mMoveOffsetX, 0);
                if (null != mMovableViewList) {
                    for (int i = 0; i < mMovableViewList.size(); i++) {                      	  
                    	mMovableViewList.get(i).scrollTo(mMoveOffsetX, 0);  
                    }  
                    Log.e("TEST", "List数量" + mMovableViewList.size());  
                }
                // mLayoutMovable.scrollTo(mOffsetX, 0);  
            }  
            break;  
        case android.view.MotionEvent.ACTION_UP:  
            Log.e("TEST", "移动抬起x= " + ev.getX());  
            mFixX = mMoveOffsetX; // mFixX + (int) ((int) ev.getX() - mStartX)  
            actionUP();  
            break;  
  
        default:  
            break;  
        }  
  
        return super.onTouchEvent(ev);  
    }  
    
    /**  
     * 触摸抬起  
     */  
    private int MovableHeaderWidth () {
    	return mTitleWidth * mTitleMovableStr.length;
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
            if ((mLayoutTitleMovable.getWidth() + Math.abs(mFixX)) > MovableHeaderWidth()) {  
                mLayoutTitleMovable.scrollTo(mTitleWidth * mTitleMovableStr.length - mLayoutTitleMovable.getWidth(), 0);
                if (null != mMovableViewList) {
                    for (int i = 0; i < mMovableViewList.size(); i++) {  
                    	mMovableViewList.get(i).scrollTo(mTitleWidth  
                                * mTitleMovableStr.length-mLayoutTitleMovable.getWidth(), 0);  
                    }  
                }  
            }    
        }  
    }  
}
