package utils;

import base.app.BaseApp;

import android.util.DisplayMetrics;
import android.view.View;

/*
case COMPLEX_UNIT_PX: return value;
case COMPLEX_UNIT_SP: return value * metrics.scaledDensity;
case COMPLEX_UNIT_PT: return value * metrics.xdpi * (1.0f/72);
scaledDensity = DENSITY_DEVICE / (float) DENSITY_DEFAULT;
xdpi = DENSITY_DEVICE;
DENSITY_DEFAULT = DENSITY_MEDIUM = 160;
*/

public class SysScreen {
	
	//也可以理解为1dip相当于多少个px啦。
	public static float mDensity = 0;
	// 就是屏幕密度
	public static int mDensityDpi = 0;
	public static int mHeightPixels = 0;
	public static int mWidthPixels = 0;
	public static float mScaledDensity = 0;
	public static float mXdpi = 0;
	public static float mYdpi = 0;
	
	private SysScreen() {		
	}
	
	private static SysScreen mInstance = null;
	public static SysScreen getInstance() {
		if (null == mInstance) {
			mInstance = new SysScreen(); 
		}
		return mInstance;
	}
	
	// dpi是240，1dip=1.5px	
	public static void initParam() {
		if ((0 == mDensity) || (0 == mDensityDpi)) {
			android.util.DisplayMetrics display = BaseApp.instance.getResources().getDisplayMetrics();
			if (null != display) {
				mDensity = display.density;
				mDensityDpi = display.densityDpi;
				mHeightPixels = display.heightPixels;
				mWidthPixels = display.widthPixels;
				mScaledDensity = display.scaledDensity;
				mXdpi = display.xdpi;
				mYdpi = display.ydpi;
			}
		} 
	}

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
	public static int dip2px(int dip) {
		initParam();
		//return (int)(dip * mDensity + 0.5f*(dip>=0?1:-1));
		return (int)(dip * mScaledDensity + 0.5f*(dip>=0?1:-1)); 
	}
	
	//*//
    public static int px2dip(float pxValue) { 
		initParam(); 
        return (int) (pxValue / mDensity + 0.5f); 
    } 

	/*//
    public static int dip2px(Context context, float dipValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dipValue * scale + 0.5f); 
    } 
    
    public static int px2sp(Context context, float pxValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (pxValue / fontScale + 0.5f); 
    } 
   
    public static int sp2px(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    } 
	//*/
   
    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
//        int height = view.getMeasuredHeight();
//        if(0 < height){
//            return height;
//        }
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }	

    /**
     * 测量控件的尺寸
     * @param view
     */
    public static void calcViewMeasure(View view) {
//        int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        view.measure(width,height);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }
    
   public static String getDenstyMode(int mode) {
	   DisplayMetrics display = BaseApp.instance.getResources().getDisplayMetrics();
	   if (display.densityDpi < DisplayMetrics.DENSITY_XHIGH) {
		   return "@!low"+mode;
	   }
	   if (display.densityDpi < DisplayMetrics.DENSITY_XXHIGH) {
		   return "@!medium"+mode;
	   }
	   return "@!high"+mode;
   }
    
}
