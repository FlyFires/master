package cn.nineox.robot.appstrore;

import android.app.Application;
import android.content.Context;

import com.liulishuo.filedownloader.FileDownloader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.yanzhenjie.nohttp.NoHttp;

import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.utils.DynamicTimeFormat;
import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;
import me.yokeyword.fragmentation_components.BuildConfig;

/**
 * Created by me on 17/9/27.
 */

public class PluginAPP extends Application {

    private static PluginAPP mInstance;

    private int mFinalCount;


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
        FileDownloader.setup(this);
        FileDownloader.setGlobalPost2UIInterval(400);
        Const.FILE_DOWNLOAD_DIR = this.getExternalCacheDir().getAbsolutePath();

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
