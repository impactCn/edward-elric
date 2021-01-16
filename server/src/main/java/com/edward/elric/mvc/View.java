package com.edward.elric.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: impactCn
 * @createTime: 2021-01-10
 */
public class View {

    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;


    public View(File viewFile) {
        this.viewFile = viewFile;
    }

    public static String getDefaultContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer stringBuffer = new StringBuffer();

        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

        String line = null;

        String lineReg = "\\$\\{[^\\}]+\\}";

        String paramReg = "\\$\\{|\\}";
        try {

            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                Pattern pattern = Pattern.compile(lineReg, Pattern.CASE_INSENSITIVE);

                Matcher matcher = pattern.matcher(line);


                while (matcher.find()) {

                    String paramName = matcher.group();

                    paramName = paramName.replaceAll(paramReg, "");

                    Object paramVal = model.get(paramName);

                    if (null == paramVal) {
                        continue;
                    }
                    line = matcher.replaceFirst(makeStringForRegExp(paramVal.toString()));
                    matcher = pattern.matcher(line);

                }
                stringBuffer.append(line);


            }
        } finally {
            ra.close();
        }

        response.setCharacterEncoding("utf-8");
        response.getWriter().write(stringBuffer.toString());


    }

    public String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }




}
