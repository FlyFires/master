package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.TimerTask;

import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityLoginBinding;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.MainActivity;
import cn.nineox.robot.ui.activity.SetShenfenActivity;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.PackageUtil;
import cn.nineox.xframework.core.common.utils.TelephoneUtil;


/**
 * Created by me on 17/9/27.
 */

public class LoginLogic extends BasicLogic<ActivityLoginBinding> {

    private int smsTimes = 60;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (smsTimes > 0) {
                mDataBinding.getVerifyCode.setText((smsTimes--) + "秒后重获");
            }
            if (smsTimes <= 0) {
                mDataBinding.getVerifyCode.setText("获取验证码");
                mDataBinding.getVerifyCode.setClickable(true);
            } else {
                mHandle.sendEmptyMessageDelayed(0, 1000);
            }

        }
    };

    public LoginLogic(Activity activity, ActivityLoginBinding dataBinding) {
        super(activity, dataBinding);
    }


    public void login(final Activity activity) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(activity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在登录").create();
        dialog.show();
        String mobile = mDataBinding.mobile.getText().toString();
        String code = mDataBinding.verifyCodeEt.getText().toString();
        final EntityRequest request = new EntityRequest(Const.URL_LOGIN, LoginInfoBean.class);
        request.add("mobile", mobile);
        request.add("code", code);
        request.add("clientVesion", PackageUtil.getAppVersionName(mActivity));
        request.add("osVesion", Build.VERSION.SDK_INT);
        request.add("imei", TelephoneUtil.getIMEI(mActivity));
        request.add("mobile_model", android.os.Build.MODEL);
        request.add("channelId", App.CHANNEL_ID);
        execute(request, new ResponseListener<LoginInfoBean>() {
            @Override
            public void onSucceed(int what, Result<LoginInfoBean> result) {
                super.onSucceed(what, result);
                Log.i("LoginSuccess:" + result.getResult());
                dialog.dismiss();
                APPDataPersistent.getInstance().setLoginInfoBean(result.getResult());
//                if(result.getResult().getUserType() == null){
//                    activity.startActivityForResult(new Intent(activity, SetShenfenActivity.class),1000);
//                }else{
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
                // }

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
                dialog.dismiss();
            }
        });
    }


    public synchronized void loginByOther(final Activity activity, String uid, String username, String userIcon, String type) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final QMUITipDialog dialog = new QMUITipDialog.Builder(activity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在登录").create();
        dialog.show();
        final EntityRequest request = new EntityRequest(Const.URL_LOGIN_BY_OTHER, LoginInfoBean.class);
        request.add("oth", uid);
        request.add("type", type);
        request.add("clientVesion", PackageUtil.getAppVersionName(mActivity));
        request.add("osVesion", Build.VERSION.SDK_INT);
        request.add("imei", TelephoneUtil.getIMEI(mActivity));
        request.add("mobile_model", android.os.Build.MODEL);
        request.add("channelId", App.CHANNEL_ID);
//        request.add("username",username);
//        request.add("headPic",userIcon);
        execute(request, new ResponseListener<LoginInfoBean>() {
            @Override
            public void onSucceed(int what, Result<LoginInfoBean> result) {
                super.onSucceed(what, result);
                Log.i("LoginSuccess:" + result.getResult());
                dialog.dismiss();
                APPDataPersistent.getInstance().setLoginInfoBean(result.getResult());
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
            }
        });

    }


    public synchronized void loginByWx(final Activity activity, String uid, String type) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        final QMUITipDialog dialog = new QMUITipDialog.Builder(activity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在登录").create();
        dialog.show();
        final EntityRequest request = new EntityRequest(Const.URL_LOGIN_WX_LOGIN, LoginInfoBean.class);
        request.add("code", uid);
        request.add("type", type);
        execute(request, new ResponseListener<LoginInfoBean>() {
            @Override
            public void onSucceed(int what, Result<LoginInfoBean> result) {
                super.onSucceed(what, result);
                Log.i("LoginSuccess:" + result.getResult());
                dialog.dismiss();
                APPDataPersistent.getInstance().setLoginInfoBean(result.getResult());
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
            }
        });

    }


    public void refresh(String token) {
        StringReqeust request = new StringReqeust(Const.URL_LOGIN);
        request.add("token", token);
        execute(request, null);
    }

    public void sendValidCode(String mobile) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在获取验证码").create();
        dialog.show();
        mDataBinding.getVerifyCode.setClickable(false);
        StringReqeust request = new StringReqeust(Const.URL_SENDVALIDCODE);
        request.add("mobile", mobile);
        execute(request, new ResponseListener() {
            @Override
            public void onSucceed(int what, Result result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                smsTimes = 60;
                mHandle.sendEmptyMessage(0);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
                new Toastor(mActivity).showSingletonToast(error);
                mDataBinding.getVerifyCode.setClickable(true);
            }
        });
    }


}
