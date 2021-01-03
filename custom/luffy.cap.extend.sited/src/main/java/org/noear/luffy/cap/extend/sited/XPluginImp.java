package org.noear.luffy.cap.extend.sited;

import org.noear.luffy.cap.extend.sited.controller.ApiController;
import org.noear.luffy.cap.extend.sited.dao.DbUtil;
;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.sharedGet("db", (DbContext db) -> {
            DbUtil.setDefDb(db);
        });

        app.sharedGet("cache", (ICacheServiceEx cache) -> {
            DbUtil.setDefCache(cache);
        });

        app.sharedAdd("eSiteD", new eSiteD());
        app.beanMake(ApiController.class);
    }
}
