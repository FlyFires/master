package cn.nineox.robot.appstrore.logic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.nineox.robot.appstrore.R;
import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.databinding.ActivityAppinfoBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.DownloadEvent;
import cn.nineox.robot.appstrore.utils.Utils;
import cn.nineox.xframework.base.BaseActivity;

/**
 * Created by me on 17/11/18.
 */

public class AppInfoActivity extends BaseActivity<ActivityAppinfoBinding>{


    private AppInfoLogic mLogic;

    private AppBean mAppBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAppBean = (AppBean) getIntent().getSerializableExtra(Const.EXTRA_APP);
        super.onCreate(savedInstanceState);

        BaseDownloadTask task = FileDownloadMgr.getInstance().getTask(mAppBean.getId());
        mViewDataBinding.circleProgressBar.setTextColor(Color.BLACK);
        if(task != null && FileDownloadStatus.isIng(task.getStatus())){
            mViewDataBinding.downloadBtn.setVisibility(View.GONE);
            mViewDataBinding.circleProgressBar.setVisibility(View.VISIBLE);
            setProgress(task);
        }else{
            mViewDataBinding.downloadBtn.setVisibility(View.VISIBLE);
            mViewDataBinding.circleProgressBar.setVisibility(View.GONE);
        }

        File file = new File(Const.FILE_DOWNLOAD_DIR +  "/"+ mAppBean.getId()+ ".apk");
        if(file.exists()){
            mViewDataBinding.downloadBtn.setText("安装");
        }else{
            mViewDataBinding.downloadBtn.setText("下载");
        }
        mViewDataBinding.setClickLinstener(this);

    }

    @NonNull
    @Override
    protected void createViewBinding() {
        mLogic = new AppInfoLogic(this,mViewDataBinding);
        mLogic.appinfo(mAppBean.getId());

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_appinfo;
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadEvent event) {
        BaseDownloadTask task = FileDownloadMgr.getInstance().getTask(mAppBean.getId());
        setProgress(task);

    }

    private void setProgress(BaseDownloadTask task){
        if(task != null){
            mViewDataBinding.circleProgressBar.setVisibility(View.VISIBLE);
            mViewDataBinding.downloadBtn.setVisibility(View.GONE);
            mViewDataBinding.circleProgressBar.setMaxValue(1000);
            int progress =(int)((task.getSmallFileSoFarBytes() * 1000f) / task.getSmallFileTotalBytes());
            mViewDataBinding.circleProgressBar.setProgress(progress);
        }else{
            mViewDataBinding.circleProgressBar.setVisibility(View.GONE);
            mViewDataBinding.downloadBtn.setVisibility(View.VISIBLE);
        }

        File file = new File(Const.FILE_DOWNLOAD_DIR +  "/"+ mAppBean.getId()+ ".apk");
        if(file.exists()){
            mViewDataBinding.downloadBtn.setText("安装");
        }else{
            mViewDataBinding.downloadBtn.setText("下载");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_btn:

                File file = new File(Utils.getAPKCachePath(mAppBean.getId()));
                if(file.exists()){
                    Utils.install(this.getApplicationContext(),file);
                }else{
                    mLogic.startDownload();
                }
                break;

        }
    }
}
