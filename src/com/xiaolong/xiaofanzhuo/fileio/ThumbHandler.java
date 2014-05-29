package com.xiaolong.xiaofanzhuo.fileio;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ThumbHandler extends Handler {
	
	private BasePictAdapter adapter = null;
	
	public ThumbHandler(BasePictAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public void handleMessage(Message msg) {
		System.out.println("ThumbHandler Received Message");
		switch (msg.what) {
		case 1:
			Bundle data = msg.getData();
			adapter.addPicture(data);
			break;
		case 2:
//			adapter.clear();
			break;
		default:
			break;
		}
	}
}
