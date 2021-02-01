package com.edward.elric.aop.aspect;

import com.edward.elric.aop.intercept.MethodInterceptor;
import com.edward.elric.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: impactCn
 * @createTime: 2021-02-01
 */
public class AfterThrowingAdvice extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private String throwingName;

    private MethodInvocation methodInvocation;

    public AfterThrowingAdvice(Method method, Object target) {
        super(method, target);
    }

    public void setThrowingName(String name) {
        this.throwingName = name;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
