package pkg.stock.data;


public class StockPrice {

	private static final String LOGTAG = StockPrice.class.getSimpleName();
	public static java.math.BigDecimal priceFactor = new java.math.BigDecimal("1000");

	public static java.text.DecimalFormat mPackedPriceFormat = null;

    public static java.text.DecimalFormat getPackedPriceFormatter() {
    	if (null == mPackedPriceFormat) {
    		mPackedPriceFormat = new java.text.DecimalFormat("0.00");
    	}
	    return mPackedPriceFormat;
    }

	public static String getPackedPriceText(int price) {
		return getPackedPriceFormatter().format(((double)price) / 1000);
	}
	
	public static int getPackedPrice(String price) {
    	int result = 0;
    	try {
    		//java.math.BigDecimal a = new java.math.BigDecimal();
    	  //result = (int) ((new java.math.BigDecimal(price)).doubleValue() * 1000);
    		result = ((new java.math.BigDecimal(price)).multiply(priceFactor)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }

	public static long tryParseLongValue(String longvalue) {
		//Log.d(LOGTAG, "tryParseLongValue:" + longvalue);
		long result = 0;
    	try {
    	  int p = longvalue.indexOf("e+");
    	  if (0 < p) {
    		  //double d = Double.parseDouble(longvalue);
    		  //result = (long) d;
    		  result = (long) Double.parseDouble(longvalue);
      		  //Log.d(LOGTAG, "result:" + result + "/" + d);   		
    	  } else {
        	  p = longvalue.indexOf(".");
        	  if (0 < p) {
            	  result = Long.parseLong(longvalue.substring(0, p));    		  
        	  } else {
            	  result = Long.parseLong(longvalue);    		  
        	  }
    	  }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }

	public static int tryParseIntValue(String intvalue) {
		int result = 0;
    	try {
    	  int p = intvalue.indexOf(".");
    	  if (0 < p) {
        	  result = Integer.parseInt(intvalue.substring(0, p));    		  
    	  } else {
        	  result = Integer.parseInt(intvalue);    		  
    	  }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }

	public static double tryParseDoubleValue(String doublevalue) {
		double result = 0;
    	try {
    	  result = Double.parseDouble(doublevalue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
}
