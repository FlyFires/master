package cn.nineox.robot.monitor.logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;

import cn.finalteam.toolsfinal.coder.MD5Coder;
import cn.nineox.robot.monitor.R;
import cn.nineox.robot.monitor.common.Const;
import cn.nineox.robot.monitor.databinding.FragmentAddBinding;
import cn.nineox.robot.monitor.utils.SharePrefUtil;
import cn.nineox.xframework.base.BaseLogic;
import cn.nineox.xframework.core.android.log.Log;
import cn.nineox.xframework.core.common.assist.Toastor;
import cn.nineox.xframework.core.common.http.ResponseListener;
import cn.nineox.xframework.core.common.http.Result;
import cn.nineox.xframework.core.common.http.StringReqeust;
import cn.nineox.xframework.core.common.utils.AndroidUtil;
import cn.nineox.xframework.core.common.utils.BitmapUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by me on 18/4/25.
 */

public class AddLogic extends BaseLogic<FragmentAddBinding> {

    private Bitmap mBitmap;
    QMUITipDialog dialog;

    public AddLogic(Fragment fragment, FragmentAddBinding binding) {
        super(fragment, binding);

    }

    public void getApkUrl() {
        dialog = new QMUITipDialog.Builder(mActivity)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("请稍后").create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        StringReqeust request = new StringReqeust(Const.GET_APK);
        request.add("mid", AndroidUtil.getAndroidId(mActivity));
        execute(request, new ResponseListener<String>() {
            @Override
            public void onSucceed(int what, Result<String> result) {
                super.onSucceed(what, result);
                if (!TextUtils.isEmpty(result.getResult())) {
                    mergeBitmap(result.getResult());
                }
            }

            @Override
            public void onFailed(int what, String error) {
                super.onFailed(what, error);
                dialog.dismiss();
                new Toastor(mActivity).showSingletonToast(error);
                ((SupportFragment) mFragment).pop();
            }
        });
    }


    private void mergeBitmap(final String apkUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String mid = AndroidUtil.getAndroidId(mActivity.getApplicationContext());
                final String filename = new String(MD5Coder.getMD5Code(apkUrl)) + "-" + mid + ".jpg";
                String path = mActivity.getExternalCacheDir().getAbsolutePath() + "/" + filename;
                Log.e("--","path:"  +path + "  " + new File(path).exists());
                if(new File(path).exists()){//
                   Bitmap bitmap =  BitmapFactory.decodeFile(path);
                    mBitmap = bitmap;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDataBinding.helpIv.setImageBitmap(mBitmap);
                            if(mActivity != null && !mActivity.isFinishing() && dialog != null){
                                try{
                                    dialog.dismiss();
                                }catch (Exception e){

                                }

                            }

                        }
                    });
                }else{
                    Bitmap bg = BitmapUtil.drawableToBitmap(mActivity.getResources().getDrawable(R.mipmap.bg_help));
                    float scan = bg.getWidth() / 1024f;

                    final Bitmap midBitmap = CodeUtils.createImage(mid, (int) (150 * scan), (int) (150 * scan), BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_launcher));
                    //Bitmap downloadBitmap = BitmapUtil.drawableToBitmap(mActivity.getResources().getDrawable(R.mipmap.ic_launcher));
                    Bitmap downloadBitmap = CodeUtils.createImage(apkUrl, (int) (150 * scan), (int) (150 * scan), BitmapFactory.decodeResource(mActivity.getResources(), R.mipmap.ic_launcher));
                    Bitmap bitmap = Bitmap.createBitmap(bg.getWidth(), bg.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(bg, new Matrix(), null);
                    canvas.drawBitmap(downloadBitmap, 52 * scan, 170 * scan, null);
                    canvas.drawBitmap(midBitmap, 815 * scan, 170 * scan, null);
                    mBitmap = bitmap;
                    BitmapUtil.saveBitmap(mBitmap, path);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDataBinding.helpIv.setImageBitmap(mBitmap);
                            dialog.dismiss();
                        }
                    });
                }

            }
        }).start();

    }

}
