package cn.nineox.robot.monitor.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/7/27.
 * @author Administrator
 */

public class StepMonitorUtil {
        public static Object getService() {
            Class<?> threadClazz = null;
            try {
                threadClazz = Class.forName("android.os.ServiceManager");
                Method method = threadClazz.getMethod("getService", String.class);
                return method.invoke("null", "steering");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
}
