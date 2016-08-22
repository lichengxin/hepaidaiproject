package pkg.stock.data;

import java.io.FileNotFoundException;
import java.io.IOException;

import utils.*;

import base.app.AppPath;
import base.app.BaseApp;


import android.text.TextUtils;

public class StockDataStore_Day {

	private static final String LOGTAG = StockDataStore_Day.class.getSimpleName();
	
	public StockDataStore_Day() {
	}
	
	
  /*
   * 将文件的一段区域映射到内存中，比传统的文件处理速度要快很多
   * 
  { 日线数据 }
  PStore_Quote64_M1_Day_V1  = ^TStore_Quote64_M1_Day_V1;
  TStore_Quote64_M1_Day_V1  = packed record  // 56
    PriceRange          : TStore_PriceRange;  // 16
    DealVolume          : Int64;         // 8 - 24 成交量
    DealAmount          : Int64;         // 8 - 32 成交金额
    DealDate            : Integer;       // 4 - 36 交易日期
    Weight              : TStore_Weight; // 4 - 40 复权权重 * 100
    TotalValue          : Int64;         // 8 - 48 总市值
    DealValue           : Int64;         // 8 - 56 流通市值 
  end;
         
   */
	private StockDataBuffer_Day mInternalDataBuffer = null;
	public StockDataBuffer_Day getDataBuffer() {
		if (null == mInternalDataBuffer) {
			mInternalDataBuffer = new StockDataBuffer_Day();
		}
		return mInternalDataBuffer; 
	}
	
	public int getRecordCount() {
		return getDataBuffer().mRecordCount;
	}
	
	public int getBeginDate() {
		return getDataBuffer().mBeginDate;
	}
	
	public int getEndDate() {
		return getDataBuffer().mEndDate;
	}
	
	public void addRecord(StockDataRecord_Day record) {
		if (!record.isValid()) 
			return;
		StockDataBuffer_Day buffer = getDataBuffer();
		buffer.addRecord(record);
	}
	
	public String mStockCode = null;
	
	public void loadStock(String AStockCode) {
		if (!android.os.Environment.MEDIA_MOUNTED.equals(AppPath.SDCardState))
			return;
			
		this.mStockCode = AStockCode;
		java.io.File filepath = new java.io.File(AppPath.stockFilePath);
        if(!filepath.exists()) {
        	filepath.mkdirs();
	    }
		String fileName = AppPath.getStockFileName(AStockCode);
		java.io.File file = new java.io.File(filepath, fileName);
	    try {
	       if(!file.exists())
	       {
	    	   file.createNewFile();
	    	   return;
	       }
	       loadFromFile(AppPath.stockFilePath + fileName);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void save() {
		if (!android.os.Environment.MEDIA_MOUNTED.equals(AppPath.SDCardState))
			return;
		if (TextUtils.isEmpty(mStockCode))
			return;
		java.io.File filepath = new java.io.File(AppPath.stockFilePath);
        if(!filepath.exists()) {
        	filepath.mkdirs();
	    }
		saveToFile(AppPath.stockFilePath + AppPath.getStockFileName(mStockCode));
	}

	public void loadFromFile(String AFileName) {
		loadDataFileMapping1(AFileName);
	}

	public void loadDataFileMapping1(String AFileName) {
		java.io.FileInputStream in;
		java.nio.channels.FileChannel channel = null;  
		try {
			in = new java.io.FileInputStream(AFileName);
			channel = in.getChannel();  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
		if (null == channel)
			return;
        int length = 0;
		try {
			length = (int) channel.size();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		if (0 < length) {
	        java.nio.MappedByteBuffer dataInput = null;
			try {
				dataInput = channel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, length);
			} catch (IOException e) {
				e.printStackTrace();
			}  
	        if (null != dataInput) {
	    		StockDataBuffer_Day buffer = getDataBuffer();
	        	readDataFileHeader(dataInput, buffer);
	        	readDataRecords(dataInput, buffer);
	        }
		}
	}

	private static int bigFileMapLength = 0xFFFFF; // 1Mb  
	public void loadDataFileMapping2(String AFileName) { 
	}

	public void saveToFile(String AFileName) {
		saveDataFileMapping1(AFileName);
	}

	public void readDataFileHeader(java.nio.MappedByteBuffer dataInput, StockDataBuffer_Day buffer) {
		byte[] bytes2 = new byte[2];
		byte[] bytes4 = new byte[4];
		byte byte1 = 0;
		// 2 Signature 2
		dataInput.get(bytes2);
	    // 6 1 DataVer1  DataVer2 DataVer3 DataVer4
		dataInput.get(bytes4);
		// 10 HeadSize StoreSizeMode DataType 
		dataInput.get(bytes4);
		// 12 DataMode RecordSizeMode
		dataInput.get(bytes2);
		// 16
		dataInput.get(bytes4);
		buffer.mRecordCount = DelphiUtils.JavaDelphiBytes4_JavaInt(bytes4);
		// 18 CompressFlag EncryptFlag
		dataInput.get(bytes2);
		// 20 DataSourceCode
		dataInput.get(bytes2);
		// 32 mStockCode
		byte[] bytes12 = new byte[12];
		dataInput.get(bytes12);
		// 34 priceFactor
		dataInput.get(bytes2);
	    // 36 2 FirstDealDate       : Word;             // 2 - 36
		dataInput.get(bytes2);
		//mBeginDate = DelphiUtils.JavaDelphiBytes2_JavaInt(bytes2);
	    // 38 2 LastDealDate        : Word;             // 2 - 38
		dataInput.get(bytes2);
		//mEndDate = DelphiUtils.JavaDelphiBytes2_JavaInt(bytes2);
		// 40 endDealDate
		dataInput.get(bytes2);
		byte[] bytesReserve = new byte[64 - dataInput.position()];
		dataInput.get(bytesReserve);
	}

	//public int mBeginDate = 0;
	//public int mEndDate = 0;
	
	public void saveDataFileHeader(java.nio.MappedByteBuffer out, StockDataBuffer_Day buffer) {
		// 2 Signature 2
		byte[] bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(7784);
		out.put(bytes2);
	    // 3 1 DataVer1            : Byte;
		out.put((byte) 1);
		// 4 1 DataVer2            : Byte;
		out.put((byte) 1);
		// 5 1 DataVer3            : Byte;
		out.put((byte) 1);
		// 6 1 DataVer4: Byte; -- store byte order -- delphi integer byte4 byte[0..4] or byte[4..0]
		out.put((byte) 1);
	    // 7 1 HeadSize            : Byte;             // 1 -- 7
		out.put((byte) 64);
	    // 8 1 StoreSizeMode       : TStore_SizeMode;  // 1 -- 8 page size mode
		out.put((byte) 16);
	    // 10 2 DataType            : Word;             // 2 -- 10   
		bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(1);
		out.put(bytes2);
	    // 11 1 DataMode            : Byte;             // 1 -- 11
		out.put((byte) 1);
	    // 12 1 RecordSizeMode      : TStore_SizeMode;  // 1 -- 12
		out.put((byte) 6);
	    // 16 4 RecordCount         : integer;          // 4 -- 16
		byte[] bytes4 = DelphiUtils.JavaInt2JavaDelphiBytes4(buffer.mRecordCount);
		out.put(bytes4);
	    // 17 1 CompressFlag        : Byte;             // 1 -- 17
		out.put((byte) 0);
		// 18 1 EncryptFlag         : Byte;             // 1 -- 18
		out.put((byte) 0);
	    // 20 2 DataSourceCode      : Word;             // 2 -- 20
		bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(DBConsts.DataSrc_163);
		out.put(bytes2);
	    // 32 12 Code                : array[0..11] of AnsiChar; // 12 - 32
		byte[] bytes12 = new byte[12];
		//this.mStockCode.toCharArray();
		System.arraycopy(this.mStockCode.getBytes(), 0, bytes12, 0, Math.min(this.mStockCode.length(), 12));
		out.put(bytes12);
	    // 34 2 StorePriceFactor    : Word;             // 2 - 34
		bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(1000);
		out.put(bytes2);
	    // 36 2 FirstDealDate       : Word;             // 2 - 36
		bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(buffer.mBeginDate);
		out.put(bytes2);
	    // 38 2 LastDealDate        : Word;             // 2 - 38
		bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(buffer.mEndDate);
		out.put(bytes2);
	    // 40 2 EndDealDate         : Word;             // 2 - 40
		bytes2 = DelphiUtils.JavaInt2JavaDelphiBytes2(0);
		out.put(bytes2);
		if (64 > out.position()) {
			byte[] bytesReserve = new byte[64 - out.position()];
			out.put(bytesReserve);
		}
	}

	public void readDataRecords(java.nio.MappedByteBuffer dataInput, StockDataBuffer_Day buffer) {
		byte[] bytes64 = new byte[64];
		byte[] bytes4 = new byte[4];
		byte[] bytes8 = new byte[8];
		
		int recordCount = buffer.mRecordCount;
		if (30000 < recordCount) {
			// 一定发生了 数据错误
			return;			
		}
		buffer.mRecordCount = 0;
		java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyyMMdd");
		StockDataRecord_Day record = new StockDataRecord_Day();
		while (0 < recordCount) {
			dataInput.get(bytes64);

			System.arraycopy(bytes64, 0, bytes4, 0, 4);			
			record.priceOpen = DelphiUtils.JavaDelphiBytes4_JavaInt(bytes4);
			System.arraycopy(bytes64, 4, bytes4, 0, 4);			
			record.priceHigh = DelphiUtils.JavaDelphiBytes4_JavaInt(bytes4);
			System.arraycopy(bytes64, 8, bytes4, 0, 4);			
			record.priceLow = DelphiUtils.JavaDelphiBytes4_JavaInt(bytes4);   // 最低价
			System.arraycopy(bytes64, 12, bytes4, 0, 4);
			record.priceClose = DelphiUtils.JavaDelphiBytes4_JavaInt(bytes4); // 收盘价

			System.arraycopy(bytes64, 16, bytes8, 0, 8);
			record.volume = DelphiUtils.JavaDelphiBytes8_JavaLong(bytes8); // 成交量

			System.arraycopy(bytes64, 24, bytes8, 0, 8);
			record.amount = DelphiUtils.JavaDelphiBytes8_JavaLong(bytes8); // 成交金额

			System.arraycopy(bytes64, 32, bytes4, 0, 4);
			record.dealDate = DelphiUtils.JavaDelphiBytes4_JavaInt(bytes4); // 交易日期

			System.arraycopy(bytes64, 40, bytes8, 0, 8);
			record.totalValue = DelphiUtils.JavaDelphiBytes8_JavaLong(bytes8); // 总市值
			
			System.arraycopy(bytes64, 48, bytes8, 0, 8);
			record.dealValue = DelphiUtils.JavaDelphiBytes8_JavaLong(bytes8);  // 流通市值
			
			//Log.d(LOGTAG, " add deal:" + fmt.format(DelphiUtils.DelphiTime2JavaTime(record.dealDate)) + 
			//		"/" + record.priceOpen);			
			buffer.addRecord(record);
			recordCount--;
		}
	}
	
	public void saveDataBufferNode(java.nio.MappedByteBuffer out, StockDataBuffer_Day.BufferNode bufferNode) {
		byte[] bytes4 = null;
		byte[] bytes8 = null;
		for (int i = 0; i < bufferNode.bufcount; i ++) {
		    //PriceOpen           : TStore_Price;  // 4 - 4
			bytes4 = DelphiUtils.JavaInt2JavaDelphiBytes4(bufferNode.priceOpen[i]);
			out.put(bytes4);
			//PriceHigh           : TStore_Price;  // 4 - 8  ( price * 100 )
			bytes4 = DelphiUtils.JavaInt2JavaDelphiBytes4(bufferNode.priceHigh[i]);
			out.put(bytes4);
			//PriceLow            : TStore_Price;  // 4 - 12
			bytes4 = DelphiUtils.JavaInt2JavaDelphiBytes4(bufferNode.priceLow[i]);
			out.put(bytes4);
			//PriceClose          : TStore_Price;  // 4 - 16
			bytes4 = DelphiUtils.JavaInt2JavaDelphiBytes4(bufferNode.priceClose[i]);
			out.put(bytes4);
		    //DealVolume          : Int64;         // 8 - 24 成交量
			bytes8 = DelphiUtils.JavaLong2JavaDelphiBytes8(bufferNode.volume[i]);
			out.put(bytes8);
	        //DealAmount          : Int64;         // 8 - 32 成交金额
			bytes8 = DelphiUtils.JavaLong2JavaDelphiBytes8(bufferNode.amount[i]);
			out.put(bytes8);
	        //DealDate            : Integer;       // 4 - 36 交易日期
			bytes4 = DelphiUtils.JavaInt2JavaDelphiBytes4(bufferNode.dealDate[i]);
			out.put(bytes4);
	        //Weight              : TStore_Weight; // 4 - 40 复权权重 * 100
			out.putInt(0);
	        //TotalValue          : Int64;         // 8 - 48 总市值
			bytes8 = DelphiUtils.JavaLong2JavaDelphiBytes8(bufferNode.totalValue[i]);
			out.put(bytes8);
	        //DealValue           : Int64;         // 8 - 56 流通市值 
			bytes8 = DelphiUtils.JavaLong2JavaDelphiBytes8(bufferNode.dealValue[i]);
			out.put(bytes8);
			// 56 + 8 = 64
			out.putLong(0);
		}
	}
	
	public void saveDataFileMapping1(String AFileName) {
	    try {
	    	java.io.RandomAccessFile file = new java.io.RandomAccessFile(AFileName, "rw");
	    	java.nio.channels.FileChannel channel = file.getChannel();
	    	
	    	//必须指明，它是从文件的哪个位置开始映射的，映射的范围又有多大；也就是说，它还可以映射一个大文件的某个小片断
	    	long mapBeginPosition = 0;
	    	StockDataBuffer_Day buffer = getDataBuffer();
	    	//Log.d(LOGTAG, "saveDataFileMapping1:" + AFileName + " recordcount:" +  buffer.mRecordCount);
	    	long mapSize = ((64 + buffer.mRecordCount * 64) - ((64 + buffer.mRecordCount * 64) % (64 * 1024)) + 64 * 1024);
	    	//BaseApp.showToast("mapSize:" + mapSize);
			java.nio.MappedByteBuffer out = channel.map(java.nio.channels.FileChannel.MapMode.READ_WRITE, 
					mapBeginPosition, mapSize);
			if (null != out) {
				saveDataFileHeader(out, buffer);
				StockDataBuffer_Day.BufferNode node = buffer.firstNode;
				while (null != node) {
					saveDataBufferNode(out, node);
					node = node.nextSibling;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}   
	}
}
