package com.xiaolong.xiaofanzhuo.enteractivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.historyorders.HistoryOrderActivity;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class PersonInfoActivity extends BaseActivity {

	private ImageButton buttonBack;
	private ImageButton button_history_order;

	private ImageButton buttonLogout;
	private ImageButton buttonInvite;

	private TextView titleView;

	@SuppressWarnings("unused")
	private static final String TAG = "PersonInfoActivity";
	
	public static final String USERNAME = "user_name";
	public static final String PASSWORD = "password";
	public static final String INVITED = "invited";
	public static final String REGTIME = "reg_time";
	public static final String LASTTIME = "last_login_time";
	public static final String KEEPPWD = "KEEPPWD";
	public static final String AUTOLOGIN = "AUTOLOGIN";
	public static final String ALREADLOGIN = "ALREADLOGIN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_login_personinfo);

		MyApplication.getInstance().addActivity(PersonInfoActivity.this);
		MyApplication.getInstance().onLowMemory();

		Toast.makeText(PersonInfoActivity.this, "登录成功", Toast.LENGTH_SHORT)
				.show();

		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(PersonInfoActivity.this,
				titleView);
		titleView.setText("个人中心");

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		button_history_order = (ImageButton) findViewById(R.id.btn_history_order);
		buttonLogout = (ImageButton) findViewById(R.id.button_logout);
		buttonInvite = (ImageButton) findViewById(R.id.btn_invite);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				PersonInfoActivity.this.finish();
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
				sp.edit().clear().commit(); 
//				sp.edit().remove(KEEPPWD);
//				sp.edit().remove(AUTOLOGIN);
//				sp.edit().remove(USERNAME);
//				sp.edit().remove(PASSWORD);
//				sp.edit().remove(ALREADLOGIN);
//				sp.edit().remove(REGTIME);
//				sp.edit().remove(LASTTIME);
//				sp.edit().remove(INVITED);
//				sp.edit().commit();
				
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this, LoginActivity.class);
				startActivity(intent);
				PersonInfoActivity.this.finish();
			}

		});

		buttonInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(PersonInfoActivity.this,
						"亲，您的手机号就是您的邀请号，请告知您的好友!", Toast.LENGTH_SHORT).show();
			}
		});
		button_history_order.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this, HistoryOrderActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			MyApplication.getInstance().onLowMemory();
			PersonInfoActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
