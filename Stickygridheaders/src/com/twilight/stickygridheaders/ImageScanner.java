package com.twilight.stickygridheaders;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 * 
 * @note 图片扫描器
 * 
 */

public class ImageScanner {
	private Context m_context;
	
	public ImageScanner(Context context) {
		this.m_context = context;
	}
	
	/*
	 * 利用ContentProvider扫描手机中图片，将扫描的Cursor回调到ScanCompleteCallBack
	 * 接口的scanComplete方法中，此方法与运行在子线程中(扫描图片比较耗时)
	 */
	public void scanImages(final ScanCompleteCallBack callback) {
		final Handler m_handler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.i("HEHEHEHEH", "msg.obj == null " + (msg.obj).equals(null));
				callback.scanComplete((Cursor)msg.obj);
			}
		};
		
		new Thread(new Runnable () {

			@Override
			public void run() {
				// 发送广播，扫描整个sd卡，同步媒体库
				m_context.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				Log.i("SAOMIAOTUPIAN", "==========================");
				Uri m_imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver m_contentResolver = m_context.getContentResolver();
				
				Cursor m_cursor = m_contentResolver.query(m_imageUri, null, null, null, MediaStore.Images.Media.DATE_ADDED);
				
				Log.i("HEHEHEHEH", "m_curosr == null " + m_cursor.equals(null));
				
				//利用Handler通知调用线程
				Message msg = m_handler.obtainMessage();
				msg.obj = m_cursor;
				m_handler.sendMessage(msg);
			}
		}).start();
	}
	
	/*
	 * 扫描完成后的回调接口
	 */
	public static interface ScanCompleteCallBack {
		public void scanComplete(Cursor cursor);
	}
}
