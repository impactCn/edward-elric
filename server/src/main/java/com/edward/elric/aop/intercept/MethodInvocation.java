package com.edward.elric.aop.intercept;

import com.edward.elric.aop.aspect.JoinPoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author: impactCn
 * @createTime: 2021-01-19
 */
public class MethodInvocation implements JoinPoint {

    private Object proxy;

    private Method method;

    private Object target;

    private Class<?> targetClass;

    private Object[] args;

    private List<Object> methodMatchers;

    private Map<String, Object> attributes;


    public MethodInvocation(Object proxy,
                            Method method,
                            Object target,
                            Class<?> targetClass,
                            Object[] args,
                            List<Object> methodMatchers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.args = args;
        this.methodMatchers = methodMatchers;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public Object getThis() {
        return null;
    }

    @Override
    public void setAttribute() {

    }

    @Override
    public void getAttribute() {

    }
}
