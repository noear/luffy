package org.noear.luffy.lock.redis;

import org.noear.solon.Solon;;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.luffy.dso.JtBridge;

import java.util.Properties;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Properties prop = app.cfg().getProp("luffy.lock.redis");

        if (prop != null && prop.size() >= 5) {
            JtBridge.lockSet(new RedisJtLock(prop));
        }
    }
}
