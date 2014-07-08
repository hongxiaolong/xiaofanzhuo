package com.xiaolong.xiaofanzhuo.fileio;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiaolong.xiaofanzhuo.dataoperations.JSQLiteAdapter;

public class LoadingBusinessListFromLocal extends Thread {

	private Handler mHandler = null;
	private Context mContext;
	private volatile boolean isRun = true;
	private String mData = null;

	/**
	 * 
	 * @param context
	 * @param handler
	 *            对应Activity的handler
	 * @param fields
	 *            区域字段
	 * @param isShop
	 *            true 菜单 false 商家列表
	 * @param id
	 *            isShop==true id = ShopID
	 */
	public LoadingBusinessListFromLocal(Context context, Handler handler,
			String data) {
		this.mContext = context;
		this.mHandler = handler;
		this.mData = data;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(mContext, mData);
		jsonLiteAdapter.open();
		List<Bundle> datas = null;
		datas = jsonLiteAdapter.fetchShopIndex();
		jsonLiteAdapter.close();

		for (Bundle data : datas) {
			
			/*
			 * 终止线程时终止循环
			 */
			if (!isRun)
				break;
			
			FileUtil.getBitMapIfNecessary(data.getString("ShopImgUrl"));// 由图片url获取bitmap
			Message msg = mHandler.obtainMessage();
			msg.setData(data);
			msg.what = 1;
			mHandler.sendMessage(msg);
		}

	}

	public void setStop() {
		this.isRun = false;
	}

	public boolean isRun() {
		return isRun;
	}

}
