package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yanzhenjie.nohttp.RequestMethod;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivitySetNameBinding;
import cn.nineox.robot.databinding.ActivitySetShenfenBinding;
import cn.nineox.robot.databinding.ItemUserTypeBinding;
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

public class SetNickNameLogic extends BasicLogic<ActivitySetNameBinding> {

    public SetNickNameLogic(Activity activity, ActivitySetNameBinding dataBinding) {
        super(activity, dataBinding);
    }


    public void save() {
        final String name = mDataBinding.name.getText().toString();
        if(TextUtils.isEmpty(name)){
            new Toastor(mActivity).showSingletonToast("请输入您的设备名称");
            return;
        }

        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在保存").create();
        dialog.show();
        final EntityRequest request = new EntityRequest(Const.URL_USER_UPDATE, Boolean.class);
        request.add("name", name);
        request.add("mobile", APPDataPersistent.getInstance().getLoginInfoBean().getMobile());
        request.add("mid",APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        execute(request, new ResponseListener<Boolean>() {
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                if (result.getResult()) {
                    new Toastor(mActivity).showToast("修改成功.");
                    APPDataPersistent.getInstance().getLoginInfoBean().getDevice().setMidName(name);
                    APPDataPersistent.getInstance().saveLoginUserBean();
                    Intent intent = new Intent();
                    mActivity.setResult(Activity.RESULT_OK,intent);
                    mActivity.finish();
                    UserChangeEvent event = new UserChangeEvent();
                    event.setName(name);
                    EventBus.getDefault().post(event);
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


}
