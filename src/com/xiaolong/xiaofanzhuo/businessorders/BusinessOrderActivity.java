package com.xiaolong.xiaofanzhuo.businessorders;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.businessdetails.BusinessDetailActivity;
import com.xiaolong.xiaofanzhuo.businesslistings.BusinessListActivity;
import com.xiaolong.xiaofanzhuo.dataoperations.CartDatabaseAdapter;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.enteractivity.ZoneShowActivity;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.DownloadOrderFromSQLite;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.historyorders.HistoryOrderDataOperation;
import com.xiaolong.xiaofanzhuo.historyorders.HistoryOrderDatabaseAdapter;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.ButtonClickEffect;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/*
 * BusinessOrderActivity
 * @author hongxiaolong
 */

public class BusinessOrderActivity extends BaseActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "BusinessOrderActivity";

	private BasePictAdapter mAdapter;
	private ListView mListView;

	private TextView tvNum;
	private ImageView basketImg;
	private ImageView phoneImg;
	private TextView totalView;
	private TextView titleView;;

	private DownloadOrderFromSQLite thread = null;
	private Handler handler = null;

	private Bundle extraData = null;
	private Bundle totalData = null;

	private ImageButton buttonBack;
	private ImageButton buttonHome;

	private AsyncHttpClient Down = null;
	private SharedPreferences sp;
	public static final String USERNAME = "user_name";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(BusinessOrderActivity.this);
		MyApplication.getInstance().onLowMemory();
		Down = MyApplication.getInstance().getDownThreadPool();
		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);

		setContentView(R.layout.hong_menu_order_main);

		mMainFrameTask = new MainFrameTask(this);
		mMainFrameTask.setLoadingTime(1);
		mMainFrameTask.execute();

		// 从Intent 中获取数据
		extraData = this.getIntent().getExtras();

		mListView = (ListView) findViewById(R.id.menu_order_listview);
		basketImg = (ImageView) findViewById(R.id.shopping_img_cart);
		tvNum = (TextView) findViewById(R.id.tv_rolla);
		DataOperations
				.setTypefaceForTextView(BusinessOrderActivity.this, tvNum);
		tvNum.getPaint().setFakeBoldText(true);
		phoneImg = (ImageView) findViewById(R.id.order_phone_img);
		totalView = (TextView) findViewById(R.id.order_total_price);
		DataOperations.setTypefaceForTextView(BusinessOrderActivity.this,
				totalView);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(BusinessOrderActivity.this,
				titleView);
		titleView.setText(extraData.getString("ShopName"));

		buttonHome = (ImageButton) findViewById(R.id.button_home);
		buttonBack = (ImageButton) findViewById(R.id.button_back);

		ButtonClickEffect.setButtonFocusChanged(buttonBack);
		ButtonClickEffect.setButtonFocusChanged(buttonHome);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (thread != null) {
					thread.setStop();
				}

				Intent intent = new Intent(BusinessOrderActivity.this,
						BusinessDetailActivity.class);
				intent.putExtras(extraData);
				startActivity(intent);
				MyApplication.getInstance().onLowMemory();
				BusinessOrderActivity.this.finish();

			}
		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(BusinessOrderActivity.this,
						ZoneShowActivity.class);
				startActivity(intent);
				MyApplication.getInstance().onLowMemory();
				BusinessOrderActivity.this.finish();
			}

		});

		basketImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				creatBasketDialog();
			}
		});

		phoneImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				creatPhoneDialog();
			}
		});

		mAdapter = new BasePictAdapter(BusinessOrderActivity.this);
		mAdapter.setIViewAddAndEventSet(new OrderPictureViewAdd(mAdapter));
		mAdapter.setCartBadgeView(tvNum);
		mListView.setAdapter(mAdapter);

		CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(
				BusinessOrderActivity.this);
		dbHelper.open();
		totalData = dbHelper.fetchDataByID(extraData.getString("ShopID"));
		dbHelper.close();

		handler = new ThumbHandler(BusinessOrderActivity.this, mAdapter);
		startDwonloadBusinessLocal(BusinessOrderActivity.this);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.updateOrderUI");
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);

		if ("0" != totalData.getString("TP")) {
			totalView.setText("总价: " + totalData.getString("TP") + "元");
			tvNum.setText(totalData.getString("TQ"));
		}
	}

	private void startDwonloadBusinessLocal(Context context) {
		thread = new DownloadOrderFromSQLite(context, handler,
				extraData.getString("ShopID"));
		thread.start();
	}

	@Override
	protected void onPause() {
		if (thread != null) {
			thread.setStop();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (thread != null) {
			thread.setStop();
		}
		/*
		 * 停止加载框
		 */
		if (mMainFrameTask != null && !mMainFrameTask.isCancelled()) {
			mMainFrameTask.cancel(true);
		}
		super.onDestroy();
		this.unregisterReceiver(mRefreshBroadcastReceiver);
		MyApplication.getInstance().remove(BusinessOrderActivity.this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (thread != null) {
			thread.setStop();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 弹出提示清空对话框
	 */
	private void creatBasketDialog() {
		new AlertDialog.Builder(this).setMessage("亲，您确定要清空购物篮么?")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						final CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(
								BusinessOrderActivity.this);
						dbHelper.open();
						dbHelper.removeAllByID(new String[] { extraData
								.getString("ShopID") });
						dbHelper.close();

						/*
						 * 更新商家页购物车数量
						 */
						Intent intentDetail = new Intent();
						intentDetail.setAction("action.updateDetailUI");
						BusinessOrderActivity.this.sendBroadcast(intentDetail);

						BusinessOrderActivity.this.finish();
						MyApplication.getInstance().onLowMemory();

						Toast toast = Toast.makeText(getApplicationContext(),
								"亲，购物篮已清空，您可以尽情点餐!!", Toast.LENGTH_SHORT);
						// 可以控制toast显示的位置
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();

					}
				}).show();
	}

	/**
	 * 弹出提示拨叫对话框
	 */
	private void creatPhoneDialog() {
		new AlertDialog.Builder(this)
				.setMessage("亲，下单、呼叫店家点餐: " + extraData.getString("PhoneNum"))
				.setPositiveButton("下单", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if ("0" == totalData.getString("TP"))
							Toast.makeText(getApplicationContext(),
									"亲，购物篮是空的哦，请先点餐!!", Toast.LENGTH_SHORT)
									.show();
						else {

							/*
							 * 生成订单且插入历史订单数据库
							 */
							Date now = new Date();
							SimpleDateFormat f2 = new SimpleDateFormat(
									"yyyyMMddHHmmss");
							String time = f2.format(now);
							if (sp.contains(USERNAME)
									&& !sp.getString(USERNAME, null).equals(
											null)) {
								extraData
										.putString(
												HistoryOrderDatabaseAdapter.COL_USER_ID,
												sp.getString(USERNAME, null));
								extraData.putString(
										HistoryOrderDatabaseAdapter.COL_PRICE,
										totalData.getString("TP"));
								extraData.putString(
										HistoryOrderDatabaseAdapter.COL_TIME,
										time);

								List<Bundle> datas = HistoryOrderDataOperation
										.getDatasFromCart(
												BusinessOrderActivity.this,
												extraData.getString("ShopID"));
								HistoryOrderDatabaseAdapter dbHelper = new HistoryOrderDatabaseAdapter(
										BusinessOrderActivity.this);
								dbHelper.open();
								String orderJson = dbHelper.insertFromCart(
										extraData, datas);
								dbHelper.close();

								orderJson = URLEncoder.encode(orderJson);
								orderJson = orderJson.replace("%", "___");
								orderJson = orderJson.replace("/", "xiegang");
								final String infoOrder = orderJson.replace(".",
										"hxldian");
								/*
								 * 服务端校验 ShangChuanOrderInfo____JSON
								 */

								String requestCode = "ShangChuanOrderInfo____"
										+ infoOrder;

								Down.get(
										DataOperations.webServer + requestCode,
										new AsyncHttpResponseHandler() {

											@SuppressWarnings("deprecation")
											@Override
											public void onFailure(
													Throwable error,
													String content) {
												// TODO Auto-generated method
												// stub
												super.onFailure(error, content);
												Toast.makeText(
														BusinessOrderActivity.this,
														"亲，网络不给力!",
														Toast.LENGTH_SHORT)
														.show();
											}

											@SuppressWarnings("deprecation")
											@Override
											public void onSuccess(
													int statusCode,
													String content) {
												// TODO Auto-generated method
												// stub
												super.onSuccess(statusCode,
														content);

												/*
												 * 无效数据
												 */
												if (DataOperations
														.isInvalidDataFromServer(content)) {
													Toast.makeText(
															BusinessOrderActivity.this,
															"订单生成失败，请稍后重试，凭订单领奖~",
															Toast.LENGTH_SHORT)
															.show();
												} else {
													Toast.makeText(
															BusinessOrderActivity.this,
															"您正在生成订单，请别忘了索要小票领奖哦!",
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										});

								// String requestCode = "GetOrderFromUser____"
								// + sp.getString(USERNAME, null)
								// + "____"
								// + extraData.getString("ShopID")
								// + "____"
								// + (int) Double.parseDouble(totalData
								// .getString("TP")) + "____"
								// + time;
								//
								// Down.get(
								// DataOperations.webServer + requestCode,
								// new AsyncHttpResponseHandler() {
								//
								// @SuppressWarnings("deprecation")
								// @Override
								// public void onFailure(
								// Throwable error,
								// String content) {
								// // TODO Auto-generated method
								// // stub
								// super.onFailure(error, content);
								// Toast.makeText(
								// BusinessOrderActivity.this,
								// "亲，网络不给力!",
								// Toast.LENGTH_SHORT)
								// .show();
								// }
								//
								// @SuppressWarnings("deprecation")
								// @Override
								// public void onSuccess(
								// int statusCode,
								// String content) {
								// // TODO Auto-generated method
								// // stub
								// super.onSuccess(statusCode,
								// content);
								//
								// /*
								// * 无效数据
								// */
								// if (DataOperations
								// .isInvalidDataFromServer(content)) {
								// Toast.makeText(
								// BusinessOrderActivity.this,
								// "订单生成失败，请稍后重试，凭订单领奖~",
								// Toast.LENGTH_SHORT)
								// .show();
								// } else {
								// Toast.makeText(
								// BusinessOrderActivity.this,
								// "您正在生成订单，请别忘了索要小票领奖哦!",
								// Toast.LENGTH_SHORT)
								// .show();
								// }
								// }
								// });

								Intent phoneIntent = new Intent(
										"android.intent.action.CALL",
										Uri.parse("tel:"
												+ extraData
														.getString("PhoneNum")));
								startActivity(phoneIntent);// 这个activity要把通话界面隐藏以及相应的把菜品界面展示；
							} else {
								/*
								 * 强制登录，增加注册量
								 */
								if (!sp.contains(USERNAME)
										|| false == forceLogin(BusinessOrderActivity.this))
									return;
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/*
	 * 广播：更新总价
	 */
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("action.updateOrderUI")) {

				CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(
						BusinessOrderActivity.this);
				dbHelper.open();
				totalData = dbHelper.fetchDataByID(extraData
						.getString("ShopID"));
				dbHelper.close();

				/*
				 * 购物车已被清空
				 */
				if (0 >= Integer.valueOf(totalData.getString("TQ"))) {
					totalData.putString("TQ", "0");
					totalData.putString("TP", "0");

					totalView.setText("购物车空空如也!");
					tvNum.setText("0");

					/*
					 * 更新商家页购物车数量
					 */
					Intent intentDetail = new Intent();
					intentDetail.setAction("action.updateDetailUI");
					context.sendBroadcast(intentDetail);

					MyApplication.getInstance().onLowMemory();
					BusinessOrderActivity.this.finish();

					Toast toast = Toast.makeText(getApplicationContext(),
							"亲，购物篮空空如也，请先点餐!", Toast.LENGTH_SHORT);
					// 可以控制toast显示的位置
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					totalView.setText("总价: " + totalData.getString("TP") + "元");
				}

			}
		}
	};

}