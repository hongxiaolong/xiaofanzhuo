package com.xiaolong.xiaofanzhuo.businessorders;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.DatabaseAdapter;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/*
 * OrderPictureViewAdd
 * @author hongxiaolong
 */

public class OrderPictureViewAdd implements IViewAddAndEventSet {

	private BasePictAdapter adapter;

	public OrderPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View addViewAndAddEvenet(final Context context, View convertView,
			int position, List<Bundle> list, boolean isMultiSelectMode) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.hong_menu_order_item,
					null);

			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.order_image_view);
			holder.textTitle = (TextView) convertView
					.findViewById(R.id.menu_name);
			holder.textPrice = (TextView) convertView
					.findViewById(R.id.menu_price);

			holder.buttonAdd = (Button) convertView
					.findViewById(R.id.button_add);
			holder.buttonDel = (Button) convertView
					.findViewById(R.id.button_delete);
			holder.textAmount = (TextView) convertView
					.findViewById(R.id.menu_amount);

			DataOperations.setTypefaceForTextView(context, holder.textTitle);
			DataOperations.setTypefaceForTextView(context, holder.textPrice);
			DataOperations.setTypefaceForTextView(context, holder.textAmount);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Bundle data = list.get(position);

		FileUtil.setImageSrc(holder.imageView,
				data.getString(DatabaseAdapter.COL_FOOD_IMG_URL));
		holder.textTitle.setText(data.getString(DatabaseAdapter.COL_FOOD));
		holder.textPrice.setText(data.getString(DatabaseAdapter.COL_PRICE)
				+ "元");

		final TextView quantityView = holder.textAmount;
		quantityView.setText(data.getString(DatabaseAdapter.COL_NUMBER));
		/*
		 * 增减按钮逻辑
		 */
		holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				DatabaseAdapter dbHelper = new DatabaseAdapter(context);
				dbHelper.open();
				int num = dbHelper.updateAfterClick(data, true);
				int cartQ = dbHelper.fetchTotalNumByID(data.getString("ShopID"));
				dbHelper.close();

				data.putString(DatabaseAdapter.COL_NUMBER, String.valueOf(num));
				quantityView.setText(String.valueOf(num));
				
				/*
				 * 更新购物车总量
				 */
				TextView cartQV = adapter.getCartBadgeView();
				cartQV.setText(String.valueOf(cartQ));
				
				/*
				 * 更新总价
				 */
				Intent intent = new Intent();
				intent.setAction("action.updateOrderUI");
				context.sendBroadcast(intent);
				
				return;
				
			}
		});
		holder.buttonDel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				DatabaseAdapter dbHelper = new DatabaseAdapter(context);
				dbHelper.open();
				int num = dbHelper.updateAfterClick(data, false);
				int cartQ = dbHelper.fetchTotalNumByID(data.getString("ShopID"));
				dbHelper.close();
				
				if (0 < num) {	
					data.putString(DatabaseAdapter.COL_NUMBER, String.valueOf(num));
					quantityView.setText(String.valueOf(num));
				} else {
					adapter.removePicture(data);
				}

				/*
				 * 更新购物车总量
				 */
				TextView cartQV = adapter.getCartBadgeView();
				cartQV.setText(String.valueOf(cartQ));
				
				/*
				 * 更新总价
				 */
				Intent intent = new Intent();
				intent.setAction("action.updateOrderUI");
				context.sendBroadcast(intent);
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView textTitle;
		TextView textPrice;
		Button buttonAdd;
		Button buttonDel;
		TextView textAmount;
	}
}
