package cn.nineox.robot.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by me on 17/11/25.
 */

public class SpeakerUtil {

    private static int currVolume = 0;
    /**
     * 打开扬声器
     */
    public static void openSpeaker(Context context) {
        try{
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭扬声器
     */
    public static void closeSpeaker(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
