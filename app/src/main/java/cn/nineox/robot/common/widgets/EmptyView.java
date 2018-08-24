package cn.nineox.robot.common.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.yanzhenjie.nohttp.tools.NetUtils;

import cn.nineox.robot.R;

/**
 * Created by me on 18/5/12.
 */

public class EmptyView extends LinearLayout{
    public EmptyView(Context context) {
        super(context);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.empty_view,this);
    }


    public void set(int resId,String tips,OnClickListener listener){
        setIcon(resId);
        setTips(tips);
        setRefeshListener(listener);
    }

    public void error(OnClickListener listener){
        if(NetUtils.isNetworkAvailable()){
            set(R.drawable.icon_loading_error,"数据加载失败",listener);
        }else{
            set(R.drawable.icon_no_network,"目前没有网络,请检查网络设置",listener);
        }
    }

    public void success(RecyclerView.Adapter adapter,OnClickListener listener){
        if(adapter.getItemCount() <= 0){
            this.setVisibility(View.VISIBLE);
            set(R.drawable.icon_no_data,"暂无数据",listener);
        }else{
            this.setVisibility(View.GONE);
        }
    }

    public void setIcon(int resId){
        ((ImageView)findViewById(R.id.icon)).setImageResource(resId);
    }


    public void setTips(String tips){
        ((TextView)findViewById(R.id.tips)).setText(tips);
    }

    public void setRefeshListener(OnClickListener listener){
        if(listener == null){
            findViewById(R.id.refresh).setVisibility(GONE);
        }else{
            findViewById(R.id.refresh).setVisibility(View.VISIBLE);
            findViewById(R.id.refresh).setOnClickListener(listener);
        }

    }

}
