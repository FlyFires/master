package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityChatRecordBinding;
import cn.nineox.robot.databinding.ActivityConversationDetailBinding;
import cn.nineox.robot.databinding.ItemAllRecordBinding;
import cn.nineox.robot.databinding.ItemConversationRecordBinding;
import cn.nineox.robot.databinding.ItemVideoRecordBinding;
import cn.nineox.robot.logic.bean.ChatRecordBean;
import cn.nineox.robot.logic.bean.ChatRecordListBean;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.EquipmentGet;
import cn.nineox.robot.logic.bean.VideoRecordBean;
import cn.nineox.robot.logic.bean.VideoRecordListBean;
import cn.nineox.robot.ui.activity.ChatRecordActivity;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;

/**
 * Created by me on 17/11/2.
 */

public class ConversationDetailLogic extends BasicLogic<ActivityConversationDetailBinding>{


    public ConversationDetailLogic(Activity activity,ActivityConversationDetailBinding dataBinding) {
        super(activity,dataBinding);
    }

    public void chatRecordList(final Activity activity, final DeviceBean deviceBean){
        final EntityRequest request = new EntityRequest(Const.URL_CHATRECORD_LIST,ChatRecordListBean.class);
        request.add("mid", deviceBean.getMid());
        request.add("start", 0);
        request.add("size", 3);
        execute(request,new ResponseListener<ChatRecordListBean>(){

            @Override
            public void onSucceed(int what, Result<ChatRecordListBean> result) {
                super.onSucceed(what, result);

                generateChatView(activity,result,deviceBean);

            }
        });
    }
    private void generateVideoView(final Activity activity,Result<VideoRecordListBean> result,final DeviceBean deviceBean){
        mDataBinding.recordVideoLayout.removeAllViews();
        if(result.getResult() != null &&result.getResult().getList().size() > 0){
            List<VideoRecordBean> chatRecordBeanList = result.getResult().getList();
            if(chatRecordBeanList.size() > 0){
                for (int i = 0;i < chatRecordBeanList.size();i++){
                    VideoRecordBean bean = chatRecordBeanList.get(i);
                    ItemVideoRecordBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_video_record,null,false);
                    binding.setData(bean);
                    binding.rightText.setVisibility(View.GONE);
                    mDataBinding.recordVideoLayout.addView(binding.getRoot());
                }
                ItemAllRecordBinding all = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.item_all_record,null,false);
                all.leftText.setText("全部视频记录");
                all.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity,ChatRecordActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Const.EXTRA_DEVICE,deviceBean);
                        bundle.putInt(Const.EXTRA_TYPE,0);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                        }
                });
                mDataBinding.recordVideoLayout.addView(all.getRoot());
            }

        }
    }


    private void generateChatView(final Activity activity,Result<ChatRecordListBean> result,final DeviceBean deviceBean){
        mDataBinding.recordChatLayout.removeAllViews();
        if(result.getResult() != null &&result.getResult().getList().size() > 0){
            List<ChatRecordBean> chatRecordBeanList = result.getResult().getList();
            if(chatRecordBeanList.size() > 0){
                for (int i = 0;i < chatRecordBeanList.size();i++){
                    ChatRecordBean bean = chatRecordBeanList.get(i);
                    ItemConversationRecordBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_conversation_record,null,false);
                    binding.setData(bean);
                    binding.rightText.setVisibility(View.GONE);
                    mDataBinding.recordChatLayout.addView(binding.getRoot());
                }
                ItemAllRecordBinding all = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.item_all_record,null,false);
                all.leftText.setText("全部交流记录");
                all.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity,ChatRecordActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Const.EXTRA_DEVICE,deviceBean);
                        bundle.putInt(Const.EXTRA_TYPE,1);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                });
                mDataBinding.recordChatLayout.addView(all.getRoot());
            }
        }
    }


    public void videoRecordList(final Activity activity, final DeviceBean deviceBean){
        EntityRequest request = new EntityRequest(Const.URL_VIDEORECORD_LIST,VideoRecordListBean.class);
        request.add("mid", deviceBean.getMid());
        request.add("start", 0);
        request.add("size", 3);
        execute(request,new ResponseListener<VideoRecordListBean>(){

            @Override
            public void onSucceed(int what, Result<VideoRecordListBean> result) {
                super.onSucceed(what, result);
                generateVideoView(activity,result,deviceBean);
            }
        });
    }


    public void equipmentGet(String mid){
        mDataBinding.getRoot().setVisibility(View.GONE);

        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在查询设备信息..").create();
        EntityRequest request = new EntityRequest(Const.URL_EQUIPMENT_GET,EquipmentGet.class);
        request.add("mid", mid);
        execute(request,new ResponseListener<EquipmentGet>(){

            @Override
            public void onSucceed(int what, Result<EquipmentGet> result) {
                super.onSucceed(what, result);
                EquipmentGet equipmentGet = result.getResult();
                mDataBinding.setEquipment(equipmentGet);
                dialog.dismiss();
                mDataBinding.getRoot().setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast("获取设备信息失败,"+error);
                dialog.dismiss();
                mActivity.finish();
            }
        });
    }


}
