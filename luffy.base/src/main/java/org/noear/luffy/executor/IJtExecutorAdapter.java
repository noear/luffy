package org.noear.luffy.executor;

import org.noear.luffy.model.AFileModel;

import java.util.Map;

/**
 * 执行器适配器
 * */
public interface IJtExecutorAdapter {
    void log(AFileModel file, Map<String,Object> data);
    //记录异常
    void logError(AFileModel file, String msg, Throwable err);

    //获取文件
    AFileModel fileGet(String path) throws Exception;

    //获取节点ID
    String nodeId();

    //默认执行器代号
    String defaultExecutor();
}
