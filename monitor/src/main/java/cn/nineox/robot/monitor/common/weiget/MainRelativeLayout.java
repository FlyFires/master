package cn.nineox.robot.monitor.common.weiget;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import cn.nineox.xframework.core.android.log.Log;

/**
 * Created by me on 17/12/26.
 */

public class MainRelativeLayout extends RelativeLayout {


    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener;

    private AudioManager mAudioManager;
    private int mMaxVolume;//最大声音
    private int mShowVolume;//声音

    private MoveListener listener;


    public MainRelativeLayout(Context context) {
        super(context);
        addTouchListener();
    }

    public MainRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTouchListener();
    }

    public MainRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTouchListener();
    }


    /**
     * 添加手势操作
     */
    private void addTouchListener() {
        Log.e("--","addTouchListener");
        mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            //滑动操作
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {

                float x1 = e1.getX();
                float y1 = e1.getY();
                float x2 = e2.getX();
                float y2 = e2.getY();
                float absX = Math.abs(x1 - x2);
                float absY = Math.abs(y1 - y2);
                Log.e("--", "onScroll-"+"x:"+x1+"y:"+y1);
                float absDistanceX = Math.abs(distanceX);// distanceX < 0 从左到右
                float absDistanceY = Math.abs(distanceY);// distanceY < 0 从上到下

                // Y方向的距离比X方向的大，即 上下 滑动
                if (absDistanceX < absDistanceY) {
                    if (distanceY > 0) {//向上滑动
                        // changeVolume(ADD_FLAG);

                        Log.e("--","absY:" + absY);
                        listener.onMoveUp();
                    } else {
                        listener.onMoveDown();
                    }

                }

                return false;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent arg0) {
                return false;
            }

            //双击事件，支持双击播放暂停，可从这实现
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

        };

        mGestureDetector = new GestureDetector(this.getContext(), mSimpleOnGestureListener);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            //手指抬起

        }

        return super.onTouchEvent(event);
    }





    public void setMoveListener(MoveListener listener) {
        this.listener = listener;
    }


    public interface MoveListener {

        void onMoveUp();

        void onMoveDown();

    }
}
