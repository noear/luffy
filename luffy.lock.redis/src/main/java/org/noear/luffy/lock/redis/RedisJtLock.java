package org.noear.luffy.lock.redis;

import org.noear.luffy.dso.*;
import org.noear.luffy.utils.ConfigUtils;
import org.noear.redisx.RedisClient;

import java.util.Properties;

public class RedisJtLock implements IJtLock {
    private RedisClient _redisX;

    public RedisJtLock(Properties prop) {
        _redisX = new RedisClient(prop);
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
            JtBridge.lockSet(new RedisJtLock(prop));
        } else {
            LogUtil.log("RedisJtLock", LogLevel.WARN, "初始化失败，参数有问题", prop_str);
        }
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        String key2 = group + ".lk." + key;

        return _redisX.getLock(key2).tryLock(inSeconds, inMaster);
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        String key2 = group + ".lk." + key;

        return _redisX.getLock(key2).tryLock();
    }

    @Override
    public boolean tryLock(String group, String key) {
        return tryLock(group, key, 3);
    }

    @Override
    public boolean isLocked(String group, String key) {
        String key2 = group + ".lk." + key;

        return _redisX.getLock(key2).isLocked();
    }

    @Override
    public void unLock(String group, String key) {
        String key2 = group + ".lk." + key;

        _redisX.getLock(key2).unLock();
    }
}
