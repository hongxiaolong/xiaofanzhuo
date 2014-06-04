package com.xiaolong.xiaofanzhuo.dataoperations;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Adapts the database to deal with the front end.
 * 
 * @author Andrei
 * 
 */
public class DatabaseAdapter {

	// Cart table name
	private static final String CART_TABLE_NAME = "xiaofanzhuoCART";

	// Table unique id
	public static final String COL_ID = "_id";
	// Table food and quantity columns
	public static final String COL_SHOP_ID = "ShopID";
	public static final String COL_FOOD_ID = "FoodID";
	public static final String COL_NUMBER = "Number";
	public static final String COL_PRICE = "FoodPrice";
	public static final String COL_FOOD = "Food";
	public static final String COL_FOOD_IMG_URL = "FoodImgUrl";

	public static final String[] CART_COLUMNS = new String[] { COL_ID,
			COL_SHOP_ID, COL_FOOD_ID, COL_FOOD, COL_PRICE, COL_FOOD_IMG_URL,
			COL_NUMBER };

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	/**
	 * The adapter constructor.
	 * 
	 * @param context
	 */
	public DatabaseAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Creates the database helper and gets the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DatabaseAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * 清空表中的数据
	 */
	public void clean() {
		database.execSQL("DROP TABLE IF EXISTS " + CART_TABLE_NAME);
		System.out.println("DROP TABLE: " + CART_TABLE_NAME);
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private ContentValues createFoodTableContentValues(Bundle data) {

		ContentValues values = new ContentValues();
		values.put(COL_SHOP_ID, data.getString(COL_SHOP_ID));
		values.put(COL_FOOD_ID, data.getString(COL_FOOD_ID));
		values.put(COL_PRICE, data.getString(COL_PRICE));
		values.put(COL_FOOD, data.getString(COL_FOOD));
		values.put(COL_FOOD_IMG_URL, data.getString(COL_FOOD_IMG_URL));
		values.put(COL_NUMBER, data.getString(COL_NUMBER));
		return values;
	}

	/**
	 * INSERT
	 * 
	 * @param data
	 * @return
	 */
	public long insertFood(Bundle data) {
		ContentValues initialValues = createFoodTableContentValues(data);
		return database.insert(CART_TABLE_NAME, null, initialValues);
	}

	/**
	 * Removes a user's details given an id.
	 * 
	 * @param foodID
	 *            Food id.
	 * @return
	 */
	public boolean remove(String foodID) {
		return database.delete(CART_TABLE_NAME, COL_FOOD_ID + "=" + foodID,
				null) > 0;
	}

	public boolean removeAllByID(String[] shopID) {
		return database.delete(CART_TABLE_NAME, COL_SHOP_ID + "= ?", shopID) > 0;
	}

	/**
	 * Retrieves the details of a specific user, given a food and quantity.
	 * 
	 * @param foodID
	 * @return
	 */
	public Cursor fetchFood(String foodID) {
		Cursor myCursor = null;
		try {
			myCursor = database.query(CART_TABLE_NAME, CART_COLUMNS,
					COL_FOOD_ID + "='" + foodID + "'", null, null, null, null);
			if (myCursor != null)
				myCursor.moveToFirst();
		} catch (Exception e) {

		} finally {

		}
		return myCursor;
	}

	/**
	 * Retrieves the details of all the users stored in the login table.
	 * 
	 * @return
	 */
	public Cursor fetchCartByID(String shopID) {
		Cursor cursor = null;
		try {
			cursor = database.query(CART_TABLE_NAME, CART_COLUMNS, COL_SHOP_ID
					+ "='" + shopID + "'", null, null, null, null);
		} catch (Exception e) {

		} finally {

		}
		return cursor;
	}

	/**
	 * 查询数据库中指定物品的总数量
	 * 
	 * @param foodID
	 * @return
	 */
	public int fetchNumByID(String foodID) {
		Cursor cur = null;
		int num = 0;
		try {
			cur = fetchFood(foodID);
			if (cur == null) {
				System.out.println("Database query error");
			} else {
				String fetch = cur.getString(cur.getColumnIndex(COL_NUMBER));
				num = (fetch == null) ? 0 : Integer.valueOf(fetch);
				cur.close();
			}
		} catch (Exception e) {

		} finally {
			if (cur != null)
				cur.close();
		}
		return num;
	}

	/**
	 * 
	 * @return 查询数据库中所有物品的总价
	 */
	public Bundle fetchDataByID(String shopID) {
		int tQ = 0;
		double tP = 0;
		Cursor cursor = null;
		try {
			cursor = fetchCartByID(shopID);
			while (cursor.moveToNext()) {
				String tQuantity = cursor.getString(cursor
						.getColumnIndex(COL_NUMBER));
				String tPrice = cursor.getString(cursor
						.getColumnIndex(COL_PRICE));
				tQ += Integer.valueOf(tQuantity);
				tP += Integer.valueOf(tQuantity) * Double.parseDouble(tPrice);
			}
			// 中间省略某些出现异常的代码
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (cursor != null)
				cursor.close();
		}
		Bundle data = new Bundle();
		data.putString("TQ", String.valueOf(tQ));
		data.putString(
				"TP",
				tP == 0 ? "0.00" : DataOperations.padDoubleLeft(new Double(tP),
						10, 2));
		return data;
	}

	/**
	 * 
	 * @param data
	 * @param symbol
	 *            true "+"; false "-"
	 * @return 点击后返回的实际数量
	 */
	public int updateAfterClick(Bundle data, boolean symbol) {

		// 增加一个FoodID字段以匹配xiaofanzhuoCART表, FoodID=_id
		if (!data.containsKey(COL_FOOD_ID))
			data.putString("FoodID", data.getString(COL_ID));
		int count = 0;
		Cursor cur = null;
		try {
			while (true) {
				// check exist
				cur = fetchFood(data.getString(COL_FOOD_ID));
				if (cur == null) {
					System.out.println("Database query error");
					break;
				} else {
					if (cur.getCount() > 0) {

						System.out.println("点击前: <<<<<<<"
								+ data.getString(COL_NUMBER));

						count = Integer.valueOf(cur.getString(cur
								.getColumnIndex(COL_NUMBER)))
								+ (symbol ? 1 : -1);

						System.out.println("点击后: >>>>>>>" + count);

						// remove food
						if (count <= 0) {
							remove(data.getString(COL_FOOD_ID));
							count = 0;
							break;
						}

						data.putString(COL_NUMBER, String.valueOf(count));
						// update
						ContentValues updateValues = createFoodTableContentValues(data);
						database.update(
								CART_TABLE_NAME,
								updateValues,
								COL_FOOD_ID + "=" + data.getString(COL_FOOD_ID),
								null);
						break;
					}
				}

				// Create the new food
				count = 1;
				data.putString(COL_NUMBER, String.valueOf(count));
				long id = insertFood(data);
				if (id > 0) {
					System.out.println("The food is already inserted to cart!");
				} else {
					System.out.println("Failed to inserted to cart!");
				}
				break;
			}// while
				// 中间省略某些出现异常的代码
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (cur != null)
				cur.close();
		}
		return count;
	}

	/**
	 * 
	 * @return 查询数据库中所有物品的总数量
	 */
	public int fetchTotalNumByID(String shopID) {
		int tQ = 0;
		Cursor cursor = null;
		try {
			cursor = fetchCartByID(shopID);
			while (cursor.moveToNext()) {
				String tQuantity = cursor.getString(cursor
						.getColumnIndex(COL_NUMBER));
				tQ = tQ + Integer.valueOf(tQuantity);
			}
			// 中间省略某些出现异常的代码
		} catch (Exception e) {
			// 出现异常
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return tQ;
	}

	/**
	 * 订单页：查询购物车中商品列表
	 * 
	 * @param shopID
	 * @return
	 */
	public List<Bundle> fetchDataListByID(String shopID) {
		List<Bundle> datas = new ArrayList<Bundle>();
		Cursor cursor = null;
		try {
			cursor = fetchCartByID(shopID);
			while (cursor.moveToNext()) {
				Bundle data = new Bundle();
				for (String i : CART_COLUMNS) {
					data.putString(i,
							cursor.getString(cursor.getColumnIndex(i)));
				}
				datas.add(data);
			}
		} catch (Exception e) {

		} finally {
			if (cursor != null)
				cursor.close();
		}
		return datas;
	}

}