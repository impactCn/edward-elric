package com.edward.elric.core.context.support;

import com.edward.elric.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: impactCn
 * @createTime: 2020-12-20
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext{

    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


}
