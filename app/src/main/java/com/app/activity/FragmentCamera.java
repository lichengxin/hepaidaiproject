package com.app.activity;

import ui.activity.BaseFragment;
import ui.widget.CameraViewEx;
import utils.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import base.app.BaseApp;

import com.app.R;

public class FragmentCamera extends BaseFragment {

	private static final String LOGTAG = FragmentCamera.class.getSimpleName();
	
	/* <uses-permission android:name = "android.permission.CAMERA" />
	 * <uses-feature android:name = "android.hardware.camera" />
	 * 通过 Camera API或摄像头意图 Intent ，Android框架为图像和视频捕获提供支持。下面列出了有关的类：
    Camera        此类是控制摄像头的主要API。在创建摄像头应用程序时，此类用于拍摄照片或视频。
    SurfaceView   此类用于向用户提供摄像头实时预览功能。
    MediaRecorder 此类用于从摄像头录制视频。
    Intent        动作类型为MediaStore.ACTION_IMAGE_CAPTURE 或
              MediaStore.ACTION_VIDEO_CAPTURE 的意图, 可在不直接使用Camera对象的情况下捕获图像和视频
    */

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mView = inflater.inflate(getLayoutId(), container, false);		
		initViews();
		return mView;
	}

    @Override
    protected int getLayoutId() {
        return R.layout.pkg_app_fragment_empty;
    }
    
	protected void initViews() {
		initCameraView();
	}

	CameraViewEx mCameraView = null;
	protected void initCameraView() {
		RelativeLayout mainLayout = (RelativeLayout) mView.findViewById(R.id.activity_main);
		if (null != mainLayout) {
		    mCameraView = new ui.widget.CameraViewEx(getActivity());
		    mainLayout.addView(mCameraView, new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		    

	        Thread openThread = new Thread(){  
	            @Override  
	            public void run() {  
	                // TODO Auto-generated method stub  
	            	CameraViewEx.CameraAPI.getInstance().doOpenCamera(mCameraView.getHolder());  
	            }  
	        };  
	        openThread.start();  
		}
	}
	
	//改写物理按键——返回的逻辑
    //@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
        }
        return false;
        //return super.onKeyDown(keyCode, event);
    }
}
