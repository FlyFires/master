package cn.nineox.robot.appstrore;

/**
 * Created by me on 17/12/2.
 */

import android.view.View;
import android.widget.Toast;

import cn.nineox.robot.appstrore.common.weiget.TFTipsToast;
import cn.nineox.robot.appstrore.databinding.FragmentMainBinding;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.core.android.log.Log;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 17/10/16.
 */

public class MainFragment extends BaseFragment<FragmentMainBinding> {

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOUR = 3;
    public static final int FIVE = 4;

    private SupportFragment[] mFragments = new SupportFragment[5];

    private int mCurrentPos;

    private int mNextPos;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void createViewBinding() {
        mViewDataBinding.setClickListener(this);
        //SupportFragment firstFragment = findChildFragment(SongFragment.class);
        //if (firstFragment == null) {
            mFragments[FIRST] = MeAppFragment.newInstance(0,"edu");
            mFragments[SECOND] = AppListFragment.newInstance(0,"song");
            mFragments[THIRD] = AppListFragment.newInstance(0,"story");
            mFragments[FOUR] = AppListFragment.newInstance(0,"video");
            mFragments[FIVE] = AppListFragment.newInstance(0,"edu");

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOUR],
                    mFragments[FIVE]);
       // }
//        } else {
//            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
//
//            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.findFragmentByTag自行进行判断查找(效率更高些),用下面的方法查找更方便些
//            mFragments[FIRST] = findChildFragment(SongFragment.class);
//            mFragments[SECOND] = findChildFragment(StoryFragment.class);
//            mFragments[THIRD] = findChildFragment(VideoFragment.class);
//            mFragments[FOUR] = findChildFragment(EduFragment.class);
//        }
        this.mCurrentPos = FIRST;
        mViewDataBinding.ergeBtn.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.me_btn:
                mNextPos = FIRST;
                showTab(v);
                break;
            case R.id.erge_btn:
                mNextPos = SECOND;
                showTab(v);
                break;
            case R.id.gushi_btn:
                mNextPos = THIRD;
                showTab(v);
                break;

            case R.id.video_btn:
                mNextPos = FOUR;
                showTab(v);
                break;
            case R.id.yaojiao_btn:
                mNextPos = FIVE;
                showTab(v);
                break;



        }
    }

    private void showTab(View v){
        Log.e("--","mCurrentPos:"+ mCurrentPos   +"  mNextPos:" + mNextPos);
        showHideFragment(mFragments[mNextPos], mFragments[mCurrentPos]);
        mCurrentPos = mNextPos;
        resetBtnStatus();
        switch (mCurrentPos){
            case FIRST:
                mViewDataBinding.meBtn.setImageResource(R.mipmap.category_me_s);
                break;
            case SECOND:
                mViewDataBinding.ergeBtn.setImageResource(R.mipmap.category_erge_s);
                break;

            case THIRD:
                mViewDataBinding.gushiBtn.setImageResource(R.mipmap.category_gushi_s);
                break;
            case FOUR:
                mViewDataBinding.videoBtn.setImageResource(R.mipmap.category_video_s);
                break;
            case FIVE:
                mViewDataBinding.yaojiaoBtn.setImageResource(R.mipmap.category_youjiao_s);
                break;

        }
        //v.setSelected(true);
    }

    private void resetBtnStatus(){
        mViewDataBinding.ergeBtn.setImageResource(R.mipmap.category_erge_n);
        mViewDataBinding.gushiBtn.setImageResource(R.mipmap.category_gushi_n);
        mViewDataBinding.videoBtn.setImageResource(R.mipmap.category_video_n);
        mViewDataBinding.yaojiaoBtn.setImageResource(R.mipmap.category_youjiao_n);
        mViewDataBinding.meBtn.setImageResource(R.mipmap.category_me_n);
    }

}
