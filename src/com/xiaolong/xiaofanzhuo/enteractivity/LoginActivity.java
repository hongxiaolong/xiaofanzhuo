package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaolong.xiaofanzhuo.dataoperations.GetResponseFromServerAction;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * LoginActivity
 * 
 * @author hongxiaolong
 * 
 */

public class LoginActivity extends BaseActivity {

	private static final String TAG = "LoginActivity";
	static final int CANSHU = 1;
	private EditText edTextUser, edTextSecretCode;
	private Button buttonLogin;
	private Button buttonRegister;
	private Button buttonBack;
	private String userName = "";
	private String password = "";
	private CheckBox keepPassword;
	private CheckBox autoLogin;
	private SharedPreferences sp;
	private boolean autoLoginFlag = false;
	private boolean keepPwdFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_login);

		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);

		edTextUser = (EditText) findViewById(R.id.edittext_user);
		edTextSecretCode = (EditText) findViewById(R.id.edittext_secretcode);
		keepPassword = (CheckBox) findViewById(R.id.checkbox_secretcode);
		autoLogin = (CheckBox) findViewById(R.id.checkbox_autologin);
		buttonLogin = (Button) findViewById(R.id.button_login);
		buttonRegister = (Button) findViewById(R.id.button_regeist);

		buttonBack = (Button) findViewById(R.id.register_button_back);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginActivity.this.finish();
			}

		});

		checkAutoLogin();
		checkKeepPwd();

		buttonLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				userName = edTextUser.getText().toString();
				password = edTextSecretCode.getText().toString();
				
				if (userName.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入手机号",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (password.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
					return;
				}		
				
				AccountAuthentication authentication = new AccountAuthentication();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);
				if (!authentication.checkup(userName, password, builder))
					return;

				try {
					String requestCode = "WhiteLogin____" + userName + "_"
							+ password;

					GetResponseFromServerAction reponse = new GetResponseFromServerAction();
					String ret = reponse.getStringFromServerById(requestCode);
					if (ret.contains("WhiteLogin_Result____UsernameTRUE_PasswdTRUE")) {

						Toast.makeText(LoginActivity.this, "登录成功!",
								Toast.LENGTH_SHORT).show();

						if (true == autoLoginFlag || true == keepPwdFlag) {
							sp.edit().putString("USERNAME", userName).commit();
							sp.edit().putString("PASSWORD", password).commit();
						}

						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								PersonInfoActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
						return;
					}
					if (ret.contains("WhiteLogin_Result____UsernameFALSE_PasswdFALSE")) {
						Toast.makeText(LoginActivity.this, "登录失败，用户名或密码错误!",
								Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(LoginActivity.this, "亲，网络不给力，请稍后!",
							Toast.LENGTH_SHORT).show();
					Log.i(TAG, "登录服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		keepPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (keepPassword.isChecked()) {
					keepPwdFlag = true;
					sp.edit().putBoolean("KEEPPWD", true).commit();
				} else {
					sp.edit().putBoolean("KEEPPWD", false).commit();
				}

			}
		});

		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (autoLogin.isChecked()) {
					autoLoginFlag = true;
					keepPassword.setChecked(true);
					sp.edit().putBoolean("AUTOLOGIN", true).commit();
				} else {
					sp.edit().putBoolean("AUTOLOGIN", false).commit();
				}

			}
		});

		buttonRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				LoginActivity.this.startActivity(intent);
				// LoginActivity.this.finish();
			}

		});
	}

	private void checkAutoLogin() {

		if (sp.contains("KEEPPWD") && sp.contains("AUTOLOGIN")
				&& sp.contains("USERNAME") && sp.contains("PASSWORD")) {

			boolean autoFlag = sp.getBoolean("AUTOLOGIN", false);
			if (true == autoFlag) {
				autoLogin.setChecked(true);
				keepPwdFlag = true;
				autoLoginFlag = true;
				try {
					String user = sp.getString("USERNAME", "");
					String pwd = sp.getString("PASSWORD", "");
					String requestCode = "WhiteLogin____" + user + "_" + pwd;
					GetResponseFromServerAction reponse = new GetResponseFromServerAction();
					String ret = reponse.getStringFromServerById(requestCode);
					if (ret.contains("WhiteLogin_Result____UsernameTRUE_PasswdTRUE")) {
						Toast.makeText(LoginActivity.this, "自动登录!",
								Toast.LENGTH_SHORT).show();

						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,
								PersonInfoActivity.class);
						startActivity(intent);
						LoginActivity.this.finish();
						return;
					}
					if (ret.contains("WhiteLogin_Result____UsernameFALSE_PasswdFALSE")) {
						Toast.makeText(LoginActivity.this, "登录失败，用户名或密码错误!",
								Toast.LENGTH_SHORT).show();
						return;
					}
					Log.i(TAG, "登录服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return;
	}

	private void checkKeepPwd() {

		if (sp.contains("KEEPPWD") && sp.contains("USERNAME")
				&& sp.contains("PASSWORD")) {
			boolean keepFlag = sp.getBoolean("KEEPPWD", false);
			if (true == keepFlag) {
				keepPassword.setChecked(true);
				keepPwdFlag = true;
				String user = sp.getString("USERNAME", "");
				String pwd = sp.getString("PASSWORD", "");
				edTextUser.setText(user);
				edTextSecretCode.setText(pwd);
			}
		}
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
