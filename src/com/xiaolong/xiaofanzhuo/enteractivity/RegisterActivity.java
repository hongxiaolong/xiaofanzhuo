package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

public class RegisterActivity extends BaseActivity {

	private static final String TAG = "RegisterActivity";
	private EditText editTextPhoneR;
	private EditText editTextPasswordR;
	private EditText editTextRePasswordR;
	private Button buttonR;
	private Button buttonBack;
	private String phoneNumber = "";
	private String password = "";
	private String rePassword = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.hong_register);

		editTextPhoneR = (EditText) findViewById(R.id.register_phonenumber);
		editTextPasswordR = (EditText) findViewById(R.id.register_password);
		editTextRePasswordR = (EditText) findViewById(R.id.register_repassword);
		buttonR = (Button) findViewById(R.id.regsiter_registerbutton);
		buttonBack = (Button) findViewById(R.id.register_button_back);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterActivity.this.finish();
			}

		});

		buttonR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				phoneNumber = editTextPhoneR.getText().toString();
				Log.i(TAG, phoneNumber + "1");
				password = editTextPasswordR.getText().toString();
				rePassword = editTextRePasswordR.getText().toString();
				if (!password.equals(rePassword)) {
					Toast.makeText(RegisterActivity.this, "输入密码不一致!",
							Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					String requestCode = "WhiteZhuCe_Username_Passwd____"
							+ phoneNumber + "_" + password;
					GetResponseFromServerAction reponse = new GetResponseFromServerAction();
					String ret = reponse.getStringFromServerById(requestCode);
					Log.i(TAG, ret);
					if (ret.contains("WhiteZhuCe_Result____UsernameTRUE_PasswdTRUE")) {
						Toast.makeText(RegisterActivity.this, "注册成功!",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this,
								LoginActivity.class);
						startActivity(intent);
						RegisterActivity.this.finish();
						return;
					}
					if (ret.contains("WhiteZhuCe_Result____UsernameFALSE_PasswdFALSE")) {
						Toast.makeText(RegisterActivity.this,
								"注册失败，请检查用户名、密码或网络!", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					Log.i(TAG, "注册服务器返回信息未定义!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
