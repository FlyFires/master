package cn.nineox.robot.logic.persistent;

import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;

import cn.nineox.robot.PluginAPP;
import cn.nineox.robot.logic.bean.LoginInfoBean;
import cn.nineox.robot.logic.bean.UserBean;

/**
 * Created by me on 17/11/1.
 */

public class APPDataPersistent {

    public static APPDataPersistent mInstance = new APPDataPersistent();

    public final String KEY_LOGIN_USER = "login_user";


    private UserBean mUserBean;


    private LoginInfoBean mLoginInfoBean;


    public static APPDataPersistent getInstance() {
        return mInstance;
    }

    public UserBean getUserBean() {
        if(mUserBean == null){
            mUserBean = new UserBean();
        }
        return mUserBean;
    }

    public void setUserBean(UserBean mUserBean) {
        this.mUserBean = mUserBean;
    }

    public LoginInfoBean getLoginInfoBean() {
        if(mLoginInfoBean == null){
            String json = PreferenceManager.getDefaultSharedPreferences(PluginAPP.getInstance())
                    .getString(KEY_LOGIN_USER,"");
            try {
                mLoginInfoBean = JSON.parseObject(json,LoginInfoBean.class);
            }catch (Exception e){
            }
            if(mLoginInfoBean == null){
                mLoginInfoBean = new LoginInfoBean();
            }

        }
        return mLoginInfoBean;
    }

    public void setLoginInfoBean(LoginInfoBean mLoginInfoBean) {
        this.mLoginInfoBean = mLoginInfoBean;
        String json = JSON.toJSONString(mLoginInfoBean);
        PreferenceManager.getDefaultSharedPreferences(PluginAPP.getInstance()).edit()
                .putString(KEY_LOGIN_USER,json).commit();
    }

    public void saveLoginUserBean(){
        if(mLoginInfoBean != null){
            setLoginInfoBean(mLoginInfoBean);
        }
    }

    public void logout(){
        this.mLoginInfoBean = null;
        this.mUserBean = null;
        PreferenceManager.getDefaultSharedPreferences(PluginAPP.getInstance()).edit()
                .putString(KEY_LOGIN_USER,"").commit();
    }
}
