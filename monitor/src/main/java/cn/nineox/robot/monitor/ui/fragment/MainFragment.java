package cn.nineox.robot.monitor.ui.fragment;

import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.databinding.FragmentMainBinding;
import cn.nineox.robot.monitor.logic.MainLogic;
import cn.nineox.robot.monitor.logic.bean.EventMain;
import cn.nineox.xframework.base.BaseFragment;

/**
 * Created by me on 18/4/21.
 */

public class MainFragment extends BaseFragment<FragmentMainBinding> {
    private MainLogic sLogic;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void createViewBinding() {
        sLogic = new MainLogic(this, mViewDataBinding);
        sLogic.contactList(null);
        mViewDataBinding.setClickListener(this);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                showExitDialog();
                break;
        }

    }

    @Override
    public boolean onBackPressedSupport() {
        showExitDialog();
        return true;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMain event){
        if(sLogic != null){
            sLogic.contactList(event.getEventList());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewDataBinding.getRoot().setBackgroundResource(R.mipmap.bg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void showExitDialog() {
        this.getActivity().moveTaskToBack(true);
//        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this.getActivity());
//        builder.setTitle("提示");
//        builder.setMessage("是否要退出应用");
//        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
//            @Override
//            public void onClick(QMUIDialog dialog, int index) {
//                dialog.dismiss();
//            }
//        }).addAction(0, "退出", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
//            @Override
//            public void onClick(QMUIDialog dialog, int index) {
//                dialog.dismiss();
//                MainFragment.this.getActivity().finish();
//                System.exit(0);
//            }
//        })
//                .show();
    }


}
