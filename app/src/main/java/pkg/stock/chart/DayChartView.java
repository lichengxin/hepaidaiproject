package pkg.stock.chart;

import pkg.stock.data.*;
import utils.*;

import com.app.R;

import android.content.*;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.view.*;

public class DayChartView extends ChartView {
	
	private static final String LOGTAG = DayChartView.class.getSimpleName();
	
	//  android中根据touch事件判断单击及双击
	private static final int MAX_INTERVAL_FOR_CLICK = 250;  
    private static final int MAX_DISTANCE_FOR_CLICK = 100;  
    private static final int MAX_DOUBLE_CLICK_INTERVAL = 500;  

    private int [] CandleGap = {1,1,2,2,3,3,4};
    private int MaxCandleWidthDip = 7;
    
    private int mCandleWidthDip = MaxCandleWidthDip;
	private int mCandleWidth = SysScreen.dip2px(mCandleWidthDip);
	private int mCandleGap = 4;
	
	public DayChartView(Context context) {
		super(context);
	}

	public DayChartView(Context context, android.util.AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DayChartView(Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
	}

	public int colorStockPriceOffset0() {
		return idColor(R.color.white);
	}

	public int colorStockPriceOffsetUp() {
		return idColor(R.color.red);
	}

	public int colorStockPriceOffsetDown() {
		return idColor(R.color.green);
	}
	
	private void doTouchDown() {
    	mTouchDownX = mTouchX;
    	mIsTouchDown = 1;
    	StockCandleChartData chartdata = getChartData();
    	if (null != chartdata) {
        	mTouchDownDataCursorIndex0 = chartdata.index_datacursor[0];
        	if (TouchMoveMode_MoveDataCursor == mTouchMoveMode) {
        		mTouchMoveMode = TouchMoveMode_None;
        	}
    	} else {
    		mTouchMoveMode = TouchMoveMode_None;
    	}
	}

	private void doTouchUp() {
    	if (TouchMoveMode_None == mTouchMoveMode) {
    		mTouchMoveMode = TouchMoveMode_ShowPriceCoord;
        	this.repaint();
        	mIsTouchDown = 2;
            return;
    	}
    	if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
    		if (1 == mIsTouchDown) {
                if (SysScreen.dip2px(10) > distance(mTouchDownX, mLastUpShowPriceCoordX)) {
                	mTouchMoveMode = TouchMoveMode_None;     
                }
    		}
        	this.repaint();
        	mIsTouchDown = 2;
        	if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
        	    mLastUpShowPriceCoordX = mShowPriceCoordX;
        	}
            return;
    	};
    	if (TouchMoveMode_MoveDataCursor == mTouchMoveMode) {
        	mIsTouchDown = 2;
        	mTouchMoveMode = TouchMoveMode_None;
            return;
    	}
	}
	
	private void doTouchMove() {
    	mTouchMoveX = mTouchX;
    	float moveOffset = 0;
    	StockCandleChartData chartdata = getChartData();
    	if (TouchMoveMode_None == mTouchMoveMode) {
        	if (1 == mIsTouchDown) {
            	if (mTouchMoveX > mTouchDownX) {
            		moveOffset = mTouchMoveX - mTouchDownX;
            	} else {
            		moveOffset = mTouchDownX - mTouchMoveX;
            	}            		
        	}
        	if (moveOffset > mCandleWidth * 2) {
        		if (null != chartdata) {
            		mTouchMoveMode = TouchMoveMode_MoveDataCursor;        			
        		}
        	}  		
    	}
    	if (TouchMoveMode_ShowPriceCoord == mTouchMoveMode) {
        	this.repaint();
            return;            		
    	}
    	if (TouchMoveMode_MoveDataCursor == mTouchMoveMode) {
        	if (1 == mIsTouchDown) {
            	if (mTouchMoveX > mTouchDownX) {
            		moveOffset = mTouchMoveX - mTouchDownX;
            	} else {
            		moveOffset = mTouchDownX - mTouchMoveX;
            	}      
            	int offsetCount = (int) (moveOffset / mCandleWidth);
            	int newDataCursorIndex = 0;
               	if (mTouchMoveX > mTouchDownX) {
               		newDataCursorIndex = mTouchDownDataCursorIndex0 - offsetCount;
               	} else {
               		newDataCursorIndex = mTouchDownDataCursorIndex0 + offsetCount;
               	}   
               	if (0 > newDataCursorIndex)
               		newDataCursorIndex = 0;               	
               	if ((newDataCursorIndex + chartdata.maxRecordCount) >= mStockDayData.getRecordCount()) {
               		newDataCursorIndex = mStockDayData.getRecordCount() - chartdata.maxRecordCount;
               	}
               	if (newDataCursorIndex != mFirstDataCursorIndex) {
                   	loadChartData(chartdata, mStockDayData, newDataCursorIndex);
    		    	mFirstDataCursorIndex = chartdata.index_datacursor[0];
                   	this.repaint();            		
               	}
        	}
            return;            		
    	}
	}
	
	private double getZoomHandPointLen(MotionEvent event) {
		int touchPointerCount = event.getPointerCount();
		for(int i=0; i < touchPointerCount; i++)  
        {  
            float x = event.getX(i);  
            float y = event.getY(i);
            Point pt = new Point((int)x, (int)y);  
        }     
        int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));  
        int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));  
        return Math.sqrt((double)xlen*xlen + (double)ylen * ylen);  
	}

	private double mZoomLenStart = 0;
	private int mCandleWidthDipStart = 0;
	private double mZoomLenMax = 0;
	private double mZoomLenMin = 0;
	private static int zoomLenGapUnit = SysScreen.dip2px(20);
	
	private boolean zoomChart(double lenStart, double lenEnd) {
		if (0 == mZoomLenMax) {
			mZoomLenMax = lenEnd;
			mZoomLenMin = lenEnd;
		} else {
			if (0 < lenEnd) {
				if (mZoomLenMax < lenEnd) {
					mZoomLenMax = lenEnd;
				}				
				if (mZoomLenMin > lenEnd) {
					mZoomLenMin = lenEnd;
				}				
			}
		}
		int zoomLen = 0;
		if (mZoomLenStart > lenEnd) {
			zoomLen = (int) ((mZoomLenStart - lenEnd) / zoomLenGapUnit);
		} else {
			zoomLen = (int) ((lenEnd - mZoomLenStart) / zoomLenGapUnit);
		}
		Log.d(LOGTAG, "zoomlen:" + zoomLen);
		
		StockCandleChartData chartdata = getChartData();
		if (null == chartdata)
			return false;
    	int lastDataCursorIndex = -1;
    	if (0 < chartdata.validRecordCount) {
    		Log.d(LOGTAG, "zoom chart validRecordcount:" + chartdata.validRecordCount);
    		lastDataCursorIndex = chartdata.index_datacursor[chartdata.validRecordCount - 1];
    	} else {
    		return false;
    	}
    	int lastCandleWidth = mCandleWidth;
    	int newCandleWidthdip = 0; 
        if (mZoomLenStart > lenEnd) {	
        	newCandleWidthdip = mCandleWidthDipStart - zoomLen;
        } else {			        
        	newCandleWidthdip = mCandleWidthDipStart + zoomLen;	
        }
    	if (1 > newCandleWidthdip) {
    		newCandleWidthdip = 1;
    	}
    	if (MaxCandleWidthDip < newCandleWidthdip) {
    		newCandleWidthdip = MaxCandleWidthDip;
    	}
    	int candleGap = CandleGap[newCandleWidthdip - 1];        		
    	int newCandleWidth = SysScreen.dip2px(newCandleWidthdip);
    	int maxRecordCnt = computeMaxRecordCount(newCandleWidth, candleGap);
    	if (maxRecordCnt > mStockDayData.getRecordCount() + 20) {
    		newCandleWidth = (int) (((double) mViewWidth / (mStockDayData.getRecordCount() + 1)) - candleGap);
    		newCandleWidthdip = SysScreen.px2dip(newCandleWidth);
    		maxRecordCnt = computeMaxRecordCount(newCandleWidth, candleGap);
    	}
		Log.d(LOGTAG, "newCandleWidth:" + newCandleWidthdip + "/" + newCandleWidth);
        if (lastCandleWidth != newCandleWidth) {
        	mCandleWidthDip = newCandleWidthdip;
        	mCandleGap = candleGap;
        	updateCandleWidth(newCandleWidth, maxRecordCnt);
        	int newFirstDataCursorIndex = lastDataCursorIndex - chartdata.maxRecordCount;
        	if (0 > newFirstDataCursorIndex)
        		newFirstDataCursorIndex = 0;
           	loadChartData(chartdata, mStockDayData, newFirstDataCursorIndex);
        }
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
    	mTouchX = event.getX();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {  
            case MotionEvent.ACTION_DOWN:
            	if (TouchMoveMode_Zoom != mTouchMoveMode)
            	    doTouchDown();
	            return true;
            case MotionEvent.ACTION_POINTER_DOWN:
        		if (TouchMoveMode_None == mTouchMoveMode) {
        			int touchPointerCount = event.getPointerCount();
        			if (2 == touchPointerCount) {
        				// 两个手指缩放 操作
        				mZoomLenStart = getZoomHandPointLen(event);
        				mCandleWidthDipStart = mCandleWidthDip;
        				mTouchMoveMode = TouchMoveMode_Zoom;
        			}
        		}        		
        		if (TouchMoveMode_Zoom != mTouchMoveMode)
            	    doTouchDown();
	            return true;
	        case MotionEvent.ACTION_MOVE: 
        		if (TouchMoveMode_Zoom == mTouchMoveMode) {
    				if (zoomChart(mZoomLenStart, getZoomHandPointLen(event))) {
        				repaint();    					
    				}
        		} else {
	                doTouchMove();
        		}
            	return true;
            case MotionEvent.ACTION_UP:
            	boolean isZoom = false;
            	if (0 < mZoomLastPointUpTime) {
                	long uptime = (new java.util.Date()).getTime();
                	if (uptime > mZoomLastPointUpTime) {
                		if (200 > (uptime - mZoomLastPointUpTime)) {
                			isZoom = true;
                		}
                	}
    		        mZoomLastPointUpTime = 0;
            	}
            	if (!isZoom) 
            	    doTouchUp();
				if (TouchMoveMode_Zoom == mTouchMoveMode) 
    				mTouchMoveMode = TouchMoveMode_None;
            	mIsTouchDown = 2;
	            return true;
            case MotionEvent.ACTION_POINTER_UP:
				if (TouchMoveMode_Zoom == mTouchMoveMode) {
    				mTouchMoveMode = TouchMoveMode_None;
    				//zoomChart(mZoomLenStart, getZoomHandPointLen(event));
			        mZoomLastPointUpTime = (new java.util.Date()).getTime();
	            	mIsTouchDown = 2;
			        repaint();
			        return true;
				}
            	doTouchUp();
            	mIsTouchDown = 2;
	            return true;
        }
		return super.onTouchEvent(event);
	}

	private final static int TouchMoveMode_None = 0;
	private final static int TouchMoveMode_ShowPriceCoord = 1;
	private final static int TouchMoveMode_MoveDataCursor = 2;	
	private final static int TouchMoveMode_Zoom = 3;	
	private int mTouchMoveMode = TouchMoveMode_None;
	private long mZoomLastPointUpTime = 0;
	
	private int mIsTouchDown = 0;
	private int mTouchDownDataCursorIndex0 = 0;
	private int mFirstDataCursorIndex = 0;
	
	private float mTouchDownX = 0;
	private float mTouchMoveX = 0;
	private float mTouchX = 0;
	private float mShowPriceCoordX = 0;
	private int mShowPriceCoordCursorIndex = -1;
	private float mLastUpShowPriceCoordX = 0;
	
	public int computeMaxRecordCount(int ACandleWidth, int ACandleGap) {
		return (int) (mViewWidth / (ACandleWidth + ACandleGap));
	}
	
	public void updateCandleWidth(int ACandleWidth, int AMaxRecordCount) {
		if (0 < ACandleWidth) {
			mCandleWidth = ACandleWidth;
			StockCandleChartData chartdata = getChartData();
			if (null != chartdata) {
				chartdata.updateMaxRecordCount(AMaxRecordCount);
			}			
		}
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
		StockCandleChartData chartdata = getChartData();
	    if (null != chartdata) {
			if (0 == chartdata.validRecordCount) {
			    if (null != mStockDayData) {	
					if (chartdata.maxRecordCount >= mStockDayData.getRecordCount()) {
						loadChartData(chartdata, mStockDayData, 0);
					} else {
						loadChartData(chartdata, mStockDayData, mStockDayData.getRecordCount() - chartdata.maxRecordCount);			
					}
			    	mFirstDataCursorIndex = chartdata.index_datacursor[0];
				}
			}
	    }
	    //if (true) 
	    //	return;
		
		Rect drawRect = new Rect();
		Rect drawPriceRect = new Rect();
		Rect drawVolumeRect = new Rect();
		
		drawRect.left = 0;
		drawRect.top = 0;
		drawRect.right = mViewWidth;
		drawRect.bottom = mViewHeight;

		drawPriceRect.left = 0;
		drawPriceRect.right = drawRect.right;
		drawPriceRect.top = SysScreen.dip2px(15);
		drawPriceRect.bottom = (int) (drawRect.bottom / 4) * 3;
		
		drawVolumeRect.left = 0;
		drawVolumeRect.right = drawRect.right;
		drawVolumeRect.top = drawPriceRect.bottom + SysScreen.dip2px(15);
		drawVolumeRect.bottom = drawRect.bottom;
		// ========================================
		// draw candle chart
		// ========================================
		drawCandleChart(ACanvas, drawRect, drawPriceRect, drawVolumeRect, chartdata);
	}
	
	private void fillChartData(StockCandleChartData AChartData, int AChartIndex,
			StockDataBuffer_Day.BufferNode ADataNode, int ANodeIndex) {
		AChartData.index_datacursor[AChartIndex] = ADataNode.index[ANodeIndex];
		AChartData.price_open[AChartIndex] = ADataNode.priceOpen[ANodeIndex];
		AChartData.price_close[AChartIndex] = ADataNode.priceClose[ANodeIndex];
		AChartData.price_high[AChartIndex] = ADataNode.priceHigh[ANodeIndex];
		AChartData.price_low[AChartIndex] = ADataNode.priceLow[ANodeIndex];
		AChartData.price_date[AChartIndex] = ADataNode.dealDate[ANodeIndex];
		AChartData.volume[AChartIndex] = ADataNode.volume[ANodeIndex];
		AChartData.amount[AChartIndex] = ADataNode.amount[ANodeIndex];
		if (0 == AChartData.volume_max) {
			AChartData.volume_max = AChartData.volume[AChartIndex];
			AChartData.volume_min = AChartData.volume[AChartIndex];
		} else {
			if (0 < AChartData.volume[AChartIndex]) {
				if (AChartData.volume[AChartIndex] > AChartData.volume_max) {
					AChartData.volume_max = AChartData.volume[AChartIndex];
				}		
				if (AChartData.volume[AChartIndex] < AChartData.volume_min) {
					AChartData.volume_min = AChartData.volume[AChartIndex];
				}		
			}
		}
			
		if (0 == AChartData.price_low_min) {
			AChartData.price_low_min = AChartData.price_low[AChartIndex];
			AChartData.price_high_max = AChartData.price_high[AChartIndex];
		}
		if (AChartData.price_high[AChartIndex] > AChartData.price_high_max) {
			AChartData.price_high_max = AChartData.price_high[AChartIndex];
		}
		//Log.d(LOGTAG, "fillChartData:" + AChartData.price_high_max + 
		//		new java.text.SimpleDateFormat("yyyyMMdd").format(DelphiUtils.DelphiTime2JavaTime(AChartData.price_date[AChartIndex])));
		if (0 < AChartData.price_low[AChartIndex]) {
			if (AChartData.price_low[AChartIndex] < AChartData.price_low_min) {
				AChartData.price_low_min = AChartData.price_low[AChartIndex];
			}
		}
	}

	private void loadChartData(StockCandleChartData AChartData, StockDataStore_Day AStockData, int AFirstDataCursorIndex) {
		if (0 > AFirstDataCursorIndex)
			return;
		StockDataBuffer_Day buffer = AStockData.getDataBuffer();
		boolean isContinue = true;
		StockDataBuffer_Day.BufferNode dataNode = buffer.firstNode;
		StockDataBuffer_Day.BufferNode fillNode = null;
		int fillIndex = 0;
		// 多计算一个 蜡烛 第一个时间的  preclose_price
		AChartData.validRecordCount = 0;
		AChartData.price_high_max = 0; // 最高的最高
		AChartData.price_low_min = 0; // 最低的最低		
		AChartData.volume_min = 0;
		AChartData.volume_max = 0;
		int chartindex = 0;
		while (isContinue) {
			if (null == fillNode) {
				if (null != dataNode) {
					if (dataNode.index[0] <= AFirstDataCursorIndex) {
						if (dataNode.index[dataNode.bufcount - 1] >= AFirstDataCursorIndex) {
							fillNode = dataNode;
							fillIndex = AFirstDataCursorIndex - dataNode.index[0];
						}
					}
				}
			}
			if (null != fillNode) {
				for (int i = fillIndex; i < dataNode.bufcount; i ++) {
					if (AChartData.maxRecordCount > AChartData.validRecordCount) {
   					    fillChartData(AChartData, chartindex, dataNode, i);
   					    chartindex++;		
					    AChartData.validRecordCount++;						
					}
				}
			}
			if (null != dataNode) {
				dataNode = dataNode.nextSibling;
				if (null != fillNode) {
					fillNode = dataNode;
					fillIndex = 0;
				}
			}
			if (isContinue) {
				if (AChartData.maxRecordCount >= AStockData.getRecordCount()) {
					isContinue = false;					
				} else {
					if (AChartData.maxRecordCount <= AChartData.validRecordCount) {
						isContinue = false;					
					}
				}
				if (null == dataNode) {
					isContinue = false;					
				}
			}
		}
	}
	
	private StockCandleChartData mChartData = null;
	public StockCandleChartData getChartData() {
		if (null == mChartData) {
			mChartData = new StockCandleChartData((int) (mViewWidth / (mCandleWidth + mCandleGap)));
		}
		return mChartData;
	}
	
	protected void drawCandleChart(Canvas ACanvas,
			Rect ADrawBackgroundRect,
			Rect ADrawPriceRect,
			Rect ADrawVolumeRect,
			StockCandleChartData AChartData) {
		android.graphics.Paint paint = new android.graphics.Paint();	
		paint.setColor(colorBackground());
		ACanvas.drawRect(ADrawBackgroundRect, paint);

		paint.setColor(colorStockSplitter());

		Paint paintMiddleLine = new Paint(Paint.ANTI_ALIAS_FLAG);  
		paintMiddleLine.setStyle(Style.STROKE);  
		paintMiddleLine.setColor(colorStockSplitter());  
		paintMiddleLine.setStrokeWidth(1);  
		PathEffect effects = new DashPathEffect(new float[] { 2, 2, 2, 2}, 1);  
		paintMiddleLine.setPathEffect(effects);  
		
		float yprice_middle = (float) (ADrawPriceRect.top + ADrawPriceRect.bottom) / 2;
		float y2 = (float) (ADrawPriceRect.top + ADrawPriceRect.bottom) / 4;
		ACanvas.drawLine(ADrawBackgroundRect.left, yprice_middle, ADrawBackgroundRect.right, yprice_middle, paintMiddleLine);
		ACanvas.drawLine(ADrawBackgroundRect.left, yprice_middle - y2, ADrawBackgroundRect.right, yprice_middle - y2, paintMiddleLine);
		ACanvas.drawLine(ADrawBackgroundRect.left, yprice_middle + y2, ADrawBackgroundRect.right, yprice_middle + y2, paintMiddleLine);
		
		ACanvas.drawLine(ADrawBackgroundRect.left, ADrawBackgroundRect.top, ADrawBackgroundRect.left, ADrawBackgroundRect.bottom, paint);
		ACanvas.drawLine(ADrawBackgroundRect.right - 1, ADrawBackgroundRect.top, ADrawBackgroundRect.right - 1, ADrawBackgroundRect.bottom, paint);
		ACanvas.drawLine(ADrawBackgroundRect.left, ADrawBackgroundRect.top, ADrawBackgroundRect.right, ADrawBackgroundRect.top, paint);
		ACanvas.drawLine(ADrawBackgroundRect.left, ADrawBackgroundRect.bottom - 1, ADrawBackgroundRect.right, ADrawBackgroundRect.bottom - 1, paint);
		
		ACanvas.drawLine(ADrawPriceRect.left, ADrawPriceRect.top, ADrawPriceRect.right, ADrawPriceRect.top, paint);
		ACanvas.drawLine(ADrawPriceRect.left, ADrawPriceRect.bottom, ADrawPriceRect.right, ADrawPriceRect.bottom, paint);
		ACanvas.drawLine(ADrawVolumeRect.left, ADrawVolumeRect.top, ADrawVolumeRect.right, ADrawVolumeRect.top, paint);
		
		float yvolume_middle = (float) (ADrawVolumeRect.top + ADrawVolumeRect.bottom) / 2;
		ACanvas.drawLine(ADrawVolumeRect.left, yvolume_middle, ADrawVolumeRect.right, yvolume_middle, paintMiddleLine);
			/*/ test
			cacheRect.right = 100;
			cacheRect.top = 10;
			cacheRect.bottom = 100;
			paint.setColor(colorStockPriceOffsetUp());			
			ACanvas.drawRect(cacheRect, paint);
			//*/
			
		boolean isContinue = true;
		int index = 0;
		if (index >= AChartData.validRecordCount) {
			isContinue = false;
		};
			//Log.d(LOGTAG, "chartData.candleCount" + chartData.validCandleCount);
		float startX = 0;
		float startY = 0;
		float stopX = 0; 
		float stopY = 0;
		int positionClose = 0;
			
		boolean isFill = false;

		AxisChartData axisPriceData = new AxisChartData();
		AxisChartData axisVolumeData = new AxisChartData();	
		axisPriceData.value_gap_high_low = 0;
		axisPriceData.value_high_max = AChartData.price_high_max;
		axisPriceData.value_low_min = AChartData.price_low_min;
		axisVolumeData.value_gap_high_low = 0;
			
		android.graphics.Rect cacheRect = new android.graphics.Rect();
		cacheRect.left = 1;
		boolean hasPaintCoord = false;
		while (isContinue) {
			cacheRect.right = cacheRect.left + mCandleWidth;
			//-------------------------------------------------
			//-------------------------------------------------

			if (AChartData.price_open[index] == AChartData.price_close[index]) {
				cacheRect.top = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_open[index]);
				cacheRect.bottom = cacheRect.top + 1;
				positionClose = cacheRect.bottom; 
				paint.setStyle(android.graphics.Paint.Style.FILL); 
				isFill = true;
				paint.setColor(colorStockPriceOffset0());						
			} else {
				if (AChartData.price_open[index] < AChartData.price_close[index]) {
					cacheRect.top = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_close[index]);
					cacheRect.bottom = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_open[index]);
					if (cacheRect.top == cacheRect.bottom) {
						cacheRect.bottom = cacheRect.top + 1;
					}
					positionClose = cacheRect.top; 
					isFill = false;
					paint.setStyle(android.graphics.Paint.Style.STROKE); 
					paint.setColor(colorStockPriceOffsetUp());							
				} else {						
					cacheRect.top = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_open[index]);
					cacheRect.bottom = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_close[index]);
					if (cacheRect.top == cacheRect.bottom) {
						cacheRect.bottom = cacheRect.top + 1;
		   		    }
					positionClose = cacheRect.bottom;
					isFill = true;
					paint.setStyle(android.graphics.Paint.Style.FILL); 
					paint.setColor(colorStockPriceOffsetDown());					
				}
			}
			ACanvas.drawRect(cacheRect, paint);
			//-------------------------------------------------
			//-------------------------------------------------
			startX = cacheRect.left + mCandleWidth / 2;
			stopX = startX;
			if (isFill) {
				startY = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_high[index]);
				stopY = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_low[index]);
				ACanvas.drawLine(startX, startY, stopX, stopY, paint);
			} else {
				startY = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_high[index]);
				stopY = cacheRect.top;
				ACanvas.drawLine(startX, startY, stopX, stopY, paint);
				startY = cacheRect.bottom;
				stopY = getChartPosY(axisPriceData, ADrawPriceRect, AChartData.price_low[index]);
				ACanvas.drawLine(startX, startY, stopX, stopY, paint);
			}
			//-------------------------------------------------
			//-------------------------------------------------
			if (0 == axisVolumeData.value_gap_high_low) {
				axisVolumeData.value_gap_high_low = (int) (AChartData.volume_max - AChartData.volume_min);
			}
			if (0 == axisVolumeData.rect_gap_top_bottom) {
				axisVolumeData.rect_gap_top_bottom = ADrawVolumeRect.bottom - ADrawVolumeRect.top - 2;
			}
			if (0 < AChartData.volume_max){
				cacheRect.top = ADrawVolumeRect.bottom - (int) ((axisVolumeData.rect_gap_top_bottom * AChartData.volume[index]) / AChartData.volume_max); 
				cacheRect.bottom = ADrawVolumeRect.bottom - 2;
				//paint.setStyle(android.graphics.Paint.Style.FILL); 
					if (AChartData.price_open[index] == AChartData.price_close[index]) {
				    paint.setColor(colorStockPriceOffsetDown());			
				}
				ACanvas.drawRect(cacheRect, paint);
			}
			//-------------------------------------------------
			//-------------------------------------------------
			if  (mTouchMoveMode == TouchMoveMode_ShowPriceCoord) {
				if (!hasPaintCoord) {
					//Log.d(LOGTAG, "paint showcoord" + startX + "/" + mTouchX + "/" + positionClose);
					if (cacheRect.left <= mTouchX) {
						if ((cacheRect.right + mCandleGap) >= mTouchX) {
							//Log.d(LOGTAG, " --------- paint ----------------- ");
							hasPaintCoord = true;
							paint.setColor(colorStockPriceOffset0());
							mShowPriceCoordX = startX;
							if (mShowPriceCoordCursorIndex != AChartData.index_datacursor[index]) {
								mShowPriceCoordCursorIndex = AChartData.index_datacursor[index];
								// 通知外部 数据变化
								if (null != mChartCoordNotifier) {
									mChartCoordNotifier.OnShow(AChartData, index, mShowPriceCoordCursorIndex);									
								}
							}
							ACanvas.drawLine(startX, ADrawPriceRect.top, startX, ADrawBackgroundRect.bottom, paint);
							ACanvas.drawLine(ADrawPriceRect.left, positionClose, ADrawPriceRect.right, positionClose, paint);  
						}
					}
				}
			} 
			//-------------------------------------------------
			//-------------------------------------------------				
			cacheRect.left = cacheRect.right + mCandleGap;
			index++;
			if (index >= AChartData.validRecordCount) {
				isContinue = false;
			}
		}
	}
	
	@Override
	protected void init() {		
	}

	private StockDataStore_Day mStockDayData = null;
	public void setDataSource(StockDataStore_Day AStockDayData) {
		mStockDayData = AStockDayData;
	}
	
	// 可视部分的 数据 蜡烛线
	public static class StockCandleChartData extends ChartData {
		
		public StockCandleChartData(int ACandleCount) {
			maxRecordCount = ACandleCount;
			if (0 < ACandleCount) {
				updateMaxRecordCount(ACandleCount);
			}
		}
		
		@Override
		public void updateMaxRecordCount(int AMaxRecordCount) {
			index_datacursor = new int[AMaxRecordCount];
			price_date = new int[AMaxRecordCount]; // 周期 日线 周线 月线
			price_high = new int[AMaxRecordCount]; // 最高
			price_low = new int[AMaxRecordCount]; // 最高
			price_open = new int[AMaxRecordCount]; // 最高
			price_close = new int[AMaxRecordCount]; // 最高
			volume = new long[AMaxRecordCount]; // 成交量 手
			amount = new long[AMaxRecordCount]; // 成交金额
			super.updateMaxRecordCount(AMaxRecordCount);
		}
		
		public int firstPreClosePrice = -1;
		public int price_high_max = 0; // 最高的最高
		public int price_low_min = 0; // 最低的最低		
		public long volume_min = 0;
		public long volume_max = 0;
		public int[] index_datacursor = null;
		public int[] price_date = null; // 周期 日线 周线 月线
		public int[] price_high = null; // 最高
		public int[] price_low = null; // 最高
		public int[] price_open = null; // 最高
		public int[] price_close = null; // 最高
		public long[] volume = null; // 成交量 手
		public long[] amount = null; // 成交金额
	}	
}
