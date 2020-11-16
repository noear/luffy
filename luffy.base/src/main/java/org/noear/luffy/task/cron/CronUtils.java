package org.noear.luffy.task.cron;

import org.noear.solon.Utils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Cron 工具类
 * */
public class CronUtils {
    private static Map<String, CronExpression> cached = new HashMap<>();

    /**
     * 获取表达式
     */
    public static CronExpression get(String cron) {
        CronExpression expr = cached.get(cron);

        if (expr == null) {
            synchronized (cron.intern()) {
                expr = cached.get(cron);

                if (expr == null) {
                    try {
                        expr = new CronExpression(cron);
                        cached.put(cron, expr);
                    } catch (ParseException ex) {
                        throw Utils.throwableWrap(ex);
                    }
                }
            }
        }

        return expr;
    }

    /**
     * 获取下个时间点
     * */
    public static Date getNextTime(String cron, Date baseTime){
        return get(cron).getNextValidTimeAfter(baseTime);
    }

    /**
     * 验证表达式有效性
     */
    public static boolean isValid(String cron) {
        CronExpression expr = cached.get(cron);

        if (expr == null) {
            synchronized (cron.intern()) {
                expr = cached.get(cron);

                if (expr == null) {
                    try {
                        expr = new CronExpression(cron);
                        cached.put(cron, expr);
                    } catch (ParseException ex) {
                        return false;
                    }
                }
            }
        }

        return expr != null;
    }
}
