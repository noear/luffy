package org.noear.luffy.model;

import org.noear.luffy.utils.DsUtils;
import org.noear.snack.ONode;
import org.noear.solon.core.NvMap;
import org.noear.luffy.utils.ConfigUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.weed.DbContext;

import javax.sql.DataSource;
import java.util.Properties;

public class AConfigM {
    public final String value;
    public AConfigM(String value){
        this.value = value;
    }


    public String getString() {
        return value;
    }

    public String getString(String def) {
        return value == null ? def : value;
    }

    /**
     * 转为Int
     */
    public int getInt(int def) {
        if (TextUtils.isEmpty(value)) {
            return def;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * 转为Long
     */
    public long getLong(long def) {
        if (TextUtils.isEmpty(value)) {
            return def;
        } else {
            return Long.parseLong(value);
        }
    }

    public NvMap getXmap() {
        NvMap map = new NvMap();
        for (String s : value.split("\\n")) {
            String[] kv = s.split("=");
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }

        return map;
    }

    /**
     * 转为Properties
     */
    private Properties _prop;

    public Properties getProp() {
        if (_prop == null) {
            _prop = ConfigUtils.getProp(value);
        }

        return _prop;
    }

    /**
     * 转为ONode
     */
    private ONode _node;

    public ONode getNode() {
        if (_node == null) {
            _node = ConfigUtils.getNode(value);
        }

        return _node;
    }

    /**
     * 获取 db:DbContext
     */
    public DbContext getDb() {
        return getDb(false);
    }

    public DbContext getDb(boolean pool) {
        return AConfigResolver.toDb(this, pool);
    }


    public DataSource getDs(boolean pool) {
        return DsUtils.getDs(getProp(), pool);
    }
}
