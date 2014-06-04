package com.xiaolong.xiaofanzhuo.fileio;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;

public class ThumbHandler extends Handler {

	private BasePictAdapter adapter = null;
	private Context context;

	public ThumbHandler(Context context, BasePictAdapter adapter) {
		this.adapter = adapter;
		this.context = context;
	}

	@Override
	public void handleMessage(Message msg) {
		System.out.println("ThumbHandler Received Message");
		switch (msg.what) {
		case 1:
			Bundle data = msg.getData();
			adapter.addPicture(data);
			toastLoading();
			break;
		case 2:
			MyApplication.getInstance().onLowMemory();
			adapter.clear();
			break;
		case 3:
			DataOperations.creatButtonClickDialog(context, "什么也不做..", "退出刷新..",
					"亲，网络错误，请检查您的网络或重新尝试.....");
			break;
		case 4:
			DataOperations.creatButtonClickDialog(context, "什么也不做..", "退出刷新..",
					"亲，您来迟了哦，店家删除了某道菜，您的购物车正在刷新中.....");
			break;
		case 5:
			DataOperations.creatNetworkSettingsDialog(context);
			break;
		default:
			break;
		}
	}

	public static boolean toastFrequency = false;

	public void toastLoading() {

		if (!toastFrequency && 100 <= adapter.getCount()) {
			DataOperations
					.toastMidShow(context, "亲，该店菜品超过100道~~\n\n若有轻微卡顿，请耐心等待加载!!");
			toastFrequency = true;
		}
	}
}
