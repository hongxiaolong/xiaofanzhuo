package com.xiaolong.xiaofanzhuo.myapplication;

import android.app.Activity;
import android.os.Bundle;

/**
 * BaseActivity
 * 
 * @author hongxiaolong
 * 
 */

public class BaseActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    MyApplication.getInstance().addActivity(BaseActivity.this);
	  }
	
}
