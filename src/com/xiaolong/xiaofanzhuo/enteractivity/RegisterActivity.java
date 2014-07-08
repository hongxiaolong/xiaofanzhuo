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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

public class RegisterActivity extends BaseActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "RegisterActivity";

	public static final String USERNAME = "user_name";
	public static final String PASSWORD = "password";
	public static final String INVITED = "invited";
	public static final String REGTIME = "reg_time";
	public static final String LASTTIME = "last_login_time";
	public static final String KEEPPWD = "KEEPPWD";
	public static final String AUTOLOGIN = "AUTOLOGIN";
	public static final String ALREADLOGIN = "ALREADLOGIN";

	private EditText editTextPhoneR;
	private EditText editTextPasswordR;
	private EditText editTextRePasswordR;
	private EditText editTextInviter;
	private Button buttonR;
	private SharedPreferences sp;

	private TextView titleView;

	private ImageButton buttonBack;
	private ImageButton buttonHome;

	private AsyncHttpClient Down = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.hong_register);

		MyApplication.getInstance().addActivity(RegisterActivity.this);
		MyApplication.getInstance().onLowMemory();
		Down = MyApplication.getInstance().getDownThreadPool();

		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);

		editTextPhoneR = (EditText) findViewById(R.id.register_phonenumber);
		editTextPasswordR = (EditText) findViewById(R.id.register_password);
		editTextRePasswordR = (EditText) findViewById(R.id.register_repassword);
		editTextInviter = (EditText) findViewById(R.id.register_inviter);

		buttonR = (Button) findViewById(R.id.regsiter_registerbutton);
		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(RegisterActivity.this, titleView);
		titleView.setText("用户注册");

		ButtonClickEffect.setButtonFocusChanged(buttonR);
		ButtonClickEffect.setButtonFocusChanged(buttonBack);
		ButtonClickEffect.setButtonFocusChanged(buttonHome);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.getInstance().onLowMemory();
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
			}

		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, ZoneShowActivity.class);
				startActivity(intent);
				MyApplication.getInstance().onLowMemory();
				RegisterActivity.this.finish();
			}

		});

		buttonR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String user_name = editTextPhoneR.getText().toString();
				String password = editTextPasswordR.getText().toString();
				String rePassword = editTextRePasswordR.getText().toString();
				String inviter = editTextInviter.getText().toString();

				if (!password.equals(rePassword)) {
					Toast.makeText(RegisterActivity.this, "输入密码不一致!",
							Toast.LENGTH_SHORT).show();
					return;
				}

				/*
				 * 本地校验
				 */
				AccountAuthentication authentication = new AccountAuthentication();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						RegisterActivity.this);
				if (!authentication.checkup(user_name, password, inviter,
						builder))
					return;

				/*
				 * 服务端校验 Register____user_name____password____inviter
				 */
				String requestCode = "Register____" + user_name + "____"
						+ password + "____" + inviter;
				Down.get(DataOperations.loginServer + requestCode,
						new AsyncHttpResponseHandler() {

							@SuppressWarnings("deprecation")
							@Override
							public void onFailure(Throwable error,
									String content) {
								// TODO Auto-generated method stub
								super.onFailure(error, content);
								Toast.makeText(RegisterActivity.this,
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
									Toast.makeText(RegisterActivity.this,
											"注册失败，请稍后重试!", Toast.LENGTH_SHORT)
											.show();
								} else {

									/*
									 * 返回值: 解析Json, 若Json中value==null,
									 * getString()方法得到的值为字面值"null"!!
									 */
									try {
										JSONObject obj = new JSONObject(content);
										String user_name = obj
												.getString(USERNAME);

										/*
										 * 注册校验
										 */
										if (user_name.equals("null")) {
											Toast.makeText(
													RegisterActivity.this,
													"账号已存在，请登录或重新注册!!",
													Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(
													RegisterActivity.this,
													"注册成功!", Toast.LENGTH_SHORT)
													.show();

											/*
											 * 记住登录状态
											 */
											remember(obj);
											
											Intent intent = new Intent();
											intent.setClass(
													RegisterActivity.this,
													PersonInfoActivity.class);
											startActivity(intent);
											RegisterActivity.this.finish();
										}

									} catch (JSONException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
								}// else
							}
						});
			}
		});

		/*
		 * 键盘确认替代注册按钮
		 */
		editTextInviter
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							InputMethodManager imm = (InputMethodManager) v
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

							/*
							 * 文本框输入
							 */
							String user_name = editTextPhoneR.getText()
									.toString();
							String password = editTextPasswordR.getText()
									.toString();
							String rePassword = editTextRePasswordR.getText()
									.toString();
							String inviter = editTextInviter.getText()
									.toString();

							if (!password.equals(rePassword)) {
								Toast.makeText(RegisterActivity.this,
										"输入密码不一致!", Toast.LENGTH_SHORT).show();
								return false;
							}

							/*
							 * 本地校验
							 */
							AccountAuthentication authentication = new AccountAuthentication();
							AlertDialog.Builder builder = new AlertDialog.Builder(
									RegisterActivity.this);
							if (!authentication.checkup(user_name, password,
									inviter, builder))
								return false;

							/*
							 * 服务端校验
							 * Register____user_name____password____inviter
							 */
							String requestCode = "Register____" + user_name
									+ "____" + password + "____" + inviter;
							Down.get(DataOperations.loginServer + requestCode,
									new AsyncHttpResponseHandler() {

										@SuppressWarnings("deprecation")
										@Override
										public void onFailure(Throwable error,
												String content) {
											// TODO Auto-generated method stub
											super.onFailure(error, content);
											Toast.makeText(
													RegisterActivity.this,
													"亲，网络不给力，请稍后重试!",
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
														RegisterActivity.this,
														"注册失败，请稍后重试!",
														Toast.LENGTH_SHORT)
														.show();
											} else {

												/*
												 * 返回值: 解析Json,
												 * 若Json中value==null,
												 * getString()方法得到的值为字面值"null"!!
												 */
												try {
													JSONObject obj = new JSONObject(
															content);
													String user_name = obj
															.getString(USERNAME);

													/*
													 * 注册校验
													 */
													if (user_name
															.equals("null")) {
														Toast.makeText(
																RegisterActivity.this,
																"账号已存在，请登录或重新注册!!",
																Toast.LENGTH_SHORT)
																.show();
													} else {
														Toast.makeText(
																RegisterActivity.this,
																"注册成功!",
																Toast.LENGTH_SHORT)
																.show();
														
														/*
														 * 记住登录状态
														 */
														remember(obj);

														Intent intent = new Intent();
														intent.setClass(
																RegisterActivity.this,
																PersonInfoActivity.class);
														startActivity(intent);
														RegisterActivity.this
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

	/*
	 * 注册校验
	 */
	public void verifyRegistration(String user_name, String password,
			String inviter) {

		/*
		 * 服务端校验 Register____user_name____password____inviter
		 */
		String requestCode = "Register____" + user_name + "____" + password
				+ "____" + inviter;
		Down.get(DataOperations.loginServer + requestCode,
				new AsyncHttpResponseHandler() {

					@SuppressWarnings("deprecation")
					@Override
					public void onFailure(Throwable error, String content) {
						// TODO Auto-generated method stub
						super.onFailure(error, content);
						Toast.makeText(RegisterActivity.this, "亲，网络不给力，请稍后重试!",
								Toast.LENGTH_SHORT).show();
					}

					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(int statusCode, String content) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, content);

						/*
						 * 无效数据
						 */
						if (DataOperations.isInvalidDataFromServer(content)) {
							Toast.makeText(RegisterActivity.this,
									"注册失败，请稍后重试!", Toast.LENGTH_SHORT).show();
						} else {

							/*
							 * 返回值: 解析Json, 若Json中value==null,
							 * getString()方法得到的值为字面值"null"!!
							 */
							try {
								JSONObject obj = new JSONObject(content);
								String user_name = obj.getString(USERNAME);

								/*
								 * 注册校验
								 */
								if (user_name.equals("null")) {
									Toast.makeText(RegisterActivity.this,
											"账号已存在，请登录或重新注册!!",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(RegisterActivity.this,
											"注册成功!", Toast.LENGTH_SHORT).show();

									/*
									 * 记住登录状态
									 */
									remember(obj);
									
									Intent intent = new Intent();
									intent.setClass(RegisterActivity.this,
											PersonInfoActivity.class);
									startActivity(intent);
									RegisterActivity.this.finish();
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

	/*
	 * 注册后默认已登录一次，较少频繁操作
	 */
	private void remember(JSONObject obj) {
		/*
		 * cookie
		 */
		try {
			sp.edit().putString(USERNAME, obj.getString(USERNAME)).commit();
			sp.edit().putString(PASSWORD, obj.getString(PASSWORD)).commit();
			sp.edit().putString(INVITED, obj.getString(INVITED)).commit();
			sp.edit().putString(REGTIME, obj.getString(REGTIME)).commit();
			sp.edit().putString(LASTTIME, obj.getString(LASTTIME)).commit();
			/*
			 * 只要登录过，则默认所有页面开放，否则强制登录
			 */
			sp.edit().putBoolean(ALREADLOGIN, true).commit();
			/*
			 * 默认打开记住密码
			 */
			sp.edit().putBoolean(KEEPPWD, true).commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
