package org.noear.luffy.queue.redis;

import org.noear.luffy.dso.*;
import org.noear.luffy.utils.ConfigUtils;
import org.noear.redisx.RedisClient;
import org.noear.redisx.plus.RedisQueue;

import java.util.Collection;
import java.util.Properties;

public class RedisJtQueue implements IJtQueue {
    private RedisQueue _queue;
    private String _name;

    public RedisJtQueue(String name, Properties prop) {
        _name = name;
        _queue = new RedisClient(prop).getQueue(name);
    }

    public static void init(String cfg) throws Exception {
        if (cfg == null) {
            return;
        }

        String prop_str = cfg;
        if (cfg.startsWith("@")) {
            prop_str = CfgUtil.cfgGetValue(cfg.substring(1));
        }

        Properties prop = ConfigUtils.getProp(prop_str);

        if (prop != null && prop.size() >= 5) {
            JtBridge.queueFactorySet((name) -> new RedisJtQueue(name, prop));
        }else{
            LogUtil.log("RedisJtQueue", LogLevel.WARN, "初始化失败，参数有问题", prop_str);
        }
    }


    @Override
    public String name() {
        return _name;
    }

    @Override
    public void add(String item) {
        if (item != null) {
            _queue.add(item);
        }
    }

    @Override
    public void addAll(Collection<String> items) {
        _queue.addAll(items);
    }

    @Override
    public String peek() {
        return _queue.peek();
    }

    @Override
    public String poll() {
        return _queue.pop();
    }

    @Override
    public void remove() {
         _queue.pop();
    }
}
