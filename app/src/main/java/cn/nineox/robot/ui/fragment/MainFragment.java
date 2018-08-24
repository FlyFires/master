package cn.nineox.robot.ui.fragment;

import android.view.View;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentMainBinding;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.core.weiget.BottomBar;
import cn.nineox.xframework.core.weiget.BottomBarTab;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 17/10/16.
 */

public class MainFragment extends BasicFragment<FragmentMainBinding> {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;


    private SupportFragment[] mFragments = new SupportFragment[4];
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        SupportFragment firstFragment = findChildFragment(MeFragmet.class);
        if (firstFragment == null) {
            mFragments[FIRST] = new MyDeviceFragment();
            mFragments[SECOND] = new MeFragmet();
            //mFragments[THIRD] = new MeFragmet();


            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND]
                    /*mFragments[THIRD]*/);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.findFragmentByTag自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(MeFragmet.class);
            //mFragments[THIRD] = findChildFragment(MeFragmet.class);
        }

        mViewDataBinding.bottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.tab_robot, getString(R.string.robot)))
                //.addItem(new BottomBarTab(_mActivity, R.drawable.tab_chat, getString(R.string.discover)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.tab_me, getString(R.string.me)));

        mViewDataBinding.bottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);

                BottomBarTab tab =  mViewDataBinding.bottomBar.getItem(FIRST);
//                if (position == FIRST) {
//                    tab.setUnreadCount(0);
//                } else {
//                    tab.setUnreadCount(tab.getUnreadCount() + 1);
//                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                //EventBus.getDefault().post(new TabSelectedEvent(position));
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bottomBar:

                break;
        }
    }
}
