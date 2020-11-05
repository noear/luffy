package org.noear.luffy.queue.redis;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.luffy.dso.JtBridge;

import java.util.Properties;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Properties prop = app.prop().getProp("luffy.queue.redis");

        if (prop != null && prop.size() >= 5) {
            JtBridge.queueFactorySet((name) -> new RedisJtQueue(name, prop));
        }
    }
}
