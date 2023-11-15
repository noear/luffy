package org.noear.luffy.executor.s.nashorn;

import org.noear.luffy.executor.ExecutorFactory;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;


public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        ExecutorFactory.register(NashornJtExecutor.singleton());
    }
}
