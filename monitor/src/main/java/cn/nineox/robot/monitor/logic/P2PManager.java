package cn.nineox.robot.monitor.logic;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.logic.service.P2PCallbacks;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.xframework.core.android.log.Log;

/**
 * Created by me on 18/5/7.
 */

public class P2PManager {

    private static P2PManager mInstance = new P2PManager();


    //private RTP2PCallKit mP2PKit;

    private String mUserid;

    private List<P2PCallbacks> mP2PCallbacks = new ArrayList<>();


    public synchronized  static P2PManager getInstance() {
        if (mInstance == null) {
            mInstance = new P2PManager();
        }
        return mInstance;
    }

//    public P2PManager() {
//        mP2PKit = APP.the().getmP2pKit();
//    }

//    public RTP2PCallKit getP2PKit() {
//        return mP2PKit;
//    }


    public void turnOn() {
        mUserid = SharePrefUtil.getString(Const.EXTAR_ANDROID_ID);
//        if (!TextUtils.isEmpty(mUserid)) {
//            mP2PKit.TurnOn(mUserid);
//            Log.e("--","turnOn:" + mUserid);
//            //mP2PKit.setP2PCallHelper(callbacks);
//        }
    }

    public void turnOn(String userid) {
        Log.e("--","turnOn:" + userid);
        //mP2PKit.TurnOn(userid);
    }

    public void trunOff() {
        //mP2PKit.TurnOff();
    }

    public void register(P2PCallbacks c){
        if(!mP2PCallbacks.contains(c)){
            mP2PCallbacks.add(c);
        }

        Log.e("--","register:" + c  +  "  " +mP2PCallbacks.size());
    }

    public void unregister(P2PCallbacks c){
        mP2PCallbacks.remove(c);
        Log.e("--","unregister:" + c);
    }


    public void setP2PCallback(P2PCallbacks c){
        //mP2PKit.setP2PCallHelper(c);
    }


    public List<P2PCallbacks> getP2PCallbacks() {
        return mP2PCallbacks;
    }
}
