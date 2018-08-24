package cn.nineox.robot.appstrore.common.weiget;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import cn.nineox.robot.appstrore.R;

public class TFTipsToast extends Toast {
    public TFTipsToast(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        ImageView iv = new ImageView(context);
        setView(iv);
        iv.setImageResource(R.mipmap.tf_tips);
        setView(iv);
        setGravity(Gravity.CENTER, 0, 0);
    }

}
