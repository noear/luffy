package org.noear.luffy.utils;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import javax.naming.ldap.LdapContext;
import java.util.Properties;

/**
 * Ldap登录 使用示例
 *
 * <pre><code>
 * String userName = "";
 * String userPassword = "";
 *
 * LdapUser user = LdapLoginUtils.ldapLogin(userName, userPassword);
 *
 * if(user == null){
 *     //登录失败
 * }else{
 *     //登录成功
 *
 *     //检查，当前系统有没有 user 相关的账号，如果有更新名显示名之类的（也可以略过）；如果没有插入到当前系统账号。
 * }
 *
 * </code></pre>
 *
 */
public class LdapLoginUtils {
    /**
     * Load 登录
     */
    public static LdapUser ldapLogin(String userName, String userPassword) throws Exception {
        //读取链拉配置
        Properties prop = Solon.cfg().getProp("ldap");

        return ldapLogin(prop, userName, userPassword);
    }

    /**
     * Load 登录
     */
    public static LdapUser ldapLogin(Properties prop, String userName, String userPassword) throws Exception {
        String url = prop.getProperty("url");
        String baseDn = prop.getProperty("baseDn");
        String groupCn = prop.getProperty("groupCn");
        String username = prop.getProperty("username");
        String paasword = prop.getProperty("paasword");

        //定义用户过滤条件
        String userFilter = "cn=" + userName;
        if (Utils.isNotEmpty(groupCn)) {
            userFilter += ",cn=" + groupCn;
        }

        LdapContext ldapCtx = null;

        try {
            ldapCtx = LdapUtils.ldapConnect(url, username, paasword);

            return LdapUtils.ldapAuth(ldapCtx, baseDn, userFilter, userPassword);
        } finally {
            if (ldapCtx != null) {
                ldapCtx.close();
            }
        }
    }
}
