package cn.nineox.robot.monitor.common.tutk;

public interface ILiveViewActivityPresenter  {
		void initData();
		void startListener();

		void stopListener();

		void startSpeak();

		void stopSpeak();

		void snapshot();

		void startRecord();

		void stopRecord(boolean isRecordSuccess);

		void switchCamera();

		void callOff();

		void addOtherAccount();

		void changePosition(int pos);

		void changeDASA(int rttResult);

	}