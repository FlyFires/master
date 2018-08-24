package cn.nineox.robot.monitor.logic;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONObject;

import java.io.File;

import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.databinding.FragmentSetHeadBinding;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.EntityRequest;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 17/12/22.
 */

public class SetHeadLogic extends BaseLogic<FragmentSetHeadBinding>{

    public String uploadPic = "";


    public SetHeadLogic(Fragment fragment, FragmentSetHeadBinding binding) {
        super(fragment, binding);
    }


    public void uploadHead(String filePath){
        final QMUITipDialog dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在上传").create();
        dialog.show();
        final StringReqeust request = new StringReqeust(Const.UPLOAD_HEAD);
        request.add("file",new File(filePath));
        execute(request,new ResponseListener<String>(){

            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                uploadPic = result.getResult();
                Log.e("SetHeadLogic",uploadPic);
//                if(!TextUtils.isEmpty(res)){
//                    try{
//                        JSONObject jsonObject = new JSONObject(res);
//                        uploadPic = jsonObject.optString("data","");
//                        Log.e("SetHeadLogic",uploadPic);
                        mDataBinding.igorn.performClick();
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

//                }
            }



            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
                new Toastor(mActivity).showSingletonToast(error);
            }
        });

    }
}
