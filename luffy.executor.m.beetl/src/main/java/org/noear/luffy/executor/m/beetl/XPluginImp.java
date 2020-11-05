package org.noear.luffy.executor.m.beetl;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements XPlugin {

    @Override
    public void start(XApp app) {
        ExecutorFactory.register(BeetlJtExecutor.singleton());
    }
}
