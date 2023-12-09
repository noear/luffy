package org.noear.luffy.dso;

import org.noear.luffy.executor.IJtConfigAdapter;
import org.noear.luffy.executor.IJtExecutorAdapter;
import org.noear.luffy.model.AFileModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JtBridge {
    private static IJtConfigAdapter _configAdapter;
    private static IJtExecutorAdapter _executorAdapter;
    private static IJtLock _lock = new DefaultJtLock();
    private static Function<String, IJtQueue> _queueFactory = (name) -> new DefaultJtQueue(name);
    private static Map<String, IJtQueue> _queueMap = new HashMap<>();

    /**
     * 设置队列工厂
     */
    public static void queueFactorySet(Function<String, IJtQueue> queueFactory) {
        _queueFactory = queueFactory;
    }


    /**
     * 设置锁服务
     */
    public static void lockSet(IJtLock lock) {
        _lock = lock;
    }

    /**
     * 获取锁服务
     */
    public static IJtLock lock() {
        return _lock;
    }


    /**
     * 设置执行适配器
     */
    public static void executorAdapterSet(IJtExecutorAdapter executorAdapter) {
        _executorAdapter = executorAdapter;
    }

    /**
     * 获取执行适配器
     */
    public static IJtExecutorAdapter executorAdapter() {
        return _executorAdapter;
    }

    //获取文件
    public static AFileModel fileGet(String path) throws Exception {
        return executorAdapter().fileGet(path);
    }

    //查找文件
    public static List<AFileModel> fileFind(String tag, String label, boolean isCache) throws Exception {
        return executorAdapter().fileFind(tag, label, isCache);
    }

    public static boolean log(Map<String, Object> data) {
        AFileModel file = AFileModel.current();
        executorAdapter().log(file, data);
        return true;
    }

    /**
     * 获取节点I
     */
    public static String nodeId() {
        return executorAdapter().nodeId();
    }


    /**
     * 设置执行适配器
     */
    public static void configAdapterSet(IJtConfigAdapter configAdapter) {
        _configAdapter = configAdapter;
    }

    /**
     * 获取执行适配器
     */
    public static IJtConfigAdapter configAdapter() {
        return _configAdapter;
    }

    public static String cfgGet(String name) throws Exception {
        return configAdapter().cfgGet(name, "");
    }

    public static Map<String, Object> cfgMap(String name) throws Exception {
        return configAdapter().cfgMap(name);
    }

    public static boolean cfgSet(String name, String value) throws Exception {
        return configAdapter().cfgSet(name, value);
    }


    /*
     * 新队列
     * */
    public static IJtQueue queue(String name) {
        IJtQueue tmp = _queueMap.get(name);

        if (tmp == null) {
            synchronized (_queueMap) {
                tmp = _queueMap.get(name);
                if (tmp == null) {
                    tmp = _queueFactory.apply(name);
                    _queueMap.put(name, tmp);
                }
            }
        }

        return tmp;
    }
}
