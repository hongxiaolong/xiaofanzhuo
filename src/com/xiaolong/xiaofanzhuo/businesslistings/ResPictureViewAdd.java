package com.xiaolong.xiaofanzhuo.businesslistings;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.GetResponseFromServerAction;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.fileio.IViewAddAndEventSet;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class ResPictureViewAdd implements IViewAddAndEventSet{

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
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.liu_vlist, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.praisnum = (TextView) convertView.findViewById(R.id.praisnum);
            holder.eatnum = (TextView) convertView.findViewById(R.id.eatnum);
            holder.isfree = (ImageView) convertView.findViewById(R.id.isfree);
            holder.iswai = (ImageView) convertView.findViewById(R.id.iswai);
            
            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Bundle data = list.get(position);
		final String id = data.getString("id");

		try {
			
			GetResponseFromServerAction reponse = new GetResponseFromServerAction();
			String name = reponse.getStringFromServerById(id + "_ShopName");
			String info = reponse.getStringFromServerById(id + "_ShopTag");
			String foodUrl = reponse.getStringFromServerById(id + "_ImgUrl");
			String praisnum = reponse.getStringFromServerById(id + "_PraiseNum");
			String eatnum = reponse.getStringFromServerById(id + "_NumofPeopleWant2Eat");
			boolean iswai;
			String temp = reponse.getStringFromServerById(id + "_SendFoodOut");
			if(temp.contains("不"))
				iswai=false;
			else
				iswai=true;
			boolean isfree;
			temp = reponse.getStringFromServerById(id + "_BusyState");
			if(temp.contains("空"))
				isfree=true;
			else
				isfree=false;
			//Bitmap map = FileUtil.loadBitmapFromCache(foodUrl);
		
	        //holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) 100));
	        holder.name.setText(name);
	        holder.info.setText(info);
	        holder.eatnum.setText(eatnum);
	        holder.praisnum.setText(praisnum);
	        if(isfree)
	        	holder.isfree.setImageResource(R.drawable.idle1_s);
	        else
	        	holder.isfree.setImageResource(R.drawable.busy);
	        if(iswai)
	        	holder.iswai.setImageResource(R.drawable.waimai_s);
	        else
	        	holder.iswai.setImageBitmap(null);
	        //holder.imageView.setImageBitmap(map);
	        FileUtil.setImageSrc(holder.imageView, DataOperations.getActualString(foodUrl));
	        
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
        TextView name;
        TextView info;
        ImageView isfree;
        ImageView iswai;
        TextView praisnum;
        TextView eatnum;
    }
}
