package cn.nineox.robot.monitor;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

//import com.turing.iot.bean.Topic;
import com.yanzhenjie.nohttp.NoHttp;

import java.util.Stack;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.common.tutk.App;
import cn.nineox.robot.monitor.common.tutk.TUTKManager;
import cn.nineox.robot.monitor.utils.GlideUtils;
import cn.nineox.robot.monitor.utils.LogUtil;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

/**
 * Created by me on 17/12/22.
 */

public class APP extends Application /*implements MQTTInitListener */{

    public static APP instance;

    public static APP the() {
        if (null == instance) {
            instance = new APP();
        }
        return instance;
    }

    private String mUserid;

    public final Stack<Activity> activityStack = new Stack<>();

    private TUTKManager mTutkManager;

//    private Topic mTopic;

    private static String mMqttName = "tulingtest";
    private static String mPwdName = "3jJxWAUS";

    public APP() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NoHttp.initialize(this);
        Fragmentation.builder()
                // 设置 栈视图 模式为 悬浮球模式   SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.NONE)
                // ture时，遇到异常："Can not perform this action after onSaveInstanceState!"时，会抛出
                // false时，不会抛出，会捕获，可以在handleException()里监听到
                .debug(me.yokeyword.fragmentation_components.BuildConfig.DEBUG)
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

        initGalleryFinal();


        SharePrefUtil.init(this);

       // mUserid = SharePrefUtil.getString(Const.EXTAR_ANDROID_ID);
        //App.sSelfUID = "F1YU8154UH7WKH6GUHZJ";
        App.sSelfAccountID = AndroidUtil.getAndroidId(this);
        /*if (null == mUserid || "".equals(mUserid)) {
            mUserid = AndroidUtil.getAndroidId(this);
            SharePrefUtil.putString(Const.EXTAR_ANDROID_ID, mUserid);
        }*/
        mUserid = AndroidUtil.getAndroidId(this);
        Log.e("--", "userid:" + mUserid);
        registerP2PCallback();
        mTutkManager = TUTKManager.getIntance(this);
        mTutkManager.deviceLogin();

        //MQTTManager.getInstance().init(this, Const.TL_APP_KEY, this);

        //WriteDataUtils.init();

//        mSongs = MusicUtils.getMusicData(this);
//        mPlayer = new MediaPlayer();
//        startService(new Intent(this, MusicServer.class));
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

    public String getmUserid() {
        return mUserid;
    }

    private int resumed;
    private int paused;
    private int started;
    private int stopped;
    private ActivityLifecycleCallbacks mLifecycleCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activityStack.add(0, activity);
//            Log.e("--","onActivityCreated:" +activityStack.size()  + activityStack.contains("cn.nineox.robot.monitor.MainActivity")  +  activityStack);
//            if(activityStack.contains("cn.nineox.robot.monitor.MainActivity")){
//
//            }

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


    @Override
    public void onTerminate() {
        super.onTerminate();
        mTutkManager.unInitAll();
    }

//    @Override
//    public void onSccess(Topic topic) {
//        mTopic = topic;
//        MQTTManager.getInstance().connect(mTopic, mMqttName, mPwdName, new MQTTConnectListener() {
//
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailed(int i, String s) {
//                new Toastor(instance).showSingletonToast(s);
//            }
//        });
//
//    }
//
//    @Override
//    public void onFailed(int i, String s) {
//        new Toastor(this).showSingletonToast(s);
//    }
}
