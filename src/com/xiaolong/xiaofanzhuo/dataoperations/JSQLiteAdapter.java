package com.xiaolong.xiaofanzhuo.dataoperations;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class JSQLiteAdapter {

	public static final String TAG = "JSQLiteAdapter";
	public static final String TABLE_BUSINESSLIST = "businesslistings";

	private Context mContext;
	private String mFields = null;

	private JSQLDBHelper mDBHelper;
	private SQLiteDatabase mDatabase;

	public static final String COLUMN_ID = "_id";
	public static final String SHOPID = "ShopID";
	public static final String SHOPTAG = "ShopTag";
	public static final String SHOPAVG = "ShopAverPrice";
	public static final String BUSYSTATE = "BusyState";
	public static final String TAKEOUT = "SendFoodOut";

	public static final String[] BUSINESSLIST_COLUMN = new String[] { "_id",
			"ShopTag", "TasteScore", "ShopName", "Other3", "ShopInfo",
			"PraiseNum", "Other1", "BusyState", "PhoneNum", "ShopID",
			"SendFoodOut", "ShopMenu", "ShopAverPrice", "ShopSite",
			"ShopImgUrl", "EnvScore", "ServiceScore", "NumofPeopleWant2Eat",
			"Other2", "ShopLocation","ShopMap" };

	public static final String[] MENULIST_COLUMN = new String[] { "_id",
			"Category", "ThumbUrl", "img_thumb_higth", "IsSpec", "Width",
			"FoodPrice", "IsRecommend", "Food", "ShopID", "Height",
			"img_thumb_width", "FoodImgUrl" };

	/**
	 * 
	 * @param context
	 * @param db_section
	 *            数据库辨识
	 */
	public JSQLiteAdapter(Context context, String fields) {
		this.mContext = context;
		this.mFields = fields;
	}

	/**
	 * 
	 * @return 表名: businesslistings+fields(area name)
	 */
	private String tableBusinessList() {
		return TABLE_BUSINESSLIST + mFields;
	}

	/**
	 * Creates the database helper and gets the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public JSQLiteAdapter open() throws SQLException {
		mDBHelper = new JSQLDBHelper(mContext, mFields);
		mDatabase = mDBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		mDBHelper.close();
	}

	/**
	 * 判断表是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		boolean isEmpty = true;
		Cursor myCursor = null;
		String selection = "SELECT * from " + tableBusinessList();
		try {
			myCursor = mDatabase.rawQuery(selection, null);
			if (myCursor.getCount() > 0) {
				isEmpty = false;
			}
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return isEmpty;
	}

	public void removeAll() {
		String sql = "DELETE FROM " + tableBusinessList() + " ;";
		mDatabase.execSQL(sql);
	}
	
	/**
	 * 清空表
	 */
	public void clean() {
		mDatabase.execSQL("DROP TABLE IF EXISTS " + tableBusinessList());
	}

	/**
	 * Inserts or update Json String to SQLite
	 * 
	 * @param json
	 * @throws JSONException
	 */
	public boolean insertFood(String json) throws JSONException {
		/*
		 * 返回值: 解析Json, 若Json包含空字段，异常处理
		 */
		String businesslistings = null;
		try {
			JSONObject obj = new JSONObject(json);
			businesslistings = obj.getString(TABLE_BUSINESSLIST + mFields);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (!businesslistings.equals(null)) {
			JSQLite jsonLite = new JSQLite(json, mDatabase);
			jsonLite.persist();
			return true;
		} else {
			DataOperations.toastMidShow(mContext, "数据服务发生异常，请联系开发人员，谢谢!!");
		}
		return false;
	}

	/**
	 * 通过ShopID从TABLE_BUSINESSLIST取得SQLite对应的列
	 * 
	 * @param ShopID
	 * @return Cursor
	 */
	public Cursor fetchShopByID(String ShopID) {
		Cursor myCursor = null;
		try {
			myCursor = mDatabase.query(tableBusinessList(),
					BUSINESSLIST_COLUMN, SHOPID + "='" + ShopID + "'", null,
					null, null, null);
			if (myCursor != null)
				myCursor.moveToFirst();
		} catch (Exception e) {
			
		} finally {
			
		}
		return myCursor;
	}

	/**
	 * 通过ShopID从TABLE_BUSINESSLIST取得SQLite对应列的Bundle
	 * fetchShopIndex()对应BUSINESSLIST_COLUMN的ShopID，该id唯一
	 * 
	 * @param ShopID
	 * @return Bundle
	 */
	public Bundle fetchShopIndex(String ShopID) {
		Bundle data = new Bundle();
		Cursor myCursor = null;
		try {
			myCursor = fetchShopByID(ShopID);
			for (String index : BUSINESSLIST_COLUMN)
				data.putString(index,
						myCursor.getString(myCursor.getColumnIndex(index)));
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return data;
	}

	/**
	 * 从TABLE_BUSINESSLIST获取对应fields的商家列表
	 * 
	 * @return List<Bundle>
	 */
	public List<Bundle> fetchShopIndex() {
		List<Bundle> list = new ArrayList<Bundle>();
		Cursor myCursor = null;
		String selection = "SELECT * from " + tableBusinessList();
		try {
			myCursor = mDatabase.rawQuery(selection, null);
			if (myCursor.moveToFirst()) {
				do {
					Bundle data = new Bundle();
					for (String index : BUSINESSLIST_COLUMN)
						data.putString(index, myCursor.getString(myCursor
								.getColumnIndex(index)));
					list.add(data);
				} while (myCursor.moveToNext());
			}
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return list;
	}


	/**
	 * 种类筛选
	 * 
	 * @param tag
	 * @return List<Bundle>
	 */
	public List<Bundle> fetchShopIndexByTag(String tag) {
		List<Bundle> list = new ArrayList<Bundle>();
		Cursor myCursor = null;
		String selection = "SELECT * from " + tableBusinessList() + " where "
				+ SHOPTAG + " = " + "'" + tag + "'";
		if (tag.equals("全部"))
			selection = "SELECT * from " + tableBusinessList();
		try {
			myCursor = mDatabase.rawQuery(selection, null);
			if (myCursor.moveToFirst()) {
				do {
					Bundle data = new Bundle();
					for (String index : BUSINESSLIST_COLUMN)
						data.putString(index, myCursor.getString(myCursor
								.getColumnIndex(index)));
					list.add(data);
				} while (myCursor.moveToNext());
			}
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return list;
	}

	/**
	 * 均价筛选
	 * @param low 
	 * @param high
	 * @return List<Bundle>
	 */
	public List<Bundle> fetchShopIndexByAvg(float low, float high) {
		List<Bundle> list = new ArrayList<Bundle>();
		Cursor myCursor = null;
		String selection = "SELECT * from " + tableBusinessList() + " where "
				+ SHOPAVG + " >= " + "'" + low + "' AND " + SHOPAVG + "<" + "'" + high + "'";
		try {
			myCursor = mDatabase.rawQuery(selection, null);
			if (myCursor.moveToFirst()) {
				do {
					Bundle data = new Bundle();
					for (String index : BUSINESSLIST_COLUMN)
						data.putString(index, myCursor.getString(myCursor
								.getColumnIndex(index)));
					list.add(data);
				} while (myCursor.moveToNext());
			}
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return list;
	}
	
	/**
	 * 忙闲筛选
	 * 
	 * @param isBusy
	 * @return List<Bundle>
	 */
	public List<Bundle> fetchShopIndexByBusyState(boolean isBusy) {
		List<Bundle> list = new ArrayList<Bundle>();
		Cursor myCursor = null;
		String selection = "SELECT * from " + tableBusinessList() + " where "
				+ BUSYSTATE + " = " + "'" + (isBusy ? "1" : "0") + "'";
		try {
			myCursor = mDatabase.rawQuery(selection, null);
			if (myCursor.moveToFirst()) {
				do {
					Bundle data = new Bundle();
					for (String index : BUSINESSLIST_COLUMN)
						data.putString(index, myCursor.getString(myCursor
								.getColumnIndex(index)));
					list.add(data);
				} while (myCursor.moveToNext());
			}
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return list;
	}
	
	/**
	 * 外卖筛选
	 * 
	 * @param isWai
	 * @return List<Bundle>
	 */
	public List<Bundle> fetchShopIndexByTakeOut(boolean isWai) {
		List<Bundle> list = new ArrayList<Bundle>();
		Cursor myCursor = null;
		String selection = "SELECT * from " + tableBusinessList() + " where "
				+ TAKEOUT + " = " + "'" + (isWai ? "1" : "0") + "'";
		try {
			myCursor = mDatabase.rawQuery(selection, null);
			if (myCursor.moveToFirst()) {
				do {
					Bundle data = new Bundle();
					for (String index : BUSINESSLIST_COLUMN)
						data.putString(index, myCursor.getString(myCursor
								.getColumnIndex(index)));
					list.add(data);
				} while (myCursor.moveToNext());
			}
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (myCursor != null)
				myCursor.close();
		}
		return list;
	}
}
