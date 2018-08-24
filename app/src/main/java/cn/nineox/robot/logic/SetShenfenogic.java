package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yanzhenjie.nohttp.RequestMethod;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityAddDeviceBinding;
import cn.nineox.robot.databinding.ActivitySetShenfenBinding;
import cn.nineox.robot.databinding.ItemUserTypeBinding;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.EquipmentGet;
import cn.nineox.robot.logic.bean.ListByUserType;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.bean.UserType;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;

/**
 * Created by me on 17/11/25.
 */

public class SetShenfenogic extends BasicLogic<ActivitySetShenfenBinding> implements BaseItemPresenter<ListByUserType> {


    private List<ListByUserType> mUserTypes = new ArrayList<>();

    private BaseBindingAdapter mAdapter;

    private int mSelectPos = -1;

    public SetShenfenogic(Activity activity, ActivitySetShenfenBinding dataBinding) {
        super(activity, dataBinding);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0); // 设置饱和度
        final ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDataBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new BaseBindingAdapter<ListByUserType, ItemUserTypeBinding>(mActivity, mUserTypes, R.layout.item_user_type) {
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemUserTypeBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                ListByUserType type = getItem(position);
                GlideUtils.loadRoundImageView(mActivity, type.getIcon(), holder.getBinding().headIv,
                        R.drawable.ic_portrait, R.drawable.ic_portrait);
                if (mSelectPos == position) {
                    holder.getBinding().headIv.setColorFilter(null);
                    holder.getBinding().nameTx.setTextColor(mActivity.getResources().getColor(R.color.titleText_color));
                } else {
                    holder.getBinding().headIv.setColorFilter(grayColorFilter);
                    holder.getBinding().nameTx.setTextColor(mActivity.getResources().getColor(R.color.subText_color));
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


    public void save() {
        if(mSelectPos == -1 || mSelectPos >= mUserTypes.size()){
            new Toastor(mActivity).showSingletonToast("请选择您的选择身份");
            return;
        }
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在保存").create();
        dialog.show();
        final EntityRequest request = new EntityRequest(Const.URL_USER_UPDATE, Boolean.class);
        final UserType type = mUserTypes.get(mSelectPos).getUserType();
        request.add("userType", type.getKey());
        request.add("name",APPDataPersistent.getInstance().getLoginInfoBean().getName());
        request.add("mobile",APPDataPersistent.getInstance().getLoginInfoBean().getMobile());
        request.add("mid",APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        execute(request, new ResponseListener<Boolean>() {
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                if (result.getResult()) {
                    new Toastor(mActivity).showToast("修改成功.");
                    APPDataPersistent.getInstance().getLoginInfoBean().setUserType(type);
                    APPDataPersistent.getInstance().saveLoginUserBean();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userType",type);
                    intent.putExtras(bundle);
                    mActivity.setResult(Activity.RESULT_OK,intent);
                    mActivity.finish();
                }

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showToast(error);
                dialog.dismiss();
            }
        });


    }

    public void getUserTypes(String mid) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载").create();
        dialog.show();
        StringReqeust request = new StringReqeust(Const.LIST_BY_USER_TYPE, RequestMethod.GET);
        if(!TextUtils.isEmpty(mid)){
            request.add("mid", mid);
        }


        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                if(TextUtils.isEmpty(result.getResult())){
                    return;
                }
                List<ListByUserType> contactList = JSON.parseArray(result.getResult(), ListByUserType.class);
                mUserTypes.clear();
                mUserTypes.addAll(contactList);
                UserType type = APPDataPersistent.getInstance().getLoginInfoBean().getUserType();
                if(contactList != null && type != null){
                    for (int i =  0;i<contactList.size();i++){
                        if(contactList.get(i).getUserType().getKey().equals(type.getKey())){
                            mSelectPos = i;
                            break;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
                dialog.dismiss();
                mActivity.finish();
            }

        });

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
