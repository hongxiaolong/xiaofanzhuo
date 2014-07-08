package com.xiaolong.xiaofanzhuo.historyorders;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates the relation with the SQLite Database Helper through which
 * queries can be SQL called.
 * 
 * @author Andrei
 * 
 */
public class HistoryOrderDatabaseHelper extends SQLiteOpenHelper {
	// Database name
	private static final String DB_NAME = "xiaofanzhuoSQLiteOrder.db";
	//Cart table name 
	private static final String ORDER_TABLE_NAME = "xiaofanzhuoORDER";
	private static final String ORDER_DETAIL_TABLE_NAME = "xiaofanzhuoORDER_DETAIL";
	private static final int DB_VERSION = 1;

	private static final String CREATE_SQL = "create table if not exists ";

	private static final String ORDER_CREATE_SQL_CONTENT = " ("
			+ "_id varchar(100)  , "
			+ "ShopID char(11) not null, "
			+ "ShopName char(50) not null, "
			+ "UserID char(11) not null, "
			+ "Price float not null default 0,"
			+ "Time char(50) not null"
			+ ");";
	
	private static final String ORDER_DETAIL_CREATE_SQL_CONTENT = " ("
			+ "_id varchar(100)  , "
			+ "FoodID char(11) not null, "
			+ "FoodName char(50) not null, "
			+ "FoodNum integer not null, "
			+ "FoodImgUrl varchar(255),"
			+ "FoodPrice float not null default 0"
			+ ");";
	
	/**
	 * Database Helper constructor.
	 * 
	 * @param context
	 */
	public HistoryOrderDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Creates the database tables.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		//database.execSQL(CREATE_SQL + CART_TABLE_NAME + CART_CREATE_SQL_CONTENT);
		database.execSQL(CREATE_SQL + ORDER_TABLE_NAME + ORDER_CREATE_SQL_CONTENT);
		database.execSQL(CREATE_SQL + ORDER_DETAIL_TABLE_NAME + ORDER_DETAIL_CREATE_SQL_CONTENT);
	}

	/**
	 * Handles the table version and the drop of a table.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(HistoryOrderDatabaseHelper.class.getName(), "Upgrading databse from version"
				+ oldVersion + "to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS cart");
		onCreate(database);
	}
}