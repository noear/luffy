package org.noear.luffy.executor.s.ruby;

import org.noear.solon.Solon;;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        ExecutorFactory.register(RubyJtExecutor.singleton());
    }
}
