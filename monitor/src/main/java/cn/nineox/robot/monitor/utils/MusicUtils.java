package cn.nineox.robot.monitor.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.monitor.logic.bean.Song;
import cn.nineox.xframework.core.android.log.Log;

/**
 * Created by me on 17/12/26.
 */

public class MusicUtils {


    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song Song = new Song();
                Song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                Log.e("song", Song.song);
                Song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                Song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (Song.size > 1000 * 800) {
                    // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
                    if (Song.song.contains("-")) {
                        String[] str = Song.song.split("-");
                        Song.singer = str[0];
                        Song.song = str[1];
                    }
                    list.add(Song);
                }
            }
            // 释放资源
            cursor.close();
        }

        return list;
    }
}
