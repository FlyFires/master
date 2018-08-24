package cn.nineox.robot.appstrore.logic;


import android.app.Activity;
import android.databinding.tool.reflection.SdkUtil;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.appstrore.R;
import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.common.weiget.TFTipsToast;
import cn.nineox.robot.appstrore.databinding.ActivityAppinfoBinding;
import cn.nineox.robot.appstrore.databinding.FragmentApplistBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppInfo;
import cn.nineox.robot.appstrore.logic.bean.FileList;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.SilentInstaller;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.SdCardUtil;

/**
 * Created by me on 17/11/18.
 */

public class AppInfoLogic extends BaseLogic<ActivityAppinfoBinding>{

    private BaseBindingAdapter mAdapter;

    private List<FileList> mDatas = new ArrayList<>();

    private AppInfo mAppInfo;

    public AppInfoLogic(Activity activity, ActivityAppinfoBinding binding) {
        super(activity, binding);
        mDataBinding.recyclerview.setLayoutManager(new GridLayoutManager(mActivity,4));
        mAdapter  = new BaseBindingAdapter(mActivity,mDatas,R.layout.item_appinfo_photo);
        mDataBinding.recyclerview.setAdapter(mAdapter);
    }


    public void appinfo(String appid){
        EntityRequest request = new EntityRequest(Const.APPS_GET_URL, AppInfo.class);
        request.add("appsId",appid);
        execute(request,new ResponseListener<AppInfo>(){
            @Override
            public void onSucceed(int what, Result<AppInfo> result) {
                super.onSucceed(what, result);
                if(result.getResult() != null ){
                    mAppInfo = result.getResult();
                    mDataBinding.setBean(result.getResult());
                    mAdapter.removeAllData();
                    mAdapter.setDatas(result.getResult().getFileList());
                }

            }
        });

    }


    public void startDownload(){
        if(mAppInfo != null){
            if (!SilentInstaller.hasSDCard(mActivity) && SdCardUtil.getAvailableInternalMemorySize() < 250) {
                //new Toastor(context).showSingletonToast("内部空间不足，请插入TF卡");
                new TFTipsToast(mActivity).show();
                return;
            }
            FileDownloadMgr.getInstance().createDownloadTask(mAppInfo);
        }

    }

}
