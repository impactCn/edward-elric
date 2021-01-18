package com.edward.elric.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author: impactCn
 * @createTime: 2021-01-19
 */
public class MethodBeforeAdvice extends AbstractAspectAdvice implements Advice{

    private JoinPoint joinPoint;

    public MethodBeforeAdvice(Method method, Object target) {
        super(method, target);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invoke(this.joinPoint, null, null);
    }

}
