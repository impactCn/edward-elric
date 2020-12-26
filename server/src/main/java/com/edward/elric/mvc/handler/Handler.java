package com.edward.elric.mvc.handler;

import com.edward.elric.mvc.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: impactCn
 * @createTime: 2020-12-19
 */
public class Handler {

    private Object controller;
    private Method method;
    private Pattern pattern;
    private Map<String, Integer> paramIndexMapping;

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        this.paramIndexMapping =  new HashMap<>();
        putParamIndexMapping(method);
    }

    private void putParamIndexMapping(Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();
                    if (!"".equals(paramName.trim())) {
                       paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class
                    || parameterType == HttpServletResponse.class) {
                paramIndexMapping.put(parameterType.getName(), i);
            }
        }
    }
}
