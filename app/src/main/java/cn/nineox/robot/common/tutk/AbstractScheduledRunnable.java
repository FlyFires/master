package cn.nineox.robot.common.tutk;

/**
 * @author zed
 * @date 2018/5/16 下午2:24
 */

public abstract class AbstractScheduledRunnable implements Runnable {

	private int mDelayTime;

	private int mTag = -1;

	private boolean mIsCancelTask = false;

	private CustomScheduleListener mListener;

	private final Object m_CustomScheduledRunnable = new Object();

	public AbstractScheduledRunnable(int delayTime, int tag) {
		mDelayTime = delayTime;
		mTag = tag;
	}

	public void runTask(boolean isCancelTask) {
		mIsCancelTask = isCancelTask;
		synchronized (m_CustomScheduledRunnable) {
			m_CustomScheduledRunnable.notify();
		}
	}

	public void setCustomScheduleListener(CustomScheduleListener listener) {
		mListener = listener;
	}

	public int getTag() {
		return mTag;
	}

	@Override
	public void run() {

		synchronized (m_CustomScheduledRunnable) {
			try {
				m_CustomScheduledRunnable.wait(mDelayTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (mListener != null) {
			mListener.onRunningTask(mIsCancelTask);
		}

		if (!mIsCancelTask) {
			runMethod();
		}
	}

	public abstract void runMethod();
}
