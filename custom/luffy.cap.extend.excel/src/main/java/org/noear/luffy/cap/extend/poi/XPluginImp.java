package org.noear.luffy.cap.extend.poi;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/10/9 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        Solon.global().sharedAdd("eExcel",new eExcel());
    }
}
