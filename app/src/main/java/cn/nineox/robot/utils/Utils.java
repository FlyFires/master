package cn.nineox.robot.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;

import static android.R.attr.path;

/**
 * Created by me on 18/3/20.
 */

public class Utils {
    MediaScannerConnection mediaScannerConnection = null;

    public static void saveImageToGallery(Context context, File file) {

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
    }


}
