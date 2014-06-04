package com.xiaolong.xiaofanzhuo.fileio;

import java.util.List;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.GetResponseFromServerAction;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DownloadPict extends Thread {
	private Handler mHandler = null;
	private String mRequesetCode = null;
	private volatile boolean isRun = true;
	
	public DownloadPict(Handler handler, String mRequesetCode) {
		super();
		this.mHandler = handler;
		this.mRequesetCode = mRequesetCode;
		setName(this.getClass().getSimpleName());
	}
	
	@Override
	/*
	 *该函数实现从Url（或者缓存）下载图片包含缩略图到pict中
	 */
	public void run() {
		GetResponseFromServerAction action = new GetResponseFromServerAction();
		List<String> ids = null;
		try {
			ids = action.getStringListFromServerById(mRequesetCode);
			Message msgClear = mHandler.obtainMessage();
			msgClear.what = 2;
			mHandler.sendMessage(msgClear);
			for (String f : ids) {
				String imageUrl = action.getStringFromServerById(f + "_ImgUrl");
				@SuppressWarnings("unused")
				Bitmap map = FileUtil.getBitMapIfNecessary(DataOperations.getActualString(imageUrl));//由图片url获取bitmap
				Message msg = mHandler.obtainMessage();
				Bundle data = new Bundle();
				data.putString("id", f);//把唯一标识符传过去
				msg.setData(data);
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setStop() {
		this.isRun = false;
	}
	
	public boolean isRun() {
		return isRun;
	}
	
}
