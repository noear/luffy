package org.noear.luffy.executor.m.beetl;

import org.beetl.core.tag.Tag;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        BeetlJtExecutor executor = BeetlJtExecutor.singleton();

        context.beanOnloaded((ctx) -> {
            ctx.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (Tag.class.isAssignableFrom(v.clz())) {
                        executor.tagReg(k.split(":")[1], v.clz());
                    }
                    return;
                }

                if (k.startsWith("share:")) { //java share object
                    executor.sharedSet(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        ExecutorFactory.register(executor);
    }
}
