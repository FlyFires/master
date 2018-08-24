package cn.nineox.robot.monitor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.databinding.ActivityMainBinding;
import cn.nineox.robot.monitor.logic.MainActivityLogic;
import cn.nineox.robot.monitor.logic.bean.EventMainActivity;
import cn.nineox.robot.monitor.ui.fragment.MainFragment;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.update.UpdateManager;
import cn.nineox.xframework.core.common.utils.AndroidUtil;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private long mExitTime;


    public static boolean isForeground = false;
    private String mUserid = "";


    private MainActivityLogic sLogic;

    @NonNull
    @Override
    protected void createViewBinding() {
        sLogic = new MainActivityLogic(this, mViewDataBinding);
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.contentPanel, new MainFragment());
        }
        mViewDataBinding.setClickListener(this);


       // mUserid = SharePrefUtil.getString(Const.EXTAR_ANDROID_ID);
        mUserid = AndroidUtil.getAndroidId(this);

        /**
         * 申请相机、录音权限
         */
        requestPermission(CAMERA, RECORD_AUDIO, READ_PHONE_STATE,WRITE_EXTERNAL_STORAGE);
        checkUpdate();
        //sLogic.initAll();
        EventBus.getDefault().register(this);


        //MQTTManager.getInstance().registerReceiveListener(MQTTReceiverListener mQTTReceiverListener);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void checkUpdate() {
        UpdateManager.setDebuggable(true);
        UpdateManager.setWifiOnly(false);
        UpdateManager.setUrl(Const.UPDATE_CHECK, "android");
        UpdateManager.check(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }

    }

    @Override
    public void onBackPressedSupport() {
     
/*        if (getTopFragment() instanceof CommonFragment) {
            exit();
            return;
        }if(getTopFragment() instanceof MainFragment){
            finish();
            return;
            //exit();
        }*/

        //return true;
        super.onBackPressedSupport();
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            new Toastor(this).showSingletonToast("再按一次退出");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        sLogic.unRegisterceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMainActivity event) {
        if (sLogic != null) {
            //sLogic.initAll();
            sLogic.deviceLogin();
        }
    }

    /**
     * 跳转至P2P界面
     *
     * @param bundle
     */
    private void startP2PActivity(Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(MainActivity.this, CallActivity.class);
        startActivity(intent);
    }

}
