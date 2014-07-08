package com.xiaolong.xiaofanzhuo.historyorders;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.xiaolong.xiaofanzhuo.dataoperations.CartDatabaseAdapter;

/**
 * Adapts the database to deal with the front end.
 * 
 * @author Andrei
 * 
 */
public class HistoryOrderDatabaseAdapter {

	// Cart table name
	private static final String ORDER_TABLE_NAME = "xiaofanzhuoORDER";
	private static final String ORDER_DETAIL_TABLE_NAME = "xiaofanzhuoORDER_DETAIL";

	// Table unique id
	public static final String COL_ID = "_id";
	// Table food and quantity columns
	public static final String COL_SHOP_ID = "ShopID";
	public static final String COL_SHOP_NAME = "ShopName";
	public static final String COL_USER_ID = "UserID";
	public static final String COL_PRICE = "Price";
	public static final String COL_TIME = "Time";
	public static final String COL_FOOD_ID = "FoodID";
	public static final String COL_FOOD_NAME = "FoodName";
	public static final String COL_FOOD_IMA_URL = "FoodImgUrl";
	public static final String COL_FOOD_PRICE = "FoodPrice";
	public static final String COL_FOOD_NUM = "FoodNum";

	public static final String[] ORDER_COLUMNS = new String[] { COL_ID,
			COL_SHOP_ID, COL_SHOP_NAME, COL_USER_ID, COL_PRICE, COL_TIME, };

	public static final String[] ORDER_DETAIL_COLUMNS = new String[] { COL_ID,
			COL_FOOD_ID, COL_FOOD_NAME, COL_FOOD_NUM, COL_FOOD_IMA_URL,
			COL_FOOD_PRICE, };

	private Context context;
	private SQLiteDatabase database;
	private HistoryOrderDatabaseHelper dbHelper;

	/**
	 * The adapter constructor.
	 * 
	 * @param context
	 */
	public HistoryOrderDatabaseAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Creates the database helper and gets the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public HistoryOrderDatabaseAdapter open() throws SQLException {
		dbHelper = new HistoryOrderDatabaseHelper(context);
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
		database.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + ORDER_DETAIL_TABLE_NAME);
		System.out.println("DROP TABLE: " + ORDER_DETAIL_TABLE_NAME);
		System.out.println("DROP TABLE: " + ORDER_TABLE_NAME);
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private ContentValues createOrderTableContentValues(Bundle data) {

		ContentValues values = new ContentValues();
		values.put(COL_ID, data.getString(COL_ID));
		values.put(COL_SHOP_ID, data.getString(COL_SHOP_ID));
		values.put(COL_SHOP_NAME, data.getString(COL_SHOP_NAME));
		values.put(COL_USER_ID, data.getString(COL_USER_ID));
		values.put(COL_PRICE, data.getString(COL_PRICE));
		values.put(COL_TIME, data.getString(COL_TIME));
		return values;
	}

	/**
	 * INSERT
	 * 
	 * @param data
	 * @return
	 */
	public long insertOrder(Bundle data) {
		ContentValues initialValues = createOrderTableContentValues(data);
		return database.insert(ORDER_TABLE_NAME, null, initialValues);
	}

	private ContentValues createOrderDetailTableContentValues(Bundle data) {

		ContentValues values = new ContentValues();
		values.put(COL_ID, data.getString(COL_ID));
		values.put(COL_FOOD_ID, data.getString(COL_FOOD_ID));
		values.put(COL_FOOD_NAME, data.getString(COL_FOOD_NAME));
		values.put(COL_FOOD_NUM, data.getString(COL_FOOD_NUM));
		values.put(COL_FOOD_IMA_URL, data.getString(COL_FOOD_IMA_URL));
		values.put(COL_FOOD_PRICE, data.getString(COL_FOOD_PRICE));
		return values;
	}

	/**
	 * INSERT
	 * 
	 * @param data
	 * @return
	 */
	public long insertOrderDetail(Bundle data) {
		ContentValues initialValues = createOrderDetailTableContentValues(data);
		return database.insert(ORDER_DETAIL_TABLE_NAME, null, initialValues);
	}

	/**
	 * Retrieves the details of a specific user, given a food and quantity.
	 * 
	 * @param foodID
	 * @return
	 */
	public Cursor fetchOrder(String UserID) {
		Cursor myCursor = null;
		try {
			myCursor = database.query(ORDER_TABLE_NAME, ORDER_COLUMNS,
					COL_USER_ID + "='" + UserID + "'", null, null, null, null);
			if (myCursor != null)
				myCursor.moveToFirst();
		} catch (Exception e) {

		} finally {

		}
		return myCursor;
	}

	public Cursor fetchOrderDetail(String ID) {
		Cursor myCursor = null;
		try {
			myCursor = database.query(ORDER_DETAIL_TABLE_NAME,
					ORDER_DETAIL_COLUMNS, COL_ID + "='" + ID + "'", null, null,
					null, null);
			if (myCursor != null)
				myCursor.moveToFirst();
		} catch (Exception e) {

		} finally {

		}
		return myCursor;
	}

	/**
	 * 历史订单插入数据库，同时将订单转换为JSON格式的数据。
	 * @param data Bundle
	 * @param datas List<Bundle>
	 * @return
	 */
	public String insertFromCart(Bundle data, List<Bundle> datas) {

		try {
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();

			/*
			 * 记录订单信息
			 */
			Bundle b = new Bundle();
			String id = System.currentTimeMillis() + "";
			b.putString(COL_ID, id);
			b.putString(COL_SHOP_ID, data.getString(COL_SHOP_ID));
			b.putString(COL_SHOP_NAME, data.getString(COL_SHOP_NAME));
			b.putString(COL_USER_ID, data.getString(COL_USER_ID));
			b.putString(COL_PRICE, data.getString(COL_PRICE));
			b.putString(COL_TIME, data.getString(COL_TIME));
			insertOrder(b);
			
			/*
			 * 将订单信息加入JSON
			 */
			JSONObject orderObject = new JSONObject();
			orderObject.put(COL_ID, id);
			orderObject.put(COL_SHOP_ID, data.getString(COL_SHOP_ID));
			orderObject.put(COL_SHOP_NAME, data.getString(COL_SHOP_NAME));
			orderObject.put(COL_USER_ID, data.getString(COL_USER_ID));
			orderObject.put(COL_PRICE, data.getString(COL_PRICE));
			orderObject.put(COL_TIME, data.getString(COL_TIME));
			object.put("order", orderObject);

			/*
			 * 记录订单详情
			 */
			for (Bundle i : datas) {

				b = new Bundle();
				b.putString(COL_ID, id);
				b.putString(COL_FOOD_ID,
						i.getString(CartDatabaseAdapter.COL_FOOD_ID));
				b.putString(COL_FOOD_NAME,
						i.getString(CartDatabaseAdapter.COL_FOOD));
				b.putString(COL_FOOD_NUM,
						i.getString(CartDatabaseAdapter.COL_NUMBER));
				b.putString(COL_FOOD_IMA_URL,
						i.getString(CartDatabaseAdapter.COL_FOOD_IMG_URL));
				b.putString(COL_FOOD_PRICE,
						i.getString(CartDatabaseAdapter.COL_PRICE));
				insertOrderDetail(b);

				JSONObject detailObject = new JSONObject();
				detailObject.put(COL_ID, id);
				detailObject.put(COL_FOOD_ID,
						i.getString(CartDatabaseAdapter.COL_FOOD_ID));
				detailObject.put(COL_FOOD_NAME,
						i.getString(CartDatabaseAdapter.COL_FOOD));
				detailObject.put(COL_FOOD_NUM,
						i.getString(CartDatabaseAdapter.COL_NUMBER));
				detailObject.put(COL_FOOD_IMA_URL,
						i.getString(CartDatabaseAdapter.COL_FOOD_IMG_URL));
				detailObject.put(COL_FOOD_PRICE,
						i.getString(CartDatabaseAdapter.COL_PRICE));
				array.put(detailObject);

			}//for
			
			object.put("orderdetail", array);
			return object.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LinkedList<Map<String, Object>> getDataById(String id)
			throws Exception {
		Cursor myCursor = fetchOrder(id);
		if (myCursor == null)
			return null;
		else {
			LinkedList<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
			for (myCursor.moveToFirst(); !myCursor.isAfterLast(); myCursor
					.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				String _id = myCursor
						.getString(myCursor.getColumnIndex(COL_ID));
				map.put("name", myCursor.getString(myCursor
						.getColumnIndex(COL_SHOP_NAME)));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				Date d = sdf.parse(myCursor.getString(myCursor
						.getColumnIndex(COL_TIME)));
				SimpleDateFormat sdf2 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				map.put("date", sdf2.format(d));
				map.put("price",
						myCursor.getString(myCursor.getColumnIndex(COL_PRICE)));
				List<String> detail_name = new LinkedList<String>();
				List<String> detail_num = new LinkedList<String>();
				List<String> detail_price = new LinkedList<String>();
				Cursor tmp_cur = fetchOrderDetail(_id);
				for (tmp_cur.moveToFirst(); !tmp_cur.isAfterLast(); tmp_cur
						.moveToNext()) {
					Log.d("what the shit", tmp_cur.getString(tmp_cur
							.getColumnIndex(COL_FOOD_NAME)));
					detail_name.add(tmp_cur.getString(tmp_cur
							.getColumnIndex(COL_FOOD_NAME)));
					detail_num.add(tmp_cur.getString(tmp_cur
							.getColumnIndex(COL_FOOD_NUM)));
					detail_price.add(tmp_cur.getString(tmp_cur
							.getColumnIndex(COL_FOOD_PRICE)));
				}
				map.put("detail_name", detail_name);
				map.put("detail_num", detail_num);
				map.put("detail_price", detail_price);
				list.add(map);
			}

			Collections.sort(list, new MyComparator());
			return list;
		}
	}

	public class MyComparator implements Comparator {

		/**
		 * 实现compare
		 */
		public int compare(Object o1, Object o2) {
			Map<String, Object> p1 = (Map<String, Object>) o1;
			Map<String, Object> p2 = (Map<String, Object>) o2;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date d1 = sdf.parse((String) p1.get("date"));
				Date d2 = sdf.parse((String) p2.get("date"));
				return d2.compareTo(d1);
			} catch (Exception e) {
				return 0;
			}
		}

	}

}