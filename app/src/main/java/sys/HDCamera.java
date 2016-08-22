package sys;

import java.io.File;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;

/* 系统相机控制类 */

public class HDCamera {

	// 从照相机获取照片
	public static int REQUEST_CODE_CAPTURE_CAMEIA = 1001;	
	public static int REQUEST_CODE_PICK_IMAGE = 1002;
	
	public void getImageFromCamera(Activity activity, int ARequestCode) {  
        String state = Environment.getExternalStorageState();  
        if (state.equals(Environment.MEDIA_MOUNTED)) {  
        	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        	String out_file_path = "";//Constant.SAVED_IMAGE_DIR_PATH;  
            File dir = new File(out_file_path);  
            if (!dir.exists()) {  
                dir.mkdirs();  
            }  
        	String capturePath = out_file_path + System.currentTimeMillis() + ".jpg";  
        	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));  
        	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);  
            activity.startActivityForResult(intent, ARequestCode);//REQUEST_CODE_CAPTURE_CAMEIA);  
        }  else {  
        //Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();  
        }  
    }  
	
	public void getImageFromAlbum(Activity activity, int ARequestCode) {  
	    Intent intent = new Intent(Intent.ACTION_PICK);  
	    intent.setType("image/*");//相片类型  
	    activity.startActivityForResult(intent, ARequestCode);//REQUEST_CODE_PICK_IMAGE);  
	}  
	/* @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {             
                Uri uri = data.getData();  
                //to do find the path of pic  
            
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA ) {             
        Uri uri = data.getData();  
                 //to do find the path of pic  
 } }  */
}
