package com.edward.elric.aop.aspect;

import com.edward.elric.aop.intercept.MethodInterceptor;
import com.edward.elric.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: impactCn
 * @createTime: 2021-01-31
 */
public class AfterReturningAdvice extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdvice(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object returnVal = methodInvocation.proceed();
        this.joinPoint = methodInvocation;
        this.afterReturning(returnVal);
        return returnVal;

    }

    public void afterReturning(Object returnValue) throws Throwable {
        invoke(joinPoint, returnValue, null);
    }
}
