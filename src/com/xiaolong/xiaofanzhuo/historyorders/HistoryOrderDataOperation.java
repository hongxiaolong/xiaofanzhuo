package com.xiaolong.xiaofanzhuo.historyorders;

import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.xiaolong.xiaofanzhuo.dataoperations.CartDatabaseAdapter;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;

public class HistoryOrderDataOperation {

	public static List<Bundle> getDatasFromCart(Context context,String shopID) {
		//CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(MyApplication.getInstance().getApplicationContext());
		CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(context);
		dbHelper.open();
		List<Bundle> datas = dbHelper.fetchDataListByID(shopID);
		dbHelper.close();
		return datas;
	}

}
