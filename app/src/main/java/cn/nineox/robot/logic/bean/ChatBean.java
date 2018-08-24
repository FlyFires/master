package cn.nineox.robot.logic.bean;

import cn.nineox.robot.R;
import cn.nineox.xframework.base.BaseBean;
import cn.nineox.xframework.base.adapter.databinding.recyclerview.inf.IBaseTypeInterface;

/**
 * Created by Eval on 2017/11/16.
 */
public class ChatBean extends BaseBean implements IBaseTypeInterface{

    private String name;

    private String content;

    private long posttime;

    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPosttime() {
        return posttime;
    }

    public void setPosttime(long posttime) {
        this.posttime = posttime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemLayoutId() {
        return type == 0 ? R.layout.item_chat_0 : R.layout.item_chat_1;
    }
}
