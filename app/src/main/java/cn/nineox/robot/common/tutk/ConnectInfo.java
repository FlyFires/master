package cn.nineox.robot.common.tutk;

/**
 * @author zed
 * @date 2018/3/7 上午11:51
 * @desc
 */

public class ConnectInfo {

	public String mAccountId;//账号id 唯一标识

	public String mNickName;//昵称

	public boolean mByClientConnect;//对面属于client ／device

	public ThreadTimerClock mThreadTimer;//计时器

	/* client */
	public int mClientSID = -1;

	/* device */
	public String mDeviceUID;

	public int mDeviceChannel = -1;

	public String getmAccountId() {
		return mAccountId;
	}

	public void setmAccountId(String mAccountId) {
		this.mAccountId = mAccountId;
	}

	public String getmNickName() {
		return mNickName;
	}

	public void setmNickName(String mNickName) {
		this.mNickName = mNickName;
	}

	public boolean ismByClientConnect() {
		return mByClientConnect;
	}

	public void setmByClientConnect(boolean mByClientConnect) {
		this.mByClientConnect = mByClientConnect;
	}

	public ThreadTimerClock getmThreadTimer() {
		return mThreadTimer;
	}

	public void setmThreadTimer(ThreadTimerClock mThreadTimer) {
		this.mThreadTimer = mThreadTimer;
	}

	public int getmClientSID() {
		return mClientSID;
	}

	public void setmClientSID(int mClientSID) {
		this.mClientSID = mClientSID;
	}

	public String getmDeviceUID() {
		return mDeviceUID;
	}

	public void setmDeviceUID(String mDeviceUID) {
		this.mDeviceUID = mDeviceUID;
	}

	public int getmDeviceChannel() {
		return mDeviceChannel;
	}

	public void setmDeviceChannel(int mDeviceChannel) {
		this.mDeviceChannel = mDeviceChannel;
	}

	@Override
	public String toString() {
		return "ConnectInfo{" +
				"mAccountId='" + mAccountId + '\'' +
				", mNickName='" + mNickName + '\'' +
				", mByClientConnect=" + mByClientConnect +
				", mThreadTimer=" + mThreadTimer +
				", mClientSID=" + mClientSID +
				", mDeviceUID='" + mDeviceUID + '\'' +
				", mDeviceChannel=" + mDeviceChannel +
				'}';
	}
}
