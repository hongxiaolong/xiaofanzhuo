package com.xiaolong.xiaofanzhuo.businesslistings;

import com.xiaolong.xiaofanzhuo.businessdetails.BussinessDetailActivity;
import com.xiaolong.xiaofanzhuo.businessdetails.MenuPictureViewAdd;
import com.xiaolong.xiaofanzhuo.fileio.BasePictAdapter;
import com.xiaolong.xiaofanzhuo.fileio.DownloadPict;
import com.xiaolong.xiaofanzhuo.fileio.ThumbHandler;
import com.xiaolong.xiaofanzhuo.myapplication.BaseActivity;
import com.xiaolong.xiaofanzhuo_xiaolonginfo.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.View;


public class BussinessListActivity extends BaseActivity 
{
	private DownloadPict thread = null;
	private Handler handler = null;
	private BasePictAdapter mAdapter;
	private ListView list;
	PopMenu popMenu_type;
	PopMenu popMenu_dis;
	PopMenu popMenu_free;
	PopMenu popMenu_iswaimai;
	boolean isloading=false;
	ImageButton button1,button2,button3,button4;
	String target="";
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liu_activity_res);
		Intent intent=this.getIntent();
		String area=intent.getStringExtra("area");
		if(area.equalsIgnoreCase("1"))
			target="GetShopUsernameListByLocation____大山子";
		else
			if(area.equalsIgnoreCase("2"))
				target="GetShopUsernameListByLocation____798";
			else
				target="GetShopUsernameListByLocation____望京";
		list=(ListView) findViewById(R.id.listView1);
		mAdapter = new BasePictAdapter(BussinessListActivity.this);
		mAdapter.setIViewAddAndEventSet(new ResPictureViewAdd(mAdapter));
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	@Override
	        	public void onItemClick(AdapterView<?> parent, View view,
	        	int position, long id) 
	        	{
	        		Bundle b=(Bundle) mAdapter.getItem(position);
					Intent intent = new Intent(BussinessListActivity.this, BussinessDetailActivity.class); 
					intent.putExtras(b);
					startActivity(intent);
	        	// TODO Auto-generated method stub
	        	}
	        	});
	       
		
		
		handler = new ThumbHandler(mAdapter);
		
		 popMenu_type = new PopMenu(this);
			popMenu_type.addItems(new String[] { "烧烤", "贵州菜", "西餐", "鲁菜", "清真菜","全部"});
			// 菜单项点击监听器
			popMenu_type.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					}
				});
			
			popMenu_dis = new PopMenu(this);
			popMenu_dis.addItems(new String[] { "<50", "50-100", ">100", "全部" });
			// 菜单项点击监听器
			popMenu_dis.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					
				}
			}
			);
			
			popMenu_free = new PopMenu(this);
			popMenu_free.addItems(new String[] { "闲", "忙", "全部" });
			// 菜单项点击监听器
			popMenu_free.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) 
				{
					
				}
			});
			
			popMenu_iswaimai = new PopMenu(this);
			popMenu_iswaimai.addItems(new String[] { "送外卖", "无外卖","全部"});
			// 菜单项点击监听器
			popMenu_iswaimai.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					
				}
			});
			
			button1 = (ImageButton) findViewById(R.id.type);
			button1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					popMenu_type.showAsDropDown(v);
				}
			});
	        
			button2 = (ImageButton) findViewById(R.id.dis);
			button2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					popMenu_dis.showAsDropDown(v);
				}
			});
			
			
			button3 = (ImageButton) findViewById(R.id.free_or_not);
			button3.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					popMenu_free.showAsDropDown(v);
				}
			});
			
			button4 = (ImageButton) findViewById(R.id.iswaimai);
			button4.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					popMenu_iswaimai.showAsDropDown(v);
				}
			});
			
				
		
		start(target);
	}
	
	private void start(String requestCode) {
		thread = new DownloadPict(handler, requestCode);
		thread.start();
	}

}
