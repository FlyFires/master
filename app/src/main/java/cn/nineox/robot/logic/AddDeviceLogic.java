package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tutk.p2p.TUTKP2P;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.tools.NetUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.tutk.CustomCommand;
import cn.nineox.robot.databinding.ActivityAddDeviceBinding;
import cn.nineox.robot.databinding.ItemUserTypeBinding;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.EquipmentGet;
import cn.nineox.robot.logic.bean.ListByUserType;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.bean.Peer;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.bean.UserType;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.robot.utils.LogUtil;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.DialogUtil;

/**
 * Created by me on 17/11/25.
 */

public class AddDeviceLogic extends BasicLogic<ActivityAddDeviceBinding> implements BaseItemPresenter<ListByUserType>{

    private EquipmentGet equipmentGet;

    private List<ListByUserType> mUserTypes = new ArrayList<>();

    private BaseBindingAdapter mAdapter;

    private int mSelectPos = -1;

    public AddDeviceLogic(Activity activity, ActivityAddDeviceBinding dataBinding) {
        super(activity,dataBinding);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0); // 设置饱和度
        final ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new BaseBindingAdapter<ListByUserType,ItemUserTypeBinding>(mActivity,mUserTypes, R.layout.item_user_type){
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemUserTypeBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                ListByUserType type = getItem(position);
                GlideUtils.loadRoundImageView(mActivity,type.getIcon(),holder.getBinding().headIv,
                        R.drawable.ic_portrait,R.drawable.ic_portrait);
                if(mSelectPos == position){
                    holder.getBinding().headIv.setColorFilter(null);
                }else{
                    holder.getBinding().headIv.setColorFilter(grayColorFilter);
                }
                if(type.isSelect()){
                    holder.getBinding().selectIv.setVisibility(View.VISIBLE);
                }else{
                    holder.getBinding().selectIv.setVisibility(View.GONE);
                }
            }
        };
        mAdapter.setItemPresenter(this);
        mDataBinding.recyclerView.setAdapter(mAdapter);
    }


    public void equipmentGet(String mid){
        mDataBinding.getRoot().setVisibility(View.GONE);

//        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
//                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
//                .setTipWord("正在查询设备信息..").create();
        EntityRequest request = new EntityRequest(Const.URL_EQUIPMENT_GET,EquipmentGet.class);
        request.add("mid", mid);
        execute(request,new ResponseListener<EquipmentGet>(){

            @Override
            public void onSucceed(int what, Result<EquipmentGet> result) {
                super.onSucceed(what, result);
                equipmentGet = result.getResult();
                mDataBinding.setData(result.getResult());
                //dialog.dismiss();
                mDataBinding.getRoot().setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast("获取设备信息失败,"+error);
                //dialog.dismiss();
                mActivity.finish();
            }
        });
    }


    public void equipmentSave(){
        final String midName =  mDataBinding.midName.getText().toString();
        if(TextUtils.isEmpty(midName)){
            new Toastor(mActivity).showSingletonToast("设备名称不能为空");
            return;
        }

        if(mSelectPos == -1 || mSelectPos >= mUserTypes.size()){
            new Toastor(mActivity).showSingletonToast("请选择用户身份");
            return;
        }
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在保存").create();
        dialog.show();
        StringReqeust request = new StringReqeust(Const.URL_EQUIPMENT_SAVE, RequestMethod.GET);
        request.add("mid", mDataBinding.mid.getText().toString());
        request.add("aliasName",midName);
        request.add("userType",mUserTypes.get(mSelectPos).getUserType().getKey());
        LogUtil.debug("mid"+mDataBinding.mid.getText().toString());
        execute(request,new ResponseListener<EquipmentGet>(){

            @Override
            public void onSucceed(int what, Result<EquipmentGet> result) {
                super.onSucceed(what, result);

                DeviceBean deviceBean = new DeviceBean();
                deviceBean.setMidName(midName);
                deviceBean.setMid( mDataBinding.mid.getText().toString());
                APPDataPersistent.getInstance().getLoginInfoBean().setDevice(deviceBean);
                APPDataPersistent.getInstance().getLoginInfoBean().setUserType(mUserTypes.get(mSelectPos).getUserType());
                APPDataPersistent.getInstance().saveLoginUserBean();
                dialog.dismiss();
                new Toastor(mActivity).showSingletonToast("保存设备信息成功");

                UserChangeEvent event = new UserChangeEvent();
                event.setName(midName);
                EventBus.getDefault().post(event);
                sendIOCtrl();
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
                new Toastor(mActivity).showSingletonToast("保存设备信息失败,"+error);
            }
        });
    }

    public void getUserTypes(String mid){
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载").create();
        dialog.show();
        StringReqeust request = new StringReqeust(Const.LIST_BY_USER_TYPE,RequestMethod.GET);
        request.add("mid",mid);

        execute(request,new ResponseListener<String>(){
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                List<ListByUserType> contactList = JSON.parseArray(result.getResult(),ListByUserType.class);
                mUserTypes.clear();
                mUserTypes.addAll(contactList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
                if(!NetUtils.isNetworkAvailable()){
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(mActivity)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord("当前网络不可用，无法连接，请检查您的网络设置")
                            .create();

                    tipDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(tipDialog != null){
                                tipDialog.dismiss();
                            }
                            mActivity.finish();
                        }
                    }, 1500);

                }else{
                    new Toastor(mActivity).showSingletonToast(error);
                    mActivity.finish();
                }


            }

        });

    }

    private void sendIOCtrl(){
        getToPeerUid(new ResponseListener<Peer>(){
            @Override
            public void onSucceed(int what, Result<Peer> result) {
                super.onSucceed(what, result);
                if(result.getResult() != null && !TextUtils.isEmpty(result.getResult().getPeerUid())){
                    Log.e("--","sendIOCtrl:");
                    TUTKP2P.TK_getInstance().TK_client_sendIOCtrl(result.getResult().getPeerUid(), App.DEVICE_CHANNEL,
                            CustomCommand.IOTYPE_USER_IPCAM_RESRESH_APP, CustomCommand.SMsgAVIoctrlCallEnd.parseContent(App.sSelfAccountID));
                }
            }
        });

    }


    /**
     * 获取我的直播uid
     */
    public void getToPeerUid(ResponseListener<Peer> listener) {
        LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
        DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
        final EntityRequest request = new EntityRequest(Const.GET_TO_PEER_UID, Peer.class);
        request.add("id", device.getMid());
        request.add("type", "app");
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        String str = loginInfoBean.getUid() + "&" + loginInfoBean.getToken() + "&" +
                timestamp + "&" + device.getMid() + "&app";
        request.addHeader("sign", getSign(str));
        execute(request, listener);
    }

    @Override
    public void onItemClick(ListByUserType listByUserType, int position) {
        if(listByUserType.isSelect()){
            return;
        }
        mSelectPos = position;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(ListByUserType listByUserType, int position) {
        return false;
    }
}
