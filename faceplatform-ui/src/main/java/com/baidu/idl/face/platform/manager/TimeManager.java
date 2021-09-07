package com.baidu.idl.face.platform.manager;

/**
 * Author: xuan
 * Created on 2021/9/7 09:35.
 * <p>
 * Describe:
 */
public class TimeManager {
    private static TimeManager instance = null;
    private int mActiveAnimTime;

    private TimeManager() {
    }

    public static TimeManager getInstance() {
        if (instance == null) {
            Class var0 = TimeManager.class;
            synchronized(TimeManager.class) {
                if (instance == null) {
                    instance = new TimeManager();
                }
            }
        }

        return instance;
    }

    public void setActiveAnimTime(int activeAnimTime) {
        this.mActiveAnimTime = activeAnimTime;
    }

    public int getActiveAnimTime() {
        return this.mActiveAnimTime;
    }
}
