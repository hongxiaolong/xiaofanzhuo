package com.xiaolong.xiaofanzhuo.fileio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

public class GetResponseFromServerAction {

	public static String webServer = "http://182.92.80.201/2.php?content=";

	@SuppressWarnings("unused")
	private String mResult = null;

	/**
	 * @param id 请求码
	 * @return String
	 */
	public String getStringFromServerById(String id)
			throws InterruptedException, Throwable {
		return requestFromServer(id);
	}
	
	/**
	 * @param id 请求码
	 * @return List-Imageurl
	 */
	public List<String> getStringListFromServerById(String id)
			throws InterruptedException, Throwable {
		List<String> list = new ArrayList<String>();
		String[] ret = splitFromStringBySymbol(requestFromServer(id), "\n");
		for (int i = 0; i < ret.length; ++i)
			list.add(ret[i]);
		return list;
	}

	private String requestFromServer(String requestCode)
			throws InterruptedException, Throwable {
		httpAsyncTask task = new httpAsyncTask(requestCode);
		return task.execute(webServer).get();
	}

	public static String[] splitFromStringBySymbol(String orginString,
			String symbol) {
		return orginString.split(symbol);
	}

	@SuppressWarnings("deprecation")
	private  String requestFromWebServer(String httpServer, String httpCode) {
		String ret = "";
		String finalServer = httpServer + URLEncoder.encode(httpCode);
		URL url = null;
		try {
			url = new URL(finalServer);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection(); // 创建一个HTTP连接
			InputStreamReader in = new InputStreamReader(
					urlConn.getInputStream()); // 获得读取的内容
			BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
			String inputLine = null;
			// 通过循环逐行读取输入流中的内容
			while ((inputLine = buffer.readLine()) != null) {
				ret += inputLine + "\n";
			}
			in.close(); // 关闭字符输入流对象
			urlConn.disconnect(); // 断开连接
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private class httpAsyncTask extends AsyncTask<String, Integer, String> {

		private String mCode;

		public httpAsyncTask(String code) {
			super();
			mCode = code;
		}

		@Override
		protected String doInBackground(String... params) {
			String ret = null;
			try {
				ret = requestFromWebServer(params[0], mCode);
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	};
}
