package cn.nineox.robot.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityMainBinding;
import cn.nineox.robot.logic.MainLogic;
import cn.nineox.robot.logic.bean.MessageEvent;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.SetShenfenActivity;
import cn.nineox.robot.ui.fragment.Main2Fragment;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 * Created by me on 17/9/21.
 */


public class MainActivity extends BasicActivity<ActivityMainBinding> {

    private static final String TAG = "Robot";

    public static boolean isForeground = false;

    private PowerManager.WakeLock wakeLock;

    private MainLogic sLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 申请相机、录音权限
         */
        requestPermission(CAMERA, RECORD_AUDIO, READ_PHONE_STATE,WRITE_EXTERNAL_STORAGE);

        EventBus.getDefault().register(this);
        sLogic = new MainLogic(this, mViewDataBinding);
        sLogic.initJPush();
        sLogic.checkUpdate();
        //sLogic.initAll();
    }

    @NonNull
    @Override
    protected void createViewBinding() {

        if (APPDataPersistent.getInstance().getLoginInfoBean().getUserType() == null &&
                APPDataPersistent.getInstance().getLoginInfoBean().getDevice() != null) {
            this.startActivityForResult(new Intent(this, SetShenfenActivity.class), 1000);
        }
        if (findFragment(Main2Fragment.class) == null) {
            loadRootFragment(R.id.contentPanel, new Main2Fragment());
        }

    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).
//                newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK|
//                        PowerManager.ON_AFTER_RELEASE, "Robot");
//        wakeLock.acquire();
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
        sLogic.destory();
        EventBus.getDefault().unregister(this);
//        if(wakeLock != null){
//            wakeLock.release();
//        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        android.util.Log.e("MainActivity", "finish");
        finish();
    }


    @Override
    public void onBackPressedSupport() {
        //super.onBackPressedSupport();
        if (this.getTopFragment() instanceof Main2Fragment) {
            showExitDialog();
        } else {
            super.onBackPressedSupport();
        }
    }


    private void showExitDialog() {
        this.moveTaskToBack(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        } else {
            //finish();
        }
    }

}
