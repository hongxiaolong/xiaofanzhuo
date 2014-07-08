package com.xiaolong.xiaofanzhuo.businessdetails;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnItemClickListener;
import com.xiaolong.xiaofanzhuo.businessorders.BusinessOrderActivity;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.CartDatabaseAdapter;
import com.xiaolong.xiaofanzhuo.enteractivity.LoginActivity;
import com.xiaolong.xiaofanzhuo.enteractivity.ZoneShowActivity;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.LoadingMenuListFromNet;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.ButtonClickEffect;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * BussinessDetailActivity
 * 
 * @author hongxiaolong
 * 
 */

@SuppressWarnings("deprecation")
public class BusinessDetailActivity extends BaseActivity {

	private LoadingMenuListFromNet thread = null;
	private Handler handler = null;
	private BasePictAdapter mAdapter;
	private SlidingDrawer slidingDrawer;
	private ImageView imageView;
	private TextView titleView;

	private ImageView basketImg;
	private ImageView phoneImg;
	private TextView tvNum;
	private TextView textView01;
	private TextView textView02;
	private TextView textView03;
	private TextView textView04;

	private int totalNum = 0;
	private Bundle extraData;

	private ImageButton buttonBack;
	private ImageButton buttonHome;

	private SharedPreferences sp;
	private boolean praisedStatus = false;
	private String message = null;

	private AsyncHttpClient Down = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(BusinessDetailActivity.this);
		MyApplication.getInstance().onLowMemory();
		Down = MyApplication.getInstance().getDownThreadPool();

		setContentView(R.layout.hong_business_detail);

		mMainFrameTask = new MainFrameTask(this);
		mMainFrameTask.setLoadingTime(5);
		mMainFrameTask.execute();

		StaggeredGridView gridView = (StaggeredGridView) this
				.findViewById(R.id.mycontent);

		// 从Intent 中获取数据
		extraData = this.getIntent().getExtras();
		final String shopId = extraData.getString("ShopID");

		tvNum = (TextView) findViewById(R.id.tv_rolla);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this,
				tvNum);
		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this,
				titleView);
		titleView.setText(extraData.getString("ShopName"));

		textView01 = (TextView) findViewById(R.id.textview01);
		textView02 = (TextView) findViewById(R.id.textview02);
		textView03 = (TextView) findViewById(R.id.textview03);
		textView04 = (TextView) findViewById(R.id.textview04);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this,
				textView01);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this,
				textView02);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this,
				textView03);
		DataOperations.setTypefaceForTextView(BusinessDetailActivity.this,
				textView04);
		textView01.setText(extraData.getString("ShopAverPrice") + "元");
		textView02.setText(extraData.getString("ShopTag"));
		textView03.setText(extraData.getString("ShopSite"));
		textView04.setText(extraData.getString("PhoneNum"));

		sp = this.getSharedPreferences("xiaofanzhuologininfo", MODE_PRIVATE);

		slidingDrawer = (SlidingDrawer) findViewById(R.id.sliding_drawer);
		imageView = (ImageView) findViewById(R.id.my_image);

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);

		ButtonClickEffect.setButtonFocusChanged(buttonBack);
		ButtonClickEffect.setButtonFocusChanged(buttonHome);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (thread != null) {
					thread.setStop();
				}
				MyApplication.getInstance().onLowMemory();
				BusinessDetailActivity.this.finish();
			}
		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BusinessDetailActivity.this,
						ZoneShowActivity.class);
				startActivity(intent);
				MyApplication.getInstance().onLowMemory();
				BusinessDetailActivity.this.finish();
			}

		});

		basketImg = (ImageView) findViewById(R.id.shopping_img_cart);
		basketImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}
				Intent intent = new Intent(BusinessDetailActivity.this,
						BusinessOrderActivity.class);
				intent.putExtras(extraData);
				startActivity(intent);
			}
		});

		phoneImg = (ImageView) findViewById(R.id.img_shop_phone);
		phoneImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}
				creatPhoneDialog();
			}
		});

		slidingDrawer
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
					public void onDrawerOpened() {
						// textview.setVisibility(View.GONE);
						imageView
								.setImageResource(R.drawable.hong_menu_pull_down);
					}
				});
		slidingDrawer
				.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
					public void onDrawerClosed() {
						// textview.setVisibility(View.VISIBLE);
						imageView
								.setImageResource(R.drawable.hong_menu_pull_up);
					}
				});

		final SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.menu);
		// Set from XML, possible to programmatically set
		float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				175, getResources().getDisplayMetrics());
		menu.setSatelliteDistance((int) distance);
		menu.setExpandDuration(500);
		menu.setCloseItemsOnClick(true);
		menu.setTotalSpacingDegree(90);
		menu.setMainImage(R.drawable.ic_button);
		// menu.setFocusable(true);
		// menu.setFocusableInTouchMode(true);

		final List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(6, R.drawable.ic_phone));
		items.add(new SatelliteMenuItem(5, R.drawable.ic_search));
		items.add(new SatelliteMenuItem(4, R.drawable.ic_chat));
		items.add(new SatelliteMenuItem(3, R.drawable.ic_person));

		if (sp.contains(shopId + "BUSY")) {
			if (true == sp.getBoolean(shopId + "BUSY", false))
				items.add(new SatelliteMenuItem(2, R.drawable.ic_busy));
			else
				items.add(new SatelliteMenuItem(2, R.drawable.ic_idle));
			;
		} else
			items.add(new SatelliteMenuItem(2, R.drawable.ic_idle));

		if (sp.contains(shopId + "PRAISED")) {
			if (true == sp.getBoolean(shopId + "PRAISED", false))
				items.add(new SatelliteMenuItem(1, R.drawable.ic_praised));
			else
				items.add(new SatelliteMenuItem(1, R.drawable.ic_unpraise));
		} else
			items.add(new SatelliteMenuItem(1, R.drawable.ic_unpraise));

		menu.addItems(items);

		menu.setOnItemClickedListener(new SateliteClickedListener() {
			public void eventOccured(int id) {

				// GetShopByID____id____PRAISE
				switch (id) {
				case 1:
					String code = null;
					praisedStatus = true;
					code = "____PRAISE";
					message = "点赞成功";
					if (sp.contains(shopId + "PRAISED")) {
						if (true == sp.getBoolean(shopId + "PRAISED", false)) {
							praisedStatus = false;
							code = "____UNPRAISE";
							message = "取消赞成功";
						}
					}
					Down.get(DataOperations.webServer + "GetShopByID____"
							+ shopId + code, new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(Throwable error, String content) {
							// TODO Auto-generated method stub
							super.onFailure(error, content);
							DataOperations.toastMidShow(
									BusinessDetailActivity.this,
									"亲，网络不给力，点/取消赞失败，请稍后尝试...");
						}

						@Override
						public void onSuccess(int statusCode, String content) {
							// TODO Auto-generated method stub
							super.onSuccess(statusCode, content);
							if (true == praisedStatus) {
								sp.edit().putBoolean(shopId + "PRAISED", true)
										.commit();
								DataOperations.toastMidShow(
										BusinessDetailActivity.this, "亲，恭喜您，"
												+ message
												+ "!!!\n\n再次加载时，刷新点赞状态..");
							} else {
								creatPraiseClickDialog();
							}

						}
					});
					break;
				case 2:
					if (sp.contains(shopId + "BUSY")) {
						if (true == sp.getBoolean(shopId + "BUSY", false)) {
							sp.edit().putBoolean(shopId + "BUSY", false)
									.commit();
							DataOperations
									.toastMidShow(BusinessDetailActivity.this,
											"亲，您对该店的状态已反馈为: 空闲......\n\n再次加载时，可以看到您的反馈状态..");
							break;
						}
					}
					sp.edit().putBoolean(shopId + "BUSY", true).commit();
					DataOperations.toastMidShow(BusinessDetailActivity.this,
							"亲，您对该店的状态已反馈为: 忙......\n\n再次加载时，可以看到您的反馈状态..");
					break;
				case 3:
					Intent intent = new Intent();
					intent.setClass(BusinessDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
					break;
				case 4:
					DataOperations.toastMidShow(BusinessDetailActivity.this,
							"亲，该功能正在开发中..请耐心期待....");
					break;
				case 5:
					DataOperations.toastMidShow(BusinessDetailActivity.this,
							"亲，该功能正在开发中..请耐心期待....");
					break;
				case 6:
					creatPhoneDialog();
					break;
				default:
					DataOperations.toastMidShow(BusinessDetailActivity.this,
							"亲，您的点击动作有误，请确认按钮后点击....");
				}
			}
		});

		@SuppressWarnings("unused")
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		gridView.setFastScrollEnabled(true);

		mAdapter = new BasePictAdapter(BusinessDetailActivity.this);
		mAdapter.setIViewAddAndEventSet(new MenuPictureViewAdd(mAdapter));
		gridView.setAdapter(mAdapter);
		mAdapter.setCartBadgeView(tvNum);

		gridView.setOnItemClickListener(new BusinessMenuClickListener());

		handler = new ThumbHandler(BusinessDetailActivity.this, mAdapter);
		startDwonloadMenuListings(BusinessDetailActivity.this,
				extraData.getString("ShopID"));

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.updateDetailUI");
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);

	}

	public class BusinessMenuClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(StaggeredGridView parent, View view,
				int position, long id) {
			TextView tQView = (TextView) view.findViewById(R.id.news_quantity);
			CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(
					BusinessDetailActivity.this);
			dbHelper.open();
			int tQ = dbHelper.updateAfterClick(
					(Bundle) mAdapter.getItem(position), true);
			dbHelper.close();
			
			if (tQ > 0) {
				MenuPictureViewAdd.setChecked((Bundle) mAdapter.getItem(position), true);
			}
			if (MenuPictureViewAdd.getChecked((Bundle) mAdapter.getItem(position))) {
				tQView.setTextColor(Color.rgb(255, 99, 71));
				tQView.setText("已点: " + String.valueOf(tQ) + "份");
			} else {
				tQView.setTextColor(Color.rgb(0, 201, 87));
				tQView.setText("敬请品尝");
			}

			Intent intent = new Intent();
			intent.setAction("action.updateDetailUI");
			BusinessDetailActivity.this.sendBroadcast(intent);
		}
	}

	private void startDwonloadMenuListings(Context context, String request) {
		thread = new LoadingMenuListFromNet(context, handler, Down, request);
		thread.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 刷新方法
		refreshData();
		if (0 != totalNum) {
			tvNum.setText(String.valueOf(totalNum));
		}
		mAdapter.notifyDataSetChanged();
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
		MyApplication.getInstance().remove(BusinessDetailActivity.this);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (thread != null) {
				thread.setStop();
			}
			BusinessDetailActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
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
	 * 弹出提示拨叫对话框
	 */
	private void creatPhoneDialog() {
		new AlertDialog.Builder(this)
				.setMessage("亲，呼叫店家点餐: " + extraData.getString("PhoneNum"))
				.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent phoneIntent = new Intent(
								"android.intent.action.CALL", Uri.parse("tel:"
										+ extraData.getString("PhoneNum")));
						startActivity(phoneIntent);// 这个activity要把通话界面隐藏以及相应的把菜品界面展示；

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
	 * 广播：更新购物车总量
	 */
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("action.updateDetailUI")) {
				refreshData();
				if (0 == totalNum) {
					tvNum.setText("0");
					return;
				}
				tvNum.setText(String.valueOf(totalNum));
			}
		}
	};

	/*
	 * 更新购物车总量
	 */
	private void refreshData() {
		totalNum = 0;
		CartDatabaseAdapter dbHelper = new CartDatabaseAdapter(
				BusinessDetailActivity.this);
		dbHelper.open();
		totalNum = dbHelper.fetchTotalNumByID(extraData.getString("ShopID"));
		dbHelper.close();
	}

	/**
	 * 弹出提示取消赞对话框
	 */
	public void creatPraiseClickDialog() {
		new AlertDialog.Builder(this)
				.setMessage("亲，您已赞过该店，您确定要取消赞么!")
				.setPositiveButton("下次再说..",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setNegativeButton("取消赞吧..",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sp.edit()
										.putBoolean(
												extraData.getString("ShopID")
														+ "PRAISED", false)
										.commit();
								DataOperations
										.toastMidShow(
												BusinessDetailActivity.this,
												"亲，恭喜您，"
														+ message
														+ "!!\n重新打开该商家或刷新商家列表时，您可以看到您的点赞状态...");
							}
						}).show();
	}
}