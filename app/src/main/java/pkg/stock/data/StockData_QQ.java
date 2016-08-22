package pkg.stock.data;

import java.io.IOException;

import pkg.stock.chart.RealTimeChartView;

import utils.Log;

import base.app.BaseApp;


import android.text.TextUtils;

public class StockData_QQ {

	private static final String LOGTAG = StockData_QQ.class.getSimpleName();
	
	public static void getStockData(String AStockCode) {
		if (TextUtils.isEmpty(AStockCode))
			return;
		if (!BaseApp.isNetworkAvailable()) {
			return;
		}
		System.out.println("getStockData:" + AStockCode);
		Log.d(LOGTAG, "getStockData" + AStockCode);
		String code = null;
		if (6 == AStockCode.length()) {
			if (AStockCode.substring(0, 1).equals("6")) {
				code = "sh" + AStockCode;				
			} else {
				code = "sz" + AStockCode;
			}
		} else {
			code = AStockCode;
		}
		String result = null;
		boolean isContinue = true;
		int page = 0;
		String url = null;
		net.HttpUtils http = new net.HttpUtils();
		net.http.ResponseStream response = null;
		while (isContinue) {
			try {
				url = "http://stock.gtimg.cn/data/index.php?appn=detail&action=data&c=" + code + "&p=" + page;
				Log.d(LOGTAG, "geturl:" +url);
				response = http.sendSync(net.http.Protocol.HttpMethod.GET, url, null);
				if (null != response) {
					result = response.readString();
					if (0 < result.length()) {
						parseStockData(result);
						isContinue = false;
					} else {
						isContinue = false;
					}
				} else {
					isContinue = false;
				}
				page++;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (net.http.HttpException e) {
				e.printStackTrace();
			}
		}
	}

	public static void parseStockData(String AStockData) {
		String[] array_stockdata = AStockData.split("\\|") ;
		StockDataQuote.RealPointData pointdata = null;
		//App.showToast("| array:" + array_stockdata.length + "");
		StockDataRecord_QQ parseRecord = new StockDataRecord_QQ();
		for (int i = 0; i < array_stockdata.length; i ++) {
			parseStockDataRecord_QQ(parseRecord, array_stockdata[i]);
		}
	}

	// 时间计算  09:00:00  -- 0
	//        09:00:59  -- 59
	//        09:01:00  -- 60
	//        10:00:00  -- 3600
	//        11:00:00  -- 7200
	//        12:00:00  -- 10800
	//        13:00:00  -- 14400
	//        14:00:00  -- 18000
	//        15:00:00  -- 21600
	
	public static int computeDealTime(String ATime) {
		String [] times = ATime.split(":");
		try {
			if (3 == times.length) {
				int hour = Integer.parseInt(times[0]);
				if (9 <= hour) {
					if (16 > hour) {
						int min = Integer.parseInt(times[1]);
						if (0 <= min) {
							if (60 > min) {
								int sec = Integer.parseInt(times[2]);
								if (0 <= sec) {
									if (60 > sec) {
									  return (hour - 9) * 3600 + min * 60 + sec;
									}
								}
							}
						}
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void parseStockDataRecord_QQ(StockDataRecord_QQ ARecord, String ARowData) {
		ARecord.price = 0;
		ARecord.mode = 0;
		if (TextUtils.isEmpty(ARowData)) {
			return;
		}
		String[] array_stockdata = ARowData.split("/") ; 
		if (7 == array_stockdata.length) {
				//pointdata = new StockDataQuote.RealPointData();
			ARecord.time = computeDealTime(array_stockdata[1]);
			if (0 < ARecord.time) {
				ARecord.price = StockPrice.getPackedPrice(array_stockdata[2]);
			}
        	Log.d(LOGTAG, "parse line:" + array_stockdata[1] + "/" + ARecord.time + "/" + ARecord.price);
				//pointdata.datetime = array_stockrecorddata[1]; // 09:37:04
				//pointdata.price_lastest = Double.parseDouble(array_stockrecorddata[2]);
		}
	}
	
	public static class StockDataRecord_QQ {
		// 172/09:24:59/31.95/0.00/3008/9610560/B
		int time = 0;
		int price = 0;
		int volume = 0;
		int amount = 0;
		int mode = 0;
	}
	
	public static class StockDataBuffer {
		public StockDataBufferNode firstNode = null;		
		public StockDataBufferNode lastNode = null;
	}
	
	public static class StockDataBufferNode {
		
		public StockDataBufferNode() {
			price = new int[240]; // 周期 日线 周线 月线
			price_average = new int[240]; // 成交均价 -- 均价的均价
			volume = new int[240]; // 成交量
			amount = new int[240]; // 成交金额
		}

		public StockDataBufferNode prevSibling = null;
		public StockDataBufferNode nextSibling = null;
		public int[] price = null; // 成交价 -- 其实是个均价
		public int[] price_average = null; // 成交均价 -- 均价的均价
		public int[] volume = null; // 成交量
		public int[] amount = null; // 成交金额
	}
}
