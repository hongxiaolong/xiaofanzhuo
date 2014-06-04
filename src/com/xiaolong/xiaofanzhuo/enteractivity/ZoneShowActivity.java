package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.xiaolong.xiaofanzhuo.businessdetails.BussinessDetailActivity;
import com.xiaolong.xiaofanzhuo.businesslistings.BussinessListActivity;
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
	@SuppressWarnings("unused")
	private MyApplication myApplication;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// return true;//返回真表示返回键被屏蔽掉
			creatDialog();// 创建弹出的Dialog
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 弹出提示退出对话框
	 */
	private void creatDialog() {
		new AlertDialog.Builder(this)
				.setMessage("亲，您真的要退出小饭桌么?")
				.setPositiveButton("残忍退出",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								MyApplication.getInstance().exit();
							}
						})
				.setNegativeButton("再逛会儿", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 去除标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_zone_show);

		FileUtil.init();//设置文件缓存路径
		
		myApplication = (MyApplication) getApplication();// 获得我们的应用程序MyApplication

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
				Intent intent = new Intent();
     			intent.putExtra("area", "1");
				intent.setClass(ZoneShowActivity.this, BussinessListActivity.class);
				startActivity(intent);
			}
		});

		// personal information, login
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ZoneShowActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		imageButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("area", "2");
				intent.setClass(ZoneShowActivity.this, BussinessListActivity.class);
				startActivity(intent);
				finish();
			}
		});

		imageButton4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("area", "3");
				intent.setClass(ZoneShowActivity.this, BussinessListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
