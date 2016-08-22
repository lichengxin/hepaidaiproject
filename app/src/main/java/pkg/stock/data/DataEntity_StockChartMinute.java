package pkg.stock.data;

public class DataEntity_StockChartMinute {
	public static int minuteCount = 242;
	public int validRecordCount = 0;
	public int price_preclose = 0; // 昨收盘
	public int price_high = 0; // 最高
	public int price_low = 0; // 最高
	public int volume_high = 0; // 最高
	public int volume_low = 0; // 最高
	public int[] price_minute = new int[minuteCount]; // 价格
	public int[] price_average_minute = new int[minuteCount]; // 均价
	public int[] volume_minute = new int[minuteCount]; // 成交量 手
	
	public void updateprice(int AMinuteindex, int APrice) {
		price_minute[AMinuteindex] = APrice;
		if (0 == price_high) {
			price_high = APrice;
		} else {
			if (price_high < APrice) {
				price_high = APrice;            					
			}
		}
		if (0 == price_low) {
			price_low = APrice;
		} else {
			if (price_low > APrice) {
				if (0 < APrice) {
    				price_low = APrice;            						
				}            					
			}
		}			
	}

	public void updatevolume(int AMinuteindex, int AVolume) {
		volume_minute[AMinuteindex] = AVolume;	
		if (0 == price_high) {
			volume_high = AVolume;
		} else {
			if (volume_high < AVolume) {
				volume_high = AVolume;            					
			}
		}
		if (0 == volume_low) {
			volume_low = AVolume;
		} else {
			if (volume_low > AVolume) {
				if (0 < AVolume) {
					volume_low = AVolume;            						
				}            					
			}
		}	
	}
	
	public static int getMinuteIndex(String time) {
		// 09:25:00  -- 0
		// 09:30:00  -- 1
		// 15:00:00  -- 242
		int result = -1;
    	String[] timedata = time.split(":");
    	if (3 == timedata.length) {
    		int hour = Integer.parseInt(timedata[0]);
    		int min = Integer.parseInt(timedata[1]);
    		if (9 > hour) {
    			return -1;
    		}
    		if (9 == hour) {
    			if (25 == min) {
    				return 0;
    			} else {
    				if (30 <= min) {
    					return min - 30 + 1;
    				}
    			}
    			return -1;
    		};
       		if (12 > hour) {
       			result = (hour - 10) * 60 + min + 31;
       			// 只到 11:29:00
       			if (120 < result) 
       				return -1;
       			return result;
       		} else {
       			// 只到 15:00:00
       			result = (hour - 13) * 60 + min + 121;
       			if (241 < result) 
       				return -1;
       			return result;
    		}
    	}
		return -1;
	}
}
