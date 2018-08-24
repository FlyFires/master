package cn.nineox.robot.appstrore.logic;

import android.content.pm.PackageInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.databinding.FragmentHomeBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppList;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AppUtil;

/**
 * Created by me on 17/11/18.
 */

public class HomeLogic extends BaseLogic<FragmentHomeBinding>{

    private List<AppBean> mDatas = new ArrayList<>();

    private AppList mAppList;

    private PageAdapter mPagerAdapter;


    public HomeLogic(Fragment fragment, FragmentHomeBinding binding) {
        super(fragment, binding);
    }

    public void applist(final String category){
        final EntityRequest request = new EntityRequest(Const.APP_LIST_URL, AppList.class);
        request.add("size",6);
        request.add("category",category);
        execute(request,new ResponseListener<AppList>(){
            @Override
            public void onSucceed(int what, Result<AppList> result) {
                super.onSucceed(what, result);
                if(result.getResult() != null){
                    mAppList = result.getResult();
                }
                if(result.getResult() != null && result.getResult().getList() != null){
                    mDatas = result.getResult().getList();
                }

                initPager(category);
            }

            @Override
            public void onFailed(int what,String error) {
                super.onFailed(what,error);
            }
        });

    }


    private void initPager(String category){
        mPagerAdapter = new PageAdapter(mFragment.getChildFragmentManager(),mAppList,category);
        mDataBinding.pager.setAdapter(mPagerAdapter);
        mDataBinding.indicator.setViewPager(mDataBinding.pager);
    }
}
