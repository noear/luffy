package org.noear.luffy.executor.m.thymeleaf;

import org.noear.solon.Solon;;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;
import org.thymeleaf.processor.element.IElementTagProcessor;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        ThymeleafJtExecutor executor = ThymeleafJtExecutor.singleton();

        context.beanOnloaded((ctx) -> {
            ctx.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if(IElementTagProcessor.class.isAssignableFrom(v.clz())) {
                        executor.tagReg(k.split(":")[1], v.raw());
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
