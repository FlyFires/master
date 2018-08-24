package cn.nineox.robot.ui.fragment;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yanzhenjie.nohttp.tools.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentMain2Binding;
import cn.nineox.robot.databinding.ItemMainMenuBinding;
import cn.nineox.robot.logic.Main2Logic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.Menu;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.BaobaoRzActivity;
import cn.nineox.robot.ui.activity.CallActivity;
import cn.nineox.robot.ui.activity.ChatRecordActivity;
import cn.nineox.robot.ui.activity.PersonalActivity;
import cn.nineox.robot.ui.activity.RobotPreviewActivity;
import cn.nineox.robot.ui.activity.ShareActivity;
import cn.nineox.robot.ui.activity.TulingActivity;
import cn.nineox.robot.ui.activity.WaitCallActivity;
import cn.nineox.robot.utils.DialogUtil;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.robot.utils.LogUtil;
import cn.nineox.robot.utils.PhoneFormatCheckUtils;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.base.adapter.listview.CommonAdapter;
import cn.nineox.xframework.base.adapter.listview.ViewHolder;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.weiget.decoration.HorizontalDividerItemDecoration;
import cn.nineox.xframework.core.weiget.decoration.VerticalDividerItemDecoration;

/**
 * Created by me on 18/1/16.
 */

public class Main2Fragment extends BasicFragment<FragmentMain2Binding> implements AdapterView.OnItemClickListener {

    private Main2Logic mLogic;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main2;
    }

    @Override
    protected void createViewBinding() {
        EventBus.getDefault().register(this);
        mLogic = new Main2Logic(this, mViewDataBinding);
        mViewDataBinding.toolbarLayout.titleBar.setTitle(R.string.app_name);
        if(APPDataPersistent.getInstance().getLoginInfoBean().getDevice() != null){
            mViewDataBinding.nameTx.setText(APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMidName());
        }
        LogUtil.debug("main2fragment createViewBinding");
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu("视频通话", R.drawable.menu_videocall));
        menus.add(new Menu("宝宝心情", R.drawable.menu_notice));
        menus.add(new Menu("视频监控", R.drawable.menu_monitor));
        //menus.add(new Menu("宝宝日志", R.drawable.ic_baobao_rz));
       //menus.add(new Menu("宝宝听听", R.drawable.menu_baobao));
        menus.add(new Menu("设备管理", R.drawable.ic_device_mgr));
        menus.add(new Menu("内容点播", R.drawable.yaoqingma));
        menus.add(new Menu("", R.drawable.transparent));
        mViewDataBinding.setClickListener(this);
        LogUtil.debug("pic  "+APPDataPersistent.getInstance().getLoginInfoBean().getHeadpic());
        GlideUtils.loadRoundImageView(_mActivity,APPDataPersistent.getInstance().getLoginInfoBean().getHeadpic(),mViewDataBinding.headIv,
                R.drawable.ic_portrait,R.drawable.ic_portrait);


    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.debug("main2fragment onResume");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head_iv:
            case R.id.name_tx:
                startActivity(new Intent(_mActivity, PersonalActivity.class));
                break;
            case R.id.videocall:
                if(!NetUtils.isNetworkAvailable()){
                    DialogUtil.showNetNoAvailableDialog(this.getActivity());
                    return;
                }
                mLogic.statrtVideoCall();
                break;
            case R.id.baobao:
                startChatRecord();
                break;
            case R.id.monitor:
                if(!NetUtils.isNetworkAvailable()){
                    DialogUtil.showNetNoAvailableDialog(this.getActivity());
                    return;
                }
                mLogic.statrtMonitor();
                break;
            case R.id.device_mgr:
                start(new MyDeviceFragment());
                break;
            case R.id.yaoqingma:
                if(!NetUtils.isNetworkAvailable()){
                    DialogUtil.showNetNoAvailableDialog(this.getActivity());
                    return;
                }
                if(APPDataPersistent.getInstance().getLoginInfoBean().getDevice() != null && !TextUtils.isEmpty(APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid())){
                    startActivity(new Intent(_mActivity, ShareActivity.class));
                }else{
                    new Toastor(_mActivity).showSingletonToast("请先绑定设备");
                }


//                Intent intent = new Intent(_mActivity,TulingActivity.class);
//                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!NetUtils.isNetworkAvailable()){
            DialogUtil.showNetNoAvailableDialog(this.getActivity());
            return;
        }
        switch (i) {
            case 0:

                break;
            case 1:


                break;
            case 2:

                break;
            case 3:

                break;
            case 4:


                break;
        }
    }


    private void startBaoBao() {

        try {
            Intent intent = _mActivity.getPackageManager().
                    getLaunchIntentForPackage("com.kunpeng.babyting");
            _mActivity.startActivity(intent);
        } catch (Exception e) {
            showDownloadDialog();
        }
    }


    private void showDownloadDialog(){
        if(mLogic.isDownloading()){
            new Toastor(_mActivity).showToast("宝宝听听正在下载.");
            return;
        }
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("宝宝听听未安装,是否要下载并安装？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        mLogic.babyApkGet(_mActivity);
                    }
                })
                .show();
    }

    private void startChatRecord() {
        DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        if (device != null) {
            Intent intent = new Intent(_mActivity, ChatRecordActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Const.EXTRA_DEVICE, device);
            bundle.putInt(Const.EXTRA_TYPE, 1);
            intent.putExtras(bundle);
            _mActivity.startActivity(intent);
        } else {
            start(new MyDeviceFragment());
            Toast.makeText(Main2Fragment.this.getActivity(), "请先绑定设备", Toast.LENGTH_SHORT).show();
        }

    }


    private void statrtVideoCall() {
        DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        if (device != null) {
            if (TextUtils.isEmpty(APPDataPersistent.getInstance().getLoginInfoBean().getMobile())) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(Main2Fragment.this.getContext());
                builder.setTitle("您需要先绑定手机号码")
                        .setPlaceholder("请输入您的手机号码")
                        .setInputType(InputType.TYPE_CLASS_PHONE)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                String text = builder.getEditText().getText().toString();

                                if (text != null && text.length() > 0 && PhoneFormatCheckUtils.isPhoneLegal(text)) {
                                    mLogic.userUpdatePhone(Main2Fragment.this.getActivity(), text);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(Main2Fragment.this.getActivity(), "手机号码不正确", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            } else {

                Bundle bundle = new Bundle();
                Intent intent = new Intent(this.getContext(), WaitCallActivity.class);
                bundle.putString(App.BUNDLE_ACCOUNT_ID, "208401");
                //bundle.putString(App.BUNDLE_CALL_UID, "F1YU8154UH7WKH6GUHZJ");
                //mCallUID = "F1YU8154UH7WKH6GUHZJ";
                bundle.putBoolean(App.BUNDLE_IS_CALL_TO, true);
                bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, true);
                intent.putExtras(bundle);
                intent.putExtra(Const.EXTRA_DEVICE, device);
                this.getActivity().startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
            }
        } else {
            start(new MyDeviceFragment());
            Toast.makeText(Main2Fragment.this.getActivity(), "请先绑定设备", Toast.LENGTH_SHORT).show();
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserChangeEvent event) {
        LogUtil.debug("main2fragment onMessageEvent");
        if(!TextUtils.isEmpty(event.getName())){
            mViewDataBinding.nameTx.setText(event.getName());
        }
        LogUtil.debug("pic 1 "+APPDataPersistent.getInstance().getLoginInfoBean().getHeadpic());
        if(!TextUtils.isEmpty(event.getHeadPic())){
            GlideUtils.loadRoundImageView(_mActivity,event.getHeadPic(),mViewDataBinding.headIv,
                    R.drawable.ic_portrait,R.drawable.ic_portrait);
            //mViewDataBinding.nameTx.setText(event.getName());
        }
        mLogic.userGet(event.getEventList());
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
