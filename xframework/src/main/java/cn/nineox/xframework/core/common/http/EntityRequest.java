package cn.nineox.xframework.core.common.http;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.nohttp.RequestMethod;

public class EntityRequest<Entity> extends AbstractRequest<Entity> {

    private Class<Entity> aClazz;

    public EntityRequest(String url, RequestMethod requestMethod, Class<Entity> clazz) {
        super(url, requestMethod);
        this.aClazz = clazz;
    }


    public EntityRequest(String url, Class<Entity> clazz) {
        super(url, RequestMethod.GET);
        this.aClazz = clazz;
    }

    @Override
    protected Entity getResult(String responseBody) throws Exception {
        return JSON.parseObject(responseBody, aClazz);
    }
}