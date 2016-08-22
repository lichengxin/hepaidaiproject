package ui.activity;

import base.app.*;

public class BaseDialogActivity extends android.app.Activity  {

	// 利用Activity的Dialog风格完成弹出框设计
	// 问题的关键在MainActivity里的一句 Android:theme="@android:style/Theme.Dialog"，
	// 这就是Activity的Dialog风格
	
	/* 调用的使用 使用 startActivityForResult(i, 0);
	 *   
	 * //利用返回试Activity接收输入的数据并显示，证明我们的Dialog式的Activity确实可以完成数据的处理  (non-Javadoc)
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        //取出字符串  
        android.os.Bundle bundle = data.getExtras();  
        String str = bundle.getString("str");  
        showString.setText(str);  
    }  //*/
	private static String LOGTAG = BaseDialogActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApp.core.getActivitiesManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	public android.view.View.OnClickListener getButtonClickListener() {
		return new android.view.View.OnClickListener() {  
            public void onClick(android.view.View v) {  
                //String input = inputEditor.getText().toString();  
                android.content.Intent returnIntent = null;  
                		//new android.content.Intent(testDialog.this, MainActivity.class);  
                android.os.Bundle returnBundle = new android.os.Bundle();  
                //returnBundle.putString("str", input);  
                returnIntent.putExtras(returnBundle);  
                BaseDialogActivity.this.setResult(RESULT_OK, returnIntent);  
                BaseDialogActivity.this.finish();  
            }  
        };  
	}	
}
