package cn.nineox.robot.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.util.HashMap;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityLoginBinding;
import cn.nineox.robot.logic.LoginLogic;
import cn.nineox.robot.ui.MainActivity;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by me on 17/10/31.
 */

public class LoginActivity extends BasicActivity<ActivityLoginBinding> {


    private LoginLogic mLogic;


    @NonNull
    @Override
    protected void createViewBinding() {
        mLogic = new LoginLogic(this, mViewDataBinding);
        mViewDataBinding.setClickListener(this);
        /**
         * 申请相机、录音权限
         */
        requestPermission(CAMERA, RECORD_AUDIO, READ_PHONE_STATE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                mLogic.login(LoginActivity.this);
                break;
            case R.id.get_verify_code:
                String mobile = mViewDataBinding.mobile.getText().toString();
                if (!TextUtils.isEmpty(mobile)) {
                    mLogic.sendValidCode(mobile);
                } else {
                    new Toastor(this).showSingletonToast("手机号码不能为空");
                }
                break;
            case R.id.qq_btn:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(new PlatformListener());
                qq.authorize();
                qq.showUser(null);//授权并获取用户信息
                break;
            case R.id.wechat_btn:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(new PlatformListener());
                wechat.authorize();
                wechat.showUser(null);//授权并获取用户信息
                break;

        }
    }


    class PlatformListener implements PlatformActionListener {

        @Override
        public void onComplete(final Platform platform, int i, HashMap<String, Object> hashMap) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String userId = platform.getDb().getUserId();
                    String userName = platform.getDb().getUserName();
                    String userIcon = platform.getDb().getUserIcon();
                    if (platform.getName().equals(QQ.NAME)) {
                        requestUnionId(platform);
                    } else {
                        mLogic.loginByOther(LoginActivity.this, userId, userName, userIcon, platform.getName().toLowerCase());
                    }

                    // Log.e("--","username:" + platform.getDb().getUserName()  + "  " + platform.getDb().getUserIcon());
//                    if(platform.getName().equals(Wechat.NAME)){
//                        mLogic.loginByWx(LoginActivity.this,userId,platform.getName().toLowerCase());
//                    }else{
                    //mLogic.loginByOther(LoginActivity.this, userId, userName, userIcon, platform.getName().toLowerCase());
//                    }

                }
            });

        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e("--", "onError");
            throwable.printStackTrace();
            new Toastor(LoginActivity.this).showSingletonToast("授权失败.");
        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    }


    private void requestUnionId(final Platform platform) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(LoginActivity.this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在登录").create();
        dialog.show();
        String userId = platform.getDb().getUserId();
        final String userName = platform.getDb().getUserName();
        final String userIcon = platform.getDb().getUserIcon();
        final String token = platform.getDb().getToken();
        final StringRequest request = new StringRequest(String.format(Const.QQ_UNIONID, token));
        NoHttp.getRequestQueueInstance().add(0, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                try {
                    if (!TextUtils.isEmpty(result)) {
                        String str = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
                        JSONObject jsonObject = JSON.parseObject(str);
                        String uinionId = jsonObject.getString("unionid");
                        mLogic.loginByOther(LoginActivity.this, uinionId, userName, userIcon, platform.getName().toLowerCase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(int what, Response<String> response) {
                new Toastor(LoginActivity.this).showSingletonToast("授权失败.");
            }

            @Override
            public void onFinish(int what) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });
    }




    /*--------------------------------权限处理------------------------------------*/

    /**
     * 申请权限
     *
     * @param permissions 权限的名称
     */
    public void requestPermission(String... permissions) {
        if (checkPremission(permissions)) return;
        ActivityCompat.requestPermissions(this, permissions, 114);
    }

    /**
     * 权限检测
     *
     * @param permissions 权限的名称
     * @return
     */
    public boolean checkPremission(String... permissions) {
        boolean allHave = true;
        PackageManager pm = getPackageManager();
        for (String permission : permissions) {
            switch (pm.checkPermission(permission, getApplication().getPackageName())) {
                case PERMISSION_GRANTED:
                    allHave = allHave && true;
                    continue;
                case PERMISSION_DENIED:
                    allHave = allHave && false;
                    continue;
            }
        }
        return allHave;
    }

    /**
     * 权限处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 114 && permissions != null && permissions.length > 0) {
            String permission = "";
            for (int i = 0; i < permissions.length; i++) {
                permission = permissions[i];
                grantedResultDeal(
                        permission,
                        grantResults.length > i && grantResults[i] == PERMISSION_GRANTED);
            }
        }
    }

    /**
     * 权限返回值处理
     *
     * @param permission 权限的名称
     * @param isGranted  是否授权
     */
    protected void grantedResultDeal(String permission, boolean isGranted) {
        switch (permission) {
            case CAMERA:
                if (isGranted) {

                }
                break;
        }
    }


}
