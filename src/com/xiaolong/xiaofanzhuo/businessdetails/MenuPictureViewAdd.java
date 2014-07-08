package com.xiaolong.xiaofanzhuo.businessdetails;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo.dataoperations.CartDatabaseAdapter;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo.myapplication.ShoppingAnimation;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * MenuPictureViewAdd
 * 
 * @author hongxiaolong
 * 
 */

public class MenuPictureViewAdd implements IViewAddAndEventSet {

	private BasePictAdapter adapter;
	private ShoppingAnimation mAnimation = null;
	@SuppressWarnings("unused")
	private Context mContext;

	public static void setChecked(Bundle data, boolean isChecked) {
		data.putBoolean("checked", isChecked);
	}

	public static boolean getChecked(Bundle data) {
		return data.getBoolean("checked");
	}

	public MenuPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;

	}

	public void animationInit(Context context) {
		if (null == mAnimation)
			mAnimation = new ShoppingAnimation(context,
					adapter.getCartBadgeView());
		this.mContext = context;
	}

	@Override
	public View addViewAndAddEvenet(final Context context, View convertView,
			final int position, final List<Bundle> list,
			boolean isMultiSelectMode) {

		animationInit(context);

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.hong_menu_infos_list,
					null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.news_pic);
			holder.contentView = (TextView) convertView
					.findViewById(R.id.news_title);
			holder.timeView = (TextView) convertView
					.findViewById(R.id.news_time);
			holder.tQView = (TextView) convertView
					.findViewById(R.id.news_quantity);
			DataOperations.setTypefaceForTextView(context, holder.contentView);
			DataOperations.setTypefaceForTextView(context, holder.timeView);
			DataOperations.setTypefaceForTextView(context, holder.tQView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Bundle data = list.get(position);

		holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, (int) Integer
						.valueOf(data.getString("Height"))));
		FileUtil.setImageSrc(holder.imageView, data.getString("FoodImgUrl"));
		holder.contentView.setText(data.getString("Food"));
		holder.timeView.setText(data.getString("FoodPrice") + "元");

		CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(context);
		dbHelper.open();
		int tQ = dbHelper.fetchNumByID(data.getString("_id"));
		dbHelper.close();

		final TextView tQView = holder.tQView;
		holder.imageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(context);
				dbHelper.open();
				int tQ = dbHelper.updateAfterClick(data, true);
				int cartQ = dbHelper.fetchTotalNumByID(data.getString("ShopID"));
				dbHelper.close();
				/*
				 * 第一次点击
				 */
				if (!getChecked(list.get(position))) {
					setChecked(list.get(position), true);
					tQView.setTextColor(Color.rgb(255, 99, 71));
					tQView.setText("已点: " + String.valueOf(tQ) + "份");
				} else if (getChecked(list.get(position))){
					tQView.setTextColor(Color.rgb(255, 99, 71));
					tQView.setText("已点: " + String.valueOf(tQ) + "份");
				}

				/*
				 * 更新购物车总量
				 */
				TextView cartQV = adapter.getCartBadgeView();
				cartQV.setText(String.valueOf(cartQ));

				/*
				 * 动画效果
				 */
				// mAnimation.setAnim(imgIcon, cartQ);

			}
		});

		if (tQ > 0) {
			setChecked(list.get(position), true);
		} else {
			setChecked(list.get(position), false);
		}
		if (getChecked(list.get(position))) {
			holder.tQView.setTextColor(Color.rgb(255, 99, 71));
			holder.tQView.setText("已点: " + String.valueOf(tQ) + "份");
		} else {
			holder.tQView.setTextColor(Color.rgb(0, 201, 87));
			holder.tQView.setText("敬请品尝");
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView contentView;
		TextView timeView;
		TextView tQView;
	}
}
