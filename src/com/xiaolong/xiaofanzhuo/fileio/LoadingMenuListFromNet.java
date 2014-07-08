package com.xiaolong.xiaofanzhuo.fileio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;

public class LoadingMenuListFromNet extends Thread {

	private AsyncHttpClient Down = null;
	private Handler mHandler = null;
	private volatile boolean isRun = true;
	private String id = null;
	private Context mContext;

	public static final String[] MENULIST_COLUMN = new String[] { "_id",
			"Category", "ThumbUrl", "img_thumb_higth", "IsSpec", "Width",
			"FoodPrice", "IsRecommend", "Food", "ShopID", "Height",
			"img_thumb_width", "FoodImgUrl" };

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
	public LoadingMenuListFromNet(Context context, Handler handler,
			AsyncHttpClient Down, String id) {
		super();
		setName(this.getClass().getSimpleName());
		this.Down = Down;
		this.mHandler = handler;
		this.id = id;
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

		/*
		 * GetMenuListByID____shop_id
		 */
		final String Download = DataOperations.webServer
				+ "GetMenuListByID____" + this.id;
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

				/**
				 * 这里需要分析服务器回传的json格式数据
				 */
				try {
					JSONObject obj = new JSONObject(content);
					String menulistings = obj.getString("menulistings" + id);
					JSONArray arr = new JSONArray(menulistings);
					for (int i = 0; i < arr.length(); i++) {

						/*
						 * 终止线程时终止循环
						 */
						if (!isRun)
							break;

						JSONObject json = (JSONObject) arr.get(i);

						Bundle data = new Bundle();
						for (String f : MENULIST_COLUMN) {
							data.putString(f, json.getString(f));
						}

						FileUtil.getBitMapIfNecessary(data
								.getString("FoodImgUrl"));// 由图片url获取bitmap
						Message msg = mHandler.obtainMessage();
						msg.setData(data);
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
