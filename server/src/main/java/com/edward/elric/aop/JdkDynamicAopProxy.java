package com.edward.elric.aop;

import com.edward.elric.aop.intercept.MethodInvocation;
import com.edward.elric.aop.support.AdviseSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author: impactCn
 * @createTime: 2021-01-19
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdviseSupport config;

    public JdkDynamicAopProxy(AdviseSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,
                this.config.getTargetClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Object> methodMatchers = config.dynamicInterception(method, this.config.getTargetClass());
        MethodInvocation invocation = new MethodInvocation(
                proxy,
                method,
                this.config.getTarget(),
                this.config.getTargetClass(),
                args,
                methodMatchers);

        return invocation.p;
    }
}
