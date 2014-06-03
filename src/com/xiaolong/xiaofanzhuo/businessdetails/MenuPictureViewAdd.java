package com.xiaolong.xiaofanzhuo.businessdetails;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo.dataoperations.GetResponseFromServerAction;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * MenuPictureViewAdd
 * 
 * @author hongxiaolong
 * 
 */

public class MenuPictureViewAdd implements IViewAddAndEventSet{

	@SuppressWarnings("unused")
	private BasePictAdapter adapter;

	public MenuPictureViewAdd(BasePictAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View addViewAndAddEvenet(Context context, View convertView,
			int position, List<Bundle> list, boolean isMultiSelectMode) {
			
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.hong_menu_infos_list, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.news_pic);
            holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Bundle data = list.get(position);
		final String id = data.getString("id");

		try {
			
			GetResponseFromServerAction reponse = new GetResponseFromServerAction();
			String foodName = reponse.getStringFromServerById(id + "_Food");
			String foodPrice = reponse.getStringFromServerById(id + "_FoodPrice");
			String foodUrl = reponse.getStringFromServerById(id + "_ImgUrl");
			String foodImageHeight = reponse.getStringFromServerById(id + "_Height");		
			
			//Bitmap map = FileUtil.loadBitmapFromCache(foodUrl);
		
	        holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) 100));
	        holder.contentView.setText(foodName + " ￥" + foodPrice);
	        //holder.imageView.setImageBitmap(map);
	        FileUtil.setImageSrc(holder.imageView, foodUrl);
	        
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}

	class ViewHolder {
        ImageView imageView;
        TextView contentView;
        TextView timeView;
    }
}
