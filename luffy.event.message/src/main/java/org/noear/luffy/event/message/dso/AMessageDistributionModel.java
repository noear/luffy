package org.noear.luffy.event.message.dso;

import java.util.Date;

public class AMessageDistributionModel {

    /** 分发ID */
    public long dist_id;
    /** 待分发的消息ID */
    public long msg_id;
    /**  */
    public int file_id;
    /**  */
    public String receive_url;
    /** 接收方式（0HTTP异步等待；1HTTP同步等待；2HTTP异步不等待） */
    public int receive_way;
    /** 消耗时长（s） */
    public int duration;
    /** 分发状态（-1忽略；0开始；1失败；2成功；） */
    public int state;
    /** 分发日期（yyyyMMdd） */
    public int log_date;
    /** 分发时间 */
    public Date log_fulltime;


    public long _start_time;
    public long _duration;

}
