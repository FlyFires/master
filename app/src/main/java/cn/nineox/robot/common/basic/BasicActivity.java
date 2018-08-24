package cn.nineox.robot.common.basic;

import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import cn.nineox.robot.R;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.core.weiget.TitleBar;

/**
 * Created by me on 17/11/5.
 */

public abstract class BasicActivity<DataBinding extends ViewDataBinding> extends BaseActivity<DataBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & this.getWindow().getAttributes().flags)
                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                //沉浸了，巴拉巴拉
                View titleLayout = mViewDataBinding.getRoot().findViewById(R.id.toolbarLayout);
                if (titleLayout != null) {
                    View title = titleLayout.findViewById(R.id.title_bar);
                    if (title != null) {
                        QMUIStatusBarHelper.setStatusBarLightMode(this);
                        QMUIStatusBarHelper.translucent(this);
                        ((TitleBar) title).setImmersive(true);
                    }
                }
            }
        }
    }

}
