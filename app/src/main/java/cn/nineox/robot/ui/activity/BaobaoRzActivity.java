package cn.nineox.robot.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityBaobaoRzBinding;
import cn.nineox.robot.logic.BaobaoRzPagerAdapter;

/**
 * Created by me on 18/1/23.
 */

public class BaobaoRzActivity extends BasicActivity<ActivityBaobaoRzBinding> implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener{
    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.toolbarLayout.titleBar.setTitle("宝宝日志");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mViewDataBinding.viewpager.setAdapter(new BaobaoRzPagerAdapter(this));
        mViewDataBinding.segmented.setOnCheckedChangeListener(this);
        mViewDataBinding.viewpager.setOnPageChangeListener(this);
        mViewDataBinding.oneday.setChecked(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_baobao_rz;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.oneday:
                mViewDataBinding.viewpager.setCurrentItem(0);
                break;
            case R.id.oneweek:
                mViewDataBinding.viewpager.setCurrentItem(1);
                break;
            case R.id.onmouth:
                mViewDataBinding.viewpager.setCurrentItem(2);
                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mViewDataBinding.oneday.setChecked(false);
        mViewDataBinding.oneweek.setChecked(false);
        mViewDataBinding.onmouth.setChecked(false);
        switch (position){
            case 0:
                mViewDataBinding.oneday.setChecked(true);
                break;
            case 1:
                mViewDataBinding.oneweek.setChecked(true);
                break;
            case 2:
                mViewDataBinding.onmouth.setChecked(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
