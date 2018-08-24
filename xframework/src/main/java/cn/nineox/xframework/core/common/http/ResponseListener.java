package cn.nineox.xframework.core.common.http;

import android.content.Intent;

import com.yanzhenjie.nohttp.NoHttp;

import cn.nineox.xframework.core.android.log.Log;

/**
 * Created by me on 17/9/28.
 */

public class ResponseListener<T> implements HttpListener<T>  {


    @Override
    public void onSucceed(int what, Result<T> result) {
    }

    @Override
    public void onFailed(int what,String error) {
        if(error.equals("ERROR_CODE_INVALID_TOKEN")){

        }

    }

    @Override
    public void onFinish(int what) {

    }
}
