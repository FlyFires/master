package cn.nineox.robot.common.widgets;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * @author zed
 * @date 2018/3/26 上午10:19
 * @desc
 */

public class CustomLayoutManager extends StaggeredGridLayoutManager {

	public CustomLayoutManager(int spanCount, int orientation) {
		super(spanCount, orientation);
	}



	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

		try {
			super.onLayoutChildren(recycler, state);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

	}
}
