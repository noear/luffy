package org.noear.luffy.executor.s.graaljs;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        ExecutorFactory.register(GraaljsJtExecutor.singleton());
        //不能替代（不能识别，java的多态）
        //ExecutorFactory.register("javascript",GraaljsJtExecutor.singleton(),1);
    }
}
