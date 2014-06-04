package com.xiaolong.xiaofanzhuo.businesslistings;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class PopMenu {
	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;

	// private OnItemClickListener listener;

	@SuppressWarnings("deprecation")
	public PopMenu(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		itemList = new ArrayList<String>(5);

		View view = LayoutInflater.from(context)
				.inflate(R.layout.liu_popmenu, null);

		listView = (ListView) view.findViewById(R.id.listView);
		listView.setAdapter(new PopAdapter());
		listView.setFocusableInTouchMode(true);
		listView.setFocusable(true);

		popupWindow = new PopupWindow(view, 100, LayoutParams.WRAP_CONTENT);
		popupWindow = new PopupWindow(view, context.getResources()
				.getDimensionPixelSize(R.dimen.popmenu_width),
				LayoutParams.WRAP_CONTENT);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		// this.listener = listener;
		listView.setOnItemClickListener(listener);
	}

	public void addItems(String[] items) {
		for (String s : items)
			itemList.add(s);
	}

	public void addItem(String item) {
		itemList.add(item);
	}

	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent,
				10,
				context.getResources().getDimensionPixelSize(
						R.dimen.popmenu_yoff));
popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
	}

	public void dismiss() {
		popupWindow.dismiss();
	}

	private final class PopAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.liu_pomenu_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.textView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.groupItem.setText(itemList.get(position));

			return convertView;
		}

		private final class ViewHolder {
			TextView groupItem;
		}
	}
}
