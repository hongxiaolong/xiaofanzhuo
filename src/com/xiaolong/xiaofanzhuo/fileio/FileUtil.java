package com.xiaolong.xiaofanzhuo.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.xiaolong.xiaofanzhuo.dataoperations.DataOperations;

public class FileUtil {

	private static final String TAG = "FileUtil";

	private static final List<String> supportedFile = new ArrayList<String>();
	private static final String appDirName = "xiaofanzhuo";
	private static final String cacheDirName = "cache";

	static {
		supportedFile.add(".jpg");
		supportedFile.add(".jpeg");
		supportedFile.add(".png");
		supportedFile.add(".gif");
	}

	private static AsyncImageLoaderByPath asyncImageLoaderByPath = null;

	/**
	 * 异步加载SD卡图片
	 * 
	 * @param context
	 * @return
	 */
	public static AsyncImageLoaderByPath getSoftReferenceBitmapInstance() {
		if (null == asyncImageLoaderByPath)
			return asyncImageLoaderByPath = new AsyncImageLoaderByPath();
		return asyncImageLoaderByPath;
	}

	// 缓存，实际就是SD卡
	private static final String cacheDirPath = new StringBuffer(Environment
			.getExternalStorageDirectory().getAbsolutePath())
			.append(File.separator).append(appDirName).append(File.separator)
			.append(cacheDirName).toString();

	/**
	 * FileUtil的初始化
	 */
	public static void init() {
		System.out.println(cacheDirPath);
		File f = new File(cacheDirPath);// 新建缓存文件夹
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * true: 图片 false:非图片
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isPicture(String file) {
		String extension = getFileExtension(file);// 获取该文件的扩展名
		if (supportedFile.contains(extension.toLowerCase())) {// 判断是否是图片
			return true;
		}
		System.out.print("No picture types!");
		return false;
	}

	/**
	 * 获取文件的扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getFileExtension(String fileName) {
		int dot = fileName.lastIndexOf(".");
		if (dot != -1 && dot != 0) {
			return fileName.substring(dot);
		}
		return null;
	}

	/**
	 * 利用URL从网络获取Bitmap
	 * 
	 * @param url
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static Bitmap returnBitmap(String url) {

		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(url))
			return null;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(url))
			return null;

		String absolutPath = url.replaceAll("%2F", "/").replace("%3A", ":");// 得到绝对路径
		/*
		 * 解析中文Url
		 */
		String path[] = absolutPath.split("/");
		String species = path[path.length - 2];
		String file = path[path.length - 1];

		URL fileUrl = null;

		try {
			fileUrl = new URL("http://182.92.155.100/images/"
					+ java.net.URLEncoder.encode(species) + "/"
					+ java.net.URLEncoder.encode(file));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Bitmap bitmap = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) fileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;

	}

	/**
	 * 加载图片
	 * 
	 * @param name
	 * @return
	 */
	public static void downloadPic(String name) {

		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(name))
			return;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(name))
			return;

		String filePath = getCacheDirPath() + File.separator
				+ name.replaceAll("/", "%2F").replace(":", "%3A");
		File f = new File(filePath);

		if (!f.exists()) {
			Bitmap bmp = returnBitmap(name);
			saveBitmapToCache(name, bmp);
			return;
		}

		return;
	}

	/**
	 * 从cache中同步加载bitmap
	 * 
	 * @param name
	 * @return
	 */
	public static Bitmap loadBitmapFromCache(String name) {

		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(name))
			return null;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(name))
			return null;

		String filePath = getCacheDirPath() + File.separator
				+ name.replaceAll("/", "%2F").replace(":", "%3A");
		File f = new File(filePath);

		if (!f.exists())
			return null;

		Log.i(TAG, "Read from SD card:" + filePath);

		return BitmapFactory.decodeFile(filePath);
	}

	/**
	 * 从cache中异步加载bitmap
	 * @param imageView
	 * @param name
	 * @return Bitmap: f.exists null: f.notexist
	 */
	public static Bitmap loadBitmapFromCache(ImageView imageView, String name) {

		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(name))
			return null;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(name))
			return null;

		String filePath = getCacheDirPath() + File.separator
				+ name.replaceAll("/", "%2F").replace(":", "%3A");
		File f = new File(filePath);

		if (!f.exists())
			return null;

		Log.i(TAG, "Read from SD card:" + filePath);

		return getSoftReferenceBitmapInstance().loadBitmapByPath(filePath,
				imageView, new ImageLoader());
	}

	/**
	 * 将http://....格式转化为SD卡路径
	 * 
	 * @param name
	 *            = url
	 * @return
	 */
	public static String realCacheDirPath(String name) {
		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(name))
			return null;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(name))
			return null;

		String filePath = getCacheDirPath() + File.separator
				+ name.replaceAll("/", "%2F").replace(":", "%3A");
		File f = new File(filePath);

		if (!f.exists())
			return null;

		return filePath;
	}

	/**
	 * bitmap存储到cache中
	 * 
	 * @param name
	 * @param bitmap
	 * @return
	 */
	public static boolean saveBitmapToCache(String name, Bitmap bitmap) {

		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(name))
			return false;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(name))
			return false;

		/*
		 * 检测bitmap合法性
		 */
		if (null == bitmap)
			return false;

		String absolutPath = getCacheDirPath() + File.separator
				+ name.replaceAll("/", "%2F").replace(":", "%3A");// 得到绝对路径
		System.out.println("Save To SD card:" + absolutPath);
		File f = new File(absolutPath);
		try {
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * 获取缓存的目录
	 * 
	 * @return
	 */
	public static String getCacheDirPath() {
		return cacheDirPath;
	}

	/**
	 * 在IViewAddAndEventSet中被用来显示图片
	 * 
	 * @param imageView
	 * @param imageUrl
	 */
	public static void setImageSrc(ImageView imageView, String imageUrl) {

		/*
		 * 检测URL合法性
		 */
		if (DataOperations.isInvalidDataFromServer(imageUrl))
			return;
		/*
		 * 检测图片类型
		 */
		if (!isPicture(imageUrl))
			return;

		// String filePath = getCacheDirPath() + File.separator
		// + imageUrl.replaceAll("/", "%2F").replace(":", "%3A");
		// System.out.println("Read from SD card:" + filePath);
		//
		// BitmapFactory.Options option = new BitmapFactory.Options();
		// option.inJustDecodeBounds = true;
		// option.inSampleSize = getImageScale(filePath);
		// option.inJustDecodeBounds = false;

		// Bitmap bm = BitmapFactory.decodeFile(filePath, option);

		Bitmap bm = loadBitmapFromCache(imageView, imageUrl);
		
		if (null != bm)
			imageView.setImageBitmap(bm);
	}

	private static int IMAGE_MAX_WIDTH = 480;
	private static int IMAGE_MAX_HEIGHT = 960;

	/**
	 * scale image to fixed height and weight
	 * 
	 * @param imageUrl
	 * @return
	 */
	public static int getImageScale(String filePath) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		// set inJustDecodeBounds to true, allowing the caller to query the
		// bitmap info without having to allocate the
		// memory for its pixels.
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, option);

		int scale = 1;
		while (option.outWidth / scale >= IMAGE_MAX_WIDTH
				|| option.outHeight / scale >= IMAGE_MAX_HEIGHT) {
			scale *= 2;
		}
		return scale;
	}

	public static class ImageLoader implements
			AsyncImageLoaderByPath.ImageCallback {

		@Override
		public void imageLoaded(Bitmap imageBitmap, ImageView imageView,
				String imagePath) {
			// TODO Auto-generated method stub
			if (null != imageBitmap)
				imageView.setImageBitmap(imageBitmap);
			else {
				Log.e(TAG, "Function<<<<<<imageLoaded<<<<<Bitmap==null");
			}
		}
	}
}
