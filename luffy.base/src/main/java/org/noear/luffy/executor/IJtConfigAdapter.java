package org.noear.luffy.executor;

import java.util.Map;

/**
 * 配置适配器
 * */
public interface IJtConfigAdapter {
    /**
     * 获取配置
     */
    String cfgGet(String name, String def) throws Exception;

    /**
     * 设置配置
     */
    boolean cfgSet(String name, String value) throws Exception;

    /**
     * 获取配置的完整数据（并返回 map 格式）
     */
    Map cfgMap(String name) throws Exception;
}
