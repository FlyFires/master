package cn.nineox.robot.logic;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import cn.finalteam.toolsfinal.ApkUtils;
import cn.nineox.robot.R;
import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.FragmentMain2Binding;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.EmquipmentBean;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.bean.PageBean;
import cn.nineox.robot.logic.bean.Peer;
import cn.nineox.robot.logic.bean.UserBean;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.WaitCallActivity;
import cn.nineox.robot.ui.fragment.BindFragment;
import cn.nineox.robot.ui.fragment.Main2Fragment;
import cn.nineox.robot.ui.fragment.MyDeviceFragment;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.robot.utils.PhoneFormatCheckUtils;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 17/11/2.
 */

public class Main2Logic extends BasicLogic<FragmentMain2Binding> {

    private PageBean mPageBean;

    private BaseBindingAdapter mAdapter;

    private SupportFragment mFragment;

    private BindFragment mBindFragment;

    private List mDatas;

    private NotificationManager manager;

    private NotificationCompat.Builder builder;

    private boolean isDownloading = false;

    public Main2Logic(Fragment fragment, FragmentMain2Binding dataBinding) {
        super(fragment, dataBinding);
        //userGet(null);
    }

    public void userUpdatePhone(final Activity activity, final String mobile) {
        final StringReqeust request = new StringReqeust(Const.URL_USER_UPDATE);
        request.add("name", APPDataPersistent.getInstance().getLoginInfoBean().getName());
        request.add("mobile", mobile);
        request.add("mid", APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                new Toastor(activity).showToast("修改成功.");
                APPDataPersistent.getInstance().getUserBean().setMobile(mobile);
                APPDataPersistent.getInstance().getLoginInfoBean().setMobile(mobile);
                APPDataPersistent.getInstance().saveLoginUserBean();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(activity).showToast("修改失败:" + error);
            }
        });

    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void babyApkGet(final Activity activity) {
        final StringReqeust request = new StringReqeust(Const.URL_BABY_GET);
        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                new Toastor(activity).showToast("宝宝听听正在开始下载.");
                startDownload(result.getResult());
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(activity).showToast("下载失败:" + error);
            }
        });

    }


    public void userGet(final String eventList) {
        final EntityRequest request = new EntityRequest(Const.URL_USER_GET, UserBean.class);
        execute(request, new ResponseListener<UserBean>() {
            @Override
            public void onSucceed(int what, Result<UserBean> result) {
                super.onSucceed(what, result);
                UserBean userBean = result.getResult();
                APPDataPersistent.getInstance().setUserBean(userBean);
                verifySave();
                if (!TextUtils.isEmpty(eventList)) {
                    eventListMark(eventList);
                }

                if (userBean != null) {
                    LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
                    loginInfoBean.setHeadpic(userBean.getHeadPic());
                    loginInfoBean.setName(userBean.getName());
                    loginInfoBean.setDevice(userBean.getDevice());
                    //DeviceBean deviceBean = loginInfoBean.getDevice();
//                    if (deviceBean != null) {
//                        if(!TextUtils.isEmpty(userBean.getName())){
//                            deviceBean.setMidName(userBean.getName());
//                        }else{
//                            deviceBean.setMidName("");
//                        }
//                        if(userBean.getDevice() != null){
//                            deviceBean.setMid(userBean.getDevice().getMid());
//                        }
//                    }else{
//                        loginInfoBean.setDevice(userBean.getDevice());
//                    }

                    loginInfoBean.setUserType(userBean.getUserType());
//                    if (userBean.getUserType() != null) {
//                        loginInfoBean.setUserType(userBean.getUserType());
//                    }
                    APPDataPersistent.getInstance().saveLoginUserBean();

                    mDataBinding.nameTx.setText(loginInfoBean.getName());
                    if (!TextUtils.isEmpty(loginInfoBean.getHeadpic())) {
                        GlideUtils.loadRoundImageView(mActivity, loginInfoBean.getHeadpic(), mDataBinding.headIv,
                                R.drawable.ic_portrait, R.drawable.ic_portrait);
                    }

                }

            }
        });
    }


    private void verifySave() {
        LoginInfoBean info = APPDataPersistent.getInstance().getLoginInfoBean();
        EntityRequest request = new EntityRequest(Const.VERIFY_SAVE, Boolean.class);
        request.add("uid", info.getUid());
        request.add("name", info.getName());
        request.add("mobile", info.getMobile());
        request.add("headPic", info.getHeadpic());
        //request.add("pass", info.getHeadpic());
        if (info.getDevice() != null) {
            request.add("sn", info.getDevice().getMid());
        }
        execute(request, new ResponseListener<Boolean>() {
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
            }
        });
    }


    private void eventListMark(String eventList) {
        Log.e("http", "eventListMark");
        String token = APPDataPersistent.getInstance().getLoginInfoBean().getToken();
        String userId = APPDataPersistent.getInstance().getLoginInfoBean().getUid();
        EntityRequest request = new EntityRequest(Const.EVENT_MARK, Boolean.class);
        request.add("eventList", eventList);
        long timestamp = System.currentTimeMillis();
        request.addHeader("timestamp", String.valueOf(timestamp));
        request.addHeader("userId", userId);
        String str = userId + "&" + token + "&" + timestamp + "&" + eventList;
        request.addHeader("sign", getSign(str));
        Log.e("--", "str:" + str + "   sign:" + getSign(str));
        execute(request, new ResponseListener<Boolean>() {
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
            }
        });
    }


    public void statrtVideoCall() {
        final DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        if (device != null) {
            if (TextUtils.isEmpty(APPDataPersistent.getInstance().getLoginInfoBean().getMobile())) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(mActivity);
                builder.setTitle("您需要先绑定手机号码")
                        .setPlaceholder("请输入您的手机号码")
                        .setInputType(InputType.TYPE_CLASS_PHONE)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                String text = builder.getEditText().getText().toString();

                                if (text != null && text.length() > 0 && PhoneFormatCheckUtils.isPhoneLegal(text)) {
                                    userUpdatePhone(mActivity, text);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(mActivity, "手机号码不正确", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            } else {
                final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("正在接通").create();
                dialog.show();
                getToPeerUid(new ResponseListener<Peer>() {
                    @Override
                    public void onSucceed(int what, Result<Peer> result) {
                        super.onSucceed(what, result);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(mActivity, WaitCallActivity.class);
                        bundle.putString(App.BUNDLE_CALL_UID, result.getResult().getPeerUid());//"208401"
                        //bundle.putString(App.BUNDLE_CALL_UID, "F1YU8154UH7WKH6GUHZJ");
                        bundle.putBoolean(App.BUNDLE_IS_CALL_TO, true);
                        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, true);
                        intent.putExtras(bundle);
                        intent.putExtra(Const.EXTRA_DEVICE, device);
                        mActivity.startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
                    }

                    @Override
                    public void onFailed(int what, String error) {
                        super.onFailed(what, error);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        new Toastor(mActivity).showSingletonToast(error);
                    }
                });

            }
        } else {
            //start(new MyDeviceFragment());
            Toast.makeText(mActivity, "请先绑定设备", Toast.LENGTH_SHORT).show();
        }

    }


    public void statrtMonitor() {
        final DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        if (device != null) {
            if (TextUtils.isEmpty(APPDataPersistent.getInstance().getLoginInfoBean().getMobile())) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(mActivity);
                builder.setTitle("您需要先绑定手机号码")
                        .setPlaceholder("请输入您的手机号码")
                        .setInputType(InputType.TYPE_CLASS_PHONE)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                String text = builder.getEditText().getText().toString();

                                if (text != null && text.length() > 0 && PhoneFormatCheckUtils.isPhoneLegal(text)) {
                                    userUpdatePhone(mActivity, text);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(mActivity, "手机号码不正确", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            } else {
                final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("正在接通").create();
                dialog.show();
                getToPeerUid(new ResponseListener<Peer>() {
                    @Override
                    public void onSucceed(int what, Result<Peer> result) {
                        super.onSucceed(what, result);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(mActivity, WaitCallActivity.class);
                        bundle.putString(App.BUNDLE_CALL_UID, result.getResult().getPeerUid());
                        bundle.putBoolean(App.BUNDLE_IS_CALL_TO, true);
                        bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, false);
                        intent.putExtras(bundle);
                        intent.putExtra(Const.EXTRA_DEVICE, device);
                        mActivity.startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
                    }

                    @Override
                    public void onFailed(int what, String error) {
                        super.onFailed(what, error);
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        new Toastor(mActivity).showSingletonToast(error);
                    }
                });

            }
        } else {
            //start(new MyDeviceFragment());
            Toast.makeText(mActivity, "请先绑定设备", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 获取我的直播uid
     */
    public void getToPeerUid(ResponseListener<Peer> listener) {
        LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
        DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        final EntityRequest request = new EntityRequest(Const.GET_TO_PEER_UID, Peer.class);
        String mid = device.getMid();
        request.add("id", mid);
        request.add("type", "app");
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        String str = loginInfoBean.getUid() + "&" + loginInfoBean.getToken() + "&" +
                timestamp + "&" + mid + "&app";
        request.addHeader("sign", getSign(str));
        execute(request, listener);
    }

    private void startDownload(String url) {
        DownloadQueue queue = NoHttp.newDownloadQueue(); // 默认三个并发，此处可以传入并发数量。
        DownloadRequest request = new DownloadRequest(url, RequestMethod.GET, mActivity.getExternalCacheDir().getAbsolutePath(), "baby.apk", true, true);
        queue.add(0, request, downloadListener);
    }


    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadError(int what, Exception exception) {
            isDownloading = false;
            new Toastor(mActivity).showToast(exception.getMessage());
        }

        @Override
        public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
            // 下载开始。
            showDonwloadNotifytion();
            isDownloading = true;
        }

        @Override
        public void onProgress(int what, int progress, long downCount, long speed) {
            // 更新下载进度和下载网速。
            builder.setProgress(100, progress, false);
            manager.notify(1, builder.build());
            builder.setContentText("下载" + progress + "%");
        }

        @Override
        public void onFinish(int what, String filePath) {
            // 下载完成。
            new Toastor(mActivity).showToast("宝宝听听下载完成");
            ApkUtils.install(mActivity, new File(filePath));
            manager.cancel(1);
            isDownloading = false;
        }


        @Override
        public void onCancel(int what) {
            // 下载被取消或者暂停。
            isDownloading = false;
        }
    };

    private void showDonwloadNotifytion() {
        builder = new NotificationCompat.Builder(mActivity);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("下载");
        builder.setContentText("正在下载");
        manager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
        builder.setProgress(100, 0, false);

    }

}
