package cn.nineox.robot.appstrore.logic;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.appstrore.R;
import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.databinding.FragmentApplistBinding;
import cn.nineox.robot.appstrore.databinding.ItemAppBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppInfo;
import cn.nineox.robot.appstrore.logic.bean.AppList;
import cn.nineox.robot.appstrore.utils.Utils;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.common.assist.SilentInstaller;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AppUtil;
import cn.nineox.xframework.core.common.utils.PackageUtil;
import cn.nineox.xframework.core.common.utils.SdCardUtil;

/**
 * Created by me on 17/11/18.
 */

public class AppListLogic extends BaseLogic<FragmentApplistBinding> {

    public BaseBindingAdapter<AppBean,ItemAppBinding> mAdapter;

    private List<AppBean> mDatas = new ArrayList<>();

    public AppListLogic(Fragment fragment, FragmentApplistBinding binding) {
        super(fragment, binding);
        mAdapter = new BaseBindingAdapter<AppBean,ItemAppBinding>(mActivity, mDatas, R.layout.item_app){
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemAppBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                AppBean bean = mAdapter.getItem(position);

                if(bean != null){
                    Log.e("--","AppListLogic onBindViewHolder:" + bean.getName());
                    final BaseDownloadTask task = FileDownloadMgr.getInstance().getTask(bean.getId());
                    ItemAppBinding binding = holder.getBinding();
                    if(task != null){
                        task.setTag(binding);
                        if(task.getSmallFileTotalBytes() != 0){
                            //Log.e("--","sofar :" + task.getSmallFileSoFarBytes()   +   "total:"+ task.getSmallFileTotalBytes());
                            binding.circleProgressBar.setMax(1000);
                            int progress =(int)((task.getSmallFileSoFarBytes() * 1000f) / task.getSmallFileTotalBytes());
                            //Log.e("--","progress:" + progress);
                            binding.circleProgressBar.setProgress(progress);
                        }
                        binding.circleProgressBar.setVisibility(FileDownloadStatus.isIng(task.getStatus())
                                ?View.VISIBLE:View.GONE);

                    }else{
                        binding.circleProgressBar.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public int getItemCount() {
                if(mDatas != null && mDatas.size() >=4){
                    return mDatas.size();
                }else if(mDatas == null || mDatas.size() == 0){
                    return 0;
                }
                return 4;

            }
        };
        mAdapter.setItemPresenter(new APPListBaseItemPresenter());
        mDataBinding.recyclerview.setAdapter(mAdapter);
    }



    public void applist(final int position,String category){
        final EntityRequest request = new EntityRequest(Const.APP_LIST_URL, AppList.class);
        request.add("size",15);
        request.add("pageId",position);
        request.add("category",category);
        execute(request,new ResponseListener<AppList>(){
            @Override
            public void onSucceed(int what, Result<AppList> result) {
                super.onSucceed(what, result);
                if(result.getResult() != null && result.getResult().getList() != null){
                    if(position <= 1){
                        mAdapter.removeAllData();
                    }
                    if(result.getResult() != null && result.getResult().getList() != null){
                        mDatas = result.getResult().getList();
                        mAdapter.addDatas(result.getResult().getList());
                    }

                    mDataBinding.refreshLayout.finishRefresh();
                    mDataBinding.refreshLayout.finishLoadmore();

                }


//                AppBean appBean = new AppBean();
//                appBean.setIcon("https://www.baidu.com/img/bd_logo1.png");
//                appBean.setName("123");
//                appBean.setId("123");
//                appBean.setUrl("http://p.gdown.baidu.com/465990f3cc7dc6e5ef1ed861dd4ca988643da5c94c33c81b0cdc32f2e43991231538926fee90cd0043068e73ea842339fb9b1d77d4e40bf713447e9bd1c2ff952505034a349794985313c05c5b8f0df8f7f248779b7a82e03832c056003d1ee0ab5d6c78abd120549c33fac6ad13b0e99ecd314280474a72");
//                mAdapter.add(appBean);
            }

            @Override
            public void onFailed(int what,String error) {
                super.onFailed(what,error);
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();

            }
        });

    }



    public class APPListBaseItemPresenter implements BaseItemPresenter<AppBean> {

        @Override
        public void onItemClick(AppBean appBean, int position) {

//            Intent intent = new Intent(mActivity, AppInfoActivity.class);
//            intent.putExtra(Const.EXTRA_APP, appBean);
//            mActivity.startActivity(intent);

            if(Utils.hasUpdate(mActivity,appBean.getPackageName(),appBean.getVersionCode())){
                File file = new File(Utils.getAPKCachePath(appBean.getId()));
                if(file.exists()){
                    Utils.install(mActivity,file);
                }else{
                    startDownload(appBean);
                    //appinfo(appBean);
                }
            }else{
                new Toastor(mActivity).showSingletonToast("你已安装该应用");
            }

        }

        @Override
        public boolean onItemLongClick(AppBean appBean, int position) {
            return false;
        }

    }


    public void appinfo(final  AppBean appBean){
        EntityRequest request = new EntityRequest(Const.APPS_GET_URL, AppInfo.class);
        request.add("appsId",appBean.getId());
        execute(request,new ResponseListener<AppInfo>(){
            @Override
            public void onSucceed(int what, Result<AppInfo> result) {
                super.onSucceed(what, result);
                Log.e("--","onSucceed:"+ result.getResult());
                if(result.getResult() != null){
                    //startDownload(appBean,result.getResult());
                }
            }
        });

    }


    private void startDownload(AppBean appBean){
        FileDownloadMgr.getInstance().createDownloadTask(mActivity,appBean);
    }

    public void notifyDataSetChanged(){
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }



    private void getApllInstallApp(){
        ArrayList<AppBean> appList = new ArrayList<>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = mActivity.getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            AppBean tmpInfo =new AppBean();
            tmpInfo.setName(packageInfo.applicationInfo.loadLabel(mActivity.getPackageManager()).toString());
            //tmpInfo.setIconDrawable(packageInfo.applicationInfo.icon);
            tmpInfo.setPackageName(packageInfo.applicationInfo.packageName);
            appList.add(tmpInfo);
        }
    }



}
