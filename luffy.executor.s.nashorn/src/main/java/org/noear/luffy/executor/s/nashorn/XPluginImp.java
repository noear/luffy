package org.noear.luffy.executor.s.nashorn;

import org.noear.luffy.executor.ExecutorFactory;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;


public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        ExecutorFactory.register(NashornJtExecutor.singleton());
    }
}
