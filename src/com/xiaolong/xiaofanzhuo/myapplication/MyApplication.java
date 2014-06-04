package com.xiaolong.xiaofanzhuo.myapplication;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

import com.loopj.android.http.AsyncHttpClient;

/**
 * MyApplication
 * 
 * @author hongxiaolong
 * 
 */
public class MyApplication extends Application {

	//android-async-http
	private AsyncHttpClient mDownThreadPool = new AsyncHttpClient();
	
	public AsyncHttpClient getDownThreadPool() {
		return this.mDownThreadPool;
	}
	
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
	
	/*
	 * 更新提示，只提示一次
	 */
	private boolean updateNotification = false;
	
	public void setUpdateNotification() {
		updateNotification = true;
		return;
	}
	
	/**
	 * true 已经提示过，不再提示
	 * false 未提示，提示升级
	 * @return
	 */
	public boolean isPushedNotification () {
		return updateNotification;
	}

	public void setQueueStatus(boolean statue) {
		this.myQueueStatus = statue;
		return;
	}

	public boolean getQueueStatus() {
		return this.myQueueStatus;
	}

	public void setBusyStatus(boolean statue) {
		this.myBusyStatus = statue;
		return;
	}

	public boolean getBusyStatus() {
		return this.myBusyStatus;
	}

	public void setPraiseStatus(boolean statue) {
		this.myPraiseStatus = statue;
		return;
	}

	public boolean getPraiseStatus() {
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
	 * 如果activity已经 destory了 就移除
	 * 
	 * @param activity
	 */
	public void remove(Activity activity) {

		list.remove(activity);

	}

	/**
	 * 退出集合所有的activity
	 */
	public void exit() {
		try {
			for (Activity activity : list) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
	
}
