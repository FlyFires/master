package cn.nineox.robot.monitor;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import cn.nineox.robot.monitor.logic.MonitorPreviewLogic;
import cn.nineox.robot.monitor.logic.WaitCallMonitorViewLogic;
import cn.nineox.xframework.core.android.log.Log;


/**
 * Created by Administor on 2018/5/28.
 */

public class WaitCallMonitorView extends RelativeLayout implements View.OnClickListener {

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Context mContext;

    private int mScreenWidth;
    private int mScreenHeight;

    public WaitCallMonitorViewLogic sLogic;

    private Bundle bundle;

    public WaitCallMonitorView(Context context,Bundle bundle) {
        super(context);
        this.bundle = bundle;
        init(context);
    }

    public WaitCallMonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaitCallMonitorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //inflate(context, R.layout.dialog_gift_detail,this);
        sLogic = new WaitCallMonitorViewLogic(this,bundle);
        this.mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        //mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        //mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置
        mWmParams.gravity = Gravity.TOP | Gravity.LEFT;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
 /*       mWmParams.x = 0;
        mWmParams.y = mScreenHeight / 2;
*/

        // 设置悬浮窗口长宽数据
        mWmParams.width = 1;
        mWmParams.height = 1;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);
        sLogic.initData();
    }

    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootFloatView = inflater.inflate(R.layout.activity_wait_call, null);
        return rootFloatView;
    }


    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
    }

    public boolean isShow(){
        return getVisibility()  == View.VISIBLE;
    }

    public void hide() {
        setVisibility(View.GONE);
    }


    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void destroy() {
        android.util.Log.e("--","WaitCallMonitorView destory");
        this.post(new Runnable() {
            @Override
            public void run() {
                sLogic.unbind();
                hide();
                removeFloatView();
            }
        });

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();


    }

    @Override
    public void onClick(View v) {
    }
}
