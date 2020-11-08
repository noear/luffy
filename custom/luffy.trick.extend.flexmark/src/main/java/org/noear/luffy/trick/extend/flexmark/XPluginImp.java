package org.noear.luffy.trick.extend.flexmark;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.sharedAdd("eMark",new eMark());
    }
}
