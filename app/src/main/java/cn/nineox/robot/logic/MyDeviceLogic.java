package cn.nineox.robot.logic;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.yanzhenjie.nohttp.RequestMethod;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.FragmentMydeviceBinding;
import cn.nineox.robot.databinding.ItemDeviceBinding;
import cn.nineox.robot.logic.bean.DataResBean;
import cn.nineox.robot.logic.bean.DeviceBean;
import cn.nineox.robot.logic.bean.EmquipmentBean;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.bean.PageBean;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.fragment.BindFragment;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.weiget.decoration.HorizontalDividerItemDecoration;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 17/11/2.
 */

public class MyDeviceLogic extends BasicLogic<FragmentMydeviceBinding> implements OnRefreshListener, OnRefreshLoadmoreListener, BaseItemPresenter<DeviceBean> {

    private PageBean mPageBean;

    private BaseBindingAdapter mAdapter;

    private SupportFragment mFragment;

    private BindFragment mBindFragment;

    private List mDatas;

    private AlertDialog deleteDialog;

    public MyDeviceLogic(SupportFragment fragmenet, FragmentMydeviceBinding binding) {
        super(fragmenet, binding);
        mFragment = fragmenet;
        mBindFragment = new BindFragment();
    }


    public BaseBindingAdapter initRecyclerView() {
        mDataBinding.refreshLayout.setOnRefreshListener(this);
        mDataBinding.refreshLayout.setOnLoadmoreListener(this);
        mDataBinding.refreshLayout.setEnableAutoLoadmore(true);
        mDatas = new ArrayList();
        mDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mFragment.getContext()));
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0); // 设置饱和度
        final ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
        mAdapter = new BaseBindingAdapter<DeviceBean, ItemDeviceBinding>(mFragment.getContext(), mDatas, R.layout.item_device) {
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemDeviceBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                DeviceBean device = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
                //holder.getBinding().swithBtn.setVisibility(View.VISIBLE);
                ItemDeviceBinding binding = holder.getBinding();
                if (device != null) {
                    if (getItem(position).getMid() != null &&
                            getItem(position).getMid().equals(device.getMid())) {
                        binding.itemView.setBackgroundColor(Color.WHITE);
                        binding.deviceIv.setImageResource(R.drawable.ic_device_default);
                        binding.deviceTx.setTextColor(mActivity.getResources().getColor(R.color.titleText_color));
                    } else {
                        binding.itemView.setBackgroundColor(mActivity.getResources().getColor(R.color.e5e5e5));
                        binding.deviceIv.setImageResource(R.drawable.ic_device_disable);
                        binding.deviceTx.setTextColor(mActivity.getResources().getColor(R.color.rgb_66666));
                    }
                } else {


                }
            }
        };
        mDataBinding.recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mFragment.getContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.activity_vertical_margin).build());
        mDataBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.setItemPresenter(this);
        return mAdapter;
    }

    public void equipmentList(final BaseBindingAdapter<DeviceBean, ItemDeviceBinding> adapter) {
        EntityRequest request = new EntityRequest(Const.URL_EQUIPMENT_LIST, EmquipmentBean.class);
        request.add("size", 10);
        request.add("start", mPageBean == null ? 0 : mPageBean.getNextPage());
        execute(request, new ResponseListener<EmquipmentBean>() {
            @Override
            public void onSucceed(int what, Result<EmquipmentBean> result) {
                super.onSucceed(what, result);
                if(mPageBean.getPageCount() <= 1){
                    mAdapter.removeAllData();
                }
                adapter.addDatas(result.getResult().getList());
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
                mDataBinding.emptyView.success(adapter, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataBinding.refreshLayout.autoRefresh();
                    }
                });
                onSupportVisible();
//                if(mAdapter != null && mAdapter.getItemCount() == 0){
//                    mDataBinding.bindLayout.setVisibility(View.VISIBLE);
//                    mDataBinding.emptyView.setVisibility(View.GONE);
//                }else{
//
//                }

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                mDataBinding.refreshLayout.finishRefresh();
                mDataBinding.refreshLayout.finishLoadmore();
                mDataBinding.emptyView.error(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataBinding.refreshLayout.autoRefresh();
                    }
                });
            }
        });
    }


    public void equipmentSetDefault(final Activity activity, final DeviceBean deviceBean) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(activity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在保存").create();
        dialog.show();
        StringReqeust request = new StringReqeust(Const.EQUIPMENT_UPDATE, RequestMethod.GET);
        request.add("mid", deviceBean.getMid());
        request.add("id", deviceBean.getId());
        execute(request, new ResponseListener<DataResBean>() {
            @Override
            public void onSucceed(int what, Result<DataResBean> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                APPDataPersistent.getInstance().getLoginInfoBean().setDevice(deviceBean);
                APPDataPersistent.getInstance().saveLoginUserBean();

                UserChangeEvent event = new UserChangeEvent();
                event.setName(deviceBean.getMidName());
                EventBus.getDefault().post(event);

                mFragment.pop();
                new Toastor(mActivity).showSingletonToast("设备切换成功");
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(activity).showSingletonToast(error);
                dialog.dismiss();
            }
        });
    }


    private void delByUid(final DeviceBean deviceBean) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("请稍后").create();
        dialog.show();
        EntityRequest request = new EntityRequest(Const.EQUIPMENT_DELETE, Boolean.class);
        request.add("mid", deviceBean.getMid());
        request.add("uid", APPDataPersistent.getInstance().getLoginInfoBean().getUid());
        execute(request, new ResponseListener<Boolean>() {

            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                if (result.getResult()) {
                    DeviceBean currentDevice = APPDataPersistent.getInstance().getLoginInfoBean().getDevice();
//                    if (currentDevice != null && currentDevice.getMid().equals(deviceBean.getMid())) {
//                        APPDataPersistent.getInstance().getLoginInfoBean().setDevice(null);
//                    }
                    EventBus.getDefault().post(new UserChangeEvent());
                    new Toastor(mActivity).showSingletonToast("删除成功");
                    mDatas.remove(deviceBean);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
                dialog.dismiss();
            }

        });
    }


    public void equipmentSave(Activity activity, String url) {
        EntityRequest request = new EntityRequest(url/*Const.URL_EQUIPMENT_SAVE*/, DataResBean.class);
        execute(request, new ResponseListener<DataResBean>() {
            @Override
            public void onSucceed(int what, Result<DataResBean> result) {
                super.onSucceed(what, result);
            }
        });
    }

    public void userUpdatePhone(final Activity activity, final String mobile) {
        final StringReqeust request = new StringReqeust(Const.URL_USER_UPDATE);
        request.add("name", APPDataPersistent.getInstance().getLoginInfoBean().getName());
        request.add("mobile", mobile);
        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                new Toastor(activity).showToast("修改成功.");
                APPDataPersistent.getInstance().getUserBean().setMobile(mobile);
                APPDataPersistent.getInstance().getLoginInfoBean().setMobile(mobile);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(activity).showToast("修改失败:" + error);
            }
        });

    }


    public void onSupportVisible() {
        if (mAdapter != null && mAdapter.getItemCount() <= 0) {
            mDataBinding.bindLayout.setVisibility(View.VISIBLE);
            mDataBinding.emptyView.setVisibility(View.GONE);
        } else {
            mDataBinding.bindLayout.setVisibility(View.GONE);
            mDataBinding.emptyView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        equipmentList(mAdapter);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (mPageBean == null) {
            mPageBean = new PageBean();
        }
        mPageBean.setCurrentPage(-1);

        equipmentList(mAdapter);

    }

    @Override
    public void onItemClick(final DeviceBean deviceBean, int position) {
    }

    @Override
    public boolean onItemLongClick(final DeviceBean deviceBean, int position) {

        return true;
    }

    public void showDeleteDialog(final DeviceBean deviceBean) {
//        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(mActivity);
//        builder.setTitle("提示");
//        builder.setMessage("是否要删除该设备?");
//        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
//            @Override
//            public void onClick(QMUIDialog dialog, int index) {
//                dialog.dismiss();
//            }
//        }).addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
//            @Override
//            public void onClick(QMUIDialog dialog, int index) {
//                delByUid(deviceBean);
//                dialog.dismiss();
//            }
//        })
//                .show();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.Dialog);
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_message, null);
        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delByUid(deviceBean);
                deleteDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();

            }
        });
        builder.setView(dialogView);
        builder.create().setCanceledOnTouchOutside(true);
        deleteDialog = builder.show();
    }
}
