package org.noear.luffy.lock.redis;

import org.noear.luffy.dso.*;
import org.noear.luffy.utils.ConfigUtils;

import java.util.Properties;

public class RedisJtLock implements IJtLock {
    private RedisX _redisX;

    public RedisJtLock(Properties prop){
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
            JtBridge.lockSet(new RedisJtLock(prop));
        } else {
            LogUtil.log("RedisJtLock", LogLevel.WARN, "初始化失败，参数有问题", prop_str);
        }
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        String key2 = group + ".lk." + key;

        return _redisX.open1((ru) -> ru.key(key2).expire(inSeconds).lock(inMaster));
    }

    @Override
    public boolean tryLock(String group, String key, int inSeconds) {
        String key2 = group + ".lk." + key;

        return _redisX.open1((ru) -> ru.key(key2).expire(inSeconds).lock("_"));
    }

    @Override
    public boolean tryLock(String group, String key) {
        return tryLock(group, key, 3);
    }

    @Override
    public boolean isLocked(String group, String key) {
        String key2 = group + ".lk." + key;

        return _redisX.open1((ru) -> ru.key(key2).exists());
    }

    @Override
    public void unLock(String group, String key) {
        String key2 = group+".lk." + key;

        _redisX.open0((ru) -> {
            ru.key(key2).delete();
        });
    }
}
