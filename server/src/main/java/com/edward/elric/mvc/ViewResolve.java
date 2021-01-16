package com.edward.elric.mvc;

import java.io.File;
import java.util.Locale;

/**
 * @author: impactCn
 * @createTime: 2021-01-10
 */
public class ViewResolve {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    private String viewName;

    public ViewResolve(String templateRoot) {
        String path = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        this.templateRootDir = new File(path);

    }

    public View resolveViewName(String viewName, Locale locale) {
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName: (viewName + DEFAULT_TEMPLATE_SUFFIX);

        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new View(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
