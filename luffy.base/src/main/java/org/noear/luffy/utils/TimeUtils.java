package org.noear.luffy.utils;

import java.util.Date;

public class TimeUtils {
    public static String liveTime(Date date){
        if(date == null){
            return "";
        }

        Timespan tm = new Timespan(date);
        if(tm.minutes()<1){
            return tm.seconds()+"秒前";
        }

        if(tm.hours()<1){
            return tm.minutes()+"分钟前";
        }

        if(tm.days()<1){
            return tm.hours()+"小时前";
        }

        if(tm.days()<30){
            return tm.days()+"天前";
        }

        Datetime date2 = new Datetime(date);
        Datetime now = Datetime.Now();

        if(now.getYear() == date2.getYear()){
            return date2.toString("MM月dd日");
        }else{
            return date2.toString("yyyy-MM-dd");
        }
    }
}
