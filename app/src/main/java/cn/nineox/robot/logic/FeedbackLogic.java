package cn.nineox.robot.logic;

import android.app.Activity;
import android.text.TextUtils;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import cn.nineox.robot.common.Const;
import cn.nineox.robot.databinding.ActivityFeedbackBinding;
import cn.nineox.robot.logic.persistent.APPDataPersistent;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;

/**
 * Created by me on 17/11/5.
 */

public class FeedbackLogic extends BasicLogic<ActivityFeedbackBinding>{


    public FeedbackLogic(ActivityFeedbackBinding dataBinding) {
        super(dataBinding);
    }

    public void feedback(final Activity activity){
        String content = mDataBinding.feedbackEd.getText().toString();
        if(TextUtils.isEmpty(content)){
            new Toastor(activity).showToast("内容不能为空");
            return;
        }

        final QMUITipDialog dialog = new QMUITipDialog.Builder(activity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("提交中..").create();
        dialog.show();
        final StringReqeust request = new StringReqeust(Const.URL_FEEDBACK_SAVE);
        request.add("mobile", APPDataPersistent.getInstance().getUserBean().getMobile());
        request.add("content", content);
        execute(request,new ResponseListener<String>(){
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                dialog.dismiss();
                new Toastor(activity).showToast("谢谢你的反馈");
                activity.finish();
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
                new Toastor(activity).showToast("提交失败，"+ error);
            }
        });
    }
}
