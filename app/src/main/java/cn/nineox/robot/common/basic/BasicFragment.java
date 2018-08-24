package cn.nineox.robot.common.basic;

import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import cn.nineox.robot.R;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.weiget.TitleBar;

/**
 * Created by me on 18/4/27.
 */

public abstract class BasicFragment<DataBinding extends ViewDataBinding> extends BaseFragment<DataBinding> {


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & _mActivity.getWindow().getAttributes().flags)
                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                //沉浸了，巴拉巴拉
                Log.e("--", "沉浸了");
                View titleLayout = mViewDataBinding.getRoot().findViewById(R.id.toolbarLayout);
                Log.e("--", titleLayout);
                if (titleLayout != null) {
                    View title = titleLayout.findViewById(R.id.title_bar);
                    Log.e("--", title);
                    if (title != null) {
                        QMUIStatusBarHelper.setStatusBarLightMode(this.getActivity());
                        QMUIStatusBarHelper.translucent(this.getActivity());
                        ((TitleBar) title).setImmersive(true);
                    }
                }
            }
        }
    }
}
