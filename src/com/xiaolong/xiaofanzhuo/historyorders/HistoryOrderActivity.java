package com.xiaolong.xiaofanzhuo.historyorders;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo.enteractivity.LoginActivity;
import com.xiaolong.xiaofanzhuo.enteractivity.ZoneShowActivity;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class HistoryOrderActivity extends Activity {

	public static final String COL_ID = "_id";
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

	private ImageButton buttonBack;
	private ImageButton buttonHome;
	private TextView titleView;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_orders_activity_main);

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		titleView=(TextView) findViewById(R.id.detail_title);
		titleView.setText("历史订单");

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HistoryOrderActivity.this.finish();
			}
		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(HistoryOrderActivity.this,
						ZoneShowActivity.class);
				startActivity(intent);
				HistoryOrderActivity.this.finish();
			}

		});

		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);
		if (sp.contains(LoginActivity.USERNAME)) {

			HistoryOrderDatabaseAdapter dbHelper = new HistoryOrderDatabaseAdapter(
					HistoryOrderActivity.this);
			dbHelper.open();
			LinkedList<Map<String, Object>> data;
			try {
				data = dbHelper
						.getDataById(sp.getString(LoginActivity.USERNAME, null));
				dbHelper.close();

				ListView list = (ListView) findViewById(R.id.listView_order);
				MyAdapter adapter = new MyAdapter(this,data);
				list.setAdapter(adapter);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局 /*构造函数*/
		LinkedList<Map<String, Object>> data;

		public MyAdapter(Context context,LinkedList<Map<String, Object>> data) {
			this.mInflater = LayoutInflater.from(context);
			this.data = data;
		}

		@Override
		public int getCount() {

			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/* 书中详细解释该方法 */
		@SuppressWarnings("unchecked")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			convertView = mInflater.inflate(R.layout.history_orders_item, null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.order_detail = (LinearLayout) convertView
					.findViewById(R.id.detail_order);
			holder.name.setText(data.get(position).get("name").toString());
			holder.date.setText(data.get(position).get("date").toString());
			holder.price.setText("总价："
					+ data.get(position).get("price").toString() + "元");
			List<String> detail_name = (List<String>) data.get(position).get(
					"detail_name");
			List<String> detail_num = (List<String>) data.get(position).get(
					"detail_num");
			List<String> detail_price = (List<String>) data.get(position).get(
					"detail_price");
			for (int i = 0; i < detail_name.size(); i++) {

				LinearLayout layout = (LinearLayout) mInflater.inflate(
						R.layout.history_orders_detail_item, null);
				TextView name = (TextView) layout.findViewById(R.id.dish_name);
				TextView num = (TextView) layout.findViewById(R.id.dish_num);
				TextView price = (TextView) layout
						.findViewById(R.id.dish_price);
				name.setText(detail_name.get(i));
				num.setText("数量:" + detail_num.get(i) + "");
				price.setText("单价:" + detail_price.get(i) + "元");
				
				name.setTextColor(Color.rgb(0, 0, 0));  
				num.setTextColor(Color.rgb(0, 0, 0));  
				price.setTextColor(Color.rgb(0, 0, 0));  

				holder.order_detail.addView(layout);
			}
			return convertView;
		}

	}

	public final class ViewHolder {
		public TextView name;
		public TextView date;
		public TextView price;
		public LinearLayout order_detail;
	}

}
