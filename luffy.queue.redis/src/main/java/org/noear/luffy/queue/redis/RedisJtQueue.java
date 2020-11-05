package org.noear.luffy.queue.redis;

import org.noear.luffy.dso.*;
import org.noear.luffy.utils.ConfigUtils;

import java.util.Properties;

public class RedisJtQueue implements IJtQueue {
    private RedisX _redisX;
    private String _name;

    public RedisJtQueue(String name, Properties prop) {
        _name = name;
        _redisX = new RedisX(prop);
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
            _redisX.open0((rs) -> {
                rs.key(name()).listAdd(item);
            });
        }
    }

    @Override
    public void addAll(Iterable<String> items) {
        _redisX.open0((rs) -> {
            for (String item : items) {
                if (item != null) {
                    rs.key(name()).listAdd(item);
                }
            }
        });
    }

    @Override
    public String peek() {
        return _redisX.open1( (rs) -> rs.key(name()).listGet(-1));
    }

    @Override
    public String poll() {
        return _redisX.open1((rs) -> rs.key(name()).listPop());
    }

    @Override
    public void remove() {
        _redisX.open0((rs) -> rs.key(name()).listPop());
    }
}
