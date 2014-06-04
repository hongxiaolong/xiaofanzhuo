package com.xiaolong.xiaofanzhuo.enteractivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class PersonInfoActivity extends BaseActivity {

	private ImageButton buttonBack;
	private ImageButton buttonHome;
	
	private ImageButton buttonLogout;
	private ImageButton buttonInvite;
	
	private TextView titleView;
	
	@SuppressWarnings("unused")
	private static final String TAG = "PersonInfoActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_login_personinfo);
		
		MyApplication.getInstance().addActivity(PersonInfoActivity.this);
		MyApplication.getInstance().onLowMemory();
		
		Toast.makeText(PersonInfoActivity.this, "登录成功",
				Toast.LENGTH_SHORT).show();
		
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(PersonInfoActivity.this, titleView);
		titleView.setText("个人中心");
		
		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		buttonLogout = (ImageButton) findViewById(R.id.btn_logout);
		buttonInvite  = (ImageButton) findViewById(R.id.btn_invite);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PersonInfoActivity.this.finish();
			}
		});
		
		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				PersonInfoActivity.this.finish();
			}

		});
		
		buttonInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PersonInfoActivity.this, "亲，您的手机号就是您的邀请号，请告知您的好友!",
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
				sp.edit().putBoolean("ALREADLOGIN", false).commit();
				
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this,
						LoginActivity.class);
				startActivity(intent);
				PersonInfoActivity.this.finish();
			}
		});
	}

}
