package cn.nineox.robot.monitor.common;

/**
 * Created by me on 17/12/21.
 */

public class Const {

    public static String HOST = "http://device.efercro.com/edu";

    public static String CONTACT_SAVE = HOST + "/device/contact/save";


    public static String CONTACT_GET = HOST + "/device/contact/get";


    public static String CONTACT_LIST = HOST + "/device/contact/list";


    public static String UPLOAD_HEAD = HOST + "/device/contact/uploadHeadPic";


    public static String SET_CONTACT = HOST + "/device/contact/setContact";

    public static String SET_FAMILY = HOST + "/device/contact/setFamily";

    public static String CONTACT_DELECT = HOST + "/device/contact/delete";

    public static String TELRECORD_SAVE = HOST + "/device/telrecord/save";

    public static String TELRECORD_LIST = HOST + "/device/telrecord/list";

    public static String UPDATE_CHECK = HOST + "/device/version/get";

    public static String LIST_BY_MID = HOST + "/device/equipment/listByMid";
    public static String EQUIPMENT_DELETE_BY_UID = HOST + "/device/equipment/delByUid";

    public static String GET_APK = HOST + "/app/equipment/getApk";


    public static String GET_USER_BY_UID = HOST + "/device/user/getByUid";
    public static String GET_USER_BY_MOBILE = HOST + "/device/user/getByMobile";


    public static String DEVICE_LOGIN = HOST + "/device/login";

    public static String GET_MY_PEERUID = HOST + "/device/peertopeer/getMyPeerUid";

    public static String GET_TO_PEERUID = HOST + "/device/peertopeer/getToPeerUid";

    public static String HEARBEAT = HOST + "/device/connect/heartbeat";

    public static String GET_CALLER = HOST + "/device/peertopeer/getCaller";


    public static String EVENT_MARK = HOST + "/device/events/eventMark";


    public static String ACTION_ERROR_CODE_INVALID_TOKEN = "ERROR_CODE_INVALID_TOKEN";

    public static String ACTION_USER_CHANGGE = "USER_CHANGE";


    public static final String EXTAR_ANDROID_ID = "android_mid";

    /**
     * 监控开始
     */
    public static final String ACTION_MONITOR_START = "action_monitor_start";


    /**
     * 监控结束
     */
    public static final String ACTION_MONITOR_END = "action_monitor_end";


    public static final String VERIFY_CHECK = "http://apps.nineox.cn/verify/check";


    public static final String TL_APP_KEY = "5e8da34419d54415b625dea8e6b8b013";

}
