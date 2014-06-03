package com.xiaolong.xiaofanzhuo.businessdetails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.origamilabs.library.views.StaggeredGridView;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.DownloadPict;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo.myapplication.MyApplication;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

/**
 * BussinessDetailActivity
 * 
 * @author hongxiaolong
 * 
 */

@SuppressWarnings("deprecation")
public class BussinessDetailActivity extends BaseActivity {

	private DownloadPict thread = null;
	private Handler handler = null;
	private BasePictAdapter mAdapter;
	private SlidingDrawer slidingDrawer;
	private ImageView imageView;
	private TextView textview;
	private ImageButton buttonBack;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hong_business_detai);

		StaggeredGridView gridView = (StaggeredGridView) this
				.findViewById(R.id.mycontent);

		slidingDrawer = (SlidingDrawer) findViewById(R.id.sliding_drawer);
		imageView = (ImageView) findViewById(R.id.my_image);
		textview = (TextView) findViewById(R.id.text_view);
		buttonBack = (ImageButton) findViewById(R.id.menu_button_back);
		buttonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (thread != null) {
					thread.setStop();
				}
				BussinessDetailActivity.this.finish();
			}
		});

		slidingDrawer
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
					public void onDrawerOpened() {
						textview.setVisibility(View.GONE);
						imageView.setImageResource(R.drawable.hong_menu_pull_up);
					}
				});
		slidingDrawer
				.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
					public void onDrawerClosed() {
						textview.setVisibility(View.VISIBLE);
						imageView.setImageResource(R.drawable.hong_menu_pull_up);
					}
				});

		@SuppressWarnings("unused")
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		gridView.setFastScrollEnabled(true);

		mAdapter = new BasePictAdapter(BussinessDetailActivity.this);
		mAdapter.setIViewAddAndEventSet(new MenuPictureViewAdd(mAdapter));
		gridView.setAdapter(mAdapter);

		handler = new ThumbHandler(mAdapter);
		start("GetShopMenuInfo__GetShopInfoByUsername____18844445556");

	}

	private void start(String requestCode) {
		thread = new DownloadPict(handler, requestCode);
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
		super.onDestroy();
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