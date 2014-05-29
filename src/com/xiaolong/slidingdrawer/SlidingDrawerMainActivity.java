package com.xiaolong.slidingdrawer;

import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class SlidingDrawerMainActivity extends Activity {

	private GridView gridView;
	private SlidingDrawer slidingDrawer;
	private ImageView imageView;
	private TextView textview;

	private int[] icons = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher };

	private String[] items = { "Phone", "Message", "AddImage", "Music",
			"Telephone", "SMS" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		gridView = (GridView) findViewById(R.id.mycontent);
		slidingDrawer = (SlidingDrawer) findViewById(R.id.sliding_drawer);
		imageView = (ImageView) findViewById(R.id.my_image);
		textview = (TextView) findViewById(R.id.text_view);
		MyGridViewAdapter adapter = new MyGridViewAdapter(this, items, icons);
		gridView.setAdapter(adapter);
		slidingDrawer
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

					public void onDrawerOpened() {
						textview.setVisibility(View.GONE);
						imageView.setImageResource(R.drawable.ic_launcher);
					}
				});
		slidingDrawer
				.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

					public void onDrawerClosed() {
						textview.setVisibility(View.VISIBLE);
						imageView.setImageResource(R.drawable.ic_launcher);
					}
				});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}