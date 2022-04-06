package org.noear.luffy.dso;

import java.util.Collection;

public interface IJtQueue {
    String name();

    /**
     * 推入到尾部
     */
    void add(String item);

    /**
     * 推入到尾部
     */
    void addAll(Collection<String> items);

    /*
     * 预览头部元素
     * */
    String peek();

    /**
     * 拉取头部元素（同时移除）
     */
    String poll();

    /**
     * 移除头部元素
     * */
    void remove();
}
