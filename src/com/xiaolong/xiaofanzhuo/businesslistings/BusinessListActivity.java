package com.xiaolong.xiaofanzhuo.businesslistings;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	ImageButton button0, button1, button2, button3, button4;
	private String data = null;
	String fields = null;
	private ImageButton buttonBack;
	private ImageButton buttonHome;
	private TextView titleView;

	/*
	 * 联合搜索
	 */
	private Bundle popData = new Bundle();

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
				if (threadNet != null) {
					threadNet.setStop();
				}

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
				if (threadNet != null) {
					threadNet.setStop();
				}
				/*
				 * 强制登录，增加注册量
				 */
				if (false == forceLogin(BusinessListActivity.this))
					return;
				Bundle b = (Bundle) mAdapter.getItem(position);
				b.putString("fields", data);// 把数据库识别码发至MenuActivity
				Intent intent = new Intent(BusinessListActivity.this,
						BusinessDetailActivity.class);
				intent.putExtras(b);
				startActivity(intent);
			}
		});

		handler = new ThumbHandler(BusinessListActivity.this, mAdapter);

		/*
		 * 筛选标签按钮
		 */
		button0 = (ImageButton) findViewById(R.id.popmenu_whole);
		button1 = (ImageButton) findViewById(R.id.type);
		button2 = (ImageButton) findViewById(R.id.dis);
		button3 = (ImageButton) findViewById(R.id.free_or_not);
		button4 = (ImageButton) findViewById(R.id.iswaimai);

		ButtonClickEffect.setButtonFocusChanged(button0);
		ButtonClickEffect.setButtonFocusChanged(button1);
		ButtonClickEffect.setButtonFocusChanged(button2);
		ButtonClickEffect.setButtonFocusChanged(button3);
		ButtonClickEffect.setButtonFocusChanged(button4);

		/*
		 * 筛选按钮
		 */
		popMenu_type = new PopMenu(this);
		final String[] SHOPTAGS = new String[] { "湘菜", "川菜", "家常菜", "重庆菜",
				"港式", "西餐", "小吃", "清真菜" };
		popMenu_type.addItems(SHOPTAGS);
		// 菜单项点击监听器
		popMenu_type.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				popData.putString("popMenu_type", SHOPTAGS[position]);
				// filterByTag(SHOPTAGS[position]);
				filterByJoint(popData);

				/*
				 * 修改为点击后的标签
				 */
				switch (position) {
				case 0:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_0));
					break;
				case 1:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_1));
					break;
				case 2:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_2));
					break;
				case 3:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_3));
					break;
				case 4:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_4));
					break;
				case 5:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_5));
					break;
				case 6:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_6));
					break;
				case 7:
					button1.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_type_7));
					break;
				default:
					break;
				}

				popMenu_type.dismiss();
			}
		});

		popMenu_dis = new PopMenu(this);
		popMenu_dis.addItems(new String[] { "<=20", "20 - 50", ">=50" });
		// 菜单项点击监听器
		popMenu_dis.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					popData.putInt("popMenu_dis", 0);
					// filterByAvg(0, 20);
					filterByJoint(popData);
					button2.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_dis_0));
					break;
				case 1:
					popData.putInt("popMenu_dis", 1);
					// filterByAvg(20, 50);
					filterByJoint(popData);
					button2.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_dis_1));
					break;
				case 2:
					popData.putInt("popMenu_dis", 2);
					// filterByAvg(50, 1000);
					filterByJoint(popData);
					button2.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_dis_2));
					break;
				default:
					break;
				}
				popMenu_dis.dismiss();
			}
		});

		/*
		 * 全部
		 */
		button0.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popData.clear();
				filterByTag("全部");
				button0.setImageDrawable(getResources().getDrawable(
						R.drawable.popmenu_whole_0));
				button1.setImageDrawable(getResources().getDrawable(
						R.drawable.popmenu_type));
				button2.setImageDrawable(getResources().getDrawable(
						R.drawable.popmenu_dis));
				button3.setImageDrawable(getResources().getDrawable(
						R.drawable.popmenu_free));
				button4.setImageDrawable(getResources().getDrawable(
						R.drawable.popmenu_wai));
			}
		});

		/*
		 * 特色筛选
		 */
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu_type.showAsDropDown(v);
			}
		});

		/*
		 * 均价筛选
		 */
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu_dis.showAsDropDown(v);
			}
		});

		/*
		 * 忙闲筛选, false = 闲
		 */
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (true == popData.getBoolean("popMenu_free")) {
					/*
					 * 清除闲
					 */
					popData.remove("popMenu_free");
					button3.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_free));
				} else {

					if (!popData.containsKey("popMenu_free")) {
						/*
						 * 点击闲
						 */
						popData.putBoolean("popMenu_free", false);
						button3.setImageDrawable(getResources().getDrawable(
								R.drawable.popmenu_free_0));
					} else {
						/*
						 * 还原原始状态
						 */
						popData.remove("popMenu_free");
						button3.setImageDrawable(getResources().getDrawable(
								R.drawable.popmenu_free));
					}
				}

				filterByJoint(popData);

			}
		});

		/*
		 * 外卖筛选, true=可外卖
		 */
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (true == popData.getBoolean("popMenu_wai")) {
					/*
					 * 清除外卖
					 */
					popData.remove("popMenu_wai");
					button4.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_wai));
				} else {
					/*
					 * 点击外卖
					 */
					popData.putBoolean("popMenu_wai", true);
					button4.setImageDrawable(getResources().getDrawable(
							R.drawable.popmenu_wai_0));
				}

				// filterByWai(true);
				filterByJoint(popData);

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
					Message message = handlerRefresh.obtainMessage(0, null);  
					handlerRefresh.sendMessage(message);  
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

	/**
	 * 联合筛选
	 * 
	 * @param isWai
	 */
	private void filterByJoint(Bundle joint) {

		JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
				BusinessListActivity.this, data);
		jsonLiteAdapter.open();
		List<Bundle> datas = null;
		datas = jsonLiteAdapter.fetchShopIndexByJoint(joint);
		jsonLiteAdapter.close();

		Message msgClear = handler.obtainMessage();
		msgClear.what = 2;
		handler.sendMessage(msgClear);

		if (datas.size() <= 0) {
			Toast.makeText(BusinessListActivity.this, "亲，没有符合要求的数据....",
					Toast.LENGTH_SHORT).show();
			return;
		}

		button0.setImageDrawable(getResources().getDrawable(
				R.drawable.popmenu_whole));

		for (Bundle f : datas) {
			Message msg = handler.obtainMessage();
			msg.setData(f);
			msg.what = 1;
			handler.sendMessage(msg);
		}
	}
	
	public Handler handlerRefresh = new Handler() {
		public void handleMessage(Message message) {
			popData.clear();
			filterByJoint(popData);
			button0.setImageDrawable(getResources().getDrawable(
					R.drawable.popmenu_whole));
			button1.setImageDrawable(getResources().getDrawable(
					R.drawable.popmenu_type));
			button2.setImageDrawable(getResources().getDrawable(
					R.drawable.popmenu_dis));
			button3.setImageDrawable(getResources().getDrawable(
					R.drawable.popmenu_free));
			button4.setImageDrawable(getResources().getDrawable(
					R.drawable.popmenu_wai));
		}
	};

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (threadLocal != null) {
				threadLocal.setStop();
			}
			if (threadNet != null) {
				threadNet.setStop();
			}
			BusinessListActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		if (threadLocal != null) {
			threadLocal.setStop();
		}
		if (threadNet != null) {
			threadNet.setStop();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (threadLocal != null) {
			threadLocal.setStop();
		}
		if (threadNet != null) {
			threadNet.setStop();
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
