package com.twilight.stickygridheaders;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonicartos.stickygridheaders.R;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.twilight.stickygridheaders.MyImageView.OnMeasureListener;
import com.twilight.stickygridheaders.NativeImageLoader.NativeImageCallBack;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 * 
 * @note StickyGridHeader的适配器，除了要继承BaseAdapter之外还需要
 * 实现StickyGridHeaderSimpleAdapter接口
 * 
 */

public class StickyGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

	private List<GridViewItem> m_hasHeaderIdList;
	private LayoutInflater m_inflater;
	private GridView m_gridView;
	private Point m_point = new Point(0, 0); //封装ImageView的宽和高
	
	public StickyGridAdapter(Context context, List<GridViewItem> hasHeaderIdList, GridView m_gridView) {
		m_inflater = LayoutInflater.from(context);
		this.m_gridView = m_gridView;
		this.m_hasHeaderIdList = hasHeaderIdList;
	}
	
	@Override
	public int getCount() {
		return m_hasHeaderIdList.size();
	}

	@Override
	public Object getItem(int position) {
		return m_hasHeaderIdList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder m_viewHolder;
		if (convertView == null) {
			m_viewHolder = new ViewHolder();
			convertView = m_inflater.inflate(R.layout.grid_item, parent, false);
			m_viewHolder.m_imageView = (MyImageView)convertView.findViewById(R.id.grid_item);
			convertView.setTag(m_viewHolder);
			
			m_viewHolder.m_imageView.setOnMeasureListener(new OnMeasureListener() {

				@Override
				public void OnMeasureSize(int width, int height) {
					m_point.set(width, height);
				}
			});
		} else {
			m_viewHolder = (ViewHolder)convertView.getTag();
		}
		
		String path = m_hasHeaderIdList.get(position).getPath();
		m_viewHolder.m_imageView.setTag(path);
		
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, m_point,
				new NativeImageCallBack() {

					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						ImageView m_imageView = (ImageView)m_gridView.findViewWithTag(path);
						if (bitmap != null && m_imageView != null) {
							m_imageView.setImageBitmap(bitmap);
						}
					}
				});
		
		if (bitmap != null) {
			m_viewHolder.m_imageView.setImageBitmap(bitmap);
		} else {
			m_viewHolder.m_imageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return m_hasHeaderIdList.get(position).getHeaderId();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder m_headerHolder;
		
		if (convertView == null) {
			m_headerHolder = new HeaderViewHolder();
			convertView = m_inflater.inflate(R.layout.header, parent, false);
			m_headerHolder.m_textView = (TextView)convertView.findViewById(R.id.header);
			convertView.setTag(m_headerHolder);
		} else {
			m_headerHolder = (HeaderViewHolder)convertView.getTag();
		}
		
		m_headerHolder.m_textView.setText(m_hasHeaderIdList.get(position).getTime());
		
		return convertView;
	}
	
	public static class ViewHolder {
		public MyImageView m_imageView;
	}
	
	public static class HeaderViewHolder {
		public TextView m_textView;
	}
}