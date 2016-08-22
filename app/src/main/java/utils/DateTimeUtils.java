package utils;

public class DateTimeUtils {
	
	/*在最近做的一个项目中用到了Java和Delphi，发现它们不能正确读取对方的日期类型，
	 * 如在Java中写入一个值为“2007-12-1”的日期值，通过Delphi读取却不是这个值了。
                 通过查阅资料，发现两者对日期类型的定义略有不同。Java中日期使用的是长整型进行存储的，
                 它表示距“1970-1-1”的毫秒数。如“1970-1-2”是在“1970-1-1”后的86400000毫秒，
                 所以Java中就使用86400000表示“1970-1-2”这个日期。由于长整型是带符号的，所以我们
                 可以使用负的毫秒数来表示在“1970-1-1”之前的日期。而Delphi中的日期则是使用双精度类型
                 进行存储的，整数部分表示距“1899-12-30”的天数，小数部分表示小时。如“2.75”这个数值则
                 表示“1900-1-1 6：00PM”，“-1.25”则表示“1899-12-29 6：00 AM”。
                 由于两者的日期类型的起始日期不一样，即相同的“0”值在两者中表示不同的日期。那么在Java与
       Delphi之间进行日期值的通信时就需要进行一个转换*/

	   /** 
	    * 将时间unix转换为int类型 
	    * @param timeString 
	    * @param format 
	    * @return 
	    */  
	public static String shortDateFormat = "yyyy-MM-dd";
	public static String longDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static int DateToInt(String timeString, String format) {  		  
	   int time = 0;  
	   try {  
	       java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);  
	       java.util.Date date = sdf.parse(timeString);  
	       time = DateToInt(date);
	   }  
	   catch (java.text.ParseException e) {  
	       e.printStackTrace();  
	   }  
	   return time;  
    }

	public static int DateToInt(java.util.Date date) {  
		String strTime = date.getTime() + "";  
		strTime = strTime.substring(0, 10);  
		return Integer.parseInt(strTime);  
	}
	
	public static String shortDateString(java.util.Date date) {
	    return new java.text.SimpleDateFormat(shortDateFormat).format(date);
	}
	
	public static String longDateString(java.util.Date date) {
	    return new java.text.SimpleDateFormat(longDateFormat).format(date);
	}
	/**
	   * 获取现在时间
	   * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	   */
	public static java.util.Date getNow() {
		/*//
	    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formatter.format(new java.util.Date());
	    java.text.ParsePosition pos = new java.text.ParsePosition(8);
	    java.util.Date currentTime_2 = formatter.parse(dateString, pos);
	    return currentTime_2;
	    //*/
	    return new java.util.Date();
	}
}
