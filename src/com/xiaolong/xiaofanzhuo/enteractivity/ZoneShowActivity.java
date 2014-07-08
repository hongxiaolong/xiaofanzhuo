package com.xiaolong.xiaofanzhuo.enteractivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loveplusplus.update.UpdateChecker;
import com.xiaolong.xiaofanzhuo.businesslistings.BusinessListActivity;
import com.xiaolong.xiaofanzhuo.fileio.FileUtil;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.ButtonClickEffect;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * ZoneShowActivity
 * 
 * @author hongxiaolong
 * 
 */

public class ZoneShowActivity extends BaseActivity {

	ImageButton imageButton1, imageButton2, imageButton3, imageButton4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(ZoneShowActivity.this);
		MyApplication.getInstance().onLowMemory();
		if (!MyApplication.getInstance().isPushedNotification()) {
			/*
			 * 检查更新，Android状态栏及提示框
			 */
			MyApplication.getInstance().setUpdateNotification();
			UpdateChecker.checkForDialog(ZoneShowActivity.this);
			UpdateChecker.checkForNotification(ZoneShowActivity.this);
		}

		// 去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_zone_show);

		FileUtil.init();// 设置文件缓存路径

		imageButton1 = (ImageButton) findViewById(R.id.img_dashanzi);
		imageButton2 = (ImageButton) findViewById(R.id.img_head);
		imageButton3 = (ImageButton) findViewById(R.id.img_798);
		imageButton4 = (ImageButton) findViewById(R.id.img_wangjing);

		ButtonClickEffect.setButtonFocusChanged(imageButton1);
		ButtonClickEffect.setButtonFocusChanged(imageButton2);
		ButtonClickEffect.setButtonFocusChanged(imageButton3);
		ButtonClickEffect.setButtonFocusChanged(imageButton4);

		imageButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isFastDoubleClick()) {
					return;
				}
				Intent intent = new Intent();
				// intent.putExtra("area", "1");
				intent.setClass(ZoneShowActivity.this,
						BusinessListActivity.class);
				intent.putExtra("area", "DASHANZI");
				intent.setClass(ZoneShowActivity.this,
						BusinessListActivity.class);
				startActivity(intent);
			}
		});

		// personal information, login
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isFastDoubleClick()) {
					return;
				}
				Intent intent = new Intent();
				intent.setClass(ZoneShowActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});

		imageButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isFastDoubleClick()) {
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("area", "798");
				intent.setClass(ZoneShowActivity.this,
						BusinessListActivity.class);
				startActivity(intent);
			}
		});

		imageButton4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Utils.isFastDoubleClick()) {
					return;
				}
				Toast.makeText(ZoneShowActivity.this, "暂未开放",
						Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent();
				// intent.putExtra("area", "WANGJING");
				// intent.setClass(ZoneShowActivity.this,
				// BusinessListActivity.class);
				// startActivity(intent);
			}
		});

	}

	public static class Utils {
		private static long lastClickTime;

		public static boolean isFastDoubleClick() {
			long time = System.currentTimeMillis();
			long timeD = time - lastClickTime;
			if (0 < timeD && timeD < 2000) {
				return true;
			}
			lastClickTime = time;
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			ExitApp();
		}
		return true;
	}

}
