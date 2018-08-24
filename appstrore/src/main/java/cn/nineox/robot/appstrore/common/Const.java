package cn.nineox.robot.appstrore.common;

/**
 * Created by me on 17/11/18.
 */

public class Const {


    private final static String HOST = "http://apps.nineox.cn";

    public final static String APP_LIST_URL = HOST + "/mobile/apps/list";

    public final static String APPS_GET_URL = HOST + "/mobile/apps/get";

    public final static String INCRDOWNLOAD_URL = HOST + "/mobile/apps/incrDownload";


    public final static String EXTRA_APP = "cn.nineox.robot.appstrore.logic.bean.AppBean";

    public static String FILE_DOWNLOAD_DIR = "/sdcard/";

    public static String UPDATE_CHECK =HOST + "/version/get";


    public static String MINE_LIST = HOST + "/mine/list";


    public static String MINE_SAVE = HOST + "/mine/save";

}
