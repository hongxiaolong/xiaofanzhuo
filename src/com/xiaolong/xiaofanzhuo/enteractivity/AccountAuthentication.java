package com.xiaolong.xiaofanzhuo.enteractivity;

import android.app.Activity;
import android.app.AlertDialog;

public class AccountAuthentication extends Activity {

	/*
	 * 该类checkup()方法对外开放三个参数String name、String passwords、AlertDialog.Builder
	 * builder
	 * 
	 * 各记录之间通过验证check值链接，初值check="check_nameContent"
	 * (1)check="check_nameContent"
	 * 进行帐号的内容连接,只可输入字母，数字，下划线，正确check="nameContentRight"
	 * (2)check="nameContentRight" 进行帐号长度验证上，长度在3-10位，check="nameLengthRight"
	 * (3)check="nameLengthRight"
	 * 进行密码内容验证,字母，数字，下划线,正确check="passwordContentRight"
	 * (4)check="passwordContentRight"进行密码长度验证，6-15位,正确
	 * check="passwordLengthRight" (5)check="passwordLengthRight"
	 * 进行手机内容验证,全为数字，正确 check="telephoneContentRight"
	 * (6)check="telephoneContentRight"进行手机长度验证,11位，正确
	 * check="telephoneLengthRight/AllRight"
	 * (7)check="telephoneLengthRight/AllRight"
	 * 三项内容/长度皆符合要求，checkup()方法返回true,否则false.
	 */

	String check = null;
	String checkName;
	String checkPassword;
	String checkTelephone;

	public boolean checkup(String telephones, String passwords,
			AlertDialog.Builder builder) {
		check = "check_nameContent";
		// 分步验证
		if (passwords.equals("") || telephones.equals("")) {
			builder.setMessage("亲！账号、密码为必填哦！");
			this.dialogShow(builder);
		} else {
			// 初始状态
			if (check.equals("check_nameContent"))
				this.telephoneContentCheck(telephones, builder);
			// 校验手机号内容
			if (check.equals("telephoneContentRight"))
				this.telephoneLengthCheck(telephones, builder);
			// 校验手机号长度
			if (check.equals("telephoneLengthRight/AllRight"))
				this.passwordContentCheck(passwords, builder);
			// 校验密码内容
			if (check.equals("passwordContentRight"))
				this.passwordLenghtCheck(passwords, builder);
			// 校验密码长度
			if (check.equals("passwordLengthRight"))
				return true;
		}
		return false;
	}

	public boolean checkup(String names, String passwords, String telephones,
			AlertDialog.Builder builder) {

		check = "check_nameContent";

		// 分步验证
		if (names.equals("") || passwords.equals("") || telephones.equals("")) {
			builder.setMessage("亲！您有必填项没填哦！");
			this.dialogShow(builder);

		} else {
			// 初始状态
			if (check.equals("check_nameContent"))
				this.nameContentCheck(names, builder);
			// 校验用户名
			if (check.equals("nameContentRight"))
				this.nameLengthCheck(names, builder);
			// 校验用户名长度
			if (check.equals("nameLengthRight"))
				this.passwordContentCheck(passwords, builder);
			// 校验密码内容
			if (check.equals("passwordContentRight"))
				this.passwordLenghtCheck(passwords, builder);
			// 校验密码长度
			if (check.equals("passwordLengthRight"))
				this.telephoneContentCheck(telephones, builder);
			// 校验手机号内容
			if (check.equals("telephoneContentRight"))
				this.telephoneLengthCheck(telephones, builder);
			// 校验手机号长度
			if (check.equals("telephoneLengthRight/AllRight"))
				return true;

		}

		return false;

	}// checkup()方法

	// 帐号内容验证
	public void nameContentCheck(String names, AlertDialog.Builder builder) {
		char tempName[] = names.toCharArray();

		// 验证内容
		for (int i = 0; i < tempName.length; i++) {
			if ((tempName[i] > 47 && tempName[i] < 58)
					|| (tempName[i] > 64 && tempName[i] < 91)
					|| (tempName[i] > 96 && tempName[i] < 123)
					|| (tempName[i] == 95)) {
				check = "nameContentRight"; // 内容合格, 数字，字母，下划线

			} else {

				builder.setMessage("亲！帐号中只可出现字母、数字、下划线！");
				this.dialogShow(builder);

				break;
			}
		}
	}

	// 账号长度验证
	public void nameLengthCheck(String names, AlertDialog.Builder builder) {
		char tempName[] = names.toCharArray();
		if (check.equals("nameContentRight")) {

			if (tempName.length < 3 || tempName.length > 10) {

				builder.setMessage("帐号长度不符合规则！请您再次输入！(3--10位)");
				this.dialogShow(builder);

			} else {
				check = "nameLengthRight";
			}

		}

	}

	// 密码内容验证
	public void passwordContentCheck(String passwords,
			AlertDialog.Builder builder) {
		char tempPass[] = passwords.toCharArray();

		// 验证内容
		for (int i = 0; i < tempPass.length; i++) {
			if ((tempPass[i] > 47 && tempPass[i] < 58)
					|| (tempPass[i] > 64 && tempPass[i] < 91)
					|| (tempPass[i] > 96 && tempPass[i] < 123)
					|| (tempPass[i] == 95)) {
				check = "passwordContentRight";// 密码内容合格，字母，数字，下划线
			} else {

				builder.setMessage("亲！密码中只可出现字母、数字、下划线！");
				this.dialogShow(builder);

				break;
			}
		}
	}

	// 密码长度验证
	public void passwordLenghtCheck(String passwords,
			AlertDialog.Builder builder) {
		char tempPass[] = passwords.toCharArray();

		if (check.equals("passwordContentRight")) {
			if (tempPass.length < 6 || tempPass.length > 15) {
				builder.setMessage("密码长度不符合规则！请您再次输入！(6--15位)");
				this.dialogShow(builder);

			} else {

				check = "passwordLengthRight";
			}
		}

	}

	// **手机内容验证
	public void telephoneContentCheck(String telephones,
			AlertDialog.Builder builder) {
		char tempTelep[] = telephones.toCharArray();

		// 内容验证
		for (int i = 0; i < tempTelep.length; i++) {
			if (tempTelep[i] > 47 && tempTelep[i] < 58) {
				check = "telephoneContentRight";// 手机内容合格，数字
			} else {
				builder.setMessage("亲！请确认您输入的是手机号！");
				this.dialogShow(builder);

				break;
			}

		}
	}

	// 手机长度验证
	public void telephoneLengthCheck(String telephones,
			AlertDialog.Builder builder) {
		char tempTelep[] = telephones.toCharArray();
		if (check.equals("telephoneContentRight")) {
			if (tempTelep.length != 11) {
				builder.setMessage("亲！您输入的不是11位的手机号！");
				this.dialogShow(builder);

			} else {
				check = "telephoneLengthRight/AllRight";
			}
		}

	}

	// 公用显示提示框
	public void dialogShow(AlertDialog.Builder builder) {

		check = "check_nameContent"; // 出错,恢复初如状态
		builder.setTitle("提示：");
		builder.setNegativeButton("确认", null);

		AlertDialog dialog = builder.create();
		dialog.show();

	}

}
