package org.noear.luffy.event.message.dso;

import java.util.Date;

public class AMessageModel {

    /** 消息ID */
    public long msg_id;
    /** 主题 */
    public String topic;
    /** 原始主题 */
    public String topic_source;
    /** 消息内容 */
    public String content;
    /** 状态（-2无派发对象 ; -1:忽略；0:未处理；1处理中；2已完成；3派发超次数） */
    public int state;
    /** 派发累记次数 */
    public int dist_count;
    /** 下次派发时间 */
    public long dist_ntime;
    /** 记录日期（yyyyMMdd） */
    public int log_date;
    /** 记录时间 */
    public Date log_fulltime;

}
