package com.xiaolong.xiaofanzhuo.businesslistings;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.xiaolong.xiaofanzhuo.businessdetails.BusinessDetailActivity;
import com.xiaolong.xiaofanzhuo.businesslistings.RefreshableView.PullToRefreshListener;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.JSQLiteAdapter;
import com.xiaolong.xiaofanzhuo.enteractivity.ZoneShowActivity;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.LoadingBusinessListFromLocal;
import com.xiaolong.xiaofanzhuo.fileio.LoadingBusinessListFromNet;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.ButtonClickEffect;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

public class BusinessListActivity extends BaseActivity {

	private RefreshableView refreshableView;
	private LoadingBusinessListFromLocal threadLocal = null;
	private LoadingBusinessListFromNet threadNet = null;
	private Handler handler = null;
	private BasePictAdapter mAdapter;
	private ListView list;
	PopMenu popMenu_type;
	PopMenu popMenu_dis;
	PopMenu popMenu_free;
	PopMenu popMenu_iswaimai;
	boolean isloading = false;
	ImageButton button1, button2, button3, button4;
	private String data = null;
	String fields = null;
	private ImageButton buttonBack;
	private ImageButton buttonHome;
	private TextView titleView;

	private AsyncHttpClient Down = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(BusinessListActivity.this);
		MyApplication.getInstance().onLowMemory();
		Down = MyApplication.getInstance().getDownThreadPool();

		setContentView(R.layout.liu_activity_res);

		mMainFrameTask = new MainFrameTask(this);
		mMainFrameTask.setLoadingTime(2);
		mMainFrameTask.execute();

		Intent intent = this.getIntent();
		this.data = intent.getStringExtra("area");
		String nameArea = null;
		if (data.equals("DASHANZI")) {
			nameArea = "大山子";
			fields = "GetShopListByLocation____DASHANZI";
		} else if (data.equals("798")) {
			nameArea = "798";
			fields = "GetShopListByLocation____798";
		} else if (data.equals("WANGJING")) {
			nameArea = "望京";
			fields = "GetShopListByLocation____WANGJING";
		}

		titleView = (TextView) findViewById(R.id.detail_title);
		DataOperations.setTypefaceForTextView(BusinessListActivity.this,
				titleView);
		titleView.setText(nameArea);

		buttonBack = (ImageButton) findViewById(R.id.button_back);
		buttonHome = (ImageButton) findViewById(R.id.button_home);

		ButtonClickEffect.setButtonFocusChanged(buttonBack);
		ButtonClickEffect.setButtonFocusChanged(buttonHome);

		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (threadLocal != null) {
					threadLocal.setStop();
				}

				MyApplication.getInstance().onLowMemory();
				BusinessListActivity.this.finish();

			}
		});

		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BusinessListActivity.this,
						ZoneShowActivity.class);
				startActivity(intent);
				MyApplication.getInstance().onLowMemory();
				BusinessListActivity.this.finish();
			}

		});

		list = (ListView) findViewById(R.id.listView1);
		mAdapter = new BasePictAdapter(BusinessListActivity.this);
		mAdapter.setIViewAddAndEventSet(new ResPictureViewAdd(mAdapter));
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (threadLocal != null) {
					threadLocal.setStop();
				}

				/*
				 * 强制登录，增加注册量
				 */
				if (false == forceLogin(BusinessListActivity.this)) {
					return;
				}
				
				/*
				 * 跳转BusinessDetailActivity
				 */
				//Bundle b = (Bundle) mAdapter.getItem(position);
				//b.putString("fields", data);// 把数据库识别码发至MenuActivity
				Intent intent = new Intent(BusinessListActivity.this,
						BusinessDetailActivity.class);
				//intent.putExtras(b);
				startActivity(intent);
				BusinessListActivity.this.finish();
			}
		});

		handler = new ThumbHandler(BusinessListActivity.this, mAdapter);

		/*
		 * 筛选按钮
		 */
		popMenu_type = new PopMenu(this);
		final String[] SHOPTAGS = new String[] { "全部", "湘菜", "家常菜", "西餐", "鲁菜",
				"清真菜", "港式" };
		popMenu_type.addItems(SHOPTAGS);
		// 菜单项点击监听器
		popMenu_type.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				filterByTag(SHOPTAGS[position]);
				popMenu_type.dismiss();
			}
		});

		popMenu_dis = new PopMenu(this);
		popMenu_dis.addItems(new String[] { "<20", "20-50", ">50" });
		// 菜单项点击监听器
		popMenu_dis.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					filterByAvg(0, 20);
					break;
				case 1:
					filterByAvg(20, 50);
					break;
				case 3:
					filterByAvg(50, 1000);
					break;
				default:
					break;
				}
				popMenu_dis.dismiss();
			}
		});

		popMenu_free = new PopMenu(this);
		popMenu_free.addItems(new String[] { "闲", "忙" });
		// 菜单项点击监听器
		popMenu_free.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					filterByBusy(false);
					break;
				case 1:
					filterByBusy(true);
					break;
				default:
					break;
				}
				popMenu_free.dismiss();
			}
		});

		popMenu_iswaimai = new PopMenu(this);
		popMenu_iswaimai.addItems(new String[] { "送外卖", "无外卖" });
		// 菜单项点击监听器
		popMenu_iswaimai.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					filterByWai(true);
					break;
				case 1:
					filterByWai(false);
					break;
				default:
					break;
				}
				popMenu_iswaimai.dismiss();
			}
		});

		/*
		 * 特色筛选
		 */
		button1 = (ImageButton) findViewById(R.id.type);
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu_type.showAsDropDown(v);
			}
		});

		/*
		 * 均价筛选
		 */
		button2 = (ImageButton) findViewById(R.id.dis);
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu_dis.showAsDropDown(v);
			}
		});

		/*
		 * 忙闲筛选
		 */
		button3 = (ImageButton) findViewById(R.id.free_or_not);
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu_free.showAsDropDown(v);
			}
		});

		/*
		 * 外卖筛选
		 */
		button4 = (ImageButton) findViewById(R.id.iswaimai);
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu_iswaimai.showAsDropDown(v);
			}
		});

		/*
		 * 下拉刷新，从服务端下载json数据
		 */
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				startDwonloadBusinessNet(BusinessListActivity.this, Down,
						fields, data);

				try {
					MainFrameTask task = new MainFrameTask(
							BusinessListActivity.this);
					task.setLoadingTime(3);
					task.execute();
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		/*
		 * 若第一次加载，从服务端下载json数据，否则加载本地数据
		 */
		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
				BusinessListActivity.this, data);
		jsonLiteAdapter.open();
		if (jsonLiteAdapter.isEmpty())
			startDwonloadBusinessNet(BusinessListActivity.this, Down, fields,
					data);
		else
			startDwonloadBusinessLocal(BusinessListActivity.this, data);
		jsonLiteAdapter.close();

	}

	/**
	 * 根据种类筛选
	 * 
	 * @param tag
	 */
	private void filterByTag(String tag) {

		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
				BusinessListActivity.this, data);
		jsonLiteAdapter.open();
		List<Bundle> datas = null;
		datas = jsonLiteAdapter.fetchShopIndexByTag(tag);
		jsonLiteAdapter.close();

		Message msgClear = handler.obtainMessage();
		msgClear.what = 2;
		handler.sendMessage(msgClear);

		for (Bundle f : datas) {
			Message msg = handler.obtainMessage();
			msg.setData(f);
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 根据均价筛选
	 * 
	 * @param isBusy
	 */
	private void filterByAvg(float low, float high) {

		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
				BusinessListActivity.this, data);
		jsonLiteAdapter.open();
		List<Bundle> datas = null;
		datas = jsonLiteAdapter.fetchShopIndexByAvg(low, high);
		jsonLiteAdapter.close();

		Message msgClear = handler.obtainMessage();
		msgClear.what = 2;
		handler.sendMessage(msgClear);

		for (Bundle f : datas) {
			Message msg = handler.obtainMessage();
			msg.setData(f);
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 根据忙闲状态筛选
	 * 
	 * @param isBusy
	 */
	private void filterByBusy(boolean isBusy) {

		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
				BusinessListActivity.this, data);
		jsonLiteAdapter.open();
		List<Bundle> datas = null;
		datas = jsonLiteAdapter.fetchShopIndexByBusyState(isBusy);
		jsonLiteAdapter.close();

		Message msgClear = handler.obtainMessage();
		msgClear.what = 2;
		handler.sendMessage(msgClear);

		for (Bundle f : datas) {
			Message msg = handler.obtainMessage();
			msg.setData(f);
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 根据外卖筛选
	 * 
	 * @param isWai
	 */
	private void filterByWai(boolean isWai) {

		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
				BusinessListActivity.this, data);
		jsonLiteAdapter.open();
		List<Bundle> datas = null;
		datas = jsonLiteAdapter.fetchShopIndexByTakeOut(isWai);
		jsonLiteAdapter.close();

		Message msgClear = handler.obtainMessage();
		msgClear.what = 2;
		handler.sendMessage(msgClear);

		for (Bundle f : datas) {
			Message msg = handler.obtainMessage();
			msg.setData(f);
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}

	private void startDwonloadBusinessNet(Context context,
			AsyncHttpClient Down, String fields, String data) {
		threadNet = new LoadingBusinessListFromNet(context, handler, Down,
				fields, data);
		threadNet.start();
	}

	private void startDwonloadBusinessLocal(Context context, String data) {
		threadLocal = new LoadingBusinessListFromLocal(context, handler, data);
		threadLocal.start();
	}

	@Override
	protected void onPause() {
		if (threadLocal != null) {
			threadLocal.setStop();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (threadLocal != null) {
			threadLocal.setStop();
		}
		/*
		 * 停止加载框
		 */
		if (mMainFrameTask != null && !mMainFrameTask.isCancelled()) {
			mMainFrameTask.cancel(true);
		}
		super.onDestroy();
		MyApplication.getInstance().remove(BusinessListActivity.this);
	}
}
