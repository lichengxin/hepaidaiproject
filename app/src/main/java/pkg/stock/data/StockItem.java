package pkg.stock.data;

import utils.UtilsPingyin;

public class StockItem {

	/* stock item info */
    public String marketCode = null;
    public String stockCode = null;
    public String stockName = null;
    public StockDataQuote.InstantData cacheStockQuote = null;
    
    public String getPinYinCodeSimple() {
    	if (null != stockName) {
    		StringBuilder strbd = new StringBuilder();
    		String pinyincode = null;
    		UtilsPingyin py = UtilsPingyin.getInstance();
    		for (int j = 0; j < stockName.length(); j++) {
    			pinyincode = py.pingyin(stockName.charAt(j));
    			if (null != pinyincode) {
    				if (1 < pinyincode.length()) {
    					strbd.append(pinyincode.substring(0,  1).toUpperCase());												
    				}											
    			}
    		}
    		return strbd.toString();    		
    	}
    	return null;
    }
}
