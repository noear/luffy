package org.noear.luffy.executor.m.bsql;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements XPlugin {

    @Override
    public void start(XApp app) {
        ExecutorFactory.register(BsqlJtExecutor.singleton());
    }
}
