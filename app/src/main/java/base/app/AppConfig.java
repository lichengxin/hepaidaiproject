package base.app;

public class AppConfig {
	
	public static final String APP_CONFIG_DEFAULT_STOREPATH = "hepaidai/";
	
	public void save(android.content.Context context) {
		if (null == context) 
		    return;
		/* SharedPreferences将会把这些数据保存在test.xml文件中
		 * 可以在File Explorer的data/data/相应的包名/test.xml 下导出该文件
		 */ 
		android.content.SharedPreferences config_store = 
				context.getSharedPreferences("test", android.app.Activity.MODE_PRIVATE);
		android.content.SharedPreferences.Editor config_edit = config_store.edit(); //用putString的方法保存数据 
		config_edit.putString("", ""); 
		//提交当前数据 
		config_edit.commit(); 
	}
}
