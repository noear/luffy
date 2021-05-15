package org.noear.luffy.executor.m.enjoy;

import com.jfinal.template.Directive;
import org.noear.solon.Solon;;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.luffy.executor.ExecutorFactory;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        EnjoyJtExecutor executor = EnjoyJtExecutor.singleton();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("view:")) { //java view widget
                    if(Directive.class.isAssignableFrom(v.clz())){
                        executor.tagReg(k.split(":")[1], (Class<? extends Directive>)v.clz());
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
