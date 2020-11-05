package org.noear.luffy.task;

import java.util.HashMap;
import java.util.Map;

public class TaskFactory {
    private static Map<String, IJtTask> _taskMap = new HashMap<>();

    private static IJtTaskRunner _runner;

    public static void register(IJtTask task) {
        if (_taskMap.containsKey(task.getName())) {
            return;
        }

        _taskMap.put(task.getName(), task);

        //如果已有运行器，直接运行
        if (_runner != null) {
            _runner.run(task);
        }
    }

    public static void run(IJtTaskRunner runner) {
        //只充许赋值一次
        if (_runner != null) {
            return;
        }

        _runner = runner;

        if (_runner != null) {
            //运行一次已存在的任务
            //
            for (IJtTask task : _taskMap.values()) {
                _runner.run(task);
            }
        }
    }
}
