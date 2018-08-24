package cn.nineox.robot.common.tutk;

import android.os.SystemClock;

/**
 * @author zed
 * @date 2018/3/9 上午9:56
 * @desc 计时线程
 */

public class ThreadTimerClock extends Thread {

	private OnThreadListener mTimeListener;

	private StringBuilder mStringBuilder = new StringBuilder();

	private boolean mIsRunning = true;

	private int second = 0;

	private int min = 0;

	public void setThreadListener(OnThreadListener listener) {
		mTimeListener = listener;
	}

	public void stopThread() {
		mTimeListener = null;
		mIsRunning = false;
	}

	public long getTime(){
		return (min * 60 + second) * 1000;
	}

	public void clear() {
		second = 0;
		min = 0;
	}

	@Override
	public void run() {

		while (mIsRunning) {

			if (second >= 59) {
				second = 0;
				min++;
			} else {
				second++;
			}

			mStringBuilder.delete(0, mStringBuilder.length());
			if (min < 10) {
				mStringBuilder.append("0");
			}
			mStringBuilder.append(min).append(":");
			if (second < 10) {
				mStringBuilder.append("0");
			}
			mStringBuilder.append(second);

			if (mTimeListener != null) {
				mTimeListener.onTime(mStringBuilder.toString());
			}
			SystemClock.sleep(1000);
		}
	}

	public interface OnThreadListener {
		void onTime(String time);
	}
}
