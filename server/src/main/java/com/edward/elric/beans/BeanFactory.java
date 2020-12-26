package com.edward.elric.beans;

/**
 * @author: impactCn
 * @createTime: 2020-12-20
 */
public interface BeanFactory {

    Object getBean(String beanName) ;

    public Object getBean(Class<?> beanClass) ;
}
