package com.twilight.stickygridheaders;

import java.util.Comparator;

/**
 * @blog http://johncdy.me
 * @author John Ares
 * @email yangguangzaidongji@gmail.com
 */

public class YMDComparator implements Comparator<GridViewItem> {

	@Override
	public int compare(GridViewItem o1, GridViewItem o2) {
		return o1.getTime().compareTo(o2.getTime());
	}
	
}
