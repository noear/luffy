package org.noear.luffy.cap.extend.flexmark;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.sharedAdd("eMark",new eMark());
    }
}
