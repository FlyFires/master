package cn.nineox.robot.logic;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.finalteam.toolsfinal.coder.MD5Coder;
import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityPersonalBinding;
import cn.nineox.robot.logic.bean.UserChangeEvent;
import cn.nineox.robot.logic.bean.UserType;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.robot.utils.ImageFactory;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;

/**
 * Created by me on 17/11/4.
 */

public class PersonalLogic extends BasicLogic<ActivityPersonalBinding>{

    public PersonalLogic(ActivityPersonalBinding dataBinding) {
        super(dataBinding);
    }

    public void userUpdate(final Activity activity, final String name, final UserType userType){
        final EntityRequest request = new EntityRequest(Const.URL_USER_UPDATE,Boolean.class);
        request.add("name",name);
        request.add("mobile",APPDataPersistent.getInstance().getLoginInfoBean().getMobile());
        if(userType != null){
            request.add("userType",userType.getKey());
        }
        execute(request,new ResponseListener<Boolean>(){
            @Override
            public void onSucceed(int what, Result<Boolean> result) {
                super.onSucceed(what, result);
                if(result.getResult()){
                    new Toastor(activity).showToast("修改成功.");
                    APPDataPersistent.getInstance().getLoginInfoBean().setName(name);
                    APPDataPersistent.getInstance().saveLoginUserBean();
                    mDataBinding.nicknameTx.setText(name);
                    if(userType != null){
                        mDataBinding.shenfen.setText(userType.getDesc());
                    }
                    UserChangeEvent event = new UserChangeEvent();
                    event.setName(name);
                    EventBus.getDefault().post(event);
                }

            }

            @Override
            public void onFailed(int what,String error) {
                super.onFailed(what,error);
                new Toastor(activity).showToast("修改失败:"+error);
            }
        });

    }



    /**
     * 获取cache路径
     *
     * @param context
     * @return
     */
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    public void updateHeadPic(final Activity activity, final String path){
        File file = new File(path);
        String taget = getDiskCachePath(activity) + MD5Coder.getMD5Code(file.getPath())  + ".JPG";
        ImageFactory.compressImage(file.getAbsolutePath(),taget, 50);

        final StringReqeust request = new StringReqeust(Const.URL_UPLOADHEADPIC);
        request.add("file",new File(taget));
        execute(request,new ResponseListener<String>(){
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                new Toastor(activity).showToast("修改成功.");
                if(TextUtils.isEmpty(result.getResult())){
                    return;
                }
                APPDataPersistent.getInstance().getLoginInfoBean().setHeadpic(result.getResult());
                APPDataPersistent.getInstance().saveLoginUserBean();
                UserChangeEvent event = new UserChangeEvent();
                event.setHeadPic(result.getResult());
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailed(int what,String error) {
                super.onFailed(what,error);
                new Toastor(activity).showToast("修改失败:"+error);
            }
        });

    }


    public void logout(){
        StringReqeust request = new StringReqeust(Const.URL_LOGOUT);
        execute(request,new ResponseListener<String>(){
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
            }
        });

        APPDataPersistent.getInstance().logout();
    }


}
