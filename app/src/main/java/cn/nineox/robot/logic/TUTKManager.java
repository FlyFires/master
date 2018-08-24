package cn.nineox.robot.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tutk.p2p.TUTKP2P;
import com.yanzhenjie.nohttp.NoHttp;

import java.security.MessageDigest;

import cn.nineox.robot.common.App;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.xframework.core.common.http.DefaultResponseListener;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;

public class TUTKManager {


    private static TUTKManager mInstance;

    private boolean mInit;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //login();
                    break;
            }
        }
    };

    private Context mContext;

    public TUTKManager(Context context) {
        mContext = context;
    }

    public synchronized static TUTKManager getIntance(Context context) {
        if (mInstance == null) {
            mInstance = new TUTKManager(context);
        }
        return mInstance;
    }


    public void heabeat() {
        Log.e("--","heabeat");
        LoginInfoBean loginInfoBean = APPDataPersistent.getInstance().getLoginInfoBean();
        if (TextUtils.isEmpty(loginInfoBean.getToken())) {
            return;
        }

        StringReqeust request = new StringReqeust(Const.HEARBEAT);
        String timestamp = String.valueOf(System.currentTimeMillis());
        request.addHeader("timestamp", timestamp);
        //request.addHeader("token", loginInfoBean.getToken());
        request.addHeader("userId", loginInfoBean.getUid());
        String str = loginInfoBean.getUid() + "&" + loginInfoBean.getToken() + "&" + timestamp;
        Log.e("http", "heabeat sign str：" + str);
        request.addHeader("sign", getSign(str));
        Log.e("http", "heabeat sign：" + getSign(str));
        NoHttp.getRequestQueueInstance().add(0, request, new DefaultResponseListener(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                if (!TextUtils.isEmpty(App.sSelfUID)) {
                    TUTKP2P.TK_getInstance().TK_device_checkOnLine(App.sSelfUID, 10 * 1000);
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
            }
        }));
    }



    public String getSign(String str) {
        String sign = "";
        //String str = "12&5e40b46f-a491-4ca9-a2d0-93fbf00ae0e2&1527168795756";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] sha = digest.digest(str.getBytes());
            //sign = HexUtil.encodeHexStr(android.util.Base64.encodeToString(sha, Base64.DEFAULT).getBytes());
            sign = cn.nineox.robot.utils.Base64.getEncoder().encodeToString(sha);
            cn.nineox.xframework.core.android.log.Log.e("--", "sign:" + sign);
        } catch (Exception e) {

        }

        return sign;
    }
}
