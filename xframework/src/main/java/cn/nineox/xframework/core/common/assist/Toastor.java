package cn.nineox.xframework.core.common.assist;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author MaTianyu
 * @date 2014-07-31
 */
public class Toastor {

    private Toast mToast;
    private Context context;

    private Handler mHandle = new Handler(Looper.getMainLooper());

    public Toastor(Context context) {
        this.context = context.getApplicationContext();
    }

    public Toast getSingletonToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        return mToast;
    }

    public Toast getSingletonToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    public Toast getSingleLongToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        } else {
            mToast.setText(resId);
        }
        return mToast;
    }

    public Toast getSingleLongToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    public Toast getToast(int resId) {
        return Toast.makeText(context, resId, Toast.LENGTH_SHORT);
    }

    public Toast getToast(String text) {
        return Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }

    public Toast getLongToast(int resId) {
        return Toast.makeText(context, resId, Toast.LENGTH_LONG);
    }

    public Toast getLongToast(String text) {
        return Toast.makeText(context, text, Toast.LENGTH_LONG);
    }

    public void showSingletonToast(final int resId) {
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                getSingletonToast(resId).show();
            }
        });

    }


    public void showSingletonToast(final String text) {
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                if (text.contains("token无效") || text.contains("无效的token") || text.contains("对方已退出") || text.contains("用户退出")) {
                    return;
                }
                getSingletonToast(text).show();
            }
        });

    }

    public void showSingleLongToast(final int resId) {
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                getSingleLongToast(resId).show();
            }
        });

    }


    public void showSingleLongToast(String text) {
        getSingleLongToast(text).show();
    }

    public void showToast(int resId) {
        getToast(resId).show();
    }

    public void showToast(String text) {
        getToast(text).show();
    }

    public void showLongToast(int resId) {
        getLongToast(resId).show();
    }

    public void showLongToast(String text) {
        getLongToast(text).show();
    }

}
