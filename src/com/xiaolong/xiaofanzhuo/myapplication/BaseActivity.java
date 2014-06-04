package com.xiaolong.xiaofanzhuo.myapplication;

import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.enteractivity.LoginActivity;

/**
 * BaseActivity
 * 
 * @author hongxiaolong
 * 
 */

public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(BaseActivity.this);
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
		MyApplication.getInstance().remove(BaseActivity.this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			MyApplication.getInstance().onLowMemory();
			BaseActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
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

	/*
	 * 广播：下载完成提示安装
	 */
	@SuppressWarnings("unused")
	private BroadcastReceiver mInstallBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("action.InstallNotification")) {
				/**************************/
			}
		}
	};

	/*
	 * 加载进度框
	 */
	protected MainFrameTask mMainFrameTask = null;

	public class MainFrameTask extends AsyncTask<Integer, String, Integer> {

		@SuppressWarnings("unused")
		private BaseActivity mainFrame = null;
		public CustomProgressDialog progressDialog = null;
		private int loading = 10;

		public MainFrameTask(BaseActivity mainFrame) {
			this.mainFrame = mainFrame;
		}

		/*
		 * 设置加载时间: seconds秒
		 */
		public void setLoadingTime(int seconds) {
			this.loading = seconds;
		}

		@Override
		protected void onCancelled() {
			stopProgressDialog();
			super.onCancelled();
		}

		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				Thread.sleep(this.loading * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			startProgressDialog();
		}

		@Override
		protected void onPostExecute(Integer result) {
			stopProgressDialog();
		}

		private void startProgressDialog() {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog
						.createDialog(BaseActivity.this);
				progressDialog.setMessage("加载中,请稍后...");
			}

			progressDialog.show();
		}

		private void stopProgressDialog() {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}

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

	/**
	 * 强制登录，增加注册量
	 * 
	 * @param context
	 * @return true 已登录过 false 强制登录
	 */
	public boolean forceLogin(Context context) {

		SharedPreferences sp = context.getSharedPreferences(
				"xiaofanzhuologininfo", MODE_PRIVATE);
		if (!sp.contains("ALREADLOGIN")) {
			if (!sp.getBoolean("ALREADLOGIN", false)) {
				/*
				 * 强制登录
				 */
				DataOperations.toastMidShow(context,
						"亲，请登录支持小饭桌喔~~~\n\n推广期间，礼品多多，全民疯抢!!");

				Intent intent = new Intent();
				intent.setClass(context, LoginActivity.class);
				startActivity(intent);
				MyApplication.getInstance().onLowMemory();
				return false;
			}
		}
		return true;
	}
}
