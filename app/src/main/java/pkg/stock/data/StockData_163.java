package pkg.stock.data;

import java.text.ParseException;

import utils.*;

import base.app.BaseApp;


public class StockData_163 {

	private static final String LOGTAG = StockData_163.class.getSimpleName();
	
	private static void getStockDayData(String AStockCode) {
		if (android.text.TextUtils.isEmpty(AStockCode))
			return;
		StockDataStore_Day datastore = new StockDataStore_Day();
		datastore.loadStock(AStockCode);
		getStockDayData(datastore);
		/*//
		String datestr = null;
		java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyyMMdd");
		if (0 == datastore.getBeginDate()) {
			datestr = "begindate 0";
		} else {
			datestr = "begin:" + fmt.format(DelphiUtils.DelphiTime2JavaTime(datastore.getBeginDate())) + 
					  "end:" + fmt.format(DelphiUtils.DelphiTime2JavaTime(datastore.getEndDate()));
		};
		datestr = datestr +  " count:" + datastore.getRecordCount();
		BaseApp.showToast(datestr);
		if (true)
			return;
		//*/
	}

	public static void getStockDayData(StockDataStore_Day ADataStore) {
		if (0 == ADataStore.getBeginDate()) {
			//Log.d(LOGTAG, "get Stock Data All");
			getStockDayData(ADataStore, null, null);
		} else {
			//Log.d(LOGTAG, "get Stock Data Period");
			java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyyMMdd");
		    getStockDayData(ADataStore, 
		    		fmt.format(DelphiUtils.DelphiTime2JavaTime(ADataStore.getEndDate())),
		    		fmt.format(new java.util.Date()));
		}
	}
	
    private static void getStockDayData(StockDataStore_Day ADataStore, String startdate, String enddate) {
		//String result = null;
    	if (null == ADataStore)
    		return;
    	if (null == ADataStore.mStockCode)
    		return;
		if (!BaseApp.isNetworkAvailable()) {
			return;
		}
        System.out.println("getStockDayData" + ADataStore.mStockCode);
		String code = null;
		if (6 == ADataStore.mStockCode.length()) {
			if (ADataStore.mStockCode.substring(0, 1).equals("6")) {
				code = "0" + ADataStore.mStockCode;				
			} else {
				code = "1" + ADataStore.mStockCode;
			}
		} else {
			code = ADataStore.mStockCode;
		}
		String url = "http://quotes.money.163.com/service/chddata.html?code=" + code;
		if (!android.text.TextUtils.isEmpty(startdate)) {
			url = url + "&start=" + startdate + // "20151001" 
   				        "&end=" + enddate + // "20151019" + 
				        "&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
		}
		//Log.d(LOGTAG, "get Stock Data " + url);
		try {
			net.HttpUtils http = new net.HttpUtils(); 
			http.configResponseTextCharset("gbk");
			net.http.ResponseStream response = http.sendSync(net.http.Protocol.HttpMethod.GET, url, null);
			if (null != response) {
				// response.getContentLength() -1
				//BaseApp.showToast("content length:" + response.getContentLength());
				//result = response.readString();
				//BaseApp.showToast("getlength:" + result.length()); 
				//if (0 < result.length()) {
				//Log.d(LOGTAG, "get Stock Data 1");
				parseStockDayData(ADataStore, response);
				ADataStore.save();
					//}
			}
		} catch (net.http.HttpException e) {
			//Log.d(LOGTAG, "get Stock Data error:" + e.toString());
			e.printStackTrace();
		}
		//return result;    	
    }

	//private static int isInitTest = 0;
	private static void parseStockDayData(StockDataStore_Day ADataStore, java.io.InputStream stockdata) {
		//Log.d(LOGTAG, "parse Get Stock Data 1");
		java.io.InputStreamReader strin = new java.io.InputStreamReader(stockdata);
		if (null == strin) {
			//BaseApp.showToast("input stream reader null");
			return;
		}
		java.io.BufferedReader br = new java.io.BufferedReader(strin);
		if (null == br) {
			//BaseApp.showToast("buffer reader null");
			return;
		}
		String line = null;
		boolean isContinue = true;
	    java.text.SimpleDateFormat datefmt = new java.text.SimpleDateFormat(DateTimeUtils.shortDateFormat);
	    java.util.Date dealDate = null;
	    double delphidealdate = 0;
	    //isInitTest = 0;
    	StockDataBuffer_Day buffer = new StockDataBuffer_Day();
	    StockDataRecord_Day storedatarecord = new StockDataRecord_Day(); 
        try {  
    		boolean isSkillFirstRow = false;
            while (isContinue){
            	line = br.readLine();
                if (null == line) {
                    isContinue = false;                	
                };
                if (android.text.TextUtils.isEmpty(line)) {
                    isContinue = false;                	
                };
                if (isContinue) {
                	//Log.d(LOGTAG, "parse Get Stock Data line:" + line);
                	storedatarecord.dealDate = 0;
                	if (!isSkillFirstRow) {
                		if (0 < line.indexOf(ADataStore.mStockCode)) {
                			isSkillFirstRow = true;
                		}
                	}
                	if (isSkillFirstRow) {
                    	String[] rowdata_163 = line.split(",");
                    	//Log.d(LOGTAG, "parse Get Stock Data:" + rowdata_163.length + " : "+ line);
                    	if (14 < rowdata_163.length) {
                    		try {
								dealDate = datefmt.parse(rowdata_163[0]);
								delphidealdate = DelphiUtils.JavaTime2DelphiTime(dealDate);
								if (0 < delphidealdate) {
									storedatarecord.dealDate = (int) delphidealdate;
									storedatarecord.priceOpen = StockPrice.getPackedPrice(rowdata_163[6]);  // 开盘价
									storedatarecord.priceHigh = StockPrice.getPackedPrice(rowdata_163[4]);  // 最高价
									storedatarecord.priceLow = StockPrice.getPackedPrice(rowdata_163[5]);   // 最低价
									storedatarecord.priceClose = StockPrice.getPackedPrice(rowdata_163[3]); // 收盘价
									storedatarecord.volume = StockPrice.tryParseLongValue(rowdata_163[11]);  // 成交量
									storedatarecord.amount = StockPrice.tryParseLongValue(rowdata_163[12]);  // 成交金额
									storedatarecord.totalValue = StockPrice.tryParseLongValue(rowdata_163[13]); // 总市值
									storedatarecord.dealValue = StockPrice.tryParseLongValue(rowdata_163[14]);  // 流通市值
									buffer.addRecord(storedatarecord);
									//Log.d(LOGTAG, " add row line:" + line);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
                    	} else {
                    		// ??? 这里的数据 什么格式  ???
                    	}
                	}
                }                
            }  
        } catch (java.io.IOException e) {  
            e.printStackTrace();  
        } finally {  
            try{  
            	strin.close();  
                stockdata.close();  
                br.close();  
            }catch(java.io.IOException e){  
                e.printStackTrace();  
            }  
        }
        StockDataBuffer_Day.BufferNode node = buffer.lastNode;
        int index = 0;
        while (null != node) {
        	// 网易是倒序 的排练 这里也倒序 即可
        	index = buffer.mRecordCount - 1;
        	while (0 <= index) {
            	node.fillRecord(storedatarecord, index);
            	//Log.d(LOGTAG, " add record dealdate:" + storedatarecord.dealDate);
            	ADataStore.addRecord(storedatarecord);
            	index--;
        	}        	
        	node = node.prevSibling;
        }
	}
}
