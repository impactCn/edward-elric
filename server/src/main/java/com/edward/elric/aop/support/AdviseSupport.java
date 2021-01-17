package com.edward.elric.aop.support;

import com.edward.elric.aop.AopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: impactCn
 * @createTime: 2021-01-17
 */
public class AdviseSupport<T> {

    private Class<T> targetClass;

    private Object target;

    private Pattern pattern;

    private transient Map<Method, List<Object>> methodCache;

    private AopConfig config;

    public AdviseSupport(AopConfig config) {
        this.config = config;
    }

    public Class<T> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> dynamicInterception(Method method, Class<?> target) throws Exception {
        List<Object> cached = methodCache.get(method);

        if (cached == null) {
            Method invoke = target.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(invoke);
            this.methodCache.put(invoke, cached);

        }

        return cached;

    }

    public boolean pointCutMatch() {
        return pattern.matcher(this.getTargetClass().toString()).matches();
    }

    private void parse() {

        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");


        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);

        pattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));

        methodCache = new HashMap<>();

        Pattern pattern = Pattern.compile(pointCut);

        try {

            Class clazz = Class.forName(config.getAspectClass());

            Map<String, Method> aspectMethodMap = new HashMap<>();

            for (Method method : clazz.getMethods()) {
                aspectMethodMap.put(method.getName(), method);
            }

            for (Method method : targetClass.getMethods()) {
                String methodStr = method.toString();

                if (methodStr.contains("throws")) {
                    methodStr = methodStr.substring(0, methodStr.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodStr);
                if (matcher.matches()) {
                    // satisfied aop rule
                    LinkedList<Object> advices = new LinkedList<>();

                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore().trim()))) {
                        advices.add(new Method)
                    }

                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
