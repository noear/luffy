package org.noear.luffy.executor.m.enjoy;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        ExecutorFactory.register(EnjoyJtExecutor.singleton());
    }
}
