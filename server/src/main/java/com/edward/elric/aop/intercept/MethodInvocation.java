package com.edward.elric.aop.intercept;

import com.edward.elric.aop.aspect.JoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
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

    private int currentInterceptorIndex = -1;


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

    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.methodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.getArgs());
        }

        Object advice = this.methodMatchers.get(++this.currentInterceptorIndex);

        if (advice instanceof MethodInterceptor) {
            MethodInterceptor methodInterceptor = (MethodInterceptor) advice;
            return methodInterceptor.invoke(this);
        } else {
            return proceed();
        }

    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArgs() {
        return this.args;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setAttribute(String key, String value) {
        if (value != null) {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            this.attributes.put(key, value);
        }
        else {
            if (this.attributes != null) {
                this.attributes.remove(value);
            }
        }

    }

    @Override
    public Object getAttribute(String key) {
        return this.attributes != null ? this.attributes.get(key) : null;
    }
}
