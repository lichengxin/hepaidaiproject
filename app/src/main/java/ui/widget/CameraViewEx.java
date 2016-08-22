package ui.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraViewEx extends SurfaceView  implements SurfaceHolder.Callback  {

	public Context mContext = null;  
	public static SurfaceHolder mSurfaceHolder = null;  
	
	public CameraViewEx(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public CameraViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public CameraViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	
	private void init() {
		if (null == mSurfaceHolder) {
			mSurfaceHolder = getHolder();
			//translucent半透明 transparent透明  
			mSurfaceHolder.setFormat(android.graphics.PixelFormat.TRANSPARENT);
	        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
	        mSurfaceHolder.addCallback(this);  
		}  
	}
			
	// 闪光灯
	//  <uses-permission android:name="android.permission.FLASHLIGHT" /> 
	public static class CameraAPI {
		private android.hardware.Camera mCamera = null;  
        private android.hardware.Camera.Parameters mCameraParams = null;   
        
        private static CameraAPI mCameraApi = null;

        private boolean mIsPreviewing = false;  
        private float mPreviewRate = -1f;  
    	// 0表示后置，1表示前置
    	//private int mCameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
    	private int mCameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
        
        public static synchronized CameraAPI getInstance(){  
            if(null == mCameraApi){  
            	mCameraApi = new CameraAPI();  
            }  
            return mCameraApi;  
        }  
        
        public void doOpenCamera(SurfaceHolder AHolder){
        	if (null == mCamera) {
        		int cameraIndex = getCameraPositionIndex(mCameraPosition);
        		if (-1 == cameraIndex) {
                    mCamera = android.hardware.Camera.open();        			
        		} else {
                    mCamera = android.hardware.Camera.open(cameraIndex);        			
        		}
        	}
            doStartPreview(AHolder, mPreviewRate);  
        }  
        
        private void releaseCamera() {
    		if (mCamera != null) {
    			mCamera.setPreviewCallback(null); 
                mIsPreviewing = false;  
    			mCamera.stopPreview();// 停掉原来摄像头的预览
    			mCamera.release();
    			mCamera = null;
    		}
    	}
        public void doStopCamera(){  
            if(null != mCamera)  
            {  
            	releaseCamera();      
            }  
        }  
        
        public int getCameraPositionIndex(int ACameraPosition) {
        	android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
			int cameraCount = Camera.getNumberOfCameras();
			for (int i = 0; i < cameraCount; i++) {
				Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
				if (ACameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
						return i;
					}
				}
				if (ACameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK) {
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
						return i;
					}
				}
			}
        	return -1;
        }
        
        public void switchCameraPosition(int ACameraPosition) {
        	if (mCameraPosition == ACameraPosition)
        		return;
    		int cameraIndex = getCameraPositionIndex(ACameraPosition);
    		if (-1 != cameraIndex) {
    			releaseCamera();
    			// 打开当前选中的摄像头
    			mCamera = Camera.open(cameraIndex);
    			// 通过surfaceview显示取景画面
    			doStartPreview(mSurfaceHolder, mPreviewRate);
    			mCameraPosition = ACameraPosition;
    		}
        }
        
        private void doStartPreview(SurfaceHolder holder, float previewRate){
        	if (null == mCamera)
        		return;
            if(mIsPreviewing){  
                mCamera.stopPreview();  
                return;  
            }  
            if(mCamera != null){        
            	mCameraParams = mCamera.getParameters();  
            	mCameraParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式  
                //CamParaUtil.getInstance().printSupportPictureSize(mCameraParams);  
            	//CamParaUtil.getInstance().printSupportPreviewSize(mCameraParams);  
                //设置PreviewSize和PictureSize  
                android.hardware.Camera.Size pictureSize = getPropPictureSize(  
                		mCameraParams.getSupportedPictureSizes(),previewRate, 800);  
                // 部分定制手机，无法正常识别该方法
                //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight()); 
                mCameraParams.setPictureSize(pictureSize.width, pictureSize.height);  
                android.hardware.Camera.Size previewSize = getPropPreviewSize(  
                		mCameraParams.getSupportedPreviewSizes(), previewRate, 800);  

                mCameraParams.setPreviewSize(previewSize.width, previewSize.height);
                // 自动对焦
                //mCameraParams.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
                mCameraParams.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                
                // 开启闪光灯
                //mCameraParams.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                mCameraParams.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                
                mCamera.setDisplayOrientation(90);  
                //CamParaUtil.getInstance().printSupportFocusMode(mCameraParams);  
                java.util.List<String> focusModes = mCameraParams.getSupportedFocusModes();  
                if(focusModes.contains("continuous-video")){  
                	mCameraParams.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);  
                }  
                mCamera.setParameters(mCameraParams);   
      
                try {  
                    mCamera.setPreviewDisplay(holder);  
                    mCamera.startPreview();//开启预览  
                    mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
                } catch (java.io.IOException e) {  
                    e.printStackTrace();  
                }  
      
                mIsPreviewing = true;  
                mPreviewRate = previewRate;
                /*/      
                mCameraParams = mCamera.getParameters(); //重新get一次  
                Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width  
                        + "Height = " + mParams.getPreviewSize().height);  
                Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width  
                        + "Height = " + mParams.getPictureSize().height);
                //*/
            }  
        }    
        public android.hardware.Camera.Size getPropPictureSize(java.util.List<Camera.Size> list, float th, int minWidth){  
        	java.util.Collections.sort(list, mSizeComparator);  
      
            int i = 0;  
            for(android.hardware.Camera.Size s:list){  
                if((s.width >= minWidth) && equalRate(s, th)){  
                    break;  
                }  
                i++;  
            }  
            if(i == list.size()){  
                i = 0;//如果没找到，就选最小的size  
            }  
            return list.get(i);  
        }  
        private CameraSizeComparator mSizeComparator = new CameraSizeComparator();
        public  class CameraSizeComparator implements java.util.Comparator<Camera.Size>{  
            public int compare(android.hardware.Camera.Size lhs, android.hardware.Camera.Size rhs) {  
                if(lhs.width == rhs.width){  
                    return 0;  
                }  
                else if(lhs.width > rhs.width){  
                    return 1;  
                }  
                else{  
                    return -1;  
                }  
            }  
        }  
      
        public android.hardware.Camera.Size getPropPreviewSize(java.util.List<Camera.Size> list, float th, int minWidth){  
        	java.util.Collections.sort(list, mSizeComparator); 
            int i = 0;  
            for(android.hardware.Camera.Size s:list){  
                if((s.width >= minWidth) && equalRate(s, th)){  
                    break;  
                }  
                i++;  
            }  
            if(i == list.size()){  
                i = 0;//如果没找到，就选最小的size  
            }  
            return list.get(i);  
        }  
        public boolean equalRate(android.hardware.Camera.Size s, float rate){  
            float r = (float)(s.width)/(float)(s.height);  
            if(Math.abs(r - rate) <= 0.03)  
            {  
                return true;  
            }  
            else{  
                return false;  
            }  
        }  
        /** 
         * 拍照 
         */  
        public void doTakePicture(){  
            if(mIsPreviewing && (mCamera != null)){  
                mCamera.takePicture(getShutterCallback(), null, getJpegPictureCallback());  
            }  
        }        
        
        //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。  
        /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/  
        android.hardware.Camera.ShutterCallback mShutterCallback = null;
        private android.hardware.Camera.ShutterCallback getShutterCallback() {
            if (null == mShutterCallback) {
                mShutterCallback = new android.hardware.Camera.ShutterCallback() {  
                    public void onShutter() {  
                    }  
                };  
            }
        	return mShutterCallback;
        }

        // //对jpeg图像数据的回调,最重要的一个回调  
        android.hardware.Camera.PictureCallback mJpegCallback = null;
        private android.hardware.Camera.PictureCallback getJpegPictureCallback() {
        	if (null == mJpegCallback) {
        		mJpegCallback = new android.hardware.Camera.PictureCallback() {
			        @Override
			        public void onPictureTaken(byte[] data, Camera camera) {
        		        android.graphics.Bitmap b = null;  
                        if(null != data){  
                            b = android.graphics.BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图  
                            mCamera.stopPreview();  
                            mIsPreviewing = false;  
                        }  
                        //保存图片到sdcard  
                        if(null != b) {  
                            // 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。  
                            // 图片竟然不能旋转了，故这里要旋转下  
                        	//android.graphics.Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);  
                            //FileUtil.saveBitmap(rotaBitmap);
                        	
                        	 android.graphics.Matrix matrix = new android.graphics.Matrix();  
                             matrix.postRotate((float) 90.0f);  
                             android.graphics.Bitmap rotaBitmap = 
                            		 android.graphics.Bitmap.createBitmap(
                            				 b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);  
                        }  
                        // 再次进入预览  
                        mCamera.startPreview();  
                        mIsPreviewing = true;  
			        }
        	    };
			}
        	return mJpegCallback;
        }

        // 拍摄的未压缩原数据的回调,可以为null  
        android.hardware.Camera.PictureCallback mRawCallback = null;
        private android.hardware.Camera.PictureCallback getRawCallback() {
        	if (null == mRawCallback) {
        		mRawCallback = new android.hardware.Camera.PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
					}  
        		};        		
        	}   
        	return mRawCallback;
        }
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	    /*/ 
		//实现自动对焦
		if (null != CameraAPI.getInstance().mCamera) {
		CameraAPI.getInstance().mCamera.autoFocus(
				new android.hardware.Camera.AutoFocusCallback() {  
            @Override  
            public void onAutoFocus(boolean success, Camera camera) {  
                if(success){  
                    initCamera();//实现相机的参数初始化  
                    camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。  
                }  
            }  

        });  
		}
	    //*/
	}
    /*/
	//相机参数的初始化设置  
    private void initCamera() {  
    	CameraAPI.getInstance().mCameraParams = CameraAPI.getInstance().mCamera.getParameters();  
    	CameraAPI.getInstance().mCameraParams.setPictureFormat(PixelFormat.JPEG);  
          //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。  
    	CameraAPI.getInstance().mCameraParams.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);     
    	CameraAPI.getInstance().mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦  
          //setDispaly(parameters,camera);  
        CameraAPI.getInstance().mCamera.setParameters(CameraAPI.getInstance().mCameraParams);  
        CameraAPI.getInstance().mCamera.startPreview();  
        CameraAPI.getInstance().mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上              
    }  
    //*/
    
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		CameraAPI.getInstance().doStopCamera();  
	}
}
