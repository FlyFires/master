package cn.nineox.robot.appstrore.logic;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.nineox.robot.appstrore.R;
import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.databinding.FragmentApplistBinding;
import cn.nineox.robot.appstrore.databinding.ItemMeAppBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppList;
import cn.nineox.robot.appstrore.logic.bean.MineBean;
import cn.nineox.robot.appstrore.logic.bean.MineList;
import cn.nineox.robot.appstrore.utils.GlideUtils;
import cn.nineox.robot.appstrore.utils.Utils;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.utils.AndroidUtil;

/**
 * Created by me on 17/11/18.
 */

public class MeLogic extends BaseLogic<FragmentApplistBinding> implements View.OnLongClickListener{

    public BaseBindingAdapter<AppBean,ItemMeAppBinding> mAdapter;

    private List<AppBean> mDatas = new ArrayList<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MeLogic(Fragment fragment, FragmentApplistBinding binding) {
        super(fragment, binding);
        mAdapter = new BaseBindingAdapter<AppBean,ItemMeAppBinding>(mActivity, mDatas, R.layout.item_me_app){
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemMeAppBinding> holder, int position) {

                AppBean bean = mAdapter.getItem(position);
                if(bean != null){
                    //holder.getBinding().iconIv.setImageDrawable(bean.getIconDrawable());
                    GlideUtils.displayImageView( holder.getBinding().iconIv,bean.getIcon());
                    holder.getBinding().getRoot().setTag(bean);
                    holder.getBinding().getRoot().setOnLongClickListener(MeLogic.this);
                }
                super.onBindViewHolder(holder, position);

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

    AlertDialog dialog;
    @Override
    public boolean onLongClick(View view) {

        Object o = view.getTag();
        final AppBean appBean =  (AppBean) o;

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("删除")
                .setMessage("确定要删除"+ appBean.getName() + "吗？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.uninstallApk(mActivity,appBean.getPackageName());
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();

        return true;
    }


    public class APPListBaseItemPresenter implements BaseItemPresenter<AppBean> {

        @Override
        public void onItemClick(AppBean appBean, int position) {
            try{
                Intent intent =  mActivity.getPackageManager().
                        getLaunchIntentForPackage(appBean.getPackageName());
                mActivity.startActivity(intent);
            }catch (Exception e){

            }

        }

        @Override
        public boolean onItemLongClick(AppBean appBean, int position) {
            return true;
        }

    }



    public void meApplist(){
        final EntityRequest request = new EntityRequest(Const.MINE_LIST, MineList.class);
        request.add("size",1000);
        request.add("mid", AndroidUtil.getAndroidId(mActivity));
        execute(request,new ResponseListener<MineList>(){
            @Override
            public void onSucceed(int what, Result<MineList> result) {
                super.onSucceed(what, result);
                Log.e("MeLogic","onSucceed");
                if(result.getResult() != null && result.getResult().getList() != null){
                    List<MineBean> list  = result.getResult().getList();
                    getMeAppList(list);
                }else{
                    Log.e("MeLogic","finishRefresh");
                    mDataBinding.refreshLayout.finishRefresh();
                    mDataBinding.refreshLayout.finishLoadmore();
                }

            }

            @Override
            public void onFailed(int what,String error) {
                super.onFailed(what,error);
                Log.e("MeLogic","onFailed");
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
            }
        });

    }




    private synchronized void getMeAppList(final List<MineBean> mineList){
        final List<AppBean> apps = new ArrayList<>(); //用来存储获取的应用信息数据

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<AppBean> installApps = getApllInstallApp();
                Log.e("MeLogic","getApllInstallApp:" + installApps.size());
                for(AppBean appBean:installApps){
                    boolean loacalHas =  sp.getBoolean(appBean.getPackageName(),false);
                    if(loacalHas){
                        String iconUrl =  sp.getString(appBean.getPackageName() + "_icon","");
                        if(!TextUtils.isEmpty(iconUrl)){
                            appBean.setIcon(iconUrl);
                        }
                        apps.add(appBean);
                    }
                    if(!loacalHas){
                        for (MineBean mineBean :mineList){
                            if(appBean.getPackageName().equals(mineBean.getPackageName())){
                                if(!TextUtils.isEmpty(mineBean.getIcon())){
                                    appBean.setIcon(mineBean.getIcon());
                                }
                                apps.add(appBean);
                            }

                        }
                    }


                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDatas = apps;
                        mAdapter.removeAllData();
                        mAdapter.addDatas(apps);
                        Log.e("MeLogic","finishRefresh");
                        mDataBinding.refreshLayout.finishRefresh();
                        mDataBinding.refreshLayout.finishLoadmore();

                    }
                });
            }
        });

    }


    private List<AppBean> getApllInstallApp(){
        List<AppBean> appList = new ArrayList<>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = mActivity.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            AppBean tmpInfo =new AppBean();
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ){
                tmpInfo.setName(packageInfo.applicationInfo.loadLabel(mActivity.getPackageManager()).toString());
                tmpInfo.setIconDrawable(packageInfo.applicationInfo.loadIcon(mActivity.getPackageManager()));
                tmpInfo.setPackageName(packageInfo.applicationInfo.packageName);
                if(packageInfo.applicationInfo.icon != 0){
                    tmpInfo.setIcon("android.resource://"+packageInfo.applicationInfo.packageName +"/drawable/"+packageInfo.applicationInfo.icon);
                }else{
                    tmpInfo.setIcon("android.resource://cn.nineox.robot.appstrore/drawable/"+R.mipmap.ic_launcher);
                }

                appList.add(tmpInfo);
            }

        }
        return appList;
    }




}
