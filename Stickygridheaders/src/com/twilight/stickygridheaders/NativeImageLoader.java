package com.twilight.stickygridheaders;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 * 
 * @note 本地图片加载器，异步解析本地图片，单例模式
 * 
 */

public class NativeImageLoader {
	private static final String TAG = NativeImageLoader.class.getSimpleName();
	private static NativeImageLoader m_instance = new NativeImageLoader();
	private static LruCache<String, Bitmap> m_memoryCache;
	private ExecutorService m_imageThreadPool = Executors.newFixedThreadPool(1);
	
	private NativeImageLoader() {
		//获取应用程序的最大内存
		final int maxMemory = (int)(Runtime.getRuntime().maxMemory());
		
		//用最大内存的1/8来存储图片
		final int cacheSize = maxMemory / 8;
		m_memoryCache = new LruCache<String, Bitmap>(cacheSize) {
			//获取每张图片的bytes
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}
	
	/**
	 * 获取NativeImageLoader实例
	 * @return
	 */
	public static NativeImageLoader getInstance() {
		return m_instance;
	}
	
	/**
	 * 加载本地图片，不进行裁剪
	 * @param path
	 * @param m_callback
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final NativeImageCallBack callback) {
		return this.loadNativeImage(path, null, callback);
	}
	
	/**
	 * 加载本地图片，point封装了ImageView的宽和高，可以进行裁剪
	 * @param path
	 * @param point
	 * @param callback
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final Point point, final NativeImageCallBack callback) {
		Bitmap bitmap = getBitmapFromMemoryCache(path);
		
		final Handler m_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				callback.onImageLoader((Bitmap)msg.obj, path);
			}
		};
		
		//若bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到m_memoryCache中
		if (bitmap == null) {
			m_imageThreadPool.execute(new Runnable () {
				@Override
				public void run() {
					//先获取图片的缩略图
					Bitmap m_bitmap = decodeThumbBitmapForFile(path, point == null ? 0 : point.x, point == null ? 0 : point.y);
					Message msg = m_handler.obtainMessage();
					msg.obj = m_bitmap;
					m_handler.sendMessage(msg);
					//将图片加入缓存
					addBitmapToMemoryCache(path, m_bitmap);
				}
			});
		}
		return bitmap;
	}
	
	/**
	 * 添加Bitmap到内存缓冲
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null && bitmap != null) {
			m_memoryCache.put(key, bitmap);
		}
	}
	
	/**
	 * 根据key来获取内存中的图片
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromMemoryCache(String key) {
		Bitmap bitmap = m_memoryCache.get(key);
		
		if (bitmap == null) {
			Log.i(TAG, "get image for LRUCache, path = " + key);
		}
		return bitmap;
	}
	
	/**
	 * 根据View的宽和高来获取图片的缩略图
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		options.inJustDecodeBounds = false;
		
		Log.e(TAG, "get Image from file, path = " + path);
		
		return BitmapFactory.decodeFile(path, options);
	}
	
	/**
	 * 根据View的宽和高来计算Bitmap缩放比例，默认不缩放
	 * @param options
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
		int inSampleSize = 1;
		if (viewWidth == 0 || viewHeight == 0) {
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		//假如Bitmap的宽度或高度大于我们设定图片的宽高，则计算缩放比例
		if (bitmapWidth > viewWidth || bitmapHeight > viewHeight) {
			int widthScale = Math.round((float)bitmapWidth / (float)viewWidth);
			int heightScale = Math.round((float)bitmapHeight / (float)viewHeight);
			
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}
	
	/**
	 * 清除LruCache中的Bitmap
	 */
	public void trimMemoryCache() {
		m_memoryCache.evictAll();
	}
	
	/**
	 * 加载本地图片的回调接口
	 */
	public interface NativeImageCallBack {
		/**
		 * 当子线程加载完本地图片，将Bitmap和图片路径回调在此方法中
		 * @param bitmap
		 * @param path
		 */
		public void onImageLoader(Bitmap bitmap, String path);
	}
}
