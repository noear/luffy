package org.noear.luffy.dso;

import org.noear.solon.core.NvMap;
import org.noear.luffy.utils.DbBuilder;
import org.noear.wood.DbContext;
import org.noear.wood.cache.LocalCache;

/*
 * 数据库处理工具
 *
 * 启动参数示例：-server.port=8081 -extend=/data/sss/tk_ext/
 * */
public class DbUtil {
    /**
     * 说明：
     * <p>
     * 为启动时配一下连接信息
     */
    public static final LocalCache cache = new LocalCache("data", 60 * 5);
    private static DbContext _db = null;

    public static DbContext db() {
        return _db;
    }

    public static void setDefDb(NvMap map) {
        _db = DbBuilder.getDb(map);
    }

}
