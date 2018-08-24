package cn.nineox.robot.logic.bean;

/**
 * Created by me on 18/1/16.
 */

public class Menu {

    public String text;

    public int icon;


    public Menu(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
