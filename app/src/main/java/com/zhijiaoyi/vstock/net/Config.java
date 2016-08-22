package com.zhijiaoyi.vstock.net;

public class Config {

	public static String SITE_ZHIJIAOYI_DEV = "proxyapi.langbank.org";
	public static String SITE_ZHIJIAOYI_TEST = "proxyapi.test.langbank.org";
	public static String SITE_ZHIJIAOYI_OFFICAL = "proxyapi.zhijiaoyi.com";

	public static String WEBSITE_ZHIJIAOYI_DEV = "www.dev.zhijiaoyi.org";
	public static String WEBSITE_ZHIJIAOYI_OFFICAL = "www.zhijiaoyi.com";
	
	public static String LOGIN_UA_DEV ="LOCAL_DEV_CLIENT";
	
	
	public static String SITE_ZHIJIAOYI = SITE_ZHIJIAOYI_DEV;
	public static String WEBSITE_ZHIJIAOYI = WEBSITE_ZHIJIAOYI_DEV;

	// api 服务器 地址
	public static String API_SERVER_URL = "http://" + SITE_ZHIJIAOYI + "/index.php";
	// 打开智交易网页地址
	public static String OPEN_WEBURL_ADDRESS = "http://" + WEBSITE_ZHIJIAOYI + "//appClient.php";

	
	public static String LOGIN_UA = LOGIN_UA_DEV;
	
	//登入
	public static String ACTION_LOGIN_CALL          = "PeiZi.User.login";
	//自动登入登录
	public static String ACTION_LOGIN_BY_TOKEN_CALL = "PeiZi.User.loginByToken";
	//注销 call
	public static String ACTION_LOGOUT_CALL         = "PeiZi.User.logout";
	
}
