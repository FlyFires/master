package cn.nineox.xframework.core.common.http;

import com.yanzhenjie.nohttp.RequestMethod;

/**
 * Created by Eval on 2017/11/3.
 */
public class StringReqeust extends AbstractRequest<String> {

    public StringReqeust(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }


    public StringReqeust(String url) {
        super(url, RequestMethod.POST);
    }

    @Override
    protected String getResult(String responseBody) throws Exception {
        return responseBody;
    }
}
