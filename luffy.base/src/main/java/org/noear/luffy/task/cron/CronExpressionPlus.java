package org.noear.luffy.task.cron;

import java.text.ParseException;
import java.util.*;

/**
 * @author noear 2021/7/5 created
 */
public class CronExpressionPlus extends CronExpression {
    public CronExpressionPlus(String cronExpression) throws ParseException {
        super(cronExpression);
    }

    /**
     * Constructs a new {@code CronExpression} as a copy of an existing
     * instance.
     *
     * @param expression The existing cron expression to be copied
     */
    public CronExpressionPlus(CronExpression expression) {
        super(expression);
    }


    public Set<Integer> getSeconds() {
        return Collections.unmodifiableSet(seconds);
    }

    public Set<Integer> getMinutes() {
        return Collections.unmodifiableSet(minutes);
    }

    public Set<Integer> getHours() {
        return Collections.unmodifiableSet(hours);
    }

    public Set<Integer> getDaysOfMonth() {
        return Collections.unmodifiableSet(daysOfMonth);
    }

    public Set<Integer> getMonths() {
        return Collections.unmodifiableSet(months);
    }

    public Set<Integer> getDaysOfWeek() {
        return Collections.unmodifiableSet(daysOfWeek);
    }

    public Set<Integer> getYears() {
        return Collections.unmodifiableSet(years);
    }
}
