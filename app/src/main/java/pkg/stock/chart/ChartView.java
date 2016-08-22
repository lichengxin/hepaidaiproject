package pkg.stock.chart;

import com.app.R;

import base.app.BaseApp;

import android.content.Context;
import android.graphics.*;
import android.util.*;
import android.view.*;

public abstract class ChartView extends android.view.SurfaceView  implements android.view.SurfaceHolder.Callback { 
//public abstract class ChartView extends android.view.View {

	private static final String LOGTAG = ChartView.class.getSimpleName();

	protected int mViewHeight = 0;
	protected int mViewWidth = 0;
	
	public static class AxisChartData {
		public int value_gap_high_low = 0;
		public int rect_gap_top_bottom = 0;
		public int value_low_min = 0;
		public int value_high_max = 0;
	}

	public ChartCoordNotifier mChartCoordNotifier = null;	
	
	protected int getChartPosY(AxisChartData AChartData, Rect ARect, int value) {
		if (0 == AChartData.value_gap_high_low) {
			AChartData.value_gap_high_low = AChartData.value_high_max - AChartData.value_low_min;
		}
		if (0 == AChartData.rect_gap_top_bottom) {
			AChartData.rect_gap_top_bottom = ARect.bottom - ARect.top;
		}
		if (0 == AChartData.value_gap_high_low)
			return 0;
		int result = (value - AChartData.value_low_min) * AChartData.rect_gap_top_bottom;
		return ARect.bottom - (int)(result / AChartData.value_gap_high_low);
	}
	
	protected Context mContext = null;
	private SurfaceHolder mSurfaceHolder = null;
	public ChartView(Context context) {
		super(context);
		mContext = context;
		if (null == mSurfaceHolder) {
			mSurfaceHolder = this.getHolder();//获取holder
			mSurfaceHolder.addCallback(this);
			setZOrderOnTop(true);
			mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			
		}
		init();
	}
	public ChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		if (null == mSurfaceHolder) {
			mSurfaceHolder = this.getHolder();//获取holder
			// 这里避免 滑动黑屏 ???
			//mSurfaceHolder;
			mSurfaceHolder.addCallback(this);
			setZOrderOnTop(true);
			mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			
		}
		init();
	}

	public ChartView(Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		if (null == mSurfaceHolder) {
			mSurfaceHolder = this.getHolder();//获取holder
			mSurfaceHolder.addCallback(this);
			setZOrderOnTop(true);
			mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			
		}
		init();
	}
	
	protected int idColor(int colorId) {
		return mContext.getResources().getColor(colorId);
	}

	//这里的代码跟继承View时OnDraw中一样
    protected abstract void paint(android.graphics.Canvas canvas);

	public void repaint() {
    	android.graphics.Canvas c = null;
        try {
        	c = mSurfaceHolder.lockCanvas();
        	paint(c);
        }
        finally {
            if (c != null) {
            	mSurfaceHolder.unlockCanvasAndPost(c);
            }
        } 
    } 
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {        
		Canvas canvas = holder.lockCanvas(null);// 获取画布
        canvas.drawColor(Color.WHITE);// 设置画布背景
        holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
	}

	public int colorBackground() {
		return idColor(R.color.black);
		//return idColor(com.zhijiaoyi.vstock.R.color.white);
	}

	public int colorStockSplitter() {
		return idColor(R.color.red);
	}
	
	public int colorStockPriceMinute() {
		return idColor(R.color.white);
		//return idColor(com.zhijiaoyi.vstock.R.color.black);
	}
	
	public int colorStockVolumeMinute() {
		return android.graphics.Color.YELLOW;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public float distance(float posX1, float posX2) {
		if (posX1 > posX2) {
			return posX1 - posX2;
		} else {
			return posX2 - posX1;
		}
	}
	
	protected abstract void init();
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置触摸模式
		    case MotionEvent.ACTION_DOWN:
		    	//App.showToast("ACTION_DOWN");
		    	break;
			case MotionEvent.ACTION_POINTER_DOWN:
		    	//App.showToast("ACTION_POINTER_DOWN");
				break;
			case MotionEvent.ACTION_UP:
		    	//App.showToast("ACTION_UP");
		    	break;
			case MotionEvent.ACTION_POINTER_UP:
		    	//App.showToast("ACTION_POINTER_UP");
		    	break;
			case MotionEvent.ACTION_MOVE:
				//App.showToast("ACTION_MOVE");
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			case MotionEvent.ACTION_OUTSIDE:
				break;
			case MotionEvent.ACTION_SCROLL:
				break;
			case MotionEvent.ACTION_HOVER_ENTER:
				break;
			case MotionEvent.ACTION_HOVER_EXIT:
				break;
			case MotionEvent.ACTION_HOVER_MOVE:
				break;
		}
		return true;
	}
}
