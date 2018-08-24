package cn.nineox.robot.utils;

import android.app.Activity;
import android.os.Handler;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

/**
 * Created by me on 17/11/4.
 */

public class DialogUtil {


    public static void showNetNoAvailableDialog(Activity activity) {
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(activity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                .setTipWord("当前网络不可用，无法连接，请检查您的网络设置")
                .create();

        tipDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(tipDialog != null){
                    tipDialog.dismiss();
                }
            }
        }, 1500);

    }


}
