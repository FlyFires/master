package cn.nineox.robot.common.tutk;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

/**
 * @author zed
 * @date 2018/3/6 上午9:39
 * @desc
 */

public class CustomCommand {

	public static final int IOTYPE_USER_IPCAM_CALL_REQ  = 0x0901; // start call request
	public static final int IOTYPE_USER_IPCAM_CALL_RESP = 0x0902;// start call response

	public static final int IOTYPE_USER_IPCAM_CALL_END = 0x0900;//ring off the meeting

	public static final int IOTYPE_USER_IPCAM_SWITCH_AUDIO = 0x0903;//ring off the meeting
	public static final int IOTYPE_USER_IPCAM_RESRESH_APP = 0x0904;//ring off the meeting

	public static final int IOTYPE_USER_IPCAM_CALL_ING = 0x0905;//正在通话

	//方向 写入方向后必须要写入停止指令才会停止
	public final static int FEET_MOTOR_LEFT = 0x0906;
	public final static int FEET_MOTOR_RIGHT = 0x0907;
	public final static int FEET_MOTOR_FORWARD = 0x0908;
	public final static int FEET_MOTOR_BACK = 0x0909;
	public final static int FEET_MOTOR_STOP = 0x0910;
	public final static int HEAD_MOTOR_LEFT = 0x0911;
	public final static int HEAD_MOTOR_RIGHT = 0x0912;
	public final static int HEAD_MOTOR_FORWARD = 0x0913;
	public final static int HEAD_MOTOR_BACK = 0x0914;
	public final static int HEAD_MOTOR_STOP = 0x0915;

	public static class SMsgAVIoctrlCallReq {

		/*
		unsigned char myID[6];     // my ID
		unsigned char myUID[20];   // my UID
		unsigned char callType;    // 0,oneway; 1, twoway
		unsigned char beInvited;   // 0 means to invite and 1 means be invited
		unsigned char infoCount;   // other account count
		unsigned char reserved[3];
		unsigned AccountInfo info  // other account info in this meeting
		*/

		public static byte[] parseContent(String id, String uid, int callType, int invited, ArrayList<StructAccountInfo> otherUID) {

			Struct struct = new Struct();
			struct.myID = id;
			struct.myUID = uid;
			struct.callType = callType;
			struct.otherUID = otherUID;

			String json = JSON.toJSONString(struct);
//			byte[] result = null;
//
//			if (otherUID == null || otherUID.size() == 0) {
//				result = new byte[32];
//			} else {
//				result = new byte[32 + otherUID.size() * 28];
//			}
//
//			byte[] myID = id.getBytes();
//			System.arraycopy(myID, 0, result, 0, myID.length);
//
//			byte[] myUID = uid.getBytes();
//			System.arraycopy(myUID, 0, result, 6, myUID.length);
//
//			result[26] = (byte) callType;
//			result[27] = (byte) invited;
//
//			if (otherUID != null) {
//
//				result[28] = (byte) otherUID.size();
//
//				for (int i = 0; i < otherUID.size(); i++) {
//					byte[] uidBytes = otherUID.get(i).UID.getBytes();
//
//					System.arraycopy(uidBytes, 0, result, 32 + i * 28, uidBytes.length);
//
//					byte[] idBytes = otherUID.get(i).ID.getBytes();
//					System.arraycopy(idBytes, 0, result, 52 + i * 28, idBytes.length);
//				}
//			}

			return json.getBytes();
		}

		public static class Struct {
			public int    callType;
			public int    invited;
			public String myID;
			public String myUID;
			public ArrayList<StructAccountInfo> otherUID = new ArrayList<>();
		}

		public static Struct parseToStruct(byte[] result) {
			String json = new String(result);
			Log.e("--","parseToStruct:" + json);
			Struct struct = JSON.parseObject(json,Struct.class);
//			Struct struct = new Struct();
//
//			byte[] myIds = new byte[6];
//			System.arraycopy(result, 0, myIds, 0, myIds.length);
//			struct.myID = new String(myIds);
//
//			byte[] myUID = new byte[20];
//			System.arraycopy(result, 6, myUID, 0, myUID.length);
//			struct.myUID = new String(myUID);
//
//			struct.callType = result[26];
//			struct.invited = result[27];
//			int infoCount = result[28];
//			if (infoCount > 0) {
//				//infoCount = (result.length - 8) / 28
//				StructAccountInfo info;
//				for (int i = 0; i < infoCount; i++) {
//					info = new StructAccountInfo();
//					info.UID = new String(result, 32 + i * 28, 20);
//					info.ID = new String(result, 52 + i * 28, 6);
//					struct.otherUID.add(info);
//				}
//			}

			return struct;
		}
	}

	public static class SMsgAVIoctrlCallResp {
		/*
		unsigned char answer;       // 0, reject; 1 answer
		unsigned char myID[6];		//myID
		unsigned char reserved[1];
		*/

		public static class Struct {
			public int    answer;
			public String myID;
		}

		public static Struct parseToStruct(byte[] result) {
			String json = new String(result);
			Struct struct = JSON.parseObject(json,Struct.class);
//			Struct struct = new Struct();
//			struct.answer = result[0];
//			struct.myID = new String(result, 1, 6);
			return struct;
		}

		public static byte[] parseContent(int answer, String id) {
			Struct struct = new Struct();
			struct.answer = answer;
			struct.myID = id;
			String json = JSON.toJSONString(struct);
//			byte[] result = new byte[8];
//			result[0] = (byte) answer;
//			byte[] idBytes = id.getBytes();
//			System.arraycopy(idBytes, 0, result, 1, idBytes.length);
			return json.getBytes();
		}
	}

	public static class StructAccountInfo implements Parcelable {

		/*
		typedef struct{
			unsigned char UID[20];            //UID
			unsigned char myID[6];           //ID
			unsigned char reserved[2];
		}AccountInfo
		*/

		public String UID;
		public String ID;

		public StructAccountInfo() {

		}

		protected StructAccountInfo(Parcel in) {
			UID = in.readString();
			ID = in.readString();
		}

		public static final Creator<StructAccountInfo> CREATOR = new Creator<StructAccountInfo>() {
			@Override
			public StructAccountInfo createFromParcel(Parcel in) {
				return new StructAccountInfo(in);
			}

			@Override
			public StructAccountInfo[] newArray(int size) {
				return new StructAccountInfo[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(UID);
			dest.writeString(ID);
		}
	}

	public static class SMsgAVIoctrlCallEnd {

		public static class Struct {
			public String myID;
		}

		public static Struct parseToStruct(byte[] result) {
//			Struct struct = new Struct();
//			struct.myID = new String(result, 0, 6);
			String json = new String(result);
			Struct struct = JSON.parseObject(json,Struct.class);
			return struct;
		}

		public static byte[] parseContent(String id) {
//			byte[] result = new byte[8];
//			byte[] idBytes = id.getBytes();
//			System.arraycopy(idBytes, 0, result, 0, idBytes.length);
			Struct struct = new Struct();
			struct.myID = id;
			String json = JSON.toJSONString(struct);
			return json.getBytes();
		}
	}


}
