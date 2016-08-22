package pkg.stock.data;

public class StockDataRecord_Day {
	public int dealDate = 0; // 交易日期
	public int priceOpen = 0;  // 开盘价
	public int priceHigh = 0;  // 最高价
	public int priceLow = 0;   // 最低价
	public int priceClose = 0; // 收盘价
	public int weight = 0;
	public long volume = 0;  // 成交量
	public long amount = 0;  // 成交金额
	public long totalValue; // 总市值
	public long dealValue;  // 流通市值
	
	public boolean isValid() {
		if (0 == dealDate)
			return false;
		if (0 == priceOpen)
			return false;
		if (0 == priceClose)
			return false;
		return true;
	}
}
