package org.noear.luffy.model;

import org.noear.luffy.utils.TextUtils;
import org.noear.weed.DbContext;
import org.noear.weed.mongo.MgContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author noear 2022/4/27 created
 */
public class AConfigResolver {

    //for mongo
    //
    private static Map<String, MgContext> toMongoMap = new HashMap<>();

    public static MgContext toMongo(AConfigM cfg, String db) {
        if (TextUtils.isEmpty(cfg.value)) {
            return null;
        }

        String cacheKey = cfg.value + "::" + db;

        MgContext mg = toMongoMap.get(cacheKey);

        if (mg == null) {
            synchronized (cacheKey.intern()) {
                mg = toMongoMap.get(cacheKey);

                if (mg == null) {
                    mg = new MgContext(cfg.getProp(), db);
                    toMongoMap.put(cacheKey, mg);
                }
            }
        }

        return mg;
    }

    //for rdb
    private static Map<String, DbContext> toDbMap = new HashMap<>();

    public static DbContext toDb(AConfigM cfg, boolean pool) {
        if (TextUtils.isEmpty(cfg.value)) {
            return null;
        }

        String cacheKey = cfg.value + "::" + pool;

        DbContext db = toDbMap.get(cacheKey);

        if (db == null) {
            synchronized (cacheKey.intern()) {
                db = toDbMap.get(cacheKey);

                if (db == null) {
                    db = getDbDo(cfg, pool);
                    toDbMap.put(cacheKey, db);
                }
            }

        }
        return db;
    }

    private static DbContext getDbDo(AConfigM cfg, boolean pool) {
        Properties prop = cfg.getProp();
        String url = prop.getProperty("url");

        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String schema = prop.getProperty("schema");

        return new DbContext(cfg.getDs(pool), schema);
    }
}
