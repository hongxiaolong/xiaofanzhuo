package com.xiaolong.xiaofanzhuo.dataoperations;

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
public class DatabaseHelper extends SQLiteOpenHelper {
	// Database name
	private static final String DB_NAME = "xiaofanzhuoSQLite.db";
	//Cart table name 
	private static final String CART_TABLE_NAME = "xiaofanzhuoCART";
	private static final int DB_VERSION = 1;

	private static final String CREATE_SQL = "create table if not exists ";
	private static final String CART_CREATE_SQL_CONTENT = " ("
			+ "_id integer primary key autoincrement, "
			+ "ShopID char(11) not null, "
			+ "FoodID integer not null,"
			+ "Food varchar(40) not null, "
			+ "FoodPrice float not null default 0,"
			+ "FoodImgUrl varchar(255),"
			+ "Number integer not null default 0"
			+ ");";

	/**
	 * Database Helper constructor.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Creates the database tables.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_SQL + CART_TABLE_NAME + CART_CREATE_SQL_CONTENT);
	}

	/**
	 * Handles the table version and the drop of a table.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(DatabaseHelper.class.getName(), "Upgrading databse from version"
				+ oldVersion + "to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS cart");
		onCreate(database);
	}
}