package cn.nineox.robot.appstrore;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

import cn.nineox.robot.appstrore.databinding.ActivityMainBinding;
import cn.nineox.robot.appstrore.databinding.FragmentHomeBinding;
import cn.nineox.robot.appstrore.logic.HomeLogic;
import cn.nineox.robot.appstrore.logic.MainLogic;
import cn.nineox.xframework.base.BaseActivity;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.core.android.log.Log;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements ViewPager.OnPageChangeListener{

    private HomeLogic mLogic;

    protected String mCategory;


    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.pager.setOnPageChangeListener(this);
        mViewDataBinding.setClickListener(this);
        mLogic = new HomeLogic(this,mViewDataBinding);
        mLogic.applist(mCategory);
        mViewDataBinding.tag.setTag(mCategory);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
