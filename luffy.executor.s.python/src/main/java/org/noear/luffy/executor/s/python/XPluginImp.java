package org.noear.luffy.executor.s.python;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        ExecutorFactory.register(PythonJtExecutor.singleton());
    }
}
