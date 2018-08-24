package cn.nineox.xframework.core.common.http;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Response;

public class DefaultResponseListener<T> implements OnResponseListener<Result<T>> {

    private HttpListener<T> httpListener;
    private AbstractRequest<T> abstractRequest;

    public DefaultResponseListener(AbstractRequest<T> abstractRequest,HttpListener<T> httpListener) {
        this.httpListener = httpListener;
        this.abstractRequest = abstractRequest;
    }

    @Override
    public void onStart(int what) {
    }

    @Override
    public void onSucceed(int what, Response<Result<T>> response) {
        // http层的请求成功，响应码可能是200、400、500。
        if (httpListener != null && !abstractRequest.isCanceled()
                && response.get().isSucceed() && response.get().getResult() != null){
            httpListener.onSucceed(what, response.get());
        }else{
            httpListener.onFailed(what, response.get().getError());
        }

    }

    @Override
    public void onFailed(int what, Response<Result<T>> response) {
        Exception exception = response.getException();

        String error = exception.getMessage();
        // 请求失败
        if (exception instanceof NetworkError) {// 网络不好
            error = "当前网络不可用,请检查";
        } else if (exception instanceof TimeoutError) {// 请求超时
            error = "请求超时,请检查网络是否可用";
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器

        } else if (exception instanceof URLError) {// URL是错的
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
        } else {
            error = "请求异常,请检查网络是否可用";
        }
        Logger.e("错误：" + exception.getMessage());
        if (httpListener != null && !abstractRequest.isCanceled())
            httpListener.onFailed(what,error);
    }

    @Override
    public void onFinish(int what) {
        if (httpListener != null)
            httpListener.onFinish(what);
    }
}