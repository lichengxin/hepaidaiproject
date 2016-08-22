package pkg.stock.data;

import android.text.TextUtils;

/*
 * 股票基本行情 
 * 行数据
 */

public class StockDataQuote {
	
    public static class DayData {
  	  public double price_high;    // 当天最高价
  	  public double price_low;     // 当天最低价
  	  public double price_open;    // 当天开盘价
  	  public double price_close;   // 当天收盘价
	}
    
    public static class RealPointData {
  	  public double price_lastest; // 当前最新价
		
    }
	// 即时行情行数据
    public static class InstantData {
	  // float 与 double 的区别
	  public String stockCode;
	  public String stockName;
	  public double quotedatetime; // 当前报价时间
	  public String quotedate; // 当前报价时间
	  public String quotetime; // 当前报价时间
	  public int price_preclose; // 前收盘价
	  public int price_lastest; // 当前最新价
	  public int price_high;    // 当天最高价
	  public int price_low;     // 当天最低价
	  public int price_open;    // 当天开盘价
	  public int price_close;   // 当天收盘价
	  public int[] price_bid = new int[5];
	  public int[] volume_bid = new int[5];
	  public int[] price_ask = new int[5];
	  public int[] volume_ask = new int[5];
	  
	  public String getPriceOffsetRate(java.text.DecimalFormat fmt) {
		 if (0 < price_lastest) {
			 if (0 < price_preclose) {
				 if (price_preclose == price_lastest) {
					return "0.00%"; 
				 } else {
					if (price_preclose < price_lastest) {
						return fmt.format(((double)(price_lastest - price_preclose) / price_preclose * 100)) + "%";
					} else {
					    return "-" + fmt.format(((double)(price_preclose - price_lastest) / price_preclose * 100)) + "%";
					}
				 }
			 }
		 }
		 return ""; 
	  }
	  
	  public void assign(InstantData timedata) {
		  if (TextUtils.isEmpty(timedata.stockCode)) 
			  return;
		  if (!TextUtils.isEmpty(this.stockCode)) {
		      if (!this.stockCode.equals(timedata.stockCode)) {
		    	  return;
		      }
		  }
		  this.price_preclose = timedata.price_preclose; // 前收盘价
		  this.quotedatetime = timedata.quotedatetime; // 当前报价时间
		  this.quotedate = timedata.quotedate; // 当前报价时间
		  this.quotetime = timedata.quotetime; // 当前报价时间
		  this.price_lastest = timedata.price_lastest; // 当前最新价
		  this.price_high = timedata.price_high;    // 当天最高价
		  this.price_low = timedata.price_low;     // 当天最低价
		  this.price_open = timedata.price_open;    // 当天开盘价
		  this.price_close = timedata.price_close;   // 当天收盘价
	  }
	  
	  public void clear() {
		  stockCode = null;
		  price_preclose = 0; // 前收盘价
		  quotedatetime = 0; // 当前报价时间
		  price_lastest = 0; // 当前最新价
		  price_high = 0;    // 当天最高价
		  price_low = 0;     // 当天最低价
		  price_open = 0;    // 当天开盘价
		  price_close = 0;   // 当天收盘价
	  }  
    }
}
