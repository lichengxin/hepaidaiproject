package pkg.stock.chart;

import pkg.stock.chart.ChartView.AxisChartData;
import pkg.stock.data.*;
import utils.*;
import base.app.BaseApp;


import android.content.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.view.*;

/* 我们都知道，由于Array(数组)通常意义上讲只是一个单纯的线性序列，又基于Native，凭此它的效率历来便号称Java中最高。
 * 所以通常我们也都承认Java中效率最高的存储方式就是使用数组。但是，由于数组初始化后大小固定，索引不能超出下标，缺少灵活
 * 的扩展功能等原因，使得很多人放弃了数组的使用， 转而使用Collection,List,Map,Set等接口处理集合操作
 * */
public class RealTimeChartView extends ChartView {
	
	private static final String LOGTAG = RealTimeChartView.class.getSimpleName();
	
	public RealTimeChartView(Context context) {
		super(context);
	}

	public RealTimeChartView(Context context, android.util.AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RealTimeChartView(Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
	}
	
	/*
	 * 一分钟 一个点    1小时  60 点
	 *             09:30 -- 11:30 120
	 *             13:00 -- 15:00 120
	 *             一共需要 240 点 
	 */
	/*/
	public static class StockChartDataMinute {
		public static int minCount = 240;
		public double price_preclose = 0; // 昨收盘
		public double price_high = 0; // 最高
		public double price_low = 0; // 最高
		public double[] price_min = new double[minCount]; // 价格
		public double[] price_average_min = new double[minCount]; // 均价
		public int[] volume_min = new int[minCount]; // 成交量 手
		public int[] amount_min = new int[minCount]; // 成交金额
	}
	//*/
	
	protected DataEntity_StockChartMinute mStockMinChartData = null;
	public DataEntity_StockChartMinute getStockMinChartData() {
		return mStockMinChartData;
	}
	
	public void setStockMinChartData(DataEntity_StockChartMinute stockminchartdata) {
		mStockMinChartData = stockminchartdata;
	}

	private void doTouchDown() {	
    	mTouchDownX = mTouchX;
    	mIsTouchDown = 1;	
	}

	private static int showCoordMoveDistance = SysScreen.dip2px(10); 
	private void doTouchUp() {	
    	if (TouchMoveMode_None == mTouchMoveMode) {
    		mTouchMoveMode = TouchMoveMode_ShowPriceCoord;
        	this.repaint();
        	mIsTouchDown = 2;
            return;
    	}	
    	if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
    		Log.d(LOGTAG, "doTouchUp:" + mIsTouchDown + "/" + showCoordMoveDistance + "/" + mTouchDownX + "/" + mLastUpShowPriceCoordX);
    		if (1 == mIsTouchDown) {
                if (showCoordMoveDistance > distance(mTouchDownX, mLastUpShowPriceCoordX)) {
                	mTouchMoveMode = TouchMoveMode_None;     
                }
    		}
        	this.repaint();
        	mIsTouchDown = 2;
        	if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
        	    mLastUpShowPriceCoordX = mShowPriceCoordX;
        	}
        	Log.d(LOGTAG, "doTouchUp:" + mTouchMoveMode + "/" + mLastUpShowPriceCoordX);
            return;
    	};
	}

	private void doTouchMove() {
    	mTouchMoveX = mTouchX;
    	if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
        	this.repaint();
            return;            		
    	}		
	}

	private final static int TouchMoveMode_None = 0;
	private final static int TouchMoveMode_ShowPriceCoord = 1;
	private int mTouchMoveMode = TouchMoveMode_None;
	private float mTouchDownX = 0;
	private float mTouchMoveX = 0;
	private float mTouchX = 0;
	private float mShowPriceCoordX = 0;
	private float mLastUpShowPriceCoordX = 0;
	private int mIsTouchDown = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
    	mTouchX = event.getX();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {  
            case MotionEvent.ACTION_DOWN:
        	    doTouchDown();
            	break;
            case MotionEvent.ACTION_POINTER_DOWN:
        	    doTouchDown();
            	break;
	        case MotionEvent.ACTION_MOVE: 
	            doTouchMove();
            	return true;
            case MotionEvent.ACTION_UP:
            	doTouchUp();
            	mIsTouchDown = 2;
	            return true;
            case MotionEvent.ACTION_POINTER_UP:
            	doTouchUp();
            	mIsTouchDown = 2;
            	break;
        }
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas ACanvas) {
		super.onDraw(ACanvas);
		paint(ACanvas);
	}

	@Override
	protected void paint(Canvas ACanvas) {
		if (null == ACanvas)
			return;
		if (0 == mViewHeight)
		    mViewHeight = getHeight();
	    if (0 == mViewWidth) 
		    mViewWidth = getWidth();
	    
		float longitudeSpacing = (mViewWidth - 2) / (2 + 1);		
		Paint paint = new Paint();
		
		Rect drawRect = new Rect();
		drawRect.top = 0;
		drawRect.bottom = mViewHeight;
		drawRect.left = 0;
		drawRect.right = mViewWidth;
		
		paint.setColor(colorBackground());				
		ACanvas.drawRect(drawRect, paint);	
		
		paint.setColor(colorStockSplitter());
		ACanvas.drawLine(drawRect.left, drawRect.top, drawRect.left, drawRect.bottom, paint);
		ACanvas.drawLine(drawRect.right - 1, drawRect.top, drawRect.right - 1, drawRect.bottom, paint);
		ACanvas.drawLine(drawRect.left, drawRect.top, drawRect.right, drawRect.top, paint);
		ACanvas.drawLine(drawRect.left, drawRect.bottom - 1, drawRect.right, drawRect.bottom - 1, paint);

		int topgap = SysScreen.dip2px(15);
		Rect drawPriceRect = new Rect();
		drawPriceRect.left = 0;
		drawPriceRect.right = drawRect.right;
		drawPriceRect.top = topgap;
		drawPriceRect.bottom = (int) (drawRect.bottom - (topgap * 2)) * 2 / 3 + topgap;

		Rect drawVolumeRect = new Rect();
		drawVolumeRect.left = 0;
		drawVolumeRect.right = drawRect.right;
		drawVolumeRect.top = drawPriceRect.bottom + topgap;
		drawVolumeRect.bottom = drawRect.bottom;
		drawRealChart(ACanvas, drawRect, drawPriceRect, drawVolumeRect, mStockMinChartData);
	}

	protected void drawRealChart(Canvas ACanvas, Rect ADrawBackgroundRect, Rect ADrawPriceRect, Rect ADrawVolumeRect,
			DataEntity_StockChartMinute AChartData) {
		if (null == AChartData)
			return;
		Paint paintLine = new Paint();
		paintLine.setColor(colorStockSplitter());

		Paint paintPrice = new Paint();
		paintPrice.setColor(colorStockPriceMinute());
		paintPrice.setTextSize(30);  
		
		Paint paintVolume = new Paint();
		paintVolume.setColor(colorStockVolumeMinute());
		
		// price 顶上的线
		ACanvas.drawLine(ADrawPriceRect.left, ADrawPriceRect.top, ADrawPriceRect.right, ADrawPriceRect.top, paintLine);
		// price 底下的线
		ACanvas.drawLine(ADrawPriceRect.left, ADrawPriceRect.bottom, ADrawPriceRect.right, ADrawPriceRect.bottom, paintLine);
		ACanvas.drawLine(ADrawVolumeRect.left, ADrawVolumeRect.top, ADrawVolumeRect.right, ADrawVolumeRect.top, paintLine);
		
		// price 中间三条线
		float yprice_middle = (float) (ADrawPriceRect.top + ADrawPriceRect.bottom) / 2;
		float y2 = (float) (ADrawPriceRect.bottom - ADrawPriceRect.top) / 4;
		
		ACanvas.drawLine(ADrawPriceRect.left, yprice_middle, ADrawPriceRect.right, yprice_middle, paintLine);
		ACanvas.drawLine(ADrawPriceRect.left, yprice_middle + y2, ADrawPriceRect.right, yprice_middle + y2, paintLine);
		ACanvas.drawLine(ADrawPriceRect.left, yprice_middle - y2, ADrawPriceRect.right, yprice_middle - y2, paintLine);
		
		y2 = (float) (ADrawVolumeRect.top + ADrawVolumeRect.bottom) / 2;
		ACanvas.drawLine(ADrawPriceRect.left, y2, ADrawPriceRect.right, y2, paintLine);
		
		float x = (float) (ADrawPriceRect.left + ADrawPriceRect.right) / 2;
		float x2 = (float) (ADrawPriceRect.left + ADrawPriceRect.right) / 4;

		ACanvas.drawLine(x, ADrawPriceRect.top, x, ADrawPriceRect.bottom, paintLine);
		ACanvas.drawLine(x + x2, ADrawPriceRect.top, x + x2, ADrawPriceRect.bottom, paintLine);
		ACanvas.drawLine(x - x2, ADrawPriceRect.top, x - x2, ADrawPriceRect.bottom, paintLine);

		ACanvas.drawLine(x, ADrawVolumeRect.top, x, ADrawVolumeRect.bottom, paintLine);
		ACanvas.drawLine(x + x2, ADrawVolumeRect.top, x + x2, ADrawVolumeRect.bottom, paintLine);
		ACanvas.drawLine(x - x2, ADrawVolumeRect.top, x - x2, ADrawVolumeRect.bottom, paintLine);
		
		Log.d(LOGTAG, "drawRealChart begin" + AChartData.validRecordCount);
		boolean isContinue = true;
		int index = 1;
		if (index >= AChartData.validRecordCount) {
			isContinue = false;
		};
		AxisChartData axisPriceData = new AxisChartData();
		AxisChartData axisVolumeData = new AxisChartData();

		axisVolumeData.value_gap_high_low = 0;
		axisVolumeData.value_high_max = AChartData.volume_high;
		axisVolumeData.value_low_min = AChartData.volume_low;
		axisVolumeData.rect_gap_top_bottom = ADrawVolumeRect.bottom - ADrawVolumeRect.top - 2;
		
		axisPriceData.value_gap_high_low = 0;
		int gap1 = 0;
		if (AChartData.price_high > AChartData.price_preclose) {
			gap1 = AChartData.price_high - AChartData.price_preclose;
		} else {
			gap1 = AChartData.price_preclose - AChartData.price_high;
		} 
		int gap2 = 0;
		if (AChartData.price_low > AChartData.price_preclose) {
			gap2 = AChartData.price_low - AChartData.price_preclose;
		} else {
			gap2 = AChartData.price_preclose - AChartData.price_low;
		}
		
		if (gap1 > gap2) {
			axisPriceData.value_high_max = AChartData.price_preclose + gap1;
			axisPriceData.value_low_min = AChartData.price_preclose - gap1;			
		} else {
			axisPriceData.value_high_max = AChartData.price_preclose + gap2;
			axisPriceData.value_low_min = AChartData.price_preclose - gap2;			
		}
		
		String text = StockPrice.getPackedPriceText(AChartData.price_preclose);
		//Rect bounds1 = new Rect(); 
		ACanvas.drawText(text, ADrawPriceRect.left + 2, yprice_middle - 2, paintPrice);
		
		text = StockPrice.getPackedPriceText(axisPriceData.value_high_max);
		//paint.setTextAlign(Paint.Align.CENTER); 
		//paint.getTextBounds(text, 0, 1, bounds1);
		 FontMetricsInt fontMetrics = paintPrice.getFontMetricsInt();
		Log.d(LOGTAG, "ascent:" + paintPrice.ascent() + "/" + paintPrice.descent());		
		// 这里因为只有数字 没有 f 字符 只需要使用 paint.ascent() 就够了 baseline = 0 / paint.ascent() = -27
		ACanvas.drawText(text, ADrawPriceRect.left + 2, ADrawPriceRect.top - paintPrice.ascent(),
				//fontMetrics.descent - fontMetrics.ascent, 
				paintPrice);

		text = StockPrice.getPackedPriceText(axisPriceData.value_low_min);
		ACanvas.drawText(text, ADrawPriceRect.left + 2, ADrawPriceRect.bottom, paintPrice);

		float startX = ADrawPriceRect.left + 1;
		float gap = ((float) (ADrawPriceRect.right - ADrawPriceRect.left - 2)) / 241;
		Log.d(LOGTAG, "drawRealChart gap" + gap + "/" + ADrawPriceRect.right + "/" + ADrawPriceRect.left);
		
		float startYPrice = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_minute[index]);
		float stopX = 0;
		float stopYPrice = startYPrice;
		float startYVolume = 0;
		boolean hasPaintCoord = false;
 		while (isContinue) {
			index++;
			if (index >= AChartData.validRecordCount) {
				isContinue = false;
			}
			if (isContinue) {
	 			stopX = startX + gap; 
	 			// 中间可能有 有问题的数据
	 			if (0 < AChartData.price_minute[index]) {
		 			stopYPrice = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_minute[index]);	 				
	 			}	 			
				//Log.d(LOGTAG, "drawRealChart " + startX + "/" + stopYPrice + "/" + AChartData.price_minute[index]);
				ACanvas.drawLine(startX, startYPrice, stopX, stopYPrice, paintPrice);
				
				if (0 < AChartData.volume_high) {
					startYVolume = ADrawVolumeRect.bottom - 1 -
							(int) ((axisVolumeData.rect_gap_top_bottom * AChartData.volume_minute[index]) / AChartData.volume_high);
					ACanvas.drawLine(startX, startYVolume, startX, ADrawVolumeRect.bottom - 1, paintVolume);				
				}
				if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
					if (!hasPaintCoord) {
						if (startX <= mTouchX) {
							if (stopX >= mTouchX) {
								Log.d(LOGTAG, "drawRealChart ShowPriceCoord:" + mTouchX + "/" + startX + "/" + stopX);
								//Log.d(LOGTAG, " --------- paint ----------------- ");
								hasPaintCoord = true;
								mShowPriceCoordX = startX;
								ACanvas.drawLine(mShowPriceCoordX, ADrawPriceRect.top + 1, mShowPriceCoordX, ADrawVolumeRect.bottom - 1, paintPrice);
								ACanvas.drawLine(ADrawPriceRect.left, stopYPrice, ADrawPriceRect.right, stopYPrice, paintPrice);
							}
						}
					}				
				}				
				startX = stopX;
				startYPrice = stopYPrice;
			}
		}
	}
	
	@Override
	protected void init() {	
	}
}
