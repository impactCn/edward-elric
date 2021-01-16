package com.edward.elric.mvc;

import com.edward.elric.core.annotion.Autowired;
import com.edward.elric.core.context.ApplicationContext;
import com.edward.elric.mvc.annotation.Controller;
import com.edward.elric.mvc.annotation.RequestMapping;
import com.edward.elric.mvc.annotation.RequestParam;
import com.edward.elric.mvc.annotation.Service;
import com.edward.elric.mvc.handler.Handler;
import com.edward.elric.mvc.handler.HandlerAdapter;
import com.edward.elric.mvc.handler.HandlerMapping;

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

    private final String LOCATION = "contextConfigLocation";

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    private List<ViewResolve> viewResolves = new ArrayList<>();

    private ApplicationContext context;




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

        HandlerMapping handler = getHandler(req);

        if (handler == null) {
            processDispatchResult(req, resp, new ModelAndView("404"));
            return;
        }

        HandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        ModelAndView mv = handlerAdapter.handle(req, handler);

        processDispatchResult(req, resp, mv);


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

        context = new ApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        // upload
        initMultipartResolve(context);;

        // located
        initLocaleResolve(context);

        initThemeResolve(context);

        initHandlerMappings(context);

        initHandlerAdapter(context);

        initHandlerExceptionResolve(context);

        initRequestToViewNameTranslator(context);

        initViewResolvers(context);

        initFlashMapManage(context);

    }

    private void initMultipartResolve(ApplicationContext context) {}

    private void initFlashMapManage(ApplicationContext context) {}

    private void initRequestToViewNameTranslator(ApplicationContext context) {}

    private void initHandlerExceptionResolve(ApplicationContext context) {}

    private void initThemeResolve(ApplicationContext context) {}

    private void initLocaleResolve(ApplicationContext context) {}

    private void initHandlerMappings(ApplicationContext context) {

        String[] beanNames = context.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object controller = context.getBean(beanName);

            Class<?> clazz = controller.getClass();

            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }

            String baseUrl = "";

            if (!clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                String regex = ("/" + baseUrl + requestMapping.value()
                        .replaceAll("\\*", ".*")
                        .replaceAll("/+", "/"));

                Pattern pattern = Pattern.compile(regex);

                this.handlerMappings.add(new HandlerMapping(controller, method, pattern));


            }

        }

    }

    private void initHandlerAdapter(ApplicationContext context) {
        for (HandlerMapping handlerMapping : this.handlerMappings) {

            this.handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initViewResolvers(ApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String path = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File file = new File(path);

        for (File listFile : file.listFiles()) {

            this.viewResolves.add(new ViewResolve(listFile.getPath()));

        }


    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, ModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }

        if (this.viewResolves.isEmpty()) {
            return;
        } else {
            for (ViewResolve viewResolve : this.viewResolves) {

                View view = viewResolve.resolveViewName(mv.getViewName(), null);

                if (view != null) {
                    view.render(mv.getModel(), request, response);
                    return;
                }

            }
        }

    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }

        HandlerAdapter handlerAdapter = this.handlerAdapters.get(handler);

        if (handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
        return null;

    }

    private HandlerMapping getHandler(HttpServletRequest request) {
        if (!handlerMappings.isEmpty()) {
            return null;
        }
        String url = request.getRequestURI();

        String contextPath = request.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", ",");
        for (HandlerMapping handler : handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }


}
