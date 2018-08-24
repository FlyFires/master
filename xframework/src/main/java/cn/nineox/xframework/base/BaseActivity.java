package cn.nineox.xframework.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import me.yokeyword.fragmentation.SupportActivity;


/**
 * Created by aiden on 2/18/16.
 */
public abstract class BaseActivity<DataBinding extends ViewDataBinding>  extends SupportActivity  implements View.OnClickListener{


    protected DataBinding mViewDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateView();
    }


    protected void onCreateView(){
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        createViewBinding();
    }



    @NonNull
    protected abstract void createViewBinding();

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void onClick(View v) {

    }
}
