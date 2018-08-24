package cn.nineox.robot.appstrore.logic;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.nineox.robot.appstrore.common.Const;
import cn.nineox.robot.appstrore.databinding.ActivityMainBinding;
import cn.nineox.robot.appstrore.logic.bean.AppBean;
import cn.nineox.robot.appstrore.logic.bean.AppList;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;

/**
 * Created by me on 17/11/18.
 */

public class MainLogic extends BaseLogic<ActivityMainBinding>{

    private List<AppBean> mDatas = new ArrayList<>();

    private AppList mAppList;

    private PageAdapter mPagerAdapter;


    public MainLogic(Activity activity, ActivityMainBinding binding) {
        super(activity, binding);
    }


}
