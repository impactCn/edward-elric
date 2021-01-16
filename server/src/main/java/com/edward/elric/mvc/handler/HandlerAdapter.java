package com.edward.elric.mvc.handler;

import com.edward.elric.mvc.ModelAndView;
import com.edward.elric.mvc.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: impactCn
 * @createTime: 2021-01-10
 */
public class HandlerAdapter {

    public boolean supports(Object handler) {
        return handler instanceof HandlerMapping;
    }

    public ModelAndView handle(HttpServletRequest request, Object handler) throws Exception{
        HandlerMapping handlerMapping = (HandlerMapping) handler;

        Map<String, Integer> paramMapping = new HashMap<>();

        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();

        for (int i = 0; i < pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();

                    if (!"".equals(paramName.trim())) {
                        paramMapping.put(paramName, i);
                    }

                }
            }
        }

        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            if (paramType == HttpServletRequest.class || paramType == HttpServletResponse.class) {
                paramMapping.put(paramType.getName(), i);
            }
        }

        Map<String, String[]> requestParameterMap = request.getParameterMap();

        Object[] paramVal= new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : requestParameterMap.entrySet()) {
            String val = Arrays.toString(param.getValue()).replaceAll("\\[\\]", "").replaceAll("\\s", "");

            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }

            int index = paramMapping.get(param.getKey());

            paramVal[index] = caseStringVal(val, paramTypes[index]);

        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());

            paramVal[reqIndex] = reqIndex;

        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {

            int respIndex = paramMapping.get(HttpServletResponse.class.getName());

            paramVal[respIndex] = respIndex;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramVal);

        if (result == null) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == ModelAndView.class;

        if (isModelAndView) {
            return (ModelAndView) result;
        } else {
            return null;
        }

    }

    private Object caseStringVal(String  val, Class<?> clazz) {
        if (clazz == String.class) {
            return val;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(val);
        } else if (clazz == int.class) {
            return Integer.valueOf(val);
        } else {
            return null;
        }
    }
}
