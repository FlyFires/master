package cn.nineox.robot.monitor.common.tutk;

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
