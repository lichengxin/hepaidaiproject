package base.app.user;

import utils.*;
import android.text.TextUtils;
import base.app.AbstractManager;
import base.app.BaseApp;
import base.app.BaseConsts;
import base.app.ManagerAppConfig;
import base.app.ManagerFactory;


public class ManagerAppUser  extends AbstractManager {

	private static final String LOGTAG = ManagerAppUser.class.getSimpleName();
	
	public ManagerAppUser(ManagerFactory core) {
		super(core);
	}

    private static UserAccount mLoginedActiveUserAccount = null;
    public UserAccount ActiveUser() {
    	if (null == mLoginedActiveUserAccount) {
    		// 崩溃的 时候 自动 归 null 了 要读取一遍 ???
    		checkAutoLoginUserAccount();
    	}
    	return mLoginedActiveUserAccount;
    }

    public void setLoginedActiveUserAccount(UserAccount account) {
    	if (null != account) {
        	Log.d(LOGTAG + " setLoginedActiveUserAccount 1:" + account.getUserId());
    		if (0 == account.getUserId()) 
    			return;
    		if (TextUtils.isEmpty(account.token))
    			return;
        	if (null != mLoginedActiveUserAccount) {
            	if (mLoginedActiveUserAccount.getUserId() != account.getUserId()) {
            		mLoginedActiveUserAccount.clear();
            	} 
        	}
        	mLoginedActiveUserAccount = account;	
        	ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
        	if (null != config) {
           		config.lastUserId = account.getUserId();
           		ManagerAppConfig.Save(config);    		
        	}
        	//DVStorage.saveUserProfile2(account);
    	} else {
    		Log.d(LOGTAG + " setLoginedActiveUserAccount 2");
        	if (null != mLoginedActiveUserAccount) 
           		mLoginedActiveUserAccount.clear();
        	mLoginedActiveUserAccount = null;
        	ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
        	if (null != config) {
           		config.lastUserId = 0;
           		ManagerAppConfig.Save(config);    		
        	}
    	}
    	// save last login active user account
    }
    
    int mCheckAutoLoginUserAccount = 0;
    public void checkAutoLoginUserAccount() {
		if (null != mLoginedActiveUserAccount)
			return;
		if (0 == mCheckAutoLoginUserAccount) {
			mCheckAutoLoginUserAccount = 1;
		} else {
			return;
		}
		Log.d(LOGTAG + " checkAutoLoginUserAccount ");
    	HistoryUserAccount lastaccount = getLastLoginAccount();
    	if (null != lastaccount) {
    		if (0 != lastaccount.userId) {
        		UserAccount account = null;
        		//account = DVStorage.loadUserAccount(lastaccount.userId);
        		if (null != account) {
                	this.setLoginedActiveUserAccount(account);        			
        		}
    		}
    	}
    }

    public void activeUserLogout() {
    	if (null != mLoginedActiveUserAccount) {
        	Log.d(LOGTAG + " activeUserLogout ");
    		// 注销以后要 保存
			BaseApp.instance.sendBroadcast(BaseConsts.BROADCAST_USER_LOGOUT, null, null);
    		setLoginedActiveUserAccount(null);
    	}
    }
    
    public UserAccount newAccount() {
    	return new UserAccount();
    }
    
    public boolean isLogined() {
    	return (null != mLoginedActiveUserAccount);
    }
    
    public class HistoryUserAccount {
    	public String accountName = null;
    	public long userId = 0;
    }
    
    public HistoryUserAccount getLastLoginAccount() {
    	ManagerAppConfig.AppConfig config = BaseApp.instance.getConfig();
    	if (null == config) 
    		return null;
    	if (0 == config.lastUserId) 
    		return null;
    	HistoryUserAccount account = new HistoryUserAccount();
    	account.userId = config.lastUserId; 
    	return account;
    }
    
    public int getHistoryAccountCount() {
    	return 0;
    }

    public HistoryUserAccount getHistoryAccount(int index) {
    	return null;
    }
    
}
