package cn.nineox.robot.monitor.common.tutk;

import com.tutk.libTUTKMedia.utils.MediaCodecUtils;
import com.tutk.p2p.TUTKP2P;

import cn.nineox.robot.monitor.logic.bean.DeviceLogin;
import cn.nineox.xframework.core.android.log.Log;

/**
 * @author zed
 * @date 2018/3/6 上午11:46
 * @desc app settings
 */

public class App {
	/* 会议主讲人id */
	public static String sInviteAccountID;

	/* 账号信息 */
	public static String sSelfUID;         //ex:AAAAAAAAAAAAAAAAAAAA
	public static String sSelfAccountID;   //ex : 123456
	public static String sSelfNickName;    //ex : Zed
	public static String sSelfImageURL;    //ex : /static/media/Account/image.jpg"

	public static DeviceLogin deviceLogin;

	/* 是否有邀请权限 */
	public static boolean sHasInvitePermission;

	/* 与p2p模块的连线链表 */
	public static CustomArrayList sConnectList = new CustomArrayList();
//
//	/* 好友账号 */
//	public static ArrayList<FriendInfo> sFriendList = new ArrayList<>();

	/* p2p default info */
	public static final String DEVICE_PASSWORD    = TUTKP2P.DEFAULT_PASSWORD;
	public static final String DEVICE_ACCOUNT     = TUTKP2P.DEFAULT_ACCOUNT;
	public static final int    DEVICE_CHANNEL     = TUTKP2P.DEFAULT_CHANNEL;
	public static final String DEVICE_DEFAULT_UID = "AAAAAAAAAAAAAAAAAAAA";

	public static final String CHANNEL_ID = "1001-1001-1001";

	/* media default info */
	public static final String VIDEO_FORMAT      = MediaCodecUtils.MEDIA_CODEC_H264;
	public static final int    VIDEO_WIDTH       = 320;
	public static final int    VIDEO_HEIGHT      = 240;
	public static final int    AUDIO_CODEC       = MediaCodecUtils.TUTK_AUDIO_AAC_ADTS;
	public static final int    AUDIO_SAMPLE_RATE = MediaCodecUtils.TUTK_AUDIO_SAMPLE_RATE_16K;
	public static final int    AUDIO_BIT         = MediaCodecUtils.TUTK_AUDIO_BIT_16;

	/* record/snapshot path */
	public static final String APP_PATH          = "/MediaSDK";
	public static final String APP_PATH_SNAPSHOT = APP_PATH + "/snapshot/";
	public static final String APP_PATH_RECORD   = APP_PATH + "/record/";

	/* image format */
	public static final String IMAGE_JPG  = "jpg";
	public static final String IMAGE_PNG  = "png";
	public static final String IMAGE_JPEG = "jpeg";

	/* bundle */
	public static final String BUNDLE_IS_CALL_TO     = "is_call_to";
	public static final String BUNDLE_IS_DOUBLE_CALL = "is_double_call";
	public static final String BUNDLE_ACCOUNT_ID     = "account_id";
	public static final String BUNDLE_CALL_UID       = "call_uid";
	public static final String BUNDLE_NICK_NAME      = "nick_name";
	public static final String BUNDLE_FRIEND_POS     = "friend_pos";
	public static final String BUNDLE_SID            = "sid";
	public static final String BUNDLE_INFO_LIST      = "struct_account_info_list";

	/* intent request */
	public static final int INTENT_REQUEST_CALL_TO       = 100;
	public static final int INTENT_REQUEST_CALL_BY       = 101;
	public static final int INTENT_REQUEST_QR_IMAGE      = 102;
	public static final int INTENT_REQUEST_ADD_CONTACTS  = 103;
	public static final int INTENT_REQUEST_EDIT_CONTACTS = 104;
	public static final int INTENT_REQUEST_ALBUM         = 105;
	public static final int INTENT_REQUEST_SETTING       = 106;

	/* intent result */
	public static final int INTENT_RESULT_DELETE = 200;

	/* broadcast action */
	public static final String ACTION_CALL_CLICK  = "action_call_click";
	public static final String ACTION_CALL_REMOVE = "action_call_remove";

	/* SharedPreferences tag */
	public static final String SP_SETTINGS = "sp_settings";

	public static final String SP_SELF_UID        = "sp_self_uid";
	public static final String SP_SELF_ACCOUNT_ID = "sp_self_id";
	public static final String SP_SELF_NICK_NAME  = "sp_self_nick_name";

	public static final String SP_ENCODE_SOFT = "sp_encode_soft";
	public static final String SP_DECODE_SOFT = "sp_decode_soft";

	public static String getToken(){
		if(deviceLogin != null){
			//Log.e("HTTP","getToken:" + deviceLogin.getToken());
			return deviceLogin.getToken();
		}
		return "";
	}

}
