package org.noear.luffy.trick.extend.jsoup;

import org.noear.solon.Solon;;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    @Override
    public void start(Solon app) {
        app.sharedAdd("eDom",new eDom());
    }
}
