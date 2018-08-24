package cn.nineox.robot.logic;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;

import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yanzhenjie.nohttp.tools.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.common.widgets.EndlessRecyclerOnScrollListener;
import cn.nineox.robot.databinding.ActivityShareBinding;
import cn.nineox.robot.databinding.ItemSharePeopleBinding;
import cn.nineox.robot.logic.bean.ChatBean;
import cn.nineox.robot.logic.bean.ShareInfoBean;
import cn.nineox.robot.logic.bean.ShareUsersBean;
import cn.nineox.robot.logic.bean.UsageBean;
import cn.nineox.robot.logic.bean.UserBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.ui.activity.ShareActivity;
import cn.nineox.robot.utils.DialogUtil;
import cn.nineox.robot.utils.GlideUtils;
import cn.nineox.xframework.base.adapter.databinding.BaseItemPresenter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.inf.BaseTypeBindingAdapter;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by me on 17/11/2.
 */

public class ShareLogic extends BasicLogic<ActivityShareBinding> implements BaseItemPresenter<UserBean>{

    private BaseBindingAdapter mAdapter;

    private List<UserBean> mDatas;

    private int mCurrentPage = 1;

    private ShareInfoBean shareInfoBean;

    public ShareLogic(Activity activity, ActivityShareBinding dataBinding) {
        super(activity, dataBinding);
        initAdapter();
        initShare();
    }

    public BaseBindingAdapter initAdapter() {
        mDatas = new ArrayList<>();
        mAdapter = new BaseBindingAdapter<UserBean,ItemSharePeopleBinding>(mActivity,mDatas, R.layout.item_share_people){
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemSharePeopleBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                GlideUtils.loadRoundImageView(mActivity,getItem(position).getHeadPic(),holder.getBinding().headIv,
                        R.drawable.ic_share_headimg,R.drawable.ic_share_headimg);
                holder.getBinding().delete.setTag(getItem(position));
                holder.getBinding().delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(mActivity);
                        builder.setTitle("解除该联系人的绑定关系，该联系人将不会出现在设备联系人列表里");
                        builder.addItem("解除绑定");
                        builder.addItem("取消");
                        QMUIBottomSheet sheet = builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                            @Override
                            public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                                switch (position){
                                    case 0:
                                        UserBean userBean = (UserBean)view.getTag();
                                        delete(userBean);
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        }).build();
                        sheet.show();

                    }
                });
            }
        };
        mDataBinding.recyclerView.setAdapter(mAdapter);
        mAdapter.setItemPresenter(this);
        mDataBinding.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                // 加载更多
                onLoadmore();
            }
        });
        return mAdapter;
    }

    private void initShare() {
        getShare();
        sharelist();
    }


    public void getShare() {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在加载").create();
        dialog.show();
        EntityRequest request = new EntityRequest(Const.URL_GET_SHARE, ShareInfoBean.class);
        request.add("mid", APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        request.add("os", "android");
        request.add("pageId", mCurrentPage);
        execute(request, new ResponseListener<ShareInfoBean>() {

            @Override
            public void onSucceed(int what, Result<ShareInfoBean> result) {
                super.onSucceed(what, result);
                shareInfoBean = result.getResult();
                dialog.dismiss();

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
            }
        });
    }


    /**
     * 取消分享
     */
    public void delteBach(){
        EntityRequest request = new EntityRequest(Const.URL_SHARE_DELTEBACH, Boolean.class);
        request.add("mid", APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        execute(request, new ResponseListener<Boolean>() {

            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                if(!NetUtils.isNetworkAvailable()){
                    DialogUtil.showNetNoAvailableDialog(mActivity);
                    return;
                }else{
                    new Toastor(mActivity).showSingletonToast(error);
                }
            }
        });
    }

    public void delete(final UserBean userBean) {
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在删除").create();
        dialog.show();
        EntityRequest request = new EntityRequest(Const.URL_SHARE_DELETE, Boolean.class);
        request.add("mid", APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        request.add("uid", userBean.getUid());
        execute(request, new ResponseListener<Boolean>() {

            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                mDatas.remove(userBean);
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                new Toastor(mActivity).showSingletonToast(error);
                dialog.dismiss();
            }
        });
    }


    public void sharelist() {
        EntityRequest request = new EntityRequest(Const.URL_SHARE_LIST, ShareUsersBean.class);
        request.add("mid", APPDataPersistent.getInstance().getLoginInfoBean().getDevice().getMid());
        request.add("pageId", mCurrentPage);
        execute(request, new ResponseListener<ShareUsersBean>() {

            @Override
            public void onSucceed(int what, Result<ShareUsersBean> result) {
                super.onSucceed(what, result);
                if (mCurrentPage == 0) {
                    mAdapter.removeAllData();
                }
                mAdapter.addDatas(result.getResult().getList());
                if(mAdapter.getItemCount() == 0){
                    mDataBinding.emptyText.setVisibility(View.VISIBLE);
                    mDataBinding.recyclerView.setVisibility(View.GONE);
                }else{
                    mDataBinding.emptyText.setVisibility(View.GONE);
                    mDataBinding.recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
            }
        });
    }


    public void share(String name) {
        if(shareInfoBean == null){
            new Toastor(mActivity).showSingletonToast("分享信息获异常");
            return;
        }
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setTitle(mActivity.getResources().getString(R.string.app_name));
        sp.setTitleUrl(shareInfoBean.getUrl());
        sp.setText(shareInfoBean.getContent());
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setUrl(shareInfoBean.getUrl());
        sp.setSiteUrl(shareInfoBean.getUrl());
        sp.setImageData(BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_launcher));
        Platform platform = ShareSDK.getPlatform(name);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        platform.share(sp);
    }

    public void onLoadmore() {
        mCurrentPage += 1;
        sharelist();
    }


    @Override
    public void onItemClick(UserBean userBean, int position) {

    }

    @Override
    public boolean onItemLongClick(UserBean userBean, int position) {
        return false;
    }
}
