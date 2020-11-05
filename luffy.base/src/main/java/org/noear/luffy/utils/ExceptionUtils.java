package org.noear.luffy.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常格式化工具
 * */
public class ExceptionUtils {
    public static String getString(Throwable ex){
        StringWriter sw = new StringWriter();
        PrintWriter ps = new PrintWriter(sw);
        ex.printStackTrace(ps);

        return sw.toString();
    }
}
