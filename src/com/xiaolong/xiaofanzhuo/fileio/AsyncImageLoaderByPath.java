package com.xiaolong.xiaofanzhuo.fileio;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncImageLoaderByPath {
	
    //SoftReference是软引用，是为了更好的为了系统回收变量
    private HashMap<String, SoftReference<Bitmap>> imageCache;
    
    public AsyncImageLoaderByPath() {
        this.imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }
    
    /**
     * 从缓存中异步加载Bitmap
     * @param imagePath
     * @param imageView
     * @param imageCallback
     * @return
     */
    public Bitmap loadBitmapByPath(final String imagePath, final ImageView imageView, final ImageCallback imageCallback) {
    	
        if (imageCache.containsKey(imagePath)) {
            //从缓存中获取
            SoftReference<Bitmap> softReference = imageCache.get(imagePath);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                return bitmap;
            }
        }
        
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageView, imagePath);
            }
        };
        
        //建立新一个获取SD卡的图片
        new Thread() {
            @Override
            public void run() {
            	Bitmap bitmap = BitmapFactory.decodeByteArray(BitmapUtil.decodeBitmap(imagePath), 0, BitmapUtil.decodeBitmap(imagePath).length);
                imageCache.put(imagePath, new SoftReference<Bitmap>(bitmap));
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        
        return null;
    }
    
    //回调接口
    public interface ImageCallback {
        public void imageLoaded(Bitmap imageBitmap,ImageView imageView, String imagePath);
    }
    
}
