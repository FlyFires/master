package cn.nineox.robot.ui.fragment;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.nineox.robot.R;
import cn.nineox.robot.common.basic.BasicFragment;
import cn.nineox.robot.databinding.FragmentBaobaoRzBinding;
import cn.nineox.robot.databinding.ItemBaobaoRzBinding;
import cn.nineox.robot.logic.bean.UsageBean;
import cn.nineox.xframework.base.BaseFragment;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingAdapter;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.BaseBindingVH;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.utils.AppUtil;
import cn.nineox.xframework.core.common.utils.PackageUtil;

/**
 * Created by me on 18/1/23.
 */

public class BaobaoRzFragment extends BasicFragment<FragmentBaobaoRzBinding> {

    private BaseBindingAdapter mAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_baobao_rz;
    }

    @Override
    protected void createViewBinding() {
        int position = getArguments().getInt("position",0);
        List<UsageBean> useages = getUsageStats(position);
        mViewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mAdapter = new BaseBindingAdapter<UsageBean,ItemBaobaoRzBinding>(_mActivity,useages,R.layout.item_baobao_rz){
            @Override
            public void onBindViewHolder(BaseBindingVH<ItemBaobaoRzBinding> holder, int position) {
                super.onBindViewHolder(holder, position);
                UsageBean bean = getItem(position);
                Drawable drawable = bean.getIcon();
                holder.getBinding().icon.setImageDrawable(drawable);
            }
        };
        mViewDataBinding.recyclerView.setAdapter(mAdapter);

    }


    private List<UsageBean> getUsageStats(int position) {
        Log.e("--","getUsageStats:" + position);
        UsageStatsManager usm = (UsageStatsManager) this.getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();

        long endTime = 0;
        long startTime = 0;

        int interval = UsageStatsManager.INTERVAL_DAILY;
        switch (position){
            case 0:
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DATE, -1);//
                startTime = calendar.getTimeInMillis();
                interval = UsageStatsManager.INTERVAL_DAILY;
                break;
            case 1:
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_WEEK, -1);//
                startTime = calendar.getTimeInMillis();
                interval = UsageStatsManager.INTERVAL_WEEKLY;
                break;
            case 2:
                endTime = calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_MONTH, -1);//
                startTime = calendar.getTimeInMillis();
                interval = UsageStatsManager.INTERVAL_MONTHLY;
                break;
        }
        /**
         * 最近两周启动过所用app的List
         * queryUsageStats第一个参数是根据后面的参数获取合适数据的来源，有按天，按星期，按月，按年等。
         *  UsageStatsManager.INTERVAL_BEST
         *   UsageStatsManager.INTERVAL_DAILY 按天
         *   UsageStatsManager.INTERVAL_WEEKLY 按星期
         *   UsageStatsManager.INTERVAL_MONTHLY 按月
         *   UsageStatsManager.INTERVAL_YEARLY 按年
         */

        List<UsageStats> list = null;
        List<UsageBean> usageBeanList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list = usm.queryUsageStats(interval, startTime, endTime);
            //需要注意的是5.1以上，如果不打开此设置，queryUsageStats获取到的是size为0的list；
            if (list.size() == 0) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    } catch (Exception e) {
                        Toast.makeText(_mActivity, "无法开启允许查看使用情况的应用界面", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("--","getUsageStats" + list.size());
                for (UsageStats usageStats : list) {
                    UsageBean bean = new UsageBean();
                    PackageInfo packageInfo = PackageUtil.getPackageInfo(_mActivity,usageStats.getPackageName());

                    bean.setAppName(packageInfo.applicationInfo.loadLabel(_mActivity.getPackageManager()).toString());
                    bean.setPackageName(usageStats.getPackageName());
                    bean.setTotalTime(usageStats.getTotalTimeInForeground());
                    bean.setIcon(packageInfo.applicationInfo.loadIcon(_mActivity.getPackageManager()));
                    usageStats.getFirstTimeStamp();//获取第一次运行的时间
                    usageStats.getLastTimeStamp();//获取最后一次运行的时间
                    //获取总共运行的时间
                    try {
                        Field field = usageStats.getClass().getDeclaredField("mLaunchCount");//获取应用启动次数，UsageStats未提供方法来获取，只能通过反射来拿到
                        bean.setLaunchCount(field.getInt(usageStats));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    usageBeanList.add(bean);
                }
            }
        }
        return usageBeanList;


    }
}
