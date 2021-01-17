package com.edward.elric.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author: impactCn
 * @createTime: 2021-01-18
 */
public abstract class AbstractAspectAdvice {

    private Method method;

    private Object target;

    public AbstractAspectAdvice(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    protected Object invoke(JoinPoint joinPoint, Object returnVal, Throwable ex) throws Throwable {
        Class<?>[] paramTypes = this.method.getParameterTypes();



        if (paramTypes.length == 0 || null == paramTypes) {
            return this.method.invoke(target);
        } else {

            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnVal;
                }

            }
            return this.method.invoke(target, args);
        }

    }
}
