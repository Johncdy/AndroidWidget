package com.twilight.stickygridheaders;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 * 
 * @note Item definition.
 * 
 */

public class GridViewItem {
	/*
	 * 图片路径
	 */
	private String path;
	
	/*
	 * 图片加入手机的时间
	 */
	private String time;
	
	/*
	 * 每个item对应的headId，这个id是根据我们生成的
	 */
	private int headerId;
	
	public GridViewItem(String path, String time) {
		super();
		this.path = path;
		this.time = time;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getHeaderId() {
		return headerId;
	}
	
	public void setHeaderId(int id) {
		this.headerId = id;
	}
}
