package com.xiaolong.xiaofanzhuo.dataoperations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class DataOperations {

	// SharedPreferences
	public static String appSP = "xiaofanzhuoapplication";
	public static String businessSP = "BUSINESSIDS";
	public static String menuSP = "MENUIDS";

	public static final int frequentlyLimit = 5;
	public static final String message = "亲，您的点击过于频繁，请稍后尝试或暂时退出....服务器还在完善中...";
	public static final String messageBack = "亲，您在当前界面的回退操作过于频繁，请多体验其它界面或暂时退出....服务器还在完善中...";
	public static final String messageNetOpen = "亲，您的网络连接似乎有些问题，请检查是否连接了有效的网络...";
	public static final String messageErrorData = "亲，您的网络连接似乎有些问题，某些数据请求失败...";
	public static String webServer = "http://182.92.155.100/xiaofanzhuo/user/";
	public static String loginServer = "http://182.92.155.100/xiaofanzhuo/login/";

	/**
	 * 检查缓存中是否存在上次从服务器请求的列表
	 * 
	 * @param context
	 * @param listSP
	 * @return true 存在 false 不存在
	 */
	public static boolean checkBusinessListFromSharedPreferences(
			Context context, String listSP) {
		SharedPreferences sp = context.getSharedPreferences(
				DataOperations.appSP, 0);
		return sp.contains(listSP);
	}

	/**
	 * 从缓存中读取上次从服务器中请求的列表
	 * 
	 * @param context
	 * @param listSP
	 * @return
	 */
	public static List<String> getBusinessListFromSharedPreferences(
			Context context, String listSP) {
		SharedPreferences sp = context.getSharedPreferences(
				DataOperations.appSP, 0);
		String buffer = sp.getString(listSP, null);
		System.out.println("Read from SharedPreferences, listSp >>>>>>>= "
				+ buffer);
		List<String> ids = new ArrayList<String>();
		String[] ret = buffer.split("\n");
		for (int i = 0; i < ret.length; ++i)
			ids.add(ret[i]);
		return ids;
	}

	public static String getActualString(String str) {

		return str;

	}

	/**
	 * 将异常信息转化成字符串
	 * 
	 * @param t
	 * @return
	 * @throws IOException
	 */
	public static String exception(Throwable t) throws IOException {
		if (t == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			t.printStackTrace(new PrintStream(baos));
		} finally {
			baos.close();
		}
		return baos.toString();
	}

	/**
	 * 检测异常数据
	 * 
	 * @param input
	 * @return true: invalid data false: valid data
	 */
	public static boolean isInvalidDataFromServer(String input) {
		if (input.equals("") || input.equals(null)) {
			System.out.print("No adapter pictures!");
			return true;
		}
		return false;
	}

	/**
	 * 设置字体格式
	 * 
	 * @param context
	 * @param textView
	 */
	public static void setTypefaceForTextView(Context context, TextView textView) {
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"fonts/huakangwawa.ttf");
		textView.setTypeface(typeFace);
	}

	/**
	 * Toast在屏幕中间显示
	 * 
	 * @param context
	 * @param makeText
	 */
	public static void toastMidShow(Context context, String makeText) {
		Toast toast = Toast.makeText(context.getApplicationContext(), makeText,
				Toast.LENGTH_SHORT);
		// 可以控制toast显示的位置
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 弹出提示点击限制
	 * 
	 * @param context
	 * @param mesLeft
	 *            左边按钮，无动作
	 * @param mesRight
	 *            右边按钮，退出当前界面
	 * @param alert
	 */
	public static void creatButtonClickDialog(final Context context,
			String mesLeft, String mesRight, String alert) {
		if (null == alert)
			alert = message;
		new AlertDialog.Builder(context)
				.setMessage(alert)
				.setPositiveButton(mesLeft,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setNegativeButton(mesRight,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								((Activity) context).finish();
							}
						}).show();
	}

	/**
	 * 弹出网络设置对话框
	 */
	public static void creatNetworkSettingsDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(messageNetOpen).setMessage("是否对网络进行设置?");

		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = null;

				try {
					String sdkVersion = android.os.Build.VERSION.SDK;
					if (Integer.valueOf(sdkVersion) > 10) {
						intent = new Intent(
								android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					} else {
						intent = new Intent();
						ComponentName comp = new ComponentName(
								"com.android.settings",
								"com.android.settings.WirelessSettings");
						intent.setComponent(comp);
						intent.setAction("android.intent.action.VIEW");
					}
					context.startActivity(intent);
				} catch (Exception e) {
					System.out
							.println("open network settings failed, please check...");
					e.printStackTrace();
				}
			}
		}).setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}

	/**
	 * 非UI线程中显示Toast
	 * 
	 * @param msg
	 */
	public static void toastInNonUiThread(Context context, String msg) {
		Looper.prepare();
		DataOperations.toastMidShow(context, msg);
		Looper.loop();
	}

	/**
	 * 对网络连接状态进行判断
	 * 
	 * @return true, 可用； false， 不可用
	 */
	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}

	/**
	 * 格式化数字，如果有小数，保留2位以内，没有只显示整数
	 * 
	 * @param d
	 * @param totalDigit
	 *            限制总位数
	 * @param fractionalDigit
	 *            保留小数位数
	 * @return
	 */
	public static String padDoubleLeft(Double d, int totalDigit,
			int fractionalDigit) {
		String str = null;
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMinimumFractionDigits(fractionalDigit);
		decimalFormat.setMaximumFractionDigits(fractionalDigit);
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setMaximumIntegerDigits(totalDigit - fractionalDigit - 1);
		decimalFormat.setMinimumIntegerDigits(totalDigit - fractionalDigit - 1);
		str = decimalFormat.format(d);
		/**
		 * 去掉前面的0（比如000123213，最后输出123213）
		 */
		while (str.startsWith("0")) {
			str = str.substring(1);
		}
		return str;
	}

}
