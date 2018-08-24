package cn.nineox.robot.monitor.logic.bean;

import cn.nineox.xframework.base.BaseBean;

public class CallerUserType extends BaseBean {
        public String key;

        public String desc;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }