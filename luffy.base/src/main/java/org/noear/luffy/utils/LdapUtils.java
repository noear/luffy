package org.noear.luffy.utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.*;

/**
 * Ldap 工具类
 * */
public class LdapUtils {
    /**
     * 获取人员
     *
     * @param ctx 连接上下文
     * @param baseDn
     * @param userCn 用户CN
     * @param userPassword 用户密码
     *
     * @return LdapPerson 如果null则没找到用户，否则为用户信息
     */
    public static LdapPerson findPerson(LdapContext ctx, String baseDn, String userCn, String userPassword) throws Exception {
        if (TextUtils.isEmpty(userCn) || TextUtils.isEmpty(userPassword)) {
            return null;
        }

        String userPassword2 = LdapUtils.buildLdapPassword(userPassword);

        LdapPerson user = ldapFindBy(ctx, baseDn, "inetOrgPerson", userCn, LdapPerson.class);

        if (user != null) {
            if (userPassword.equals(user.getUserPassword())
                    || userPassword2.equals(user.getUserPassword())) {
                return user;
            }
        }


        return null;
    }

    /**
     * 查找分组
     * */
    public static LdapGroup findGroup(LdapContext ctx, String baseDn, String groupCn) throws Exception {
        if (TextUtils.isEmpty(groupCn)) {
            return null;
        }

        return ldapFindBy(ctx, baseDn, "posixGroup", groupCn, LdapGroup.class);
    }

    /**
     * 查询节点
     */
    public static <T extends LdapNode> T ldapFindBy(LdapContext ctx, String baseDn, String objectClass, String cn, Class<T> tClass) throws Exception {
        if (ctx == null) {
            return null;
        }

        //过滤条件
        StringBuilder filter = new StringBuilder();
        filter.append("(&(objectClass=")
                .append(objectClass)
                .append(")");

        if (cn.contains("=")) {
            filter.append("(")
                    .append(cn)
                    .append("))");
        } else {
            filter.append("(cn=")
                    .append(cn)
                    .append("))");
        }


        return ldapFindOne(ctx, baseDn, filter.toString(), tClass);
    }

    /**
     * 查找节点
     */
    public static <T extends LdapNode> T ldapFindOne(LdapContext ctx, String baseDn, String filter, Class<T> tClass) throws Exception {

        if (ctx != null) {
            Field[] fields = tClass.getDeclaredFields();
            String[] attrArray = Arrays.asList(fields).stream().map(f -> f.getName()).toArray(n -> new String[n]);

            //搜索控件
            SearchControls searchControls = new SearchControls();

            //搜索范围::
            searchControls.setSearchScope(2);

            //返回属性::
            searchControls.setReturningAttributes(attrArray);


            // 1.要搜索的上下文或对象的名称；
            // 2.过滤条件，可为null，默认搜索所有信息；
            // 3.搜索控件，可为null，使用默认的搜索控件
            NamingEnumeration<SearchResult> result = ctx.search(baseDn, filter, searchControls);
            while (result.hasMore()) {
                T node = tClass.newInstance();
                node.bind(result.next().getAttributes());

                if (node.getCn() != null) {
                    return node;
                }
            }
        }

        return null;
    }

    /**
     * 查找节点
     */
    public static <T extends LdapNode> List<T> ldapFindList(LdapContext ctx, String baseDn, String filter, Class<T> tClass) throws Exception {
        List<T> tList = new ArrayList<>();

        if (ctx != null) {
            Field[] fields = tClass.getDeclaredFields();
            String[] attrArray = Arrays.asList(fields).stream().map(f -> f.getName()).toArray(n -> new String[n]);

            //搜索控件
            SearchControls searchControls = new SearchControls();

            //搜索范围::
            searchControls.setSearchScope(2);

            //返回属性::
            searchControls.setReturningAttributes(attrArray);


            // 1.要搜索的上下文或对象的名称；
            // 2.过滤条件，可为null，默认搜索所有信息；
            // 3.搜索控件，可为null，使用默认的搜索控件
            NamingEnumeration<SearchResult> result = ctx.search(baseDn, filter, searchControls);
            while (result.hasMore()) {
                T node = tClass.newInstance();
                node.bind(result.next().getAttributes());

                if (node.getCn() != null) {
                    tList.add(node);
                }
            }
        }

        return tList;
    }

    /**
     * 获取ldap链接上下文
     *
     * @param url
     * @param bindLdapDn bindDn
     * @param bindLdapPwd paasword
     * @return LdapContext
     */
    public static LdapContext ldapConnect(String url, String bindLdapDn, String bindLdapPwd) throws NamingException {
        String factory = "com.sun.jndi.ldap.LdapCtxFactory";
        String simple = "simple";

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, simple);
        env.put(Context.SECURITY_PRINCIPAL, bindLdapDn);
        env.put(Context.SECURITY_CREDENTIALS, bindLdapPwd);
        Control[] connCtls = null;

        return new InitialLdapContext(env, connCtls);
    }

    /**
     * 构建ldap密码
     *
     * @param orgPwd 原始密码
     */
    public static String buildLdapPassword(String orgPwd) {
        //TEST LDAP密码MD5加密，先MD5，再BASE64
        byte[] byteArray = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(orgPwd.getBytes("UTF-8"));

            byteArray = md.digest();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //{MD5}ICy5YqxZB1uWSwcVLSNLcA==

        return "{MD5}" + Base64Utils.encodeByte(byteArray);
    }
}