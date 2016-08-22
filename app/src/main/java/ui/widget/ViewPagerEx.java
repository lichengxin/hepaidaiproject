package ui.widget;

import android.content.Context;
import android.util.AttributeSet;

public class ViewPagerEx extends android.support.v4.view.ViewPager {

	private static final String LOGTAG = ViewPagerEx.class.getSimpleName();
	//*//
	public ViewPagerEx(Context context) {
		super(context);
	}
	public ViewPagerEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		switch (ev.getAction()) {
	        case android.view.MotionEvent.ACTION_DOWN:
	        	checkAllowScrollYPosition(ev.getY());
	        	return false;
		    case android.view.MotionEvent.ACTION_POINTER_DOWN:
	        	checkAllowScrollYPosition(ev.getY());
	        	return false;
		    case android.view.MotionEvent.ACTION_MOVE:
		    	//if (isDiabledScroll()) {
		    	//	return false;
		    	//}
		        break;
		    case android.view.MotionEvent.ACTION_UP:
		    	if (isDiabledScroll()) {
		    		return false;
		    	}
		        break;
		    case android.view.MotionEvent.ACTION_POINTER_UP:
		    	if (isDiabledScroll()) {
		    		return false;
		    	}
		    	break;
		    default:
		    	if (isDiabledScroll()) {
		    		return false;
		    	};
		}
		return super.onInterceptTouchEvent(ev); 
    }
	
	//*/
	@Override
	public boolean onTouchEvent(android.view.MotionEvent ev) {
		//*//
		switch (ev.getAction()) {
		    case android.view.MotionEvent.ACTION_DOWN:
	        	checkAllowScrollYPosition(ev.getY());
		        break;
		    case android.view.MotionEvent.ACTION_POINTER_DOWN:
	        	checkAllowScrollYPosition(ev.getY());
		    	break;
		    case android.view.MotionEvent.ACTION_UP:
		        break;
		    case android.view.MotionEvent.ACTION_POINTER_UP:
		    	break;
		    case android.view.MotionEvent.ACTION_MOVE:
		        break;
		    case android.view.MotionEvent.ACTION_CANCEL:
		       	break;
		    case android.view.MotionEvent.ACTION_OUTSIDE:
		       	break;
		}
		//*/
	    return super.onTouchEvent(ev);
	}

	private void checkAllowScrollYPosition(float y) {
		if (0 < mDisableScrollMaxY) {
	    	if (mDisableScrollMaxY > y) {
	    		mIsDiableScroll = 1;
	    		return;
	    	}
		}
		mIsDiableScroll = 0;
	}

	private boolean isDiabledScroll() {
		return 1 == mIsDiableScroll;
	}
	
	public int mDisableScrollMaxY = 0;
	private int mIsDiableScroll = 0;
	
	@Override
	public void scrollTo(int x, int y){
		//*//
		if (isDiabledScroll()) {
			return;
		}
		//*/
        super.scrollTo(x, y);  
    }  
}
