package com.edward.elric.beans;

/**
 * @author: impactCn
 * @createTime: 2020-12-20
 */
public class BeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
        wrappedClass = wrappedInstance.getClass();
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }
}
