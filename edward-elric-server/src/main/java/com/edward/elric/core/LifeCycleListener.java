package com.edward.elric.core;


import com.edward.elric.core.base.BaseLifeCycle;

/**
 * @author impactCn
 */
public interface LifeCycleListener {

    void beforeInit(BaseLifeCycle server) throws Exception;

    void afterInit(BaseLifeCycle server) throws Exception;

    void beforeStart(BaseLifeCycle server) throws Exception;

    void beforeStop(BaseLifeCycle server) throws Exception;

    void afterStop(BaseLifeCycle server) throws Exception;

}
