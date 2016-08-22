package pkg.stock.data;

import utils.Log;
import base.app.BaseApp;


public class StockDataBuffer_Day {

	private static final String LOGTAG = StockDataBuffer_Day.class.getSimpleName();
	
	public class BufferNode {
		public BufferNode prevSibling = null;
		public BufferNode nextSibling = null; 
		public int bufcount = 0;
		public int[] index;  // 索引
		public int[] priceOpen;  // 开盘价
		public int[] priceHigh;  // 最高价
		public int[] priceLow;   // 最低价
		public int[] priceClose; // 收盘价
		public long[] volume;  // 成交量
		public long[] amount;  // 成交金额
		public int[] dealDate; // 交易日期
		public int[] weight; // 权重
		public long[] totalValue; // 总市值
		public long[] dealValue; //circulating shares market value
		
		public void clear() {
			index = null;
			prevSibling = null;
			nextSibling = null; 
			priceOpen = null;  // 开盘价
			priceHigh = null;  // 最高价
			priceLow = null;   // 最低价
			priceClose = null; // 收盘价
			volume = null;  // 成交量
			amount = null;  // 成交金额
			dealDate = null; // 交易日期
			weight = null; // 权重
			totalValue = null; // 总市值
			dealValue = null; //circulating shares market value
		}

		public boolean fillRecord(StockDataRecord_Day record, int index) {
			if (0 > index)
				return false;
			if (priceOpen.length <= index)
				return false;
			record.priceOpen = priceOpen[index];
			record.priceHigh = priceHigh[index];  // 最高价
			record.priceLow = priceLow[index];   // 最低价
			record.priceClose = priceClose[index]; // 收盘价
			record.volume = volume[index];  // 成交量
			record.amount = amount[index];  // 成交金额
			record.dealDate = dealDate[index]; // 交易日期
			record.weight = weight[index]; // 权重
			record.totalValue = totalValue[index]; // 总市值
			record.dealValue = dealValue[index]; //circulating shares market value
			return true;
		}
		
	}

	public static int DataBufSize = 256;
	public BufferNode firstNode = null;
	public BufferNode lastNode = null; 
	public int mRecordCount = 0;
	public BufferNode mCurrentNode = null;	
	public int mBeginDate = 0;
	public int mEndDate = 0;
	
	public BufferNode newNode() {
		BufferNode result = new BufferNode();
		result.bufcount = 0; // <===== 初始化为 0
		result.index = new int[DataBufSize];
		result.priceOpen = new int[DataBufSize];
		result.priceHigh = new int[DataBufSize];
		result.priceLow = new int[DataBufSize];
		result.priceClose = new int[DataBufSize];
		result.volume = new long[DataBufSize];
		result.amount = new long[DataBufSize];
		result.dealDate = new int[DataBufSize];
		result.weight = new int[DataBufSize];
		result.totalValue = new long[DataBufSize];
		result.dealValue = new long[DataBufSize];
		addBufferNode(result);	
		return result;
	}
	
	public boolean addRecord(StockDataRecord_Day record) {
		if ( null == record)
			return false;
		if (!record.isValid()) 
			return false;
		if (0 == mBeginDate) {
				// 第一条记录
			mBeginDate = record.dealDate;
			mEndDate = record.dealDate;
		} else {
			if (record.dealDate >= mBeginDate) {
				if (record.dealDate <= mEndDate) {
					// 漏掉的 记录 ??? 这个比较麻烦
					//Log.d(LOGTAG, "addRecord error insert mode???" + mBeginDate + "/" + mEndDate + "/" + record.dealDate);
					return false;
				}
			}
			if (record.dealDate < mBeginDate) {
				mBeginDate = record.dealDate;
			}
			if (record.dealDate > mEndDate) {
				mEndDate = record.dealDate;
			}
		}
		
		if (null == mCurrentNode) {
			mCurrentNode = newNode();
		}
		mCurrentNode.index[mCurrentNode.bufcount] = mRecordCount;
		mCurrentNode.priceOpen[mCurrentNode.bufcount] = record.priceOpen;
		mCurrentNode.priceHigh[mCurrentNode.bufcount] = record.priceHigh;  // 最高价
		mCurrentNode.priceLow[mCurrentNode.bufcount] = record.priceLow;   // 最低价
		mCurrentNode.priceClose[mCurrentNode.bufcount] = record.priceClose; // 收盘价
		mCurrentNode.volume[mCurrentNode.bufcount] = record.volume;  // 成交量
		mCurrentNode.amount[mCurrentNode.bufcount] = record.amount;  // 成交金额
		mCurrentNode.dealDate[mCurrentNode.bufcount] = record.dealDate; // 交易日期
		mCurrentNode.weight[mCurrentNode.bufcount] = record.weight; // 权重
		mCurrentNode.totalValue[mCurrentNode.bufcount] = record.totalValue; // 总市值
		mCurrentNode.dealValue[mCurrentNode.bufcount] = record.dealValue; //circulating shares market value
		//Log.d(LOGTAG, "addRecord " + record.dealDate + "/" + mCurrentNode.index[mCurrentNode.bufcount]);
		mCurrentNode.bufcount++;
		if (DataBufSize == mCurrentNode.bufcount) {
			mCurrentNode = null;
		}
		mRecordCount++;
		return true;
	}

	public void addBufferNode(BufferNode node) {
		if (null == node)
			return;
		if (null == firstNode) {
			firstNode = node;
		}
		if (null != lastNode) {
			node.prevSibling = lastNode; 
			lastNode.nextSibling = node;
		}
		lastNode = node;
	}

	public void removeBufferNode(BufferNode node) {
		if (null == node)
			return;
	    if (null == node.prevSibling) {
	    	firstNode = node.nextSibling;
	    } else {
	    	node.prevSibling.nextSibling = node.nextSibling;
	    };
	    if (null == node.nextSibling) {
	    	lastNode = node.prevSibling;
	    } else {
	    	node.nextSibling.prevSibling = node.prevSibling;
	    };
		node.prevSibling = null;
		node.nextSibling = null;
	}
}
