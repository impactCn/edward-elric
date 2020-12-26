package com.edward.elric.mvc;

import com.edward.elric.core.annotion.Autowired;
import com.edward.elric.mvc.annotation.Controller;
import com.edward.elric.mvc.annotation.RequestMapping;
import com.edward.elric.mvc.annotation.RequestParam;
import com.edward.elric.mvc.annotation.Service;
import com.edward.elric.mvc.handler.Handler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: impactCn
 * @createTime: 2020-12-13
 */
public class DispatcherServlet extends HttpServlet {

    private Properties env = new Properties();

    private Map<String, Object> beanFactory = new ConcurrentHashMap<>();

    private List<Handler> handlerMapping = new ArrayList<>();

    private List<String> classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception \n" + Arrays.toString(e.getStackTrace()));
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        Handler handler = getHandler(req);

        if (handler == null) {
            resp.getWriter().write("404 Not Found");
            return;
        }

        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        Object[] paramValues = new Object[parameterTypes.length];

        Map<String, String[]> parameterMap = req.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", ",");

            if (!handler.getParamIndexMapping().containsKey(entry.getKey())) {
                continue;
            }
            int index = handler.getParamIndexMapping().get(entry.getKey());
            paramValues[index] = convert(parameterTypes[index], value);

        }
        if (!handler.getParamIndexMapping().containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }
        if (!handler.getParamIndexMapping().containsKey(HttpServletResponse.class.getName())) {
            int reqIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getName());
            paramValues[reqIndex] = resp;
        }
        Object returnValue = handler.getMethod().invoke(handler.getController(), paramValues);
        if (returnValue == null || returnValue instanceof Void) {
            return;
        }
        resp.getWriter().write(returnValue.toString());
    }

    private Object convert(Class<?> type, String value) {
        if (Integer.class == type) {
            return Integer.valueOf(value);
        }
        if (String.class == type) {
            return String.valueOf(value);
        }
        return value;
    }

    @Override
    public void init(ServletConfig config) {
        // first loading web.xml
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        // to find application.properties
        //// todo set as annotation
        doScanner(env.getProperty("scanPackage"));

        doInstance();

        doAutowired();

        initHandleMapping();

        System.out.println(" Framework is init");

    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(Controller.class)) {
                    // url mapping
                    // getDeclaredConstructor().newInstance() jdk 9
                    beanFactory.put(toLowerFirstCase(className), clazz.getDeclaredConstructor().newInstance());

                }
                else if (clazz.isAnnotationPresent(Service.class)) {
                    // enter bean
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())) {
                        beanName = toLowerFirstCase(clazz.getName());
                    }
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beanFactory.put(beanName, instance);

                    for (Class<?> clazzInterface : clazz.getInterfaces()) {
                        if (beanFactory.containsKey(clazzInterface.getName())) {
                            //// todo handle global exception
                            throw new Exception(("The" + clazzInterface.getName() + " is exists!"));
                        }
                        beanFactory.put(clazzInterface.getName(), instance);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        if (beanFactory.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : beanFactory.entrySet()) {

            for (Field field : entry.getValue().getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = autowired.value().trim();

                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), beanFactory.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void initHandleMapping() {
        if (beanFactory.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : beanFactory.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";

            if (!clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(entry.getValue(), method, pattern));
                System.out.println("Current Mapping: " + regex + " , " + method);
            }
        }
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(contextConfigLocation);
        try {
            env.load(inputStream);
        } catch (IOException e) {
            System.out.println("must init config");
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass()
                .getClassLoader()
                .getResource(scanPackage.replaceAll("\\.", "/"));

        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String clazzName = (scanPackage + "." + file.getName().replace(".class", ""));
                classNames.add(clazzName);
            }
        }

    }

    private Handler getHandler(HttpServletRequest request) {
        if (!handlerMapping.isEmpty()) {
            return null;
        }
        String url = request.getRequestURI();

        String contextPath = request.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", ",");
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }


}
