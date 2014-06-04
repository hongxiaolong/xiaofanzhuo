package com.xiaolong.xiaofanzhuo.businessdetails;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import pla.com.dodola.model.DuitangInfo;
import pla.com.dodowaterfall.widget.ScaleImageView;
import pla.com.example.android.bitmapfun.util.ImageFetcher;
import pla.me.maxwin.view.XListView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class StaggeredAdapter extends BaseAdapter {

	private Context mContext;
	private LinkedList<DuitangInfo> mInfos;
	private XListView mListView;
	private ImageFetcher mImageFetcher = null;

	public StaggeredAdapter(Context context, XListView xListView, ImageFetcher imageFetcher) {
		mContext = context;
		mInfos = new LinkedList<DuitangInfo>();
		mListView = xListView;
		mImageFetcher = imageFetcher;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public Bitmap readBitMap(Context context, int resId) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);

	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {

		ViewHolder holder;
		final DuitangInfo duitangInfo = mInfos.get(position);

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(parent
					.getContext());
			convertView = layoutInflator.inflate(R.layout.infos_list, null);
			holder = new ViewHolder();
			holder.imageView = (ScaleImageView) convertView
					.findViewById(R.id.news_pic);

			holder.cartView = (ImageView) convertView
					.findViewById(R.id.cart_pic);
			holder.contentView = (TextView) convertView
					.findViewById(R.id.news_title);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		holder.imageView.setImageWidth(duitangInfo.getWidth());
		holder.imageView.setImageHeight(duitangInfo.getHeight());
		holder.contentView.setText(duitangInfo.getMsg());
		mImageFetcher.loadImage(duitangInfo.getIsrc(), holder.imageView);

		final ImageView imageCart = holder.cartView;
		holder.cartView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (!duitangInfo.getSelected()) {
					WeakReference<Bitmap> bitmapSelected = new WeakReference<Bitmap>(
							readBitMap(mContext,
									R.drawable.small_icon_cart_selected));
					imageCart.setImageBitmap(bitmapSelected.get());
					duitangInfo.setSelected(true);
				}

			}
		});

		/*
		 * 解决滑动时选中状态的混乱必须把这段话放在setOnClickListener之后
		 */
		if (!duitangInfo.getSelected()) {
			WeakReference<Bitmap> bitmap = new WeakReference<Bitmap>(
					readBitMap(mContext, R.drawable.small_icon_cart));
			holder.cartView.setImageBitmap(bitmap.get());
		} else {
			WeakReference<Bitmap> bitmapSelected = new WeakReference<Bitmap>(
					readBitMap(mContext,
							R.drawable.small_icon_cart_selected));
			holder.cartView.setImageBitmap(bitmapSelected.get());
		}

		return convertView;
	}

	class ViewHolder {
		ScaleImageView imageView;
		ImageView cartView;
		TextView contentView;
		TextView timeView;
	}

	@Override
	public int getCount() {
		return mInfos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mInfos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public void addItemLast(List<DuitangInfo> datas) {
		mInfos.addAll(datas);
	}

	public void addItemTop(List<DuitangInfo> datas) {
		for (DuitangInfo info : datas) {
			mInfos.addFirst(info);
		}
	}

}
