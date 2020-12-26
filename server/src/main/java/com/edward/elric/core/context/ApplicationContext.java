package com.edward.elric.core.context;

import com.edward.elric.beans.BeanDefinition;
import com.edward.elric.beans.BeanFactory;
import com.edward.elric.beans.BeanPostProcessor;
import com.edward.elric.beans.BeanWrapper;
import com.edward.elric.core.annotion.Autowired;
import com.edward.elric.core.context.support.BeanDefinitionReader;
import com.edward.elric.core.context.support.DefaultListableBeanFactory;
import com.edward.elric.mvc.annotation.Controller;
import com.edward.elric.mvc.annotation.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: impactCn
 * @createTime: 2020-12-20
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    private Map<String, Object> beanFactoryCache = new ConcurrentHashMap<>();

    private Map<String, BeanWrapper> generalBeanFactoryCache = new ConcurrentHashMap<>();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // location
        reader = new BeanDefinitionReader(this.configLocations);
        // load
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        // register
        doRegisterBeanDefinition(beanDefinitions);
        // autowired
        doAutowired();

    }

    @Override
    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);

        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

        Object instance = getInstance(beanDefinition);
        if (instance == null) {
            return null;
        }
        beanPostProcessor.postProcessAfterInitialization(instance, beanName);

        BeanWrapper beanWrapper = new BeanWrapper(instance);
        this.generalBeanFactoryCache.put(beanName, beanWrapper);

        beanPostProcessor.postProcessAfterInitialization(instance, beanName);

        populateBean(instance);

        return this.generalBeanFactoryCache.get(beanName).getWrappedInstance();
    }

    @Override
    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }
    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        System.out.println("Application container init");
    }

    private void populateBean(Object instance) {
        Class clazz = instance.getClass();

        if (!clazz.isAnnotationPresent(Controller.class) ||
                !clazz.isAnnotationPresent(Service.class)) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();

            if ("".endsWith(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);

            try {
                field.set(instance, this.generalBeanFactoryCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private Object getInstance(BeanDefinition beanDefinition) {

        Object instance = null;

        String className = beanDefinition.getBeanClassName();

        try {
            if (this.beanFactoryCache.containsKey(className)) {
                instance = this.beanFactoryCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                // jdk 9
                instance = clazz.getDeclaredConstructor().newInstance();
                this.beanFactoryCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

}
