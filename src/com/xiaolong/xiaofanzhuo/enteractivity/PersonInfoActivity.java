package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class PersonInfoActivity extends BaseActivity {

	private Button buttonBack;
	private Button buttonCart;
	private Button buttonLogout;
	private static final String TAG = "PersonInfoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_login_personinfo);
		
		Toast.makeText(PersonInfoActivity.this, "登录成功",
				Toast.LENGTH_SHORT).show();
		
		buttonBack = (Button) findViewById(R.id.register_button_back);
		buttonCart = (Button) findViewById(R.id.btn_cart);
		buttonLogout = (Button) findViewById(R.id.btn_logout);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PersonInfoActivity.this.finish();
			}
		});
		buttonCart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PersonInfoActivity.this, "敬请期待！",
						Toast.LENGTH_SHORT).show();
			}
		});
		buttonLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PersonInfoActivity.this, "已注销",
						Toast.LENGTH_SHORT).show();
				SharedPreferences sp = PersonInfoActivity.this
						.getSharedPreferences("xiaofanzhuologininfo",
								MODE_PRIVATE);
				sp.edit().putBoolean("KEEPPWD", false).commit();
				sp.edit().putBoolean("AUTOLOGIN", false).commit();
				sp.edit().putString("USERNAME", "").commit();
				sp.edit().putString("PASSWORD", "").commit();
				
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this,
						LoginActivity.class);
				startActivity(intent);
				PersonInfoActivity.this.finish();
			}
		});
	}

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
				.setNegativeButton("再逛会儿",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
}
