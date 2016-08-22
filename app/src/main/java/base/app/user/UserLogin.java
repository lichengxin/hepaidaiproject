package base.app.user;

import base.app.BaseApp;

import com.zhijiaoyi.vstock.net.UtilsNetActions;

public class UserLogin extends base.BaseObj {

	public class LoginResult {
		public boolean isSuccess = false;
		public String errorMsg = null;
		public int errorCode = 0;
		public UserAccount account = null;
		
		public void clear() {
	    	isSuccess = false;
	    	errorCode = 0;
	    	errorMsg = null;
			account = null;
		}
	}

    public LoginResult doLogin(String phonenum, String password) {
    	
    	UtilsNetActions utils = new UtilsNetActions();
    	//utils.doLogin(phonenum, password);
    	String ret = utils.doLogin("liujing", "123123");
    	BaseApp.showToast("login:" + ret);
    	LoginResult result = new LoginResult();
    	result.isSuccess = true;
		result.account = BaseApp.core.getUserManager().newAccount(); 
		result.account.token = "1234";
    	return result;
    }
}
