package com.edward.elric.aop.intercept;

/**
 * @author: impactCn
 * @createTime: 2021-01-17
 */
public interface MethodInterceptor {

    Object invoke(MethodInvocation methodInvocation) throws Throwable;
}
