package cn.nineox.robot.monitor;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.logic.MonitorPreviewLogic;
import cn.nineox.robot.monitor.logic.MonitorRefreshManager;
import cn.nineox.xframework.core.android.log.Log;


/**
 * Created by Administor on 2018/5/28.
 */

public class MonitorView extends RelativeLayout implements View.OnClickListener, MonitorRefreshManager.RefreshEventListener {

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Context mContext;

    private int mScreenWidth;
    private int mScreenHeight;

    private MonitorPreviewLogic sLogic;


    public MonitorView(Context context) {
        super(context);
        init(context);
        this.getContext().sendBroadcast(new Intent(Const.ACTION_MONITOR_START));
    }

    public MonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MonitorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        MonitorRefreshManager.addRefreshListener(this);
        //inflate(context, R.layout.dialog_gift_detail,this);
        sLogic = new MonitorPreviewLogic(this);
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
        //mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        //mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
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
        View rootFloatView = inflater.inflate(R.layout.activity_robot_preview, null);
        return rootFloatView;
    }


    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
        }
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
        android.util.Log.e("--", "MonitorView destory");
        this.post(new Runnable() {
            @Override
            public void run() {
                MonitorView.this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                sLogic.onDestory();
                sLogic.unbind();
                hide();
                removeFloatView();

            }
        });
        this.getContext().sendBroadcast(new Intent(Const.ACTION_MONITOR_END));

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MonitorRefreshManager.cleanRefreshListener();
        android.util.Log.e("--","onDetachedFromWindow：" + MonitorRefreshManager.monitorListeners.size());
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onRefreshed() {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (App.sConnectList.size() > 0) {
                    sLogic.callOff();
                    destroy();
                }
            }
        });
    }
}
