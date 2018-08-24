package cn.nineox.robot.monitor.common.weiget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.nineox.robot.monitor.R;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.utils.BitmapUtil;

/**
 * Created by me on 18/4/22.
 */

public class CustonImageView extends View{
    Bitmap mBitmap;
    public CustonImageView(Context context) {
        super(context);
        mergeBitmap(null,null);
    }

    public CustonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mergeBitmap(null,null);
    }

    public CustonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mergeBitmap(null,null);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect(0,0,this.getWidth(),this.getHeight());
        //canvas.drawBitmap(mBitmap,this.getResources().getDrawable(R.mipmap.bg_help).getBounds(),rect,null);
        canvas.drawBitmap(mBitmap,new Matrix(),null);
    }

    private Bitmap mergeBitmap(Bitmap secondBitmap,Bitmap thirdBitmap) {
        Bitmap bg = BitmapUtil.drawableToBitmap(this.getResources().getDrawable(R.mipmap.bg_help));
        secondBitmap = BitmapUtil.drawableToBitmap(this.getResources().getDrawable(R.mipmap.ic_launcher));
        thirdBitmap = secondBitmap;
        Log.e("--",bg.getWidth() + "--" + bg.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.ARGB_8888);
        float scan = bg.getWidth()/1024f;
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bg, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 83 * scan, 244 * scan, null);
        canvas.drawBitmap(thirdBitmap, 685 * scan, 468 * scan, null);
        mBitmap = bitmap;
        BitmapUtil.saveBitmap(mBitmap,"/sdcard/1.jpg");
        return bitmap;
    }

}
