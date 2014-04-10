package com.twilight.stickygridheaders;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 */

public class MyImageView extends ImageView {

	private OnMeasureListener m_onMeasureListener;
	
	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.m_onMeasureListener = onMeasureListener;
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if (m_onMeasureListener != null) {
			m_onMeasureListener.OnMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}
	
	public interface OnMeasureListener {
		public void OnMeasureSize(int width, int height);
	}
}
