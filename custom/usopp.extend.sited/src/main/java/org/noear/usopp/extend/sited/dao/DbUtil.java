package org.noear.usopp.extend.sited.dao;

import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

public class DbUtil {

    private static ICacheServiceEx _cache = null;
    private static DbContext _db = null;

    public static void setDefDb(DbContext db){
        _db = db;
    }

    public static void setDefCache(ICacheServiceEx cache){
        _cache = cache;
    }

    public static ICacheServiceEx cache() {
        return _cache;
    }

    public static DbContext db() {
        return _db;
    }


}
