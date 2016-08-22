package base.app;

public class AppPath extends base.BaseObj {

	private static final String LOGTAG = AppPath.class.getSimpleName();
	public static String SDRootPath = android.os.Environment.getExternalStorageDirectory().toString();
	public static String SDCardState = android.os.Environment.getExternalStorageState();
	
	//public static String stockFilePath = rootPath + "/.vstock/";
	public static String stockFilePath = SDRootPath + "/Astock2/";

	public static String getStockFileName(String AStockCode) {
		if (null == AStockCode) 
			return null;
		/*
		   <!-- 在SDCard中创建与删除文件的权限 -->  
           <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>  
           <!-- 往SDCard写入数据的权限 -->  
           <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
        */
		return AStockCode + ".sday";
	}
	
}
