package com.xiaolong.xiaofanzhuo.fileio;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiaolong.xiaofanzhuo.dataoperations.CartDatabaseAdapter;

public class DownloadOrderFromSQLite extends Thread {

	private Handler mHandler = null;
	private Context mContext;
	private String mShopID= null;
	private volatile boolean isRun = true;

	/**
	 * 
	 * @param context
	 * @param handler
	 * @param id
	 */
	public DownloadOrderFromSQLite(Context context, Handler handler, String id) {
		this.mContext = context;
		this.mHandler = handler;
		this.mShopID = id;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		final CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(mContext);
		dbHelper.open();
		List<Bundle> datas = dbHelper.fetchDataListByID(mShopID);
		dbHelper.close();

		for (Bundle data : datas) {
			
			/*
			 * 终止线程时终止循环
			 */
			if (!isRun)
				break;
			
			FileUtil.getBitMapIfNecessary(data.getString("FoodImgUrl"));// 由图片url获取bitmap
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
