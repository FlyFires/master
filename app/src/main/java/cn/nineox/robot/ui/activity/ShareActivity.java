package cn.nineox.robot.ui.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.yanzhenjie.nohttp.tools.NetUtils;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityShareBinding;
import cn.nineox.robot.logic.ShareLogic;
import cn.nineox.robot.utils.DialogUtil;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by me on 18/3/12.
 */

public class ShareActivity extends BasicActivity<ActivityShareBinding> {

    private ShareLogic mLogic;
    @NonNull
    @Override
    protected void createViewBinding() {
        mViewDataBinding.toolbarLayout.titleBar.setTitle("分享到");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLogic = new ShareLogic(this,mViewDataBinding);
        mViewDataBinding.setClickListener(this);
        LinearLayoutManager ms= new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        mViewDataBinding.recyclerView.setLayoutManager(ms);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(ShareActivity.this);
                builder.setTitle("取消分享，将解除所有被分享人的绑定关系");
                builder.addItem("取消分享");
                builder.addItem("取消");
                QMUIBottomSheet sheet = builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        switch (position){
                            case 0:
                                dialog.dismiss();
                                mLogic.delteBach();
                                finish();
                                break;

                            case 1:
                                dialog.dismiss();
                                break;
                        }
                    }
                }).build();
                sheet.show();
                break;
            case R.id.wechat:
                if(!NetUtils.isNetworkAvailable()){
                    DialogUtil.showNetNoAvailableDialog(ShareActivity.this);
                    return;
                }
                mLogic.share(Wechat.NAME);
                break;
//            case R.id.wechatmonents:
//                mLogic.share(WechatMoments.NAME);
//                break;
            case R.id.qq:
                if(!NetUtils.isNetworkAvailable()){
                    DialogUtil.showNetNoAvailableDialog(ShareActivity.this);
                    return;
                }
                mLogic.share(QQ.NAME);
                break;
        }
    }
}
