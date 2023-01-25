package org.noear.luffy.executor.m.freemarker;

import freemarker.template.TemplateDirectiveModel;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        FreemarkerJtExecutor executor = FreemarkerJtExecutor.singleton();

        context.beanOnloaded((ctx) -> {
            ctx.beanForeach((k, v) -> {
                if (k.startsWith("view:") || k.startsWith("ftl:")) {
                    //java view widget
                    if(TemplateDirectiveModel.class.isAssignableFrom(v.clz())) {
                        executor.tagReg(k.split(":")[1], v.raw());
                    }
                }

                if(k.startsWith("share:")){
                    //java share object
                    executor.sharedSet(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        ExecutorFactory.register(executor);
    }
}
