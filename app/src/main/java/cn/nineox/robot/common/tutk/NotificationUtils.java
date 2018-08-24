package cn.nineox.robot.common.tutk;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

import cn.nineox.robot.PluginAPP;
import cn.nineox.robot.utils.WindowUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author zed
 * @date 2018/5/15 下午5:46
 * @desc 功能:延时消失notification
 * 1 当已经点击notification时候 需要取消延时任务
 * 2 当没有点击notification时候
 * .	1)对方在线 等待至超时时间 取消notification 发广播(添加历史记录／断线)
 * .	2)对方挂断／掉线 立马取消notification 发广播(添加历史记录／断线)
 */

public class NotificationUtils {

    private static CustomScheduledExecutor mPoolExecutor;

    private static void init() {
        if (mPoolExecutor == null) {
            synchronized (NotificationUtils.class) {
                if (mPoolExecutor == null) {
                    mPoolExecutor = new CustomScheduledExecutor(5, new ThreadFactory() {
                        @Override
                        public Thread newThread(@NonNull Runnable r) {
                            //设置为守护线程 生命周期同步app
                            Thread t = new Thread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });
                }
            }
        }
    }

    public static void stopScheduleTask(int request, boolean isRunTaskByNow) {
        init();

        if (isRunTaskByNow) {
            //让任务立即运行
            mPoolExecutor.runTaskByNow(request);
        } else {
            //取消任务
            mPoolExecutor.cancelTask(request);
        }
    }

    public static void startScheduleTask(final Intent intent, int request, int delayTime) {
        init();
        mPoolExecutor.executeCustomScheduledRunnable(new AbstractScheduledRunnable(delayTime, request) {

            @Override
            public void runMethod() {

                PluginAPP application = WindowUtils.getMediaSDKApplication();

                if (application == null) {
                    return;
                }

                NotificationManager manager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(getTag());

                if (intent != null) {
                    application.sendBroadcast(intent);
                }
            }
        });
    }

}
