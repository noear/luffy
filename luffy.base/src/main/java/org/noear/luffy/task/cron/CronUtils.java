package org.noear.luffy.task.cron;

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
    public static CronExpression get(String cron) throws ParseException {
        CronExpression expr = cached.get(cron);

        if (expr == null) {
            synchronized (cron.intern()) {
                expr = cached.get(cron);

                if (expr == null) {
                    expr = new CronExpression(cron);
                    cached.put(cron, expr);
                }
            }
        }

        return expr;
    }

    /**
     * 获取下个时间点
     */
    public static Date getNextTime(String cron, Date baseTime) throws ParseException {
        return get(cron).getNextValidTimeAfter(baseTime);
    }

    /**
     * 验证表达式有效性
     */
    public static boolean isValid(String cron) {
        try {
            return get(cron) != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
