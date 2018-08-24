package cn.nineox.robot.monitor.common.tutk;

import java.util.ArrayList;

/**
 * @author zed
 * @date 2018/3/13 下午4:51
 * @desc
 */

public class CustomArrayList extends ArrayList<ConnectInfo> {

	private OnListDataChangeListener mListener;

	public void setOnListDataChangeListener(OnListDataChangeListener listener) {
		mListener = listener;
	}

	@Override
	public boolean add(ConnectInfo o) {
		boolean result = super.add(o);
		if (mListener != null) {
			mListener.onAdd(size() - 1, o);
		}
		return result;

	}

	@Override
	public void add(int index, ConnectInfo element) {
		super.add(index, element);
		if (mListener != null) {
			mListener.onAdd(index, element);
		}
	}

	@Override
	public ConnectInfo remove(int index) {
		ConnectInfo o = null;
		try{
			if(this.size() > index){
				o = super.remove(index);
			}
			if (mListener != null) {
				mListener.onRemove(index, o);
			}
		}catch (Exception e){
			e.printStackTrace();
			mListener.onRemove(index, null);
		}

		return o;
	}


	@Override
	public boolean remove(Object o) {
		boolean result = false;
		int indexOf = indexOf(o);
		try{
			result = super.remove(o);
			if (mListener != null) {
				mListener.onRemove(indexOf, (ConnectInfo) o);
			}
		}catch (Exception e){
			e.printStackTrace();
			mListener.onRemove(indexOf, null);
		}

		return result;
	}


	public interface OnListDataChangeListener {

		void onAdd(int index, ConnectInfo accountInfo);

		void onRemove(int index, ConnectInfo accountInfo);

	}


}


