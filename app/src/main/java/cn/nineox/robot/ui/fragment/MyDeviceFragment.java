package cn.nineox.robot.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentMydeviceBinding;
import cn.nineox.robot.logic.MyDeviceLogic;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.AddDeviceActivity;
import cn.nineox.robot.ui.activity.PersonalActivity;
import cn.nineox.robot.ui.activity.RobotPreviewActivity;
import cn.nineox.robot.utils.PhoneFormatCheckUtils;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.core.common.utils.TelephoneUtil;
import cn.nineox.xframework.core.weiget.TitleBar;

/**
 * Created by me on 17/10/29.
 */

public class MyDeviceFragment extends BasicFragment<FragmentMydeviceBinding> implements BaseItemPresenter<DeviceBean>{


    private BaseBindingAdapter mAdapter;


    private List<DeviceBean> mDatas;


    public static final int REQUEST_CODE = 10000;


    public static final int ADD_DEVICE_REQUEST_CODE = 10001;

    private MyDeviceLogic mLogic;


    @NonNull
    @Override
    protected void createViewBinding() {
        mLogic = new MyDeviceLogic(this,mViewDataBinding);
        mViewDataBinding.toolbarLayout.titleBar.setTitle("我的设备");
        mViewDataBinding.toolbarLayout.titleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return "";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_add;
            }

            @Override
            public void performAction(View view) {
                startCaptureActivity();
            }
        });
        mViewDataBinding.toolbarLayout.titleBar.setLeftImageResource(R.drawable.ic_back);
        mViewDataBinding.toolbarLayout.titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop();
            }
        });
        mViewDataBinding.setClickListener(this);
        BaseBindingAdapter adapter = mLogic.initRecyclerView();
        adapter.setItemPresenter(this);

        mViewDataBinding.refreshLayout.autoRefresh();



    }

    private void startCaptureActivity(){
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //Toast.makeText(getActivity(), "解析结果:" + result, Toast.LENGTH_LONG).show();
                    startAddDeviceActivity(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }else if(requestCode == ADD_DEVICE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                mViewDataBinding.refreshLayout.autoRefresh();
            }
        }

    }

    private void startAddDeviceActivity(String mid){
        Intent intent = new Intent(this.getActivity(), AddDeviceActivity.class);
        intent.putExtra(Const.EXTRA_MID,mid);
        startActivityForResult(intent,ADD_DEVICE_REQUEST_CODE   );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mydevice;
    }





    @Override
    public void onItemClick(DeviceBean deviceBean, int position) {
        mLogic.equipmentSetDefault(this.getActivity(),deviceBean);
//        if(TextUtils.isEmpty(APPDataPersistent.getInstance().getLoginInfoBean().getMobile())){
//            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(MyDeviceFragment.this.getContext());
//            builder.setTitle("您需要先绑定手机号码")
//                    .setPlaceholder("请输入您的手机号码")
//                    .setInputType(InputType.TYPE_CLASS_PHONE)
//                    .addAction("取消", new QMUIDialogAction.ActionListener() {
//                        @Override
//                        public void onClick(QMUIDialog dialog, int index) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .addAction("确定", new QMUIDialogAction.ActionListener() {
//                        @Override
//                        public void onClick(QMUIDialog dialog, int index) {
//                            String text = builder.getEditText().getText().toString();
//
//                            if (text != null && text.length() > 0 && PhoneFormatCheckUtils.isPhoneLegal(text)) {
//                                mLogic.userUpdatePhone(MyDeviceFragment.this.getActivity(),text);
//                                dialog.dismiss();
//                            } else {
//                                Toast.makeText(MyDeviceFragment.this.getActivity(), "手机号码不正确" , Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    })
//                    .show();
//        }else{
//            Intent intent = new Intent(this.getActivity(), RobotPreviewActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("userid", APPDataPersistent.getInstance().getLoginInfoBean().getP2PUid());
//            bundle.putString("callid",deviceBean.getMid());
//            bundle.putBoolean("p2p_push", true);
//            bundle.putBoolean("is_host", true);
//            bundle.putInt("p2p_mode", 3);
//            intent.putExtras(bundle);
//            intent.putExtra(Const.EXTRA_DEVICE,deviceBean);
//            this.getActivity().startActivity(intent);
//        }

    }

    @Override
    public boolean onItemLongClick(DeviceBean chatRecordBean, int position) {

        mLogic.showDeleteDialog(chatRecordBean);
        return false;
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()){
            case R.id.bind:
                startCaptureActivity();
                break;
        }
    }
}
