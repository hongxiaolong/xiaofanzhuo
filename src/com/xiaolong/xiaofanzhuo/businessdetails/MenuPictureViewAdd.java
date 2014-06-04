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

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.DatabaseAdapter;
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

	public MenuPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;

	}

	public void animationInit(Context context) {
		if (null == mAnimation)
			mAnimation = new ShoppingAnimation(context,
					adapter.getCartBadgeView());
		this.mContext= context;
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

		DatabaseAdapter dbHelper = new DatabaseAdapter(context);
		dbHelper.open();
		int tQ = dbHelper.fetchNumByID(data.getString("_id"));
		dbHelper.close();

		if (tQ > 0) {
			holder.tQView.setTextColor(Color.rgb(255, 99, 71));
			holder.tQView.setText("已点: " + String.valueOf(tQ) + "份");
		} else if (tQ == 0)
			holder.tQView.setText("敬请品尝");

		//final ImageView imgIcon = holder.imageView;
		final TextView tQView = holder.tQView;
		//data.putBoolean("frequently", false);
		holder.imageView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// 限制只能点击一次，否则容易造成图片动画后残留
				//if (true == data.getBoolean("frequently"))
				//	return;
				data.putBoolean("frequently", true);
				DatabaseAdapter dbHelper = new DatabaseAdapter(context);
				dbHelper.open();
				int tQ = dbHelper.updateAfterClick(data, true);
				int cartQ = dbHelper.fetchTotalNumByID(data.getString("ShopID"));
				tQView.setTextColor(Color.rgb(255, 99, 71));
				tQView.setText("已点: " + String.valueOf(tQ) + "份");
				dbHelper.close();
				
				/*
				 * 更新购物车总量
				 */
				TextView cartQV = adapter.getCartBadgeView();
				cartQV.setText(String.valueOf(cartQ));
				
				/*
				 * 动画效果
				 */
				//mAnimation.setAnim(imgIcon, cartQ);

			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView contentView;
		TextView timeView;
		TextView tQView;
	}
}
