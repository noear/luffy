package org.noear.luffy.cap.extend.hanlp;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.sharedAdd("eHanLP",new eHanLP());
    }
}
