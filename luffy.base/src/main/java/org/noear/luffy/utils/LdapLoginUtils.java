package org.noear.luffy.utils;

import org.noear.solon.Solon;

import javax.naming.ldap.LdapContext;
import java.util.Properties;

/**
 * Ldap 登录（工具使用示例）
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
 * @author noear
 */
public class LdapLoginUtils {
    /**
     * Load 登录
     */
    public static LdapPerson ldapLogin(String userName, String userPassword) throws Exception {
        //读取连接配置
        Properties props = Solon.cfg().getProp("ldap");

        return ldapLogin(props, userName, userPassword);
    }

    /**
     * Load 登录
     */
    public static LdapPerson ldapLogin(Properties props, String userName, String userPassword) throws Exception {
        if (props == null || props.size() < 4) {
            return null;
        }

        //1.获取连接配置
        String url = props.getProperty("url");
        String baseDn = props.getProperty("baseDn");
        String groupCn = props.getProperty("groupCn");
        String username = props.getProperty("username");
        String paasword = props.getProperty("paasword");

        //2.定义用户过滤条件
        String userCn = userName;

        //3.认证
        LdapContext ldapCtx = null;
        LdapPerson ldapPerson = null;

        try {
            ldapCtx = LdapUtils.ldapConnect(url, username, paasword);

            ldapPerson = LdapUtils.findPerson(ldapCtx, baseDn, userCn, userPassword);

            //4.如果有用户且有分组
            if (ldapPerson != null && TextUtils.isNotEmpty(groupCn)) {
                LdapGroup group = LdapUtils.findGroup(ldapCtx, baseDn, groupCn);

                //如果组不存在
                if (group == null) {
                    ldapPerson = null;
                }

                //如果组里没有此人
                if(group.hasMember(userName) == false){
                    ldapPerson = null;
                }
            }

        } finally {
            if (ldapCtx != null) {
                ldapCtx.close();
            }
        }


        return ldapPerson;
    }
}