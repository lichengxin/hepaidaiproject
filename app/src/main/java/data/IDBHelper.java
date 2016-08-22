package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 *所有SQLiteOpenHelper的基类，将数据库 db 可以创建在SD卡上
 */
public class IDBHelper extends SQLiteOpenHelper{
	
	public IDBHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version);
	}

	public enum DataType{INTEGER, TEXT , BLOB}
	public class SqlStringBuilder {		
		private StringBuilder mSqlString = new StringBuilder();
		private int mFieldCount = 0;

		public String sqlString() {
			return mSqlString.toString();
		}
		
		public void clear() {
			mSqlString.delete(0, mSqlString.length());
		}
		
		public void beginCreateTable(String tableName){
			if (mSqlString.length() > 0){
				mSqlString.delete(0, mSqlString.length());
			}
			mSqlString.append(" CREATE TABLE IF NOT EXISTS "+ tableName + " (");
		}

		public void endCreateTable(){
			mSqlString.append(")");
		}

		public void addAutoIncKey(String fieldName) {
			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			mSqlString.append(fieldName  + " INTEGER PRIMARY KEY AUTOINCREMENT");
			mFieldCount++;
		}
		
		public void addIntPrimaryKey(String fieldName) {
			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			mSqlString.append(fieldName + " INTEGER PRIMARY KEY NOT NULL");
			mFieldCount++;
		}

		public void addTextPrimaryKey(String fieldName) {
			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			mSqlString.append(fieldName + " TEXT PRIMARY KEY NOT NULL");
			mFieldCount++;
		}
		
		public void addTextField(String fieldName){
			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			mSqlString.append(fieldName  + " TEXT NOT NULL DEFAULT ''");
			mFieldCount++;
		}

		public void addIntField(String fieldName){
			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			mSqlString.append(fieldName  + " INTEGER NOT NULL DEFAULT '0'");
			mFieldCount++;
		}

		public void addBLOBField(String fieldName){
			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			mSqlString.append(fieldName  + " BLOB");
			mFieldCount++;
		}

		/**
		 * 
		 * @param sql sql语句
		 * @param fieldName 字段名称
		 * @param isLastFied 是否是最后一个字段
		 * @param dataType 字段数据类型
		 * @param notNull 是否不为空
		 * @param defaultValue 默认值
		 * @param isPrimaryKey 是否是主键
		 * @param isAuto 是否是自增长
		 */
		public void addFieldByType(String fieldName, DataType dataType, boolean notNull, String defaultValue, boolean isPrimaryKey, boolean isAuto){

			if (0 < mFieldCount) {
				mSqlString.append(fieldName  + " , ");
			}
			String type = null;
			switch (dataType) {
			case INTEGER:
				type = "INTEGER";
				break;
			
			case TEXT:
				type = "TEXT";
				break;
				
			case BLOB:
				type = "BLOB";
				break;

			default:
				break;
			}
			
			mSqlString.append(fieldName+" "+type);
			if (isPrimaryKey) {
				mSqlString.append(" PRIMARY KEY");
			}
			
			if (notNull) {
				mSqlString.append(" NOT NULL");
			}
			
			if (null != defaultValue) {
				mSqlString.append(" DEFAULT "+"'"+defaultValue+"'");
			}
			
			if (isAuto) {
				mSqlString.append(" AUTOINCREMENT");
			}
			mFieldCount++;
		}

		public String alterAddIntField(String table, String field){
			return "ALTER TABLE " + table + " ADD "+ field +" INTEGER NOT NULL DEFAULT '0'";
		}
		
		public String alterAddStrField(String table, String field){
			return "ALTER TABLE " + table + " ADD "+ field +" TEXT NOT NULL DEFAULT ''";
		}
			
	}
	
	protected void sqlAddCreateIdx(SQLiteDatabase db, String table, String field){
		StringBuilder sql = new StringBuilder();
		sql.append(" CREATE INDEX IF NOT EXISTS ");
		sql.append(" idx_" + table  + "_" + field.replace(',', '_')); 
		sql.append(" ON "  + table); 
		sql.append(" (" + field + ")");
		db.execSQL(sql.toString()); 
	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		//super.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}	
}
