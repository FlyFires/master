package cn.nineox.robot.monitor.logic;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.nohttp.NoHttp;

import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.TUTKManager;
import cn.nineox.robot.monitor.logic.bean.IotAudioBean;
import cn.nineox.robot.monitor.logic.bean.NotifyBean;
import cn.nineox.robot.monitor.logic.bean.TLBaseBean;
import cn.nineox.robot.monitor.logic.bean.TLReqBaseBean;
import cn.nineox.xframework.core.common.http.AbstractRequest;
import cn.nineox.xframework.core.common.http.DefaultResponseListener;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;

public class TulingManager {

    private Context mContext;

    private static TulingManager mInstance;

    public TulingManager(Context context) {
        mContext = context;
    }

    public synchronized static TulingManager getIntance(Context context) {
        if (mInstance == null) {
            mInstance = new TulingManager(context);
        }
        return mInstance;
    }


//    播放与暂停
//    调用以下接口实现当前播放状态在H5页面上得到更新。
//    Method: POST
//    Content-Type: application/json
//    Path: http://iot-ai.tuling123.com/iot/status/notify
//    请求参数说明：
//    {
//        "apiKey": "f369b7ec-796c-4dbd-8d51-e59412aadf2c",
//            "deviceId":"aiAA8005dfc111c1",
//            "type":1,
//            "status":{
//        "title":"小燕子",
//                "mediaId":111,
//                "play":1
//    }
//    }
    public void statusNotify(NotifyBean notifyBean) {
        EntityRequest request = new EntityRequest(Const.VERIFY_CHECK, TLBaseBean.class);
        request.setDefineRequestBodyForJson(JSON.toJSONString(notifyBean));
        execute(request, new ResponseListener<TLReqBaseBean>() {

            @Override
            public void onSucceed(int what, Result<TLReqBaseBean> result) {
                super.onSucceed(what, result);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
            }
        });

    }


//    上一首/下一首
//    调用以下接口，可实现设备端切换“播放列表”中歌曲的功能。
//    Method: POST
//    Content-Type: application/json
//    Path: http://iot-ai.tuling123.com/v2/iot/audio
//apiKey	字符串	是	用于接口请求授权验证
//    deviceId	字符串	是	设备唯一性标识
//    type	整型	是	当type=0：下一首,payload为歌曲相关信息
//            当type=1:上一首
//    albumIds	array	否	默认专辑ID数组 目前支持上传的专辑ID就是 3、27、40、41

    public void iotAudio(TLReqBaseBean baseBean) {
        EntityRequest request = new EntityRequest("http://iot-ai.tuling123.com/v2/iot/audio", IotAudioBean.class);
        request.setDefineRequestBodyForJson(JSON.toJSONString(baseBean));
        execute(request, new ResponseListener<TLReqBaseBean>() {

            @Override
            public void onSucceed(int what, Result<TLReqBaseBean> result) {
                super.onSucceed(what, result);
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
            }
        });
    }


    protected <T> void execute(AbstractRequest<T> request, ResponseListener<T> listener) {
        NoHttp.getRequestQueueInstance().add(0, request, new DefaultResponseListener<T>(request, listener));
    }


}
