package org.noear.luffy.cap.extend.sited;

import org.noear.luffy.cap.extend.sited.controller.ApiController;
import org.noear.luffy.cap.extend.sited.dao.DbUtil;
;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.wood.DbContext;
import org.noear.wood.cache.ICacheServiceEx;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        Solon.app().sharedGet("db", (DbContext db) -> {
            DbUtil.setDefDb(db);
        });

        Solon.app().sharedGet("cache", (ICacheServiceEx cache) -> {
            DbUtil.setDefCache(cache);
        });

        Solon.app().sharedAdd("eSiteD", new eSiteD());

        context.beanMake(ApiController.class);
    }
}
