package org.noear.luffy.cap.extend.diff;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        Solon.app().sharedAdd("eDiff",new eDiff());
    }
}
