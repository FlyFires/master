package cn.nineox.robot.common.tutk;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;

import com.tutk.IOTC.St_AvStatus;
import com.tutk.IOTC.St_SInfo;
import com.tutk.p2p.TUTKP2P;
import com.tutk.p2p.inner.OnP2PClientListener;
import com.tutk.p2p.inner.OnP2PDeviceListener;

import cn.nineox.robot.logic.BasicLogic;
import cn.nineox.xframework.base.BaseLogic;

public abstract class TUTKP2PLogic<DataBinding extends ViewDataBinding> extends BasicLogic<DataBinding> implements OnP2PClientListener, OnP2PDeviceListener {
    public TUTKP2PLogic(DataBinding binding) {
        super(binding);
    }

    public TUTKP2PLogic(Fragment fragment, DataBinding binding) {
        super(fragment, binding);
    }

    public TUTKP2PLogic(Activity activity, DataBinding binding) {
        super(activity, binding);
    }

    /**
     * Bind.
     */
    protected void bind() {
        TUTKP2P.TK_getInstance().TK_registerClientListener(this);
        TUTKP2P.TK_getInstance().TK_registerDeviceListener(this);
    }

    /**
     * Unbind.
     */
    protected void unbind() {
        TUTKP2P.TK_getInstance().TK_unRegisterClientListener(this);
        TUTKP2P.TK_getInstance().TK_unRegisterDeviceListener(this);
    }


    /* TUTK listener*/

    @Override
    public void receiveConnectInfo(String uid, int sid, int state) {

    }

    @Override
    public void receiveClientStartInfo(String uid, int avIndex, int state) {

    }

    @Override
    public void receiveSessionCheckInfo(String uid, St_SInfo info, int result) {

    }

    @Override
    public void receiveIOCtrlDataInfo(String uid, int avChannel, int avIOCtrlMsgType, byte[] data) {

    }


    @Override
    public void receiveVideoInfo(String uid, int avChannel, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {

    }


    @Override
    public void receiveIOTCListenInfo(int sid) {

    }

    @Override
    public void receiveAvServerStart(int sid, int avIndex, int state) {

    }

    @Override
    public void receiveSessionCheckInfo(int sid, St_SInfo info, int result) {

    }

    @Override
    public void receiveIOCtrlDataInfo(int sid, int avIOCtrlMsgType, byte[] data) {

    }

    @Override
    public void receiveAudioInfo(String s, int i, byte[] bytes, long l, int i1) {

    }

    @Override
    public void receiveStatusCheckInfo(String s, St_AvStatus st_avStatus, int i) {

    }

    @Override
    public void sendIOCtrlDataInfo(int i, int i1, int i2, byte[] bytes) {

    }

    @Override
    public void sendIOCtrlDataInfo(String s, int i, int i1, int i2, byte[] bytes) {

    }

    @Override
    public void receiveVideoInfo(int sid, byte[] videoData, long timeStamp, int number, int onlineNum, boolean isIFrame) {

    }
}
