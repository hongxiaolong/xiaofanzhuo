package com.xiaolong.xiaofanzhuo.myapplication;


import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

/**
 * MyApplication
 * @author hongxiaolong
 *
 */
public class MyApplication extends Application{
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.setQueueStatus(false);
		this.setBusyStatus(false);
		this.setPraiseStatus(false);
	}

	private boolean myQueueStatus;
	private boolean myBusyStatus;
	private boolean myPraiseStatus;
	
	public void setQueueStatus(boolean statue)
	{
		this.myQueueStatus = statue;
		return;
	}
	
	public boolean getQueueStatus()
	{
		return this.myQueueStatus;
	}
	
	public void setBusyStatus(boolean statue)
	{
		this.myBusyStatus = statue;
		return;
	}
	
	public boolean getBusyStatus()
	{
		return this.myBusyStatus;
	}
	
	public void setPraiseStatus(boolean statue)
	{
		this.myPraiseStatus = statue;
		return;
	}
	
	public boolean getPraiseStatus()
	{
		return this.myPraiseStatus;
	}
	
	// 存放activity的集合
	  private ArrayList<Activity> list = new ArrayList<Activity>();
	  private static MyApplication instance;

	  public MyApplication() {
	  }

	  /**
	   * 利用单例模式获取MyAppalication实例
	   * 
	   * @return
	   */
	  public static MyApplication getInstance() {
	    if (null == instance) {
	      instance = new MyApplication();
	    }
	    return instance;
	  }

	  /**
	   * 添加activity到list集合
	   * 
	   * @param activity
	   */
	  public void addActivity(Activity activity) {
	    list.add(activity);

	  }

	  /**
	   * 退出集合所有的activity
	   */
	  public void exit() {
	    try {
	      for (Activity activity : list) {
	        activity.finish();
	      }
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      System.exit(0);
	    }
	  }

	  @Override
	  public void onLowMemory() {
	    super.onLowMemory();
	    System.gc();
	  }
}
