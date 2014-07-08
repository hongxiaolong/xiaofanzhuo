package com.xiaolong.xiaofanzhuo.businesslistings;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class ResPictureViewAdd implements IViewAddAndEventSet {

	@SuppressWarnings("unused")
	private BasePictAdapter adapter;

	public ResPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View addViewAndAddEvenet(Context context, View convertView,
			int position, List<Bundle> list, boolean isMultiSelectMode) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.liu_vlist, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.img);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			holder.praisnum = (TextView) convertView
					.findViewById(R.id.praisnum);
			holder.eatnum = (TextView) convertView.findViewById(R.id.eatnum);

			DataOperations.setTypefaceForTextView(context, holder.name);
			DataOperations.setTypefaceForTextView(context, holder.info);
			DataOperations.setTypefaceForTextView(context, holder.praisnum);
			DataOperations.setTypefaceForTextView(context, holder.eatnum);

			holder.isfree = (ImageView) convertView.findViewById(R.id.isfree);
			holder.iswai = (ImageView) convertView.findViewById(R.id.iswai);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Bundle data = list.get(position);

		holder.name.setText(data.getString("ShopName"));
		holder.info.setText(data.getString("ShopTag"));
		holder.praisnum.setText("点赞人数：" + data.getString("PraiseNum"));
		holder.eatnum.setText("想吃人数:" + data.getString("NumofPeopleWant2Eat"));

		if (0 != Integer.valueOf(data.getString("SendFoodOut")))
			holder.iswai.setImageResource(R.drawable.waimai_s);
		else
			holder.iswai.setImageBitmap(null);

		if (0 != Integer.valueOf(data.getString("BusyState")))
			holder.isfree.setImageResource(R.drawable.busy);
		else
			holder.isfree.setImageResource(R.drawable.idle1_s);

		FileUtil.setImageSrc(holder.imageView, data.getString("ShopImgUrl"));

		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView name;
		TextView info;
		ImageView isfree;
		ImageView iswai;
		TextView praisnum;
		TextView eatnum;
	}

}
