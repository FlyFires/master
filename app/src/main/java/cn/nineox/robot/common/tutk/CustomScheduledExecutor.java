package cn.nineox.robot.common.tutk;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author zed
 * @date 2018/5/16 下午2:00
 * @desc
 */

public class CustomScheduledExecutor extends ScheduledThreadPoolExecutor {

	private final ArrayList<cn.nineox.robot.common.tutk.AbstractScheduledRunnable> mList = new ArrayList<>();



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


	public CustomScheduledExecutor(int corePoolSize, ThreadFactory threadFactory) {
		super(corePoolSize, threadFactory);
	}

	public void executeCustomScheduledRunnable(final cn.nineox.robot.common.tutk.AbstractScheduledRunnable runnable) {

		execute(runnable);

		runnable.setCustomScheduleListener(new CustomScheduleListener() {
			@Override
			public void onRunningTask(boolean isCancelTask) {
				mList.remove(runnable);
			}
		});

		mList.add(runnable);
	}

	//取消任务
	public void cancelTask(int tag) {
		for (cn.nineox.robot.common.tutk.AbstractScheduledRunnable runnable : mList) {
			if (runnable.getTag() == tag) {
				runnable.runTask(true);
				break;
			}
		}
	}

	//立即执行任务
	public void runTaskByNow(int tag) {
		for (cn.nineox.robot.common.tutk.AbstractScheduledRunnable runnable : mList) {
			if (runnable.getTag() == tag) {
				runnable.runTask(false);
				break;
			}
		}
	}

}
