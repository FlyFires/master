package cn.nineox.xframework.base;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import java.util.UUID;

import cn.nineox.xframework.core.common.http.AbstractRequest;
import cn.nineox.xframework.core.common.http.DefaultResponseListener;
import cn.nineox.xframework.core.common.http.ResponseListener;


/**
 * Created by me on 17/9/27.
 */
public class BaseLogic <DataBinding extends ViewDataBinding>{

    private RequestQueue mRequestQueue;

    public Activity mActivity;

    public Fragment mFragment;

    private String TAG = UUID.randomUUID().toString();


    public DataBinding mDataBinding;

    public BaseLogic(DataBinding binding){
        mRequestQueue = NoHttp.getRequestQueueInstance();
        this.mDataBinding = binding;
    }



    public BaseLogic(Fragment fragment,DataBinding binding){
        mRequestQueue = NoHttp.getRequestQueueInstance();
        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
        this.mDataBinding = binding;
    }


    public BaseLogic(Activity activity,DataBinding binding){
        mRequestQueue = NoHttp.getRequestQueueInstance();
        this.mActivity = activity;
        this.mDataBinding = binding;
    }


    /**
     * 异步请求
     * @param what 多个请求对象需要区分时使用该方法
     * @param request
     * @param listener
     * @param <T>
     */
    public <T> void execute(int what, AbstractRequest<T> request, ResponseListener<T> listener) {
        request.setCancelSign(TAG);
        mRequestQueue.add(what, request, new DefaultResponseListener<T>(request,listener));
    }


    /**
     * 异步请求
     * @param request
     * @param listener
     * @param <T>
     */
    protected <T> void execute(AbstractRequest<T> request, ResponseListener<T> listener) {
        request.setCancelSign(TAG);
        mRequestQueue.add(0, request, new DefaultResponseListener<T>(request,listener));
    }

    /**
     * 根据标签取消所有请求
     * @param
     */
    public void cancelByTag(){
        mRequestQueue.cancelBySign(TAG);
    }


    /**
     * 取消队列中所有请求。
     */
    public void cancelAll() {
        mRequestQueue.cancelAll();
    }



}
