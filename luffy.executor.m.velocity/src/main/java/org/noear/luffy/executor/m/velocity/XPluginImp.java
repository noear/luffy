package org.noear.luffy.executor.m.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        VelocityJtExecutor executor = VelocityJtExecutor.singleton();

        context.lifecycle(() -> {
            context.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if (v.raw() instanceof Directive) {
                        executor.tagReg(v.raw());
                    }
                    return;
                }

                if(k.startsWith("share:")){ //java share object
                    executor.sharedSet(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        ExecutorFactory.register(executor);
    }
}
