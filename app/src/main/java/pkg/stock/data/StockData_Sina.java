package pkg.stock.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import utils.*;

import net.http.HttpException;

import base.app.BaseApp;

import android.text.*;

public class StockData_Sina {

	private static final String LOGTAG = StockData_Sina.class.getSimpleName();
	
	//http://hq.sinajs.cn/list=sh601003,sh601001

	//private static String API_SINASERVER_URL = "http://" + "hq.sinajs.cn" + "/list=";
	
	private StockData_Sina() {}
	private StockData_Sina mInstance = null;
	public StockData_Sina getInstance() {
		if (null == mInstance) {
			mInstance = new StockData_Sina(); 
		}
		return mInstance;
	}
	
	public static DataEntity_StockChartMinute getStockMinuteData(DataEntity_StockChartMinute AMinuteData, String AStockCode) {
		// http://vip.stock.finance.sina.com.cn/quotes_service/view/vML_DataList.php?symbol=sh600100&num=242
		if (null == AStockCode)
			return null;
		if (!BaseApp.isNetworkAvailable()) {
			return null;
		}
		System.out.println("getStocksInstantData:" + AStockCode);
		String retdata = null;
		DataEntity_StockChartMinute result = null;
		if (6 == AStockCode.length()) {
			int minutes = 242;
			String API_SINASERVER_URL = "http://" + "vip.stock.finance.sina.com.cn" + 
			        "/quotes_service/view/vML_DataList.php?";
					//**"asc=j" +"&" + 
			StringBuilder code = new StringBuilder();
			code.append(API_SINASERVER_URL);
			if (AStockCode.substring(0, 1).equals("6")) {
				code.append("symbol=sh" + AStockCode);								
			} else {
				code.append("symbol=sz" + AStockCode);								
			}
			code.append("&num=" + minutes);
			net.HttpUtils http = new net.HttpUtils(); 
			http.configResponseTextCharset("gbk");
			net.http.ResponseStream response;
			try {
				response = http.sendSync(
						net.http.Protocol.HttpMethod.GET, 
						code.toString(), null);
				if (null != response) {
					retdata = response.readString();	
					result = parseStockMinuteData(AMinuteData, retdata);			 
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static class DataRow_Minute_Sina {
		String time = null;
		int price = 0;
		int volume = 0;
		
		public void clear() {
			price = 0;
			volume = 0;
		}
		
		public boolean isValid() {
			if (0 == price) 
				return false;
			if (0 == volume) 
				return false;
			return true;
		}
		
		public void parseData(String AMinuteData) {
			price = 0;
			volume = 0;
			time = null;
    		int p = AMinuteData.indexOf("[");
    		if (0 < p) {
    			if (8 < AMinuteData.length()) {
        	    	String[] minute_datas = AMinuteData.substring(p + 2).split(",");
        	    	String str = null;
        	    	if (3 == minute_datas.length) {
        	    		time = minute_datas[0].trim().replaceAll("'", "");    	    		
        	    		str = minute_datas[1].trim().replaceAll("'", "");  		
        	    		price = StockPrice.getPackedPrice(str);
        	    		str = minute_datas[2].trim().replaceAll("'", "");
        	    		volume = StockPrice.tryParseIntValue(str);
        	    	}
    			}
    		}
		}
	}
	
	public static DataEntity_StockChartMinute parseStockMinuteData(DataEntity_StockChartMinute AMinuteData, String AStockData) {
		if (null == AStockData)
			return null;
		DataEntity_StockChartMinute result = AMinuteData;
    	String[] rowdata_sina = AStockData.split("]");
    	// ['14:48:00', '29.5', '68700'
    	DataRow_Minute_Sina datarecord = new DataRow_Minute_Sina();
    	if (0 < rowdata_sina.length) {
    		if (null == result) {
        		result = new DataEntity_StockChartMinute();    			
    		}
    		int minuteindex = 0;
        	for (int i = 0; i < rowdata_sina.length; i++) {
        		datarecord.parseData(rowdata_sina[i]);
        		if (datarecord.isValid()) {
        			minuteindex = DataEntity_StockChartMinute.getMinuteIndex(datarecord.time);
        			if (0 <= minuteindex) {
        				result.updateprice(minuteindex, datarecord.price);
        				result.updatevolume(minuteindex, datarecord.volume);            			
            			if (minuteindex + 1 > result.validRecordCount)
            			    result.validRecordCount = minuteindex + 1;
        			}
        		}
        	}
    	}
		return result;
	}
	
	public static String getStocksInstantData(String stockcodes) {
		if (!BaseApp.isNetworkAvailable()) {
			return null;
		}
		System.out.println("getStocksInstantData:" + stockcodes);
		String result = null;
		String API_SINASERVER_URL = "http://" + "hq.sinajs.cn" + "/list=";
		try {
			net.HttpUtils http = new net.HttpUtils(); 
			http.configResponseTextCharset("gbk");
			net.http.ResponseStream response = http.sendSync(
					net.http.Protocol.HttpMethod.GET, 
					API_SINASERVER_URL + stockcodes, null);
			if (null != response) {
				//result = org.apache.http.util.EncodingUtils.getString(response.readString().getBytes("utf-8"), "utf-8");
				result = CharsetUtils.ansi2Utf8(response.readString());
				//String temp2 = org.apache.http.util.EncodingUtils.getString(result.getBytes("utf-8"),"utf-8");
				//Log.d(LOGTAG, "getStocksInstantData123:" + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (net.http.HttpException e) {
			e.printStackTrace();
		}
		return result;	
	}
	
	public static String getStockInstantData(String stockcode) {
		if (TextUtils.isEmpty(stockcode))
		    return null;
		if (6 == stockcode.length()) {
			if (stockcode.substring(0, 1).equals("6")) {
				return getStocksInstantData("sh" + stockcode);
			} else {
				return getStocksInstantData("sz" + stockcode);
			}
		}
	    return null;
	}
	/*获取新浪数据*/
	public static String getStocksInstantData(java.util.List<String> stockcodes) {
		if (0 < stockcodes.size()) {
			String code = null;
			StringBuilder codes = new StringBuilder();
			for (int i = 0; i < stockcodes.size(); i ++) {
				code = stockcodes.get(i);
				if (null != code) {
					if (6 == code.length()) {
						if (i != 0) {
							codes.append(",");
						}
						if (code.substring(0, 1).equals("6")) {
							codes.append("sh" + code);								
						} else {
							codes.append("sz" + code);								
						}
					}
				}
			}
			return getStocksInstantData(codes.toString());
		}	
		return null;
	}

	 
	public static void parseSingleStockInstantData(StockDataQuote.InstantData stockquote, String[] stockdata) {
		if (null == stockquote) 
			return;
		int p = stockdata[0].indexOf("=");
		if (0 < p) {
				//quote.stockName = utf8ToUnicode(array_stockquotedata[0].substring(p + 2));
				// new String(str.getBytes(原编码),目标编码)
				//quote.stockName = unicodeToUtf8(array_stockquotedata[0].substring(p + 2));
				/*//
				try {
					quote.stockName = new String(array_stockquotedata[0].substring(p + 2).getBytes("UTF-8"), "UNICODE");
					//quote.stockName = new String(array_stockquotedata[0].substring(p + 2).getBytes("GB2312"), "UTF-8");
					//quote.stockName = new String(array_stockquotedata[0].substring(p + 2).getBytes("UTF-8"), "UNICODE");
					//quote.stockName = new String(array_stockquotedata[0].substring(p + 2).getBytes("UNICODE"), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//*/
		}
		stockquote.price_open = StockPrice.getPackedPrice(stockdata[1]);
		stockquote.price_preclose = StockPrice.getPackedPrice(stockdata[2]);
		stockquote.price_lastest = StockPrice.getPackedPrice(stockdata[3]);
		stockquote.price_high = StockPrice.getPackedPrice(stockdata[4]);
		stockquote.price_low = StockPrice.getPackedPrice(stockdata[5]);
		stockquote.quotedate = stockdata[30];
		stockquote.quotetime = stockdata[31];
	}
	
	public static void parseSingleStockInstantData(StockDataQuote.InstantData stockquote, String stockdata) {
		String[] array_stockdata = stockdata.split(",") ;
		if (32 < array_stockdata.length) {
			if (35 > array_stockdata.length) {
				parseSingleStockInstantData(stockquote, array_stockdata);				
			}
		}
	}
	
	public static void parseSingleStockInstantData(java.util.List<StockDataQuote.InstantData> stockquotes, String stockdata) {
		if (TextUtils.isEmpty(stockdata)) 
			return;
		String[] array_stockdata = stockdata.split(",") ;
		if (32 < array_stockdata.length) {
			if (35 > array_stockdata.length) {
				StockDataQuote.InstantData quote = null;
				for (int j = 0; j < stockquotes.size(); j ++) {
					quote = stockquotes.get(j);
					if (null != array_stockdata[0]) {
						if (0 < array_stockdata[0].indexOf(quote.stockCode)) {
							parseSingleStockInstantData(quote, array_stockdata);
							return;
						}
					}
				}		
			}
		}
	}
	
	public static void parseStockInstantDatas(java.util.List<StockDataQuote.InstantData> stockquotes, String stockdatas) {
		if (null == stockdatas)
			return;
		if (null == stockquotes)
			return;
		String[] array_stockdata = stockdatas.split(";") ; 
		for(int i = 0 ;  i < array_stockdata.length; i ++ ){ 
			parseSingleStockInstantData(stockquotes, array_stockdata[i]);
		}
	}
	/*//
	public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray(); 
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            java.lang.Character.UnicodeBlock ub = java.lang.Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == java.lang.Character.UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == java.lang.Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }
	//*/
	/*//
	public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if ('\\' == aChar) {
                aChar = theString.charAt(x++);
                if ('u' == aChar) {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
	//*/
}
