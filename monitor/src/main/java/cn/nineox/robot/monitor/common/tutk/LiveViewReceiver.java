package cn.nineox.robot.monitor.common.tutk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.tutk.p2p.TUTKP2P;

import java.util.ArrayList;
import java.util.HashMap;

import cn.nineox.robot.monitor.CallActivity;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.RobotPreviewActivity;
import cn.nineox.robot.monitor.WaitCallActivity;
import cn.nineox.robot.monitor.utils.WindowUtils;


/**
 * @author zed
 * @date 2018/3/22 上午11:08
 * @desc 监听notification的点击事件
 * 收到其它人的邀请 关闭当前会话 开启新的会话
 */

public class LiveViewReceiver extends BroadcastReceiver {

    private final String TAG = LiveViewReceiver.class.getSimpleName();

    private String mAccountID;

    private String mCallUID;

    private int mSID;

    private boolean mIsDoubleCall;

    private ArrayList<CustomCommand.StructAccountInfo> mOtherList;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        mAccountID = bundle.getString(App.BUNDLE_ACCOUNT_ID);
        mCallUID = bundle.getString(App.BUNDLE_CALL_UID);
        mSID = bundle.getInt(App.BUNDLE_SID);
        mIsDoubleCall = bundle.getBoolean(App.BUNDLE_IS_DOUBLE_CALL);
        mOtherList = bundle.getParcelableArrayList(App.BUNDLE_INFO_LIST);

        Log.i(TAG, " action = " + action + " account_id = " + mAccountID);

        //当已经点击notification时候 需要取消延时任务
        //NotificationUtils.stopScheduleTask(mSID, false);

        switch (action) {
            case App.ACTION_CALL_CLICK: {

                //获取当前顶层activity
                //获取当前顶层activity
                final Activity activity = WindowUtils.getTopActivity();

                final boolean portraitLiveView = activity != null && activity instanceof CallActivity;
                final boolean landscapeLiveView = activity != null && activity instanceof CallActivity;
                final boolean waitCall = activity != null && activity instanceof WaitCallActivity;
                CustomAlertDialog customAlertDialog = new CustomAlertDialog(activity,
                        context.getString(R.string.tips_warning),
                        portraitLiveView && !mIsDoubleCall ?
                                context.getString(R.string.tips_reject_action_2) :
                                context.getString(R.string.tips_reject_action_1),
                        context.getString(R.string.text_cancel),
                        context.getString(R.string.text_ok));
                customAlertDialog.setOnDialogClickListener(new CustomAlertDialog.OnDialogClickLister() {

                    @Override
                    public void leftClick(DialogInterface dialog) {

                        //添加历史记录 -- 别人打过来 弹框 -- 自己拒接
                        long startTime = System.currentTimeMillis();
                        HashMap<String, String> map = new HashMap<>(1);
                        map.put(mAccountID, mCallUID);

                        //发送command
                        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mSID,
                                CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                                CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));
                        SystemClock.sleep(100);
                        TUTKP2P.TK_getInstance().TK_device_disConnect(mSID);
                    }

                    //接听
                    @Override
                    public void rightClick(DialogInterface dialog) {

                        //如果自己处于竖屏 且对方的请求也是单向
                        if (portraitLiveView && !mIsDoubleCall) {
                            addConnectInfo();
                            return;
                        }


                        /* 以下三个页面会退出 */
                        /* 以下三个页面会退出 */
                        if (landscapeLiveView) {
                            ((CallActivity) activity).mLogic.callOff();
                            activity.finish();
                        } else if (portraitLiveView) {
                            ((RobotPreviewActivity) activity).sLogic.callOff();
                            activity.finish();
                        } else if (waitCall) {
                            ((WaitCallActivity) activity).sLogic.callOff();
                            activity.finish();
                        }


                        SystemClock.sleep(1000);

                        /* 启动新页面 */
                        addConnectInfo();

                        Intent intent = null;
                        if (mIsDoubleCall) {
                            intent = new Intent(activity, CallActivity.class);
                        } else {
                            intent = new Intent(activity, RobotPreviewActivity.class);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, mIsDoubleCall);
                        bundle.putParcelableArrayList(App.BUNDLE_INFO_LIST, mOtherList);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);

                    }
                });
                //customAlertDialog.show();

            }
            break;
            case App.ACTION_CALL_REMOVE: {
                TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mSID,
                        CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                        CustomCommand.SMsgAVIoctrlCallResp.parseContent(0, App.sSelfAccountID));
                SystemClock.sleep(100);
                TUTKP2P.TK_getInstance().TK_device_disConnect(mSID);
            }
            break;
            default: {

            }
        }


    }

    /**
     * 添加client信息
     */
    private void addConnectInfo() {
        ConnectInfo accountInfo = new ConnectInfo();
        accountInfo.mByClientConnect = true;
        accountInfo.mClientSID = mSID;
        accountInfo.mAccountId = mAccountID;
        //String nickNameByID = StringUtils.getNickNameByID(mAccountID);
        String nickNameByID = mAccountID;
        accountInfo.mNickName = nickNameByID == null ? mAccountID : nickNameByID;
        accountInfo.mDeviceUID = mCallUID;
        App.sConnectList.add(accountInfo);

        TUTKP2P.TK_getInstance().TK_device_sendIOCtrl(mSID,
                CustomCommand.IOTYPE_USER_IPCAM_CALL_RESP,
                CustomCommand.SMsgAVIoctrlCallResp.parseContent(1, App.sSelfAccountID));
    }
}
