package cn.nineox.robot.logic;

import android.app.Activity;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;

import java.security.MessageDigest;

import cn.finalteam.toolsfinal.coder.MD5Coder;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Base64;
import cn.nineox.xframework.core.common.http.AbstractRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.utils.HexUtil;
import cn.nineox.xframework.core.common.utils.MD5Util;

/**
 * Created by me on 17/11/1.
 */

public class BasicLogic<DataBinding extends ViewDataBinding> extends BaseLogic<DataBinding> {

    public BasicLogic(DataBinding dataBinding) {
        super(dataBinding);
    }

    public BasicLogic(Activity activity, DataBinding dataBinding) {
        super(activity, dataBinding);
    }


    public BasicLogic(Fragment fragment, DataBinding dataBinding) {
        super(fragment, dataBinding);
    }

    @Override
    protected void execute(AbstractRequest request, ResponseListener listener) {
        LoginInfoBean bean = APPDataPersistent.getInstance().getLoginInfoBean();
        Log.i("HTTP", "execute:" + request.url());
        if (bean.hasLogin()) {
            request.addHeader("token", bean.getToken());
        }
        super.execute(request, listener);
    }


    public String getSign(String str) {
        String sign = "";
        //String str = "12&5e40b46f-a491-4ca9-a2d0-93fbf00ae0e2&1527168795756";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] sha = digest.digest(str.getBytes());
            //sign = HexUtil.encodeHexStr(android.util.Base64.encodeToString(sha, Base64.DEFAULT).getBytes());
            sign = cn.nineox.robot.utils.Base64.getEncoder().encodeToString(sha);
            Log.e("--", "sign:" + sign);
        } catch (Exception e) {

        }

        return sign;
    }



}
