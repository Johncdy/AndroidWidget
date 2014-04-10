package com.twilight.stickygridheaders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.GridView;

import com.tonicartos.stickygridheaders.R;
import com.twilight.stickygridheaders.ImageScanner.ScanCompleteCallBack;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 */

public class MainActivity extends Activity {
	private ProgressDialog m_progressDialog;
	/*
	 * 图片扫描器
	 */
	private ImageScanner m_scanner;
	private GridView m_gridView;
	private List<GridViewItem> m_nonHeaderIdList = new ArrayList<GridViewItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_gridView = (GridView)findViewById(R.id.asset_grid);
		m_scanner = new ImageScanner(this);
		
		m_scanner.scanImages(new ScanCompleteCallBack() {
			{
				m_progressDialog = ProgressDialog.show(MainActivity.this, null, "正在加载...");
			}
			
			@Override
			public void scanComplete(Cursor cursor) {
				m_progressDialog.dismiss();
				Log.i("SAOMIAOTUPIAN", "sao miao wan chen");
				if (cursor == null) {
					return;
				}
				
				while(cursor.moveToNext()) {
					//获取图片的路径
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					//获取图片添加到系统的毫秒数
					long times = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
					GridViewItem m_gridViewItem = new GridViewItem(path, paserTimeToYMD(times, "yyyy年MM月dd日"));
					m_nonHeaderIdList.add(m_gridViewItem);
				}
				cursor.close();
				
				//给GridView的item的数据生成HeaderId
				List<GridViewItem> m_hasHeaderIdList = generateHeaderId(m_nonHeaderIdList);
				// sort
				Collections.sort(m_hasHeaderIdList, new YMDComparator());
				m_gridView.setAdapter(new StickyGridAdapter(MainActivity.this, m_hasHeaderIdList, m_gridView));
			}
			
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出页面的时候清除LRUCache中Bitmap占用的内存
		NativeImageLoader.getInstance().trimMemoryCache();
	}
	
	/**
	 * 根据图片添加时间的年、月、日来为GridView的Item生成HeaderId
	 * @param nonHeaderIdList
	 * @return
	 */
	private List<GridViewItem> generateHeaderId(List<GridViewItem> nonHeaderIdList) {
		Map<String, Integer> m_headerIdMap = new HashMap<String, Integer>();
		int m_headerId = 1;
		List<GridViewItem> m_hasHeaderIdList;
		
		for (ListIterator<GridViewItem> it = m_nonHeaderIdList.listIterator(); it.hasNext();) {
			GridViewItem m_gridViewItem = it.next();
			String ymd = m_gridViewItem.getTime();
			if (!m_headerIdMap.containsKey(ymd)) {
				m_gridViewItem.setHeaderId(m_headerId);
				m_headerIdMap.put(ymd, m_headerId);
				m_headerId++;
			} else {
				m_gridViewItem.setHeaderId(m_headerIdMap.get(ymd));
			}
		}
		m_hasHeaderIdList = m_nonHeaderIdList;
		
		return m_hasHeaderIdList;
	}
	
	/**
	 * 将毫秒数转换为pattern格式
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String paserTimeToYMD(long time, String pattern) {
		System.setProperty("user.timezone", "Asia/Shanghai");
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date(time * 1000L));
	}
}
