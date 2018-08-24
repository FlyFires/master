package cn.nineox.robot.appstrore.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.yanzhenjie.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import cn.nineox.robot.appstrore.PluginAPP;
import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.common.weiget.TFTipsToast;
import cn.nineox.robot.appstrore.databinding.ItemAppBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppInfo;
import cn.nineox.robot.appstrore.logic.bean.AppList;
import cn.nineox.robot.appstrore.logic.bean.DownloadEvent;
import cn.nineox.robot.appstrore.utils.Utils;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.SilentInstaller;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.DefaultResponseListener;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import cn.nineox.xframework.core.common.utils.SdCardUtil;
import cn.nineox.xframework.core.common.utils.TelephoneUtil;

/**
 * Created by me on 17/11/19.
 */

public class FileDownloadMgr extends FileDownloadListener {


    private HashMap<String, AppBean> mTasks = new HashMap<>();


    public static FileDownloadMgr mInstance;

    //packageName,  path

    private HashMap<String, String> mInstallingApp = new HashMap<>();


    public static FileDownloadMgr getInstance() {
        if (mInstance == null) {
            mInstance = new FileDownloadMgr();
        }

        return mInstance;

    }

    public void createDownloadTask(AppInfo appinfo) {
        addDonwloadTotal(appinfo.getId());
        BaseDownloadTask task = null;
        if (mTasks.get(appinfo.getId()) == null) {
            String path = Utils.getAPKCachePath(appinfo.getId());
            task = FileDownloader.getImpl().create(appinfo.getUrl())
                    .setPath(path)
                    .setListener(this);
            //mTasks.put(appinfo.getId(), task);

        } else {
            task = mTasks.get(appinfo.getId()).getTask();
        }


        task.start();
    }

    public void createDownloadTask(Context context,AppBean appinfo) {
        if (!SilentInstaller.hasSDCard(context) && SdCardUtil.getAvailableInternalMemorySize() < 250) {
            //new Toastor(context).showSingletonToast("内部空间不足，请插入TF卡");
            new TFTipsToast(context).show();
            return;
        }
        addDonwloadTotal(appinfo.getId());
        minaSave(appinfo.getId(),appinfo.getPackageName());
        BaseDownloadTask task;
        if (mTasks.get(appinfo.getId()) == null || mTasks.get(appinfo.getId()).getTask() == null) {
            String path = Utils.getAPKCachePath(appinfo.getId());
            task = FileDownloader.getImpl().create(appinfo.getUrl())
                    .setPath(path)
                    .setListener(this);
            appinfo.setTask(task);
            mTasks.put(appinfo.getId(), appinfo);
        } else {
            task = mTasks.get(appinfo.getId()).getTask();
        }
        Log.e("--","status:"+task.getStatus());
        if(FileDownloadStatus.isIng(task.getStatus())){
            FileDownloader.getImpl().pause(task.getId());
            appinfo.pause();
        }else if(task.getStatus() == FileDownloadStatus.paused){
            task.reuse();
            task.start();
        }else if(task.getStatus() == FileDownloadStatus.completed){
            task.reuse();
            task.start();
        }else if(task.getStatus() == FileDownloadStatus.INVALID_STATUS){
            task.start();
        }
    }

    public BaseDownloadTask getTask(String key) {
        if(mTasks.get(key) != null && mTasks.get(key).getTask() != null){
            return mTasks.get(key).getTask();
        }
        return null;

    }


    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        Object tag =task.getTag();
        if(tag != null && tag instanceof ItemAppBinding){
            ItemAppBinding binding = (ItemAppBinding) tag;
            if(task.getSmallFileTotalBytes() != 0){
                binding.circleProgressBar.setMax(1000);
                int progress =(int)((task.getSmallFileSoFarBytes() * 1000f) / task.getSmallFileTotalBytes());
                binding.circleProgressBar.setProgress(progress);
            }
            binding.circleProgressBar.setVisibility(View.VISIBLE);
            binding.pasueIv.setVisibility(View.GONE);

        }
        EventBus.getDefault().post(new DownloadEvent(task, soFarBytes, totalBytes));

    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        //Log.e("--", soFarBytes + "");
        Object tag =task.getTag();
        if(tag != null && tag instanceof ItemAppBinding){
            ItemAppBinding binding = (ItemAppBinding) tag;
            if(task.getSmallFileTotalBytes() != 0){
                //Log.e("--","sofar :" + task.getSmallFileSoFarBytes()   +   "total:"+ task.getSmallFileTotalBytes());
                binding.circleProgressBar.setMax(1000);
                int progress =(int)((task.getSmallFileSoFarBytes() * 1000f) / task.getSmallFileTotalBytes());
                //Log.e("--","progress:" + progress);
                binding.circleProgressBar.setProgress(progress);
            }
            binding.circleProgressBar.setVisibility(View.VISIBLE);
            binding.pasueIv.setVisibility(View.GONE);

        }
        //EventBus.getDefault().post(new DownloadEvent(task, soFarBytes, totalBytes));
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {

    }

    @Override
    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        //EventBus.getDefault().post(new DownloadEvent(task, 0, 0));
        Object tag =task.getTag();
        String iconUrl = "";
        if(tag != null && tag instanceof ItemAppBinding){
            ItemAppBinding binding = (ItemAppBinding) tag;
            binding.circleProgressBar.setVisibility(View.GONE);
            binding.pasueIv.setVisibility(View.GONE);
            if(binding.getData() != null){
                iconUrl = binding.getData().getIcon();
            }
        }
        PackageManager manager = PluginAPP.getInstance().getPackageManager();
        PackageInfo info = manager.getPackageArchiveInfo(task.getPath(),PackageManager.GET_ACTIVITIES);
        Log.e("--","PreferenceManager:" + info.packageName);
        mInstallingApp.put(info.packageName,task.getPath());
        PreferenceManager.getDefaultSharedPreferences(PluginAPP.getInstance()).edit().putBoolean(info.packageName,true).commit();
        PreferenceManager.getDefaultSharedPreferences(PluginAPP.getInstance()).edit().putString(info.packageName + "_icon",iconUrl).commit();
        Utils.install(PluginAPP.getInstance(), new File(task.getPath()));
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        Object tag =task.getTag();
        Log.e("--","pasue:" + tag);
        if(tag != null && tag instanceof ItemAppBinding){
            ItemAppBinding binding = (ItemAppBinding) tag;
            binding.circleProgressBar.setVisibility(View.GONE);
            binding.pasueIv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        e.printStackTrace();
    }

    @Override
    protected void warn(BaseDownloadTask task) {
    }


    public void addDonwloadTotal(String appids) {
        TelephonyManager tm = (TelephonyManager) PluginAPP.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        final StringReqeust request = new StringReqeust(Const.INCRDOWNLOAD_URL);
        request.add("appsId", appids);
        request.add("model", Build.MODEL);
        request.add("relase", android.os.Build.VERSION.RELEASE);
        request.add("serial", tm.getSimSerialNumber());
        request.add("product", Build.PRODUCT);
        NoHttp.getRequestQueueInstance().add(0,request,new DefaultResponseListener<String>(request,new ResponseListener<String>(){
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
            }
        }));
    }

    private void minaSave(String appids,String packagename){
        final StringReqeust request = new StringReqeust(Const.MINE_SAVE);
        request.add("appsId", appids);
        request.add("packageName", packagename);
        request.add("mid", AndroidUtil.getAndroidId(PluginAPP.getInstance()));
        Log.e("HTTP","appsId:" + appids  +  packagename+"   "+ AndroidUtil.getAndroidId(PluginAPP.getInstance()));
        NoHttp.getRequestQueueInstance().add(0,request,new DefaultResponseListener<String>(request,new ResponseListener<String>(){
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
            }
        }));
    }

    public HashMap<String, String> getInstallingApp() {
        return mInstallingApp;
    }

}
