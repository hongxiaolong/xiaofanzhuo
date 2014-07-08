package com.xiaolong.xiaofanzhuo.dataoperations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JSQLDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_BUSINESSLIST = "businesslistings";
	public static final String TABLE_MENULIST = "menulistings";

	public static final String COLUMN_ID = "_id";

	private static final String DATABASE_NAME = "xiaofanzhuo";
	private static final int DATABASE_VERSION = 13;

	private static final String CREATE_SQL = "create table if not exists ";
	private static final String BUSINESSLIST_CREATE_SQL_CONTENT = " ("
			+ "_id integer primary key autoincrement, "
			+ "ShopTag varchar(40), "
			+ "TasteScore float default 0, "
			+ "ShopName varchar(40) not null, "
			+ "Other3 text, "
			+ "ShopInfo varchar(40), "
			+ "PraiseNum int default 0, "
			+ "Other1 text, "
			+ "BusyState int default 0, "
			+ "PhoneNum char(11) not null, "
			+ "ShopID char(11) not null, "
			+ "SendFoodOut int default 0, "
			+ "ShopMenu text, "
			+ "ShopAverPrice float default 0, "
			+ "ShopSite varchar(40) not null, "
			+ "ShopImgUrl varchar(255), "
			+ "EnvScore float default 0, "
			+ "ServiceScore float default 0, "
			+ "NumofPeopleWant2Eat int default 0, "
			+ "Other2 text, "
			+ "ShopLocation varchar(40) not null,"
			+ "ShopMap varchar(255)"
			+ ");";

	private static final String MENULIST_CREATE_SQL_CONTENT = " ("
			+ "_id integer primary key autoincrement, "
			+ "Category varchar(40), "
			+ "ThumbUrl varchar(255), "
			+ "img_thumb_higth int default 0, "
			+ "IsSpec int default 0, Width int default 0, "
			+ "FoodPrice float default 0, "
			+ "IsRecommend int default 0, "
			+ "Food varchar(40) not null, "
			+ "ShopID char(11) not null, "
			+ "Height int default 0, "
			+ "img_thumb_width int default 0, "
			+ "FoodImgUrl varchar(255)"
			+ ");";

	/*
	 * 数据库名: xiaofanzhuo.db 表名: businesslistings+fields(area name)
	 */
	private String fields = null;

	public JSQLDBHelper(Context context, String area) {
		super(context, DATABASE_NAME  + area + ".db", null, DATABASE_VERSION);
		this.fields = area;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_SQL + TABLE_BUSINESSLIST + fields + BUSINESSLIST_CREATE_SQL_CONTENT);
//			database.execSQL(CREATE_SQL + TABLE_MENULIST + id + MENULIST_CREATE_SQL_CONTENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(JSQLDBHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSINESSLIST + fields);
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENULIST + id);
		onCreate(db);
	}
}
