package org.noear.luffy.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.core.util.ConvertUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.wood.DbContext;
import org.noear.wood.DbDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author noear 2022/4/27 created
 */
public class DsUtils {

    public static DbContext getDb(Properties prop, boolean pool) {
        if (prop.size() < 4) {
            throw new RuntimeException("Data source configuration error!");
        }

        String url = prop.getProperty("url");

        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String schema = prop.getProperty("schema");

        return new DbContext(getDs(prop, pool), schema);
    }

    public static DataSource getDs(Properties prop, boolean pool) {
        String url = prop.getProperty("url");

        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        String driverClassName = prop.getProperty("driverClassName");

        if (pool) {
            HikariDataSource source = new HikariDataSource();

            for (FieldWrap fw : ClassWrap.get(HikariDataSource.class).getAllFieldWraps()) {
                String valStr = prop.getProperty(fw.getName());
                if (TextUtils.isNotEmpty(valStr)) {
                    Object val = ConvertUtil.to(fw.getType(), valStr);
                    fw.setValue(source, val);
                }
            }

            if (TextUtils.isNotEmpty(url)) {
                source.setJdbcUrl(url);
            }

            return source;
        } else {
            if (TextUtils.isNotEmpty(driverClassName)) {
                try {
                    Class.forName(driverClassName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return new DbDataSource(url, username, password);
        }
    }
}
