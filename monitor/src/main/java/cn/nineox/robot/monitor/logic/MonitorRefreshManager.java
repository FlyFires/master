package cn.nineox.robot.monitor.logic;

import java.util.ArrayList;
import java.util.List;

public class MonitorRefreshManager {
    public static List<RefreshEventListener> monitorListeners = new ArrayList<>();



    public static void addRefreshListener(RefreshEventListener listener){
        synchronized (monitorListeners){
            if(!monitorListeners.contains(listener)){
                monitorListeners.add(listener);
            }
        }
    }


    public static void removeRefreshListener(RefreshEventListener listener){
        synchronized (monitorListeners){
            if(monitorListeners.contains(listener)){
                monitorListeners.remove(listener);
            }
        }
    }

    public static void cleanRefreshListener(){
        synchronized (monitorListeners){
            monitorListeners.clear();
        }
    }



    public static void notifyRefreshListener(){
        synchronized (monitorListeners){
            for(RefreshEventListener listener : monitorListeners){
                listener.onRefreshed();
            }
        }
    }
    public interface RefreshEventListener{

        void onRefreshed();
    }

    public static boolean isMonitor(){
            return monitorListeners.size() > 0;
    }
}
