package org.noear.luffy.cap.extend.hanlp;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        Solon.global().sharedAdd("eHanLP",new eHanLP());
    }
}
