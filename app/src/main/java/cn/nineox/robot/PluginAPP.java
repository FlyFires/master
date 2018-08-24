package cn.nineox.robot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.yanzhenjie.nohttp.NoHttp;

import java.util.Stack;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.jpush.android.api.JPushInterface;
import cn.nineox.robot.common.App;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.utils.DynamicTimeFormat;
import cn.nineox.robot.utils.GlideUtils;
import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;
import me.yokeyword.fragmentation_components.BuildConfig;

/**
 * Created by me on 17/9/27.
 */

public class PluginAPP extends Application {

    private static PluginAPP mInstance;

    private int mFinalCount;

    private APPDataPersistent mPersistent;
    private final Stack<Activity> activityStack = new Stack<>();



    public static PluginAPP getInstance() {
        if (null == mInstance) {
            mInstance = new PluginAPP();
        }
        return mInstance;
    }

    public PluginAPP() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NoHttp.initialize(this);
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        ZXingLibrary.initDisplayOpinion(this);
        Fragmentation.builder()
                // 设置 栈视图 模式为 悬浮球模式   SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.NONE)
                // ture时，遇到异常："Can not perform this action after onSaveInstanceState!"时，会抛出
                // false时，不会抛出，会捕获，可以在handleException()里监听到
                .debug(BuildConfig.DEBUG)
                // 在debug=false时，即线上环境时，上述异常会被捕获并回调ExceptionHandler
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 建议在该回调处上传至我们的Crash监测服务器
                        // 以Bugtags为例子: 手动把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                    }
                })
                .install();

        MobSDK.init(this, "22254f97a2caa", "1b16e693de16990e490e3a758223c93f");

        initGalleryFinal();

        registerP2PCallback();

    }

    private int resumed;
    private int paused;
    private int started;
    private int stopped;
    private ActivityLifecycleCallbacks mLifecycleCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activityStack.add(0, activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            ++started;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            ++resumed;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            ++paused;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            ++stopped;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activityStack.remove(activity);
        }

    };

    private void registerP2PCallback() {


        registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    //依次退出所有页面
    public void finishAll() {
        for (Activity activity : activityStack) {
            activity.finish();
        }
    }

    //拿到app当前栈顶页面
    public Activity getTopActivity() {
        if (activityStack.size() > 0) {
            return activityStack.firstElement();
        }
        return null;
    }


    private void initGalleryFinal() {
        //设置主题
        //ThemeConfig.CYAN
        ThemeConfig theme = ThemeConfig.DEFAULT;
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder().setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        //配置imageloader
        cn.finalteam.galleryfinal.ImageLoader imageloader = new GlideUtils();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme).setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
    }


    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }


}
