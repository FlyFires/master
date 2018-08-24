package cn.nineox.robot.common;

/**
 * Created by me on 17/11/1.
 */

public class Const {

    public static String RTC_DEVID = "34871596";


    public static String RTC_APPID = "anyrtcYGmdbyDcakMD";

    public static String RTC_APPKEY = "OCazoIqQtz57jVrcAtnyajEGsAMS0bQrIEL/00oNlpU";

    public static String RTC_APPTOKEN = "b85dfb56b4890ad74a574824783287c4";


    public static String HOST = "http://app.efercro.com/edu";

    public static String HOST_2 = "http://app.efercro.com/edu";


    /**
     * 登录
     */
    public static String URL_LOGIN = HOST + "/app/login";

    /**
     * 登录
     */
    public static String URL_LOGIN_BY_OTHER = HOST + "/app/loginByOther";

    /**
     * 登录
     */
    public static String URL_LOGIN_WX_LOGIN = HOST + "/app/wxLogin";

    /**
     * 注销
     */
    public static String URL_LOGOUT = HOST + "/app/logout";

    /**
     *  刷新token
     */
    public static String URL_REFRESH = HOST +"/app/refresh";

    /**
     * 详情
     */
    public static String URL_FEEDBACK_GET = HOST+  "/app/feedback/get";

    /**
     * 意见反馈 保存
     */
    public static String URL_FEEDBACK_SAVE = HOST+  "/app/feedback/save";

    /**
     * 交流记录列表
     */
    public static String URL_CHATRECORD_LIST = HOST+  "/app/chatrecord/list";

    /**
     * 聊天记录列表
     */
    public static String URL_CHATRECORD_LISTBYCHAT = HOST+  "/app/chatrecord/listByChat";


    public static String URL_CHATRECORD_SAVE = HOST+  "/app/chatrecord/save";

    /**
     * 设备列表
     */
    public static String URL_EQUIPMENT_LIST = HOST+  "/app/equipment/list";

    /**
     * 保存(二维码扫视频调用)
     */
    public static String URL_EQUIPMENT_SAVE = HOST+  "/app/equipment/save";


    public static String URL_EQUIPMENT_GET = HOST+  "/app/equipment/get";

    /**
     * 记录列表
     */
    public static String URL_VIDEORECORD_LIST = HOST+  "/app/videorecord/list";

    /**
     * 保存(查看视频时调用)
     */
    public static String URL_VIDEORECORD_SAVE = HOST+  "/app/videorecord/save";

    /**
     * 注销
     */
    public static String EQUIPMENT_UPDATE = HOST + "/app/equipment/update";


    /**
     * 记录列表
     */
    public static String URL_VIDEORECORD_LISTVIDEO= HOST+  "/app/videorecord/listVideo";


    public static String URL_GET_SHARE = HOST + "/app/equipment/share/getShare";

    public static String URL_SHARE_LIST = HOST +"/app/equipment/share/list";
    public static String URL_SHARE_DELETE = HOST +"/app/equipment/share/delete";
    public static String URL_SHARE_DELTEBACH = HOST +"/app/equipment/share/delteBach";

    /**
     * 绑定手机下发验证码
     */
    public static String URL_SENDVALIDCODE = HOST+  "/app/validcode/sendValidCode";

    /**
     * 看自己的资料
     */
    public static String URL_USER_GET = HOST+  "/app/user/get";

    /**
     * 上传头像
     */
    public static String URL_UPLOADHEADPIC = HOST+  "/app/user/uploadHeadPic";

    /**
     * 修改姓名
     */
    public static String URL_USER_UPDATE = HOST+  "/app/user/update";

    public static String URL_BABY_GET = HOST+  "/app/baby/get";


    public static String UPDATE_CHECK =HOST + "/app/version/get";

    public static String USER_TYPE = HOST + "/app/enum/userType";

    public static String LIST_BY_USER_TYPE = HOST + "/app/equipment/listByUserType";

    public static String EQUIPMENT_DELETE  = HOST + "/app/equipment/delete";


    public static String GET_MY_PEER_UID = HOST_2 + "/app/peertopeer/getMyPeerUid";

    public static String GET_TO_PEER_UID = HOST + "/app/peertopeer/getToPeerUid";

    public static String HEARBEAT  = HOST + "/app/connect/heartbeat";

    public static String EVENT_MARK = HOST + "/app/events/eventMark";


    public static String GET_CALLER = HOST + "/app/peertopeer/getCaller";

    public static String VERIFY_SAVE  = "http://apps.nineox.cn//verify/save";

    public static String QQ_UNIONID = "https://graph.qq.com/oauth2.0/me?access_token=%s&unionid=1";

    public static String TULING = "http://iot-ai.tuling123.com/jump/app/source?apiKey=5e8da34419d54415b625dea8e6b8b013&uid=%s&client=android";

    public static String EXTRA_DEVICE = "extra_device";

    public static String EXTRA_TYPE = "extra_type";

    public static String EXTRA_USER = "extra_user";

    public static String EXTRA_CHATRECORD = "extra_chatrecord";

    public static String EXTRA_MID = "extra_mid";

    public static String ACTION_ERROR_CODE_INVALID_TOKEN = "ERROR_CODE_INVALID_TOKEN";

    public static String ACTION_USER_CHANGGE = "USER_CHANGE";

}
