package com.edward.elric.aop;

/**
 * @author: impactCn
 * @createTime: 2021-01-19
 */
public interface AopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
