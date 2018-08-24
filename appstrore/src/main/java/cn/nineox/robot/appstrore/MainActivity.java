package cn.nineox.robot.appstrore;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.databinding.ActivityMainBinding;
import cn.nineox.robot.appstrore.logic.MainLogic;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.core.common.update.ICheckAgent;
import cn.nineox.xframework.core.common.update.IUpdateChecker;
import cn.nineox.xframework.core.common.update.IUpdateParser;
import cn.nineox.xframework.core.common.update.UpdateInfo;
import cn.nineox.xframework.core.common.update.UpdateManager;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private MainLogic mLogic;

    @NonNull
    @Override
    protected void createViewBinding() {
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.contentPanel, new MainFragment());
        }
        mViewDataBinding.setClickListener(this);
        UpdateManager.setDebuggable(true);
        UpdateManager.setWifiOnly(false);
        UpdateManager.setUrl(Const.UPDATE_CHECK, "yy");
        UpdateManager.check(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e("MainActivity", "onKeyDown:" + keyCode);
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            // 按下BACK，同时没有重复
//            Log.d("MainActivity", "onKeyDown()");
//            showExitDialog();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
    @Override
    public void onBackPressedSupport() {
        //super.onBackPressedSupport();
        showExitDialog();
    }
    AlertDialog dialog = null;
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("退出")
                .setMessage("确定要退出应用商城吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                finish();
                System.exit(0);
            }
        });

        dialog = builder.create();
        dialog.show();

    }
}
