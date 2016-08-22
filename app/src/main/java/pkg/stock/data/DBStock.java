package pkg.stock.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import data.IDBHelper;
import pkg.stock.data.DBConsts.TableStock;

public class DBStock {

	private DBStock () {}
	private static DBStock mInstance = null;
	
	public static DBStock getInstance() {
		if (null == mInstance) {
			mInstance = new DBStock();
			mInstance.init();
		}
		return mInstance;
	}
	
	private static class UserDBOpenHelper extends IDBHelper {

		private UserDBOpenHelper(Context context) {
			super(context, "", 0);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//createSysContactTable(this, db);
			createStockTable(db);	
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
		private void createStockTable(SQLiteDatabase db) {

			IDBHelper.SqlStringBuilder sql = new IDBHelper.SqlStringBuilder();
			sql.beginCreateTable(TableStock.TABLE_NAME);
			sql.addAutoIncKey(DBConsts.FIELD_AUTOINCID);
			sql.addTextField(TableStock.FIELD_CODE);
			sql.addTextField(TableStock.FIELD_NAME);
			sql.addTextField(TableStock.FIELD_PINYIN);                                  // 扩展信息
			sql.endCreateTable();
			
			db.execSQL(sql.sqlString());  //db.execSQL(sql_create_table_contacts);
			sql.clear();
		}
	}
	
	private final static String initStockListUrl= "/assets/stock/stock.json";

	private String initGetStockListData() {
        StringBuilder str = new StringBuilder();
        //str.
		InputStream in = DBStock.class.getResourceAsStream(initStockListUrl);
		byte[]  buf = new byte[4096]; 
		try {
			int n;  
	        while ((n = in.read(buf))!= -1){   
	        	str.append(new String(buf,0,n));   
		    }  
			return  str.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
		}
		return null;
	}

	private boolean initParseStockListByJson(String data) {
		if (TextUtils.isEmpty(data))
			return false;

		StockItem stockitem = null;
		String stockcode = null;
		JSONTokener jsonParser = new JSONTokener(data);
		// 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
		try {
			JSONObject jobj = (JSONObject) jsonParser.nextValue();
			if (null != jobj) {
				JSONArray stocklistarray = jobj.getJSONArray("stocks");
				if (null != stocklistarray) {
					for (int i = 0; i <stocklistarray.length(); i ++) {
						JSONObject objstockitem = (JSONObject)stocklistarray.opt(i);
						
						if (objstockitem.isNull("e")) {
							stockcode = objstockitem.getString("c");
							if (null != stockcode) {
								if (8 == stockcode.length()) {
									stockitem = new StockItem();
									stockitem.stockCode = stockcode.substring(2);
									stockitem.marketCode = stockcode.substring(0, 2);
									stockitem.stockName = objstockitem.getString("n");
									addStockItem(stockitem);
								}								
							}
						} 
					}
					return true;
				}				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}  
		return false;
	}
	
	// HashMap 通过hashcode对其内容进行快速查找 hashmap是没有顺序的
	// TreeMap 中所有的元素都保持着某种固定的顺序
	// Android应用性能优化之使用SparseArray替代HashMap
	// SparseArray是android里为<Interger,Object>这样的Hashmap而专门写的类
	// HashMap<Integer, E> hashMap = new HashMap<Integer, E>();
	// SparseArray<E> sparseArray = new SparseArray<E>();
	
	/*//
	android.util.SparseArray<StockItem> mStocks2 = new android.util.SparseArray<StockItem>();
	private android.util.SparseArray<String> sortSparseArray(android.util.SparseArray<String> arr){  
        List<Integer> keys = new ArrayList<Integer>();  
        List<String> vals = new ArrayList<String>();  
        for(int i =0; i < arr.size();i++){  
            keys.add(arr.keyAt(i));  
            vals.add(arr.valueAt(i));  
        }  
        java.util.Collections.sort(keys);  
        java.util.Collections.sort(vals);  
        arr.clear();  
        for (int i =0; i<keys.size();i++){  
            arr.put(keys.get(i),vals.get(i));  
        }  
        return arr;  
    }  
	//*/
	
	private static List<StockItem> mStocks = new ArrayList<StockItem>();
	public List<StockItem> getStockItems () {
		if (1 > mStocks.size()) {
			init();
		}
		return mStocks;
	}
	
	public int getStockItemCount() {
		//return mStocks2.size();
		return mStocks.size();
	}

	public StockItem getStockItem(int index) {
		//return mStocks2.valueAt(index);
		return mStocks.get(index);
	}

	public int getStockKey(StockItem stockitem) {
		return Integer.parseInt("1" + stockitem.stockCode);
	}
	
	public void addStockItem(StockItem stockitem) {
		mStocks.add(stockitem);
	}
	
	private void init() {

 		initParseStockListByJson(initGetStockListData());
	}
	
	public void searchStock(String keyword, List<StockItem> output1, List<StockItem> output2) {
		boolean isnum = false;
		try {
			Integer.parseInt(keyword.substring(0, 1).trim());	
			isnum = true;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		String pinyincode = null;
		int p = -1;
		StockItem stockitem = null;
		if (null != mStocks) {			
			for (int i = 0; i < mStocks.size(); i ++ ) {
				stockitem = mStocks.get(i);
				if (isnum) {
					p = stockitem.stockCode.indexOf(keyword);
					if (0 <= p) {
						if (0 == p) {
							output1.add(stockitem);							
						} else {
							output2.add(stockitem);
						}					 
					}
				} else {
					pinyincode = stockitem.getPinYinCodeSimple();
					p = pinyincode.indexOf(keyword);
					if (0 <= p) {
						if (0 == p) {
						  output1.add(stockitem);
						} else {
					      output2.add(stockitem);
						}
					}
					else {
						p = stockitem.stockName.indexOf(keyword);	
						if (0 <= p) {
							if (0 == p) {
							  output1.add(stockitem);
							} else {
						      output2.add(stockitem);
							}
						}
					}
				}
			}
		}
	}
}
