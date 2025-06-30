package org.noear.luffy.model;

import java.util.Date;

public class AFileModel {
    private static ThreadLocal<AFileModel> _current = new ThreadLocal();

    /**
     * 获取当前文件
     * */
    public static AFileModel current(){
        return _current.get();
    }

    /**
     * 设置当前文件
     * */
    public static void currentSet(AFileModel file){
        _current.set(file);
    }

    /**
     * 移除当前文件
     * */
    public static void currentRemove(){
        _current.remove();
    }


    /** 文件id */
    public int file_id;
    /** 文件类型 */
    public int file_type;

    /** 分组标签 */
    public String tag;
    /** 属性标记 */
    public String label;
    /** 备注 */
    public String note;
    /** 路径 */
    public String path;
    /** 方式 */
    public String method;

    /** 是否静态 */
    public boolean is_staticize;
    /** 可编辑 */
    public boolean is_editable;
    /** 禁止访问 */
    public boolean is_disabled;
    /** 连接到 */
    public String link_to;
    /** 分类标签 */
    public String edit_mode;
    /** 内容类型 */
    public String content_type;
    /** 内容 */
    public String content;

    /** 计划状态 */
    public int plan_state;
    /** 计划开始时间 */
    public Date plan_begin_time;
    /** 计划最近执行时间 */
    public Date plan_last_time;
    /** 计划最近执行时长 */
    public long  plan_last_timespan;
    /** 计划间隔时间 */
    public String plan_interval;
    /** 计划最多执行次数 */
    public int plan_max;
    /** 计划执行次数 */
    public int plan_count;

    /** 使用白名单 */
    public String use_whitelist;

    /** 创建时间 */
    public Date create_fulltime;
    /** 更新时间 */
    public Date update_fulltime;
}