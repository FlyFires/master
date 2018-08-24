package cn.nineox.robot.appstrore.logic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.appstrore.AppListFragment;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppList;
import cn.nineox.xframework.core.android.log.Log;

/**
 * Created by me on 17/11/18.
 */

public class PageAdapter extends FragmentPagerAdapter{


    private List<AppBean> mAllapps;

    private final int mPageNumber = 9;

    private AppList mAppList;

    private String mCategory;


    public PageAdapter(FragmentManager fm,List<AppBean> allApps) {
        super(fm);
        this.mAllapps = allApps;
    }


    public PageAdapter(FragmentManager fm,AppList appList,String category) {
        super(fm);
        this.mAppList = appList;

        this.mCategory = category;
    }

    @Override
    public Fragment getItem(int position) {
//        List<AppBean> datas = new ArrayList<>();
//        for (int i = position * mPageNumber;(i - (position * mPageNumber)) < mPageNumber;i++){
//            if(i < mAllapps.size()){
//                datas.add(mAllapps.get((i)));
//            }else{
//                break;
//            }
//
//        }

        return AppListFragment.newInstance(position,mCategory);
    }

    @Override
    public int getCount() {
        //return mAllapps.size() / mPageNumber + (mAllapps.size() % mPageNumber > 0 ? 1 :0);
        return mAppList.getPageCount();
    }
}
