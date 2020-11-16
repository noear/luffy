package org.noear.luffy.utils;

import org.noear.solon.annotation.Note;

public class Timecount {
    private long start_time;

    @Note("开始计时")
    public Timecount start() {
        start_time = System.currentTimeMillis();
        return this;
    }

    @Note("结束计时，并返回间隔时间")
    public Timespan stop() {
        return new Timespan(System.currentTimeMillis(), start_time);
    }


    @Note("结束计时，并返回间隔秒数")
    public String stop(long ref_second) {
        double temp = (stop().milliseconds() / 10) / 100.00d;

        if (temp > ref_second) {
            return temp + "s******慢!!!";
        } else {
            return temp + "s";
        }
    }
}
