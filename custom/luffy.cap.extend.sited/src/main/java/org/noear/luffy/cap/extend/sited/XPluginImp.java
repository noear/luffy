package org.noear.luffy.cap.extend.sited;

import org.noear.luffy.cap.extend.sited.controller.ApiController;
import org.noear.luffy.cap.extend.sited.dao.DbUtil;
;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.wood.DbContext;
import org.noear.wood.cache.ICacheServiceEx;

public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        Solon.global().sharedGet("db", (DbContext db) -> {
            DbUtil.setDefDb(db);
        });

        Solon.global().sharedGet("cache", (ICacheServiceEx cache) -> {
            DbUtil.setDefCache(cache);
        });

        Solon.global().sharedAdd("eSiteD", new eSiteD());

        context.beanMake(ApiController.class);
    }
}
