package org.noear.luffy.utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.security.MessageDigest;
import java.util.*;

/**
 * Ldap 工具类
 * */
public class LdapUtils {
    /**
     * 获取ldap链接上下文
     *
     * @param url
     * @param bindLdapUser
     * @param bindLdapPwd
     * @return LdapContext
     */
    public static LdapContext ldapConnect(String url, String bindLdapUser, String bindLdapPwd) throws NamingException {
        String factory = "com.sun.jndi.ldap.LdapCtxFactory";
        String simple = "simple";

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, simple);
        env.put(Context.SECURITY_PRINCIPAL, bindLdapUser);
        env.put(Context.SECURITY_CREDENTIALS, bindLdapPwd);
        Control[] connCtls = null;

        return new InitialLdapContext(env, connCtls);
    }

    /**
     * 获取用户信息
     *
     * @param ctx
     * @param baseDn
     * @return LdapUser
     */
    public static LdapUser ldapAuth(LdapContext ctx, String baseDn, String userFilter, String userPassword) throws NamingException {

        if (ctx != null) {
            String userPassword2 = LdapUtils.buildLdapPassword(userPassword);


            //过滤条件
            String filter = "(&(objectClass=inetOrgPerson)(" + userFilter + "))";
            String[] attrPersonArray = {"uid", "userPassword", "displayName", "cn", "sn", "mail", "description"};
            SearchControls searchControls = new SearchControls();//搜索控件
            searchControls.setSearchScope(2);//搜索范围
            searchControls.setReturningAttributes(attrPersonArray);
            //1.要搜索的上下文或对象的名称；2.过滤条件，可为null，默认搜索所有信息；3.搜索控件，可为null，使用默认的搜索控件
            NamingEnumeration<SearchResult> answer = ctx.search(baseDn, filter.toString(), searchControls);
            while (answer.hasMore()) {
                SearchResult result = (SearchResult) answer.next();
                NamingEnumeration<? extends Attribute> attrs = result.getAttributes().getAll();
                LdapUser lu = new LdapUser();
                while (attrs.hasMore()) {
                    Attribute attr = (Attribute) attrs.next();
                    if ("userPassword".equals(attr.getID())) {
                        Object value = attr.get();
                        lu.setUserPassword(new String((byte[]) value));
                    } else if ("uid".equals(attr.getID())) {
                        lu.setUid(attr.get().toString());
                    } else if ("displayName".equals(attr.getID())) {
                        lu.setDisplayName(attr.get().toString());
                    } else if ("cn".equals(attr.getID())) {
                        lu.setCn(attr.get().toString());
                    } else if ("sn".equals(attr.getID())) {
                        lu.setSn(attr.get().toString());
                    } else if ("mail".equals(attr.getID())) {
                        lu.setMail(attr.get().toString());
                    } else if ("description".equals(attr.getID())) {
                        lu.setDescription(attr.get().toString());
                    }
                }
                if (lu.getUid() != null && (
                        userPassword2.equals(lu.getUserPassword()) ||
                                userPassword.equals(lu.getUserPassword())
                )) {
                    return lu;
                }

            }
        }

        return null;
    }

    /**
     * 构建ldap密码
     *
     * @param orgPwd 原始密码
     * */
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