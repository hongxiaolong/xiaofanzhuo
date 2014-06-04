package com.xiaolong.xiaofanzhuo.enteractivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.ButtonClickEffect;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * LoginActivity
 * 
 * @author hongxiaolong
 * 
 */

public class LoginActivity extends BaseActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "LoginActivity";

	public static final String USERNAME = "user_name";
	public static final String PASSWORD = "password";
	public static final String INVITED = "invited";
	public static final String REGTIME = "reg_time";
	public static final String LASTTIME = "last_login_time";
	public static final String KEEPPWD = "KEEPPWD";
	public static final String AUTOLOGIN = "AUTOLOGIN";
	public static final String ALREADLOGIN = "ALREADLOGIN";

	private EditText edTextUser, edTextSecretCode;
	private Button buttonLogin;
	private Button buttonRegister;
	private Button buttonForget;
	private TextView titleView;

	private String userName = null;
	private String password = null;
	private CheckBox keepPassword;
	private CheckBox autoLogin;
	private SharedPreferences sp;
	private ImageButton buttonBack;
	private ImageButton buttonHome;

	private AsyncHttpClient Down = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hong_login);

		MyApplication.getInstance().addActivity(LoginActivity.this);
		MyApplication.getInstance().onLowMemory();
		Down = MyApplication.getInstance().getDownThreadPool();

		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);

		edTextUser = (EditText) findViewById(R.id.edittext_user);
		edTextSecretCode = (EditText) findViewById(R.id.edittext_secretcode);
		keepPassword = (CheckBox) findViewById(R.id.checkbox_secretcode);
		autoLogin = (CheckBox) findViewById(R.id.checkbox_autologin);
		buttonLogin = (Button) findViewById(R.id.button_login);
		buttonRegister = (Button) findViewById(R.id.button_register);
		buttonForget = (Button) findViewById(R.id.button_forget);

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(LoginActivity.this, titleView);
		titleView.setText("用户登录");

		ButtonClickEffect.setButtonFocusChanged(buttonLogin);
		ButtonClickEffect.setButtonFocusChanged(buttonRegister);
		ButtonClickEffect.setButtonFocusChanged(buttonBack);
		ButtonClickEffect.setButtonFocusChanged(buttonHome);
		ButtonClickEffect.setButtonFocusChanged(buttonForget);

		buttonForget.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataOperations.toastMidShow(LoginActivity.this, "敬请期待");
			}

		});

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
			}

		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}

		});

		/*
		 * 检测记住密码、自动登录
		 */
		autoLogin();
		keepPassword();

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

				/*
				 * 本地校验
				 */
				AccountAuthentication authentication = new AccountAuthentication();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);
				if (!authentication.checkup(userName, password, builder))
					return;

				/*
				 * http://182.92.155.100:15001/xiaofanzhuo/login/
				 * Login____13333333337____qwert12345
				 */
				String requestCode = "Login____" + userName + "____" + password;
				Down.get(DataOperations.loginServer + requestCode,
						new AsyncHttpResponseHandler() {

							@SuppressWarnings("deprecation")
							@Override
							public void onFailure(Throwable error,
									String content) {
								// TODO Auto-generated method stub
								super.onFailure(error, content);
								Toast.makeText(LoginActivity.this,
										"亲，网络不给力，请稍后!", Toast.LENGTH_SHORT)
										.show();
							}

							@SuppressWarnings("deprecation")
							@Override
							public void onSuccess(int statusCode, String content) {
								// TODO Auto-generated method stub
								super.onSuccess(statusCode, content);

								/*
								 * 无效数据
								 */
								if (DataOperations
										.isInvalidDataFromServer(content)) {
									Toast.makeText(LoginActivity.this,
											"登录失败，存档失效，请重新登录!",
											Toast.LENGTH_SHORT).show();
								} else {

									/*
									 * 返回值: 解析Json, 若Json中value==null,
									 * getString()方法得到的值为字面值"null"!!
									 */
									try {
										JSONObject obj = new JSONObject(content);
										String user_name = obj
												.getString(USERNAME);
										String password = obj
												.getString(PASSWORD);

										/*
										 * 登录校验
										 */
										if (user_name.equals("null")) {
											Toast.makeText(LoginActivity.this,
													"账号不存在，请重新登录!!",
													Toast.LENGTH_SHORT).show();
										} else if (password.equals("null")) {
											Toast.makeText(LoginActivity.this,
													"密码错误，请重新登录!!",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(LoginActivity.this,
													"登录成功!", Toast.LENGTH_SHORT)
													.show();

											/*
											 * cookie
											 */
											sp.edit()
													.putString(
															USERNAME,
															obj.getString(USERNAME))
													.commit();
											sp.edit()
													.putString(
															PASSWORD,
															obj.getString(PASSWORD))
													.commit();
											sp.edit()
													.putString(
															INVITED,
															obj.getString(INVITED))
													.commit();
											sp.edit()
													.putString(
															REGTIME,
															obj.getString(REGTIME))
													.commit();
											sp.edit()
													.putString(
															LASTTIME,
															obj.getString(LASTTIME))
													.commit();
											/*
											 * 只要登录过，则默认所有页面开放，否则强制登录
											 */
											sp.edit()
													.putBoolean(ALREADLOGIN,
															true).commit();

											Intent intent = new Intent();
											intent.setClass(LoginActivity.this,
													PersonInfoActivity.class);
											startActivity(intent);
											LoginActivity.this.finish();
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
								}
							}
						});
			}
		});

		keepPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (keepPassword.isChecked()) {
					sp.edit().putBoolean(KEEPPWD, true).commit();
				} else {
					sp.edit().putBoolean(KEEPPWD, false).commit();
				}

			}
		});

		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (autoLogin.isChecked()) {
					keepPassword.setChecked(true);
					sp.edit().putBoolean(AUTOLOGIN, true).commit();
				} else {
					sp.edit().putBoolean(AUTOLOGIN, false).commit();
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

		/*
		 * 键盘确认替代登录按钮
		 */
		edTextSecretCode
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							InputMethodManager imm = (InputMethodManager) v
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

							userName = edTextUser.getText().toString();
							password = edTextSecretCode.getText().toString();

							if (userName.equals(null) || userName.equals("")) {
								Toast.makeText(LoginActivity.this, "请输入手机号",
										Toast.LENGTH_SHORT).show();
								return false;
							}
							if (userName.equals(null) || password.equals("")) {
								Toast.makeText(LoginActivity.this, "请输入密码",
										Toast.LENGTH_SHORT).show();
								return false;
							}

							/*
							 * 本地校验
							 */
							AccountAuthentication authentication = new AccountAuthentication();
							AlertDialog.Builder builder = new AlertDialog.Builder(
									LoginActivity.this);
							if (!authentication.checkup(userName, password,
									builder))
								return false;

							/*
							 * http://182.92.155.100:15001/xiaofanzhuo/login/
							 * Login____13333333337____qwert12345
							 */
							String requestCode = "Login____" + userName
									+ "____" + password;
							Down.get(DataOperations.loginServer + requestCode,
									new AsyncHttpResponseHandler() {

										@SuppressWarnings("deprecation")
										@Override
										public void onFailure(Throwable error,
												String content) {
											// TODO Auto-generated method stub
											super.onFailure(error, content);
											Toast.makeText(LoginActivity.this,
													"亲，网络不给力，请稍后!",
													Toast.LENGTH_SHORT).show();
										}

										@SuppressWarnings("deprecation")
										@Override
										public void onSuccess(int statusCode,
												String content) {
											// TODO Auto-generated method stub
											super.onSuccess(statusCode, content);

											/*
											 * 无效数据
											 */
											if (DataOperations
													.isInvalidDataFromServer(content)) {
												Toast.makeText(
														LoginActivity.this,
														"登录失败，存档失效，请重新登录!",
														Toast.LENGTH_SHORT)
														.show();
											} else {

												/*
												 * 返回值: 解析Json
												 */
												try {
													JSONObject obj = new JSONObject(
															content);
													String user_name = obj
															.getString(USERNAME);
													String password = obj
															.getString(PASSWORD);

													/*
													 * 登录校验
													 */
													if (user_name
															.equals("null")) {
														Toast.makeText(
																LoginActivity.this,
																"账号不存在，请重新登录!!",
																Toast.LENGTH_SHORT)
																.show();
													} else if (password
															.equals("null")) {
														Toast.makeText(
																LoginActivity.this,
																"密码错误，请重新登录!!",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														Toast.makeText(
																LoginActivity.this,
																"登录成功!",
																Toast.LENGTH_SHORT)
																.show();

														/*
														 * cookie
														 */
														sp.edit()
																.putString(
																		USERNAME,
																		obj.getString(USERNAME))
																.commit();
														sp.edit()
																.putString(
																		PASSWORD,
																		obj.getString(PASSWORD))
																.commit();
														sp.edit()
																.putString(
																		INVITED,
																		obj.getString(INVITED))
																.commit();
														sp.edit()
																.putString(
																		REGTIME,
																		obj.getString(REGTIME))
																.commit();
														sp.edit()
																.putString(
																		LASTTIME,
																		obj.getString(LASTTIME))
																.commit();
														/*
														 * 只要登录过，则默认所有页面开放，否则强制登录
														 */
														sp.edit()
																.putBoolean(
																		ALREADLOGIN,
																		true)
																.commit();

														Intent intent = new Intent();
														intent.setClass(
																LoginActivity.this,
																PersonInfoActivity.class);
														startActivity(intent);
														LoginActivity.this
																.finish();
													}
												} catch (JSONException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}
									});

							return true;
						}
						return false;
					}

				});

	}

	private void autoLogin() {

		if (sp.contains(KEEPPWD) && sp.contains(AUTOLOGIN)
				&& sp.contains(USERNAME) && sp.contains(PASSWORD)) {

			if (sp.getBoolean(AUTOLOGIN, false)) {
				autoLogin.setChecked(true);

				String user = sp.getString(USERNAME, null);
				String pwd = sp.getString(PASSWORD, null);

				/*
				 * http://182.92.155.100:15001/xiaofanzhuo/login/
				 * Login____13333333337____qwert12345
				 */
				String requestCode = "Login____" + user + "____" + pwd;

				Down.get(DataOperations.loginServer + requestCode,
						new AsyncHttpResponseHandler() {

							@SuppressWarnings("deprecation")
							@Override
							public void onFailure(Throwable error,
									String content) {
								// TODO Auto-generated method stub
								super.onFailure(error, content);
								Toast.makeText(LoginActivity.this,
										"亲，网络不给力，请稍后重试!", Toast.LENGTH_SHORT)
										.show();
							}

							@SuppressWarnings("deprecation")
							@Override
							public void onSuccess(int statusCode, String content) {
								// TODO Auto-generated method stub
								super.onSuccess(statusCode, content);

								/*
								 * 无效数据
								 */
								if (DataOperations
										.isInvalidDataFromServer(content)) {
									Toast.makeText(LoginActivity.this,
											"登录失败，存档失效，请重新登录!",
											Toast.LENGTH_SHORT).show();
								} else {

									/*
									 * 返回值: 解析Json
									 */
									try {
										JSONObject obj = new JSONObject(content);
										String user_name = obj
												.getString(USERNAME);
										String password = obj
												.getString(PASSWORD);

										/*
										 * 登录校验
										 */
										if (user_name.equals("null")) {
											Toast.makeText(LoginActivity.this,
													"账号不存在，请重新登录!!",
													Toast.LENGTH_SHORT).show();
										} else if (password.equals("null")) {
											Toast.makeText(LoginActivity.this,
													"密码错误，请重新登录!!",
													Toast.LENGTH_SHORT).show();
										} else {

											Toast.makeText(LoginActivity.this,
													"自动登录!", Toast.LENGTH_SHORT)
													.show();

											Intent intent = new Intent();
											intent.setClass(LoginActivity.this,
													PersonInfoActivity.class);
											startActivity(intent);
											LoginActivity.this.finish();
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}// try

						});
			}
		}
		return;
	}

	private void keepPassword() {

		if (sp.contains(KEEPPWD) && sp.contains(USERNAME)
				&& sp.contains(PASSWORD)) {
			boolean keepFlag = sp.getBoolean(KEEPPWD, false);
			if (true == keepFlag) {
				keepPassword.setChecked(true);
				String user = sp.getString(USERNAME, null);
				String pwd = sp.getString(PASSWORD, null);
				edTextUser.setText(user);
				edTextSecretCode.setText(pwd);
			}
		}
	}

}
