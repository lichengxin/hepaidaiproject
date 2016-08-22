package base.app;

import base.app.user.ManagerAppUser;

/*//
 * 基础工厂管理类模型
//*/
public class ManagerFactory {
	
	private static final String LOGTAG = ManagerFactory.class.getSimpleName();
	
	public static final String ANALYSIS_MANAGER = "analysis_manager";
	public static final String APPUSER_MANAGER = "appuser_manager";
	public static final String APPCONFIG_MANAGER = "appconfig_manager";
	public static final String NET_MANAGER = "net_manager";
	public static final String DEVICE_MANAGER = "device_manager";
	public static final String UPDATE_MANAGER = "update_manager";
	
	private AbstractManager abstractManager;
	private ManagerFactory() {
		abstractManager = new AbstractManager(this);
	}
	
	/**
	 * 此函数，只能用于容器调用�? 比如�?个线程中保持单例使用，避免重复初始化各种不同的manager
	 * 默认情况下，统一使用AppDragon.Core来获取全�?单例ManagerFactory
	 * @return
	 */
	public AbstractManager getManager (String manager, boolean needNew) {		
		if (ANALYSIS_MANAGER.equals(manager)) {
			if (needNew) {
				return null;
			}
			return null;
		}
		
		if (APPUSER_MANAGER.equals(manager)) {
			if (needNew) {
				return new ManagerAppUser(this);
			}
			return getUserManager();
		}
		
		if (APPCONFIG_MANAGER.equals(manager)) {
			if (needNew) {
				return null;
			}
			return null;
		}
		
		if (NET_MANAGER.equals(manager)) {
			if (needNew) {
				return null;
			}
			return null;
		}
		
		if (DEVICE_MANAGER.equals(manager)) {
			if (needNew) {
				return null;
			}
			return null;
		}	
		if (UPDATE_MANAGER.equals(manager)) {
			if (needNew) {
				return null;
			}
			return null;
		}
		
		return abstractManager;
	}

	public static ManagerFactory getManagerFactory() {
		return new ManagerFactory();
	}
	
	private ManagerActivities mActivitiesManager = null;
	public ManagerActivities getActivitiesManager() {
		if(null == mActivitiesManager){
			mActivitiesManager = ManagerActivities.getActivitiesManager(this);
		}		
		return mActivitiesManager;
	}	

	private ManagerAppUser mUserManager;
	public ManagerAppUser getUserManager() {
		if (null == mUserManager){
			mUserManager = new ManagerAppUser(this);
		}
		return mUserManager;
	}	
}
