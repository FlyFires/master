package cn.nineox.robot.logic;

import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.FragmentMeBinding;
import cn.nineox.robot.logic.bean.UserBean;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;

/**
 * Created by Eval on 2017/11/3.
 */
public class MeLogic extends BasicLogic<FragmentMeBinding>{
    public MeLogic(FragmentMeBinding dataBinding) {
        super(dataBinding);
    }


    public void userGet(){
        final EntityRequest request = new EntityRequest(Const.URL_USER_GET,UserBean.class);
        execute(request,new ResponseListener<UserBean>(){
            @Override
            public void onSucceed(int what, Result<UserBean> result) {
                super.onSucceed(what, result);
                UserBean userBean = result.getResult();
                System.out.println("user:"+userBean);
                APPDataPersistent.getInstance().setUserBean(userBean);
                mDataBinding.setUserBean(userBean);
            }
        });
    }



}
