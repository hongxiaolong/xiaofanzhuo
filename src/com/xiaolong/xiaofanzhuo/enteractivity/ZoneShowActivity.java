package com.xiaolong.xiaofanzhuo.enteractivity;

import java.lang.ref.WeakReference;

import update.com.loveplusplus.update.UpdateChecker;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xiaolong.xiaofanzhuo.businesslistings.BusinessListActivity;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
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

		WeakReference<Bitmap> bitmap1 = new WeakReference<Bitmap>(readBitMap(
				this, R.drawable.hong_zone_dashanzi_hot));
		imageButton1.setImageBitmap(bitmap1.get());
		imageButton1.getBackground().setAlpha(100);
		WeakReference<Bitmap> bitmap2 = new WeakReference<Bitmap>(readBitMap(
				this, R.drawable.hong_zone_head_img));
		imageButton2.setImageBitmap(bitmap2.get());
		imageButton2.getBackground().setAlpha(100);
		WeakReference<Bitmap> bitmap3 = new WeakReference<Bitmap>(readBitMap(
				this, R.drawable.hong_zone_jiuxianqiao));
		imageButton3.setImageBitmap(bitmap3.get());
		imageButton3.getBackground().setAlpha(100);
		WeakReference<Bitmap> bitmap4 = new WeakReference<Bitmap>(readBitMap(
				this, R.drawable.hong_zone_798));
		imageButton4.setImageBitmap(bitmap4.get());
		imageButton4.getBackground().setAlpha(100);

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
				ZoneShowActivity.this.finish();
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
				DataOperations.toastMidShow(ZoneShowActivity.this, "敬请期待");
				// Intent intent = new Intent();
				// intent.putExtra("area", "WANGJING");
				// intent.setClass(ZoneShowActivity.this,
				// BusinessListActivity.class);
				// startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getInstance().remove(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
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

	private long exitTime = 0;

	public void ExitApp() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			MyApplication.getInstance().exit();
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
