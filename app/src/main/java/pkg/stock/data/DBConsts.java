package pkg.stock.data;

public class DBConsts {

  public static final int DataSrc_CTP        = 11;
  public static final int DataSrc_Standard   = 12; // 来至官方 证券交易所
                           
  public static final int DataSrc_TongDaXin  = 21; // 通达信
  public static final int DataSrc_TongHuaSun = 22; // 同花顺
  public static final int DataSrc_DaZhiHui   = 23; // 大智慧
 
  public static final int DataSrc_Sina       = 31;
  public static final int DataSrc_163        = 32;
  public static final int DataSrc_QQ         = 33;
  public static final int DataSrc_XQ         = 34; // 雪球

  public static final int DataType_Stock            = 1;
  public static final int DataType_CTP              = 2;

  // M1 Stock
  // M1 CTP    
  //    5秒
  //    15秒
  
  //    1分钟
  //    5分钟
  //    10分钟     
  //    15分钟
  //    30分钟
  //    60分钟
  public static final int DataMode_DayData          = 1; // 日线数据 M1
  public static final int DataMode_DayDetailDataM1  = 2; // 日明细 M1 --
  public static final int DataMode_DayDetailDataM2  = 3; // 日明细 M2

  // ======================================================================
  public static final String FIELD_AUTOINCID = "t_aid"; 

  public class TableStock {
	public static final String TABLE_NAME = "t_stock"; 
	public static final String FIELD_CODE = "f_code";
	public static final String FIELD_NAME = "f_name";
	public static final String FIELD_PINYIN = "f_py";
  }  
}
