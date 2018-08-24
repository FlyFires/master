package cn.nineox.robot.common.tutk;

import android.os.SystemClock;
import android.util.Log;

import com.tutk.p2p.TUTKP2P;

import java.util.ArrayList;

import cn.nineox.robot.common.App;
import cn.nineox.robot.utils.LogUtil;


/**
 * @author zed
 * @date 2018/3/21 上午9:42
 * @desc 自动连线device线程 运行固定次数
 */

public class ThreadAutoConnect extends Thread {

	private static final String TAG = ThreadAutoConnect.class.getSimpleName();

	private static final long TIME_DELAY = 5000;

	private static final long TIME_COUNT = 30 * 1000 / TIME_DELAY;
	private final Object m_ThreadAutoConnect = new Object();

	private volatile boolean isRunning = true;

	private OnThreadListener mTimeListener;

	private String  mUID;
	private String  mSelfID;
	private String  mSelfUID;
	private boolean mIsDoubleCall;

	private ArrayList<CustomCommand.StructAccountInfo> mOtherUIDList;

	public ThreadAutoConnect(String uid, String selfID, String selfUID, boolean isDoubleCall,
							 ArrayList<CustomCommand.StructAccountInfo> otherUIDList) {
		mUID = uid;
		mSelfID = selfID;
		mSelfUID = selfUID;
		mIsDoubleCall = isDoubleCall;
		mOtherUIDList = otherUIDList;
	}

	public void setThreadListener(OnThreadListener listener) {
		mTimeListener = listener;
	}

	public void stopThread() {
		mTimeListener = null;
		isRunning = false;
		synchronized (m_ThreadAutoConnect) {
			m_ThreadAutoConnect.notify();
		}
	}

	@Override
	public void run() {
		super.run();

		Log.i(TAG, " ThreadAutoConnect start thread ");

		int runCount = 0;

	while (isRunning) {

		if (runCount >= TIME_COUNT) {
			synchronized (ThreadAutoConnect.this) {
				Log.i(TAG, " ThreadAutoConnect time out ");
				if (mTimeListener != null) {
					mTimeListener.timeOut();
					TUTKP2P.TK_getInstance().TK_client_disConnect(mUID);
				}
			}
			break;
		}

		if (!TUTKP2P.TK_getInstance().TK_client_isConnected(mUID, App.DEVICE_CHANNEL)) {

			//断线
			TUTKP2P.TK_getInstance().TK_client_disConnect(mUID);

			SystemClock.sleep(1000);
			LogUtil.debug("ThreadAutoConnect 连接");
			//重连
			TUTKP2P.TK_getInstance().TK_client_connect(mUID, App.DEVICE_PASSWORD, App.DEVICE_CHANNEL);

			//将自己的id信息发过去
			TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(mUID, App.DEVICE_CHANNEL,
					CustomCommand.IOTYPE_USER_IPCAM_CALL_REQ,
					CustomCommand.SMsgAVIoctrlCallReq.parseContent(
							mSelfID,
							mSelfUID,
							mIsDoubleCall ? 1 : 0,
							0,
							mOtherUIDList));

		} else {
//				break;
		}

		runCount++;

		synchronized (m_ThreadAutoConnect) {
			try {
				m_ThreadAutoConnect.wait(TIME_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		}

		Log.i(TAG, " ThreadAutoConnect stop thread ");

	}

	public interface OnThreadListener {

		void timeOut();
	}

}
