package com.xiaolong.xiaofanzhuo.fileio;

import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;
import com.xiaolong.xiaofanzhuo.dataoperations.JSQLiteAdapter;

public class LoadingBusinessListFromNet extends Thread {

	private AsyncHttpClient Down = null;
	private Handler mHandler = null;
	private String mFields = null;
	private volatile boolean isRun = true;
	private String mData = null;
	private Context mContext;

	/**
	 * 
	 * @param context
	 * @param handler
	 *            对应Activity的handler
	 * @param Down
	 *            AsyncHttpClient
	 * @param fields
	 *            HTTP请求码
	 * @param data
	 *            区域字段
	 */
	// Read from webServer
	public LoadingBusinessListFromNet(Context context, Handler handler,
			AsyncHttpClient Down, String fields, String data) {
		super();
		setName(this.getClass().getSimpleName());
		this.Down = Down;
		this.mHandler = handler;
		this.mFields = fields;
		mData = data;
		mContext = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		/*
		 * 检查Android网络情况
		 */
		if (!DataOperations.isOpenNetwork(mContext)) {
			Message msg = mHandler.obtainMessage();
			msg.what = 5;
			mHandler.sendMessage(msg);
			return;
		}

		final String Download = DataOperations.webServer + this.mFields;
		Down.get(Download, new AsyncHttpResponseHandler() {

			@SuppressWarnings("deprecation")
			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				System.out.println("Failed DownloadBusinessNet!"
						+ "    Code<<<<<<=" + Download
						+ "    Error reponse from server<<<<<<=" + content);
				System.out.println(error.getStackTrace()[0].getMethodName());
				Message msg = mHandler.obtainMessage();
				msg.what = 3;
				mHandler.sendMessage(msg);
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(int statusCode, String content) {

				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				System.out.println("Succeed DownloadBusinessNet!"
						+ "    Code<<<<<<=" + Download
						+ "    Success reponse from server<<<<<<=" + content);

				/*
				 * 检查服务端是否返回null或""
				 */
				if (DataOperations.isInvalidDataFromServer(content)) {
					Message msg = mHandler.obtainMessage();
					msg.what = 3;
					mHandler.sendMessage(msg);
					return;
				}

				/*
				 * json更新SQLite
				 */
				List<Bundle> datas = null;
				try {
					JSQLiteAdapter jsonLiteAdapter = new JSQLiteAdapter(
							mContext, mData);
					jsonLiteAdapter.open();
					
					jsonLiteAdapter.clean();
					
					/*
					 * 需要异常处理，若数据异常则不插入SQLite，防止程序崩溃
					 */
					if (jsonLiteAdapter.insertFood(content))
						datas = jsonLiteAdapter.fetchShopIndex();
					jsonLiteAdapter.close();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * 下拉刷新时网络请求成功后需要清空一次
				 */
				if (null != datas) {
					Message msgClear = mHandler.obtainMessage();
					msgClear.what = 2;
					mHandler.sendMessage(msgClear);
				}

				for (Bundle data : datas) {
					
					/*
					 * 终止线程时终止循环
					 */
					if (!isRun)
						break;
					
					FileUtil.getBitMapIfNecessary(data.getString("ShopImgUrl"));// 由图片url获取bitmap
					Message msg = mHandler.obtainMessage();
					msg.setData(data);
					msg.what = 1;
					mHandler.sendMessage(msg);
				}

			}

		});

	}

	public void setStop() {
		this.isRun = false;
	}

	public boolean isRun() {
		return isRun;
	}
}
