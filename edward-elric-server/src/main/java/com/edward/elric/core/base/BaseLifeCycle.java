package com.edward.elric.core.base;


import com.edward.elric.core.LifeCycleListener;

/**
 * @author impactCn
 */
public abstract class BaseLifeCycle {


    public void init() throws Exception{}

    public void start() throws Exception{}

    public void stop() throws Exception{}

    public String info() {
        return null;
    }


    /**
     * 监听器
     */
    LifeCycleListener lifecycleListener;


    public void setLifecycleListener(LifeCycleListener lifecycleListener) {
        this.lifecycleListener = lifecycleListener;
    }
}
