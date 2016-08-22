package pkg.stock.chart;

public class ChartData extends base.BaseObj {

	public int maxRecordCount = 0;
	public int validRecordCount = 0; // 实际蜡烛线
	
	public void updateMaxRecordCount(int AMaxRecordCount) {
		maxRecordCount = AMaxRecordCount;
	}
}
