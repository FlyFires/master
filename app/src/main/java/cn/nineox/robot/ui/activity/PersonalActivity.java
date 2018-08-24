package cn.nineox.robot.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicActivity;
import cn.nineox.robot.databinding.ActivityPersonalBinding;
import cn.nineox.robot.logic.PersonalLogic;
import cn.nineox.robot.logic.bean.MessageEvent;
import cn.nineox.robot.logic.bean.UserType;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import cn.nineox.xframework.core.common.utils.PackageUtil;

/**
 * Created by me on 17/10/22.
 */

public class PersonalActivity extends BasicActivity<ActivityPersonalBinding> implements QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener {


    private PersonalLogic mLogic;

    private FunctionConfig functionConfig;

    private final int REQUEST_CODE_CAMERA = 1000;

    private final int REQUEST_CODE_GALLERY = 1001;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected void createViewBinding() {
        mLogic = new PersonalLogic(mViewDataBinding);
        mViewDataBinding.setClickListener(this);
        mViewDataBinding.setBean(APPDataPersistent.getInstance().getLoginInfoBean());
        mViewDataBinding.toolbarLayout.titleBar.setTitle("个人信息");
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        GlideUtils.loadRoundImageView(PersonalActivity.this, APPDataPersistent.getInstance().getLoginInfoBean().getHeadpic(), mViewDataBinding.headIv, R.drawable.ic_portrait, R.drawable.ic_portrait);

        //配置功能
        functionConfig = new FunctionConfig.Builder().setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .setCropHeight(200)
                .setCropHeight(200)
                .build();
        mViewDataBinding.version.setText(PackageUtil.getAppVersionName(this));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.head:
                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(PersonalActivity.this);
                builder.addItem("拍照");
                builder.addItem("从相册中");
                builder.addItem("取消");
                builder.setOnSheetItemClickListener(this);
                builder.build().show();
                break;
            case R.id.nickname:
                showEditNameDialog();
                break;
            case R.id.shenfen_layout:
                Intent intent = new Intent(PersonalActivity.this, SetShenfenActivity.class);
                startActivityForResult(intent, 1000);
                break;
            case R.id.logout_btn:
                mLogic.logout();
                EventBus.getDefault().post(new MessageEvent(0));
                this.startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void showEditNameDialog() {
//        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(PersonalActivity.this);
//        builder.setTitle("请输入")
//                .setPlaceholder("请输入您的姓名")
//                .setInputType(InputType.TYPE_CLASS_TEXT)
//                .addAction("取消", new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        dialog.dismiss();
//                    }
//                })
//                .addAction("确定", new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        CharSequence text = builder.getEditText().getText();
//                        if (text != null && text.length() > 0) {
//                            mLogic.userUpdate(PersonalActivity.this,text.toString(),null);
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(PersonalActivity.this, "姓名不能为空" , Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .show();

        if(APPDataPersistent.getInstance().getLoginInfoBean().getDevice() == null){
            new Toastor(this).showSingletonToast("请先绑定设备");
            return;
        }
        Intent intent = new Intent(PersonalActivity.this, SetNickNameActivity.class);
        startActivityForResult(intent, 1001);

    }

    @Override
    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
        switch (position) {
            case 0:
                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
                break;
            case 1:
                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY,
                        functionConfig,
                        mOnHanlderResultCallback);
                break;
            case 2:

                break;
        }
        dialog.dismiss();
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, final List<PhotoInfo> resultList) {
            if (resultList != null) {
                GlideUtils.loadRoundImageView(PersonalActivity.this, "file://" + resultList.get(0).getPhotoPath(), mViewDataBinding.headIv);
                mLogic.updateHeadPic(PersonalActivity.this, resultList.get(0).getPhotoPath());
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                UserType type = (UserType) data.getExtras().getSerializable("userType");
                mViewDataBinding.shenfen.setText(type.getDesc());
            } else if (requestCode == 1001) {
                mViewDataBinding.nicknameTx.setText(APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMidName());
            }

        }
    }
}
