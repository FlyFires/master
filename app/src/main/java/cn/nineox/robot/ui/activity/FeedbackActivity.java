package cn.nineox.robot.ui.activity;

import android.support.annotation.NonNull;
import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityFeedbackBinding;
import cn.nineox.robot.logic.FeedbackLogic;

/**
 * Created by me on 17/11/5.
 */

public class FeedbackActivity extends BasicActivity<ActivityFeedbackBinding>{

    private FeedbackLogic mLogic;


    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        mLogic = new FeedbackLogic(mViewDataBinding);
        mViewDataBinding.toolbarLayout.titleBar.setTitle("帮助与反馈");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.commit:
                mLogic.feedback(this);
                break;
        }
    }
}
