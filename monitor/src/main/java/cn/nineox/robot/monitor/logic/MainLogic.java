package cn.nineox.robot.monitor.logic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.nohttp.tools.NetUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.WaitCallActivity;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.databinding.FragmentMainBinding;
import cn.nineox.robot.monitor.databinding.ItemContactBinding;
import cn.nineox.robot.monitor.logic.bean.Contact;
import cn.nineox.robot.monitor.logic.bean.EventMainActivity;
import cn.nineox.robot.monitor.logic.bean.Peer;
import cn.nineox.robot.monitor.ui.fragment.AddFragment;
import cn.nineox.robot.monitor.utils.GlideUtils;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.robot.monitor.utils.SignUtil;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 18/4/21.
 */

public class MainLogic extends BaseLogic<FragmentMainBinding> implements BaseItemPresenter<Contact> {

    private BaseBindingAdapter mAdapter;

    private List<Contact> mDatas = new ArrayList<>();
    private AlertDialog deleteDialog;

    private int mDeletePos = -1;

    private int mIconDrawables[] = new int[]{R.mipmap.mama, R.mipmap.yeye, R.mipmap.baba};

    HashMap<String, Integer> mIconDrawableHm = new HashMap<>();

    public MainLogic(Fragment fragment, FragmentMainBinding binding) {
        super(fragment, binding);
        mIconDrawableHm.put("dad", R.mipmap.baba);
        mIconDrawableHm.put("mother", R.mipmap.mama);
        mIconDrawableHm.put("grandma", R.mipmap.nainai);
        mIconDrawableHm.put("grandpa", R.mipmap.yeye);
        LinearLayoutManager ms = new LinearLayoutManager(mActivity);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDataBinding.recyclerView.setLayoutManager(ms);

        mAdapter = new BaseBindingAdapter<Contact, ItemContactBinding>(mActivity, mDatas, R.layout.item_contact) {
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemContactBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                Contact contactBean = getItem(position);
                if (!TextUtils.isEmpty(contactBean.getUid())) {
                    //holder.getBinding().image.setImageResource(mIconDrawableHm.get(contactBean.getUserType().getKey()));
                    if (contactBean.getUserType() != null && !TextUtils.isEmpty(contactBean.getUserType().getDesc())) {
                        holder.getBinding().text.setText(contactBean.getUserType().getDesc());
                    }
                    GlideUtils.loadRoundImageView(mActivity, contactBean.getIcon(), holder.getBinding().image,
                            R.mipmap.ic_portrait, R.mipmap.ic_portrait);
                } else if (TextUtils.isEmpty(contactBean.getUid())) {
                    holder.getBinding().image.setImageResource(R.mipmap.ic_add);
                    holder.getBinding().text.setText("添加");
                }

                //&& position == (mDatas.size() - 1)

                if (mDeletePos == position) {
                    holder.getBinding().deleteIv.setVisibility(View.VISIBLE);
                } else {
                    holder.getBinding().deleteIv.setVisibility(View.GONE);
                }

                //holder.getBinding().simpleText.setBackgroundResource(backgrounds[position]);
            }

            @Override
            public int getItemCount() {
                if (mDatas.size() >= 6) {
                    return 6;
                }
                return mDatas.size();
            }
        };
        mAdapter.setItemPresenter(this);
        mDataBinding.recyclerView.setAdapter(mAdapter);

        mDataBinding.pullToRefresh.setOnPullListener(new QMUIPullRefreshLayout.OnPullListener() {
            @Override
            public void onMoveTarget(int offset) {

            }

            @Override
            public void onMoveRefreshView(int offset) {

            }

            @Override
            public void onRefresh() {

                contactList(null);
            }
        });
    }

    public void contactList(final String eventList) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载").create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        StringReqeust request = new StringReqeust(Const.LIST_BY_MID);
        request.add("mid", AndroidUtil.getAndroidId(mActivity.getApplicationContext()));
        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                try {
                    mDataBinding.pullToRefresh.finishRefresh();
                    dialog.dismiss();
                    mDatas.clear();
                    if (result != null && !TextUtils.isEmpty(result.getResult())) {
                        List<Contact> contactList = JSON.parseArray(result.getResult(), Contact.class);
                        mDatas.addAll(contactList);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (!TextUtils.isEmpty(eventList)) {
                    eventListMark(eventList);
                }
                mDatas.add(new Contact());
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                mDatas.clear();
                dialog.dismiss();
                mDatas.add(new Contact());
                mDataBinding.pullToRefresh.finishRefresh();
                if (!NetUtils.isNetworkAvailable()) {
                    cn.nineox.robot.monitor.utils.DialogUtil.showNetNoAvailableDialog(mActivity);
                } else {
                    new Toastor(mActivity).showSingletonToast(error);
                }
                mAdapter.notifyDataSetChanged();
            }

        });

    }


    private void eventListMark(String eventList) {
        cn.nineox.xframework.core.android.log.Log.e("http", "eventListMark");
        String token = App.getToken();
        String deviceId = AndroidUtil.getAndroidId(mActivity.getApplicationContext());
        EntityRequest request = new EntityRequest(Const.EVENT_MARK, Boolean.class);
        request.add("eventList", eventList);
        long timestamp = System.currentTimeMillis();
        request.addHeader("timestamp", String.valueOf(timestamp));
        request.addHeader("deviceId", deviceId);
        request.addHeader("token", App.getToken());
        String str = deviceId + "&" + token + "&" + timestamp + "&" + eventList;
        request.addHeader("sign", SignUtil.getSign(str));
        execute(request, new ResponseListener<Boolean>() {
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
            }
        });
    }


    private void delByUid(final Contact contact) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("请稍后").create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        EntityRequest request = new EntityRequest(Const.EQUIPMENT_DELETE_BY_UID, Boolean.class);
        request.add("mid", AndroidUtil.getAndroidId(mActivity.getApplicationContext()));
        request.add("uid", contact.getUid());
        request.add("token", App.getToken());
        execute(request, new ResponseListener<Boolean>() {

            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                if (result.getResult()) {
                    new Toastor(mActivity).showSingletonToast("删除联系人成功");
                    mDatas.remove(contact);
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

    private void call(final Contact contact, int mode) {
        MonitorRefreshManager.notifyRefreshListener();
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("正在接通").create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        getToPeerUid(contact.getUid(), new ResponseListener<Peer>() {
            @Override
            public void onSucceed(int what, Result<Peer> result) {
                super.onSucceed(what, result);
                if (dialog != null) {
                    dialog.dismiss();
                }
                Bundle bundle = new Bundle();
                Intent intent = new Intent(mActivity, WaitCallActivity.class);
                bundle.putString(App.BUNDLE_CALL_UID, result.getResult().getPeerUid());
                bundle.putString(App.BUNDLE_ACCOUNT_ID, contact.getUid());

                bundle.putBoolean(App.BUNDLE_IS_CALL_TO, true);
                bundle.putBoolean(App.BUNDLE_IS_DOUBLE_CALL, true);
                intent.putExtras(bundle);
                Log.e("--", "call:" + contact);
                mActivity.startActivityForResult(intent, App.INTENT_REQUEST_CALL_TO);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                if (dialog != null) {
                    dialog.dismiss();
                }
                new Toastor(mActivity).showSingletonToast(error);
            }
        });


    }

    @Override
    public void onItemClick(final Contact o, int position) {

        if (!NetUtils.isNetworkAvailable()) {
            cn.nineox.robot.monitor.utils.DialogUtil.showNetNoAvailableDialog(mActivity);
            return;
        }

        if (TextUtils.isEmpty(App.getToken())) {
            EventBus.getDefault().post(new EventMainActivity());
            return;
        }

        if (TextUtils.isEmpty(o.getUid())) {
            AddFragment fragment = new AddFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            ((SupportFragment) mFragment).start(fragment);
        } else {
            call(o, 0);
//            new QMUIBottomSheet.BottomListSheetBuilder(mActivity)
//                    .addItem("语音通话")
//                    .addItem("视频通话")
//                    .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
//                        @Override
//                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
//                            dialog.dismiss();
//                            switch (position) {
//                                case 0:
//                                    call(o, 2);
//                                    break;
//                                case 1:
//                                    call(o, 0);
//                                    break;
//                            }
//                        }
//                    })
//                    .build()
//                    .show();
        }
    }

    @Override
    public boolean onItemLongClick(Contact o, int position) {
        if (!TextUtils.isEmpty(o.getUid())) {
            mDeletePos = position;
            mAdapter.notifyDataSetChanged();
            showDeleteDialog(o);
        }
        return true;
    }

    private void showDeleteDialog(final Contact contactBean) {
        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_delete_contact, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.CustomDialog).setView(dialogView);
        dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
                delByUid(contactBean);
            }
        });
        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mDeletePos = -1;
                mAdapter.notifyDataSetChanged();
            }
        });
        deleteDialog = builder.show();
    }


    private void getToPeerUid(String id, ResponseListener<Peer> listener) {
        EntityRequest request = new EntityRequest(Const.GET_TO_PEERUID, Peer.class);
        request.add("id", id);
        request.add("type", "device");
        request.addHeader("token", App.getToken());
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        request.addHeader("deviceId", AndroidUtil.getAndroidId(mActivity));
        String str = AndroidUtil.getAndroidId(mActivity) + "&" + App.getToken() + "&" +
                timestamp + "&" + id + "&device";
        request.addHeader("sign", SignUtil.getSign(str));
        execute(request, listener);
    }

}
