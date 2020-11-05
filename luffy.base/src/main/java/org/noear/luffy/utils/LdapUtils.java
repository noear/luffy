package org.noear.luffy.utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.Map;

public class LdapUtils {
    private Map<String, Object> auth(String password, String userName) throws NamingException {
        String bindLdapUser = "root"; // 连接LDAP服务器的管理员账号密码
        String bindLdapPwd = "123456";
        String ldapBaseDN = "ou=People,o=test"; // 这个根据自己需要更改
        String attrName = "password";   // 获取DN下面的属性名
        String port = "389"; // ldap 服务器占用的端口号
        String ldapIp = "192.168.120.222"; // ldap 服务器的IP地址

        String url = "ldap://" + ldapIp + ":" + port + '/' + ldapBaseDN;
        System.console().printf("[auth ldap] ldap url : " + url);

        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_CREDENTIALS, bindLdapPwd);
        env.put(Context.SECURITY_PRINCIPAL, bindLdapUser);
        env.put(Context.PROVIDER_URL, url);
        DirContext ctx = null;

        try {
            // Ldap link
            ctx = new InitialDirContext(env);
            System.console().printf("[auth ldap linked] InitialDirContext success");

            // Ldap search
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = "sid" + userName;// 加上filter之后DN就变成sid={userName},ou=People,o=test
            NamingEnumeration<?> nameEnu = ctx.search("", filter, searchControls);
            System.console().printf("[step1 ladp linked] begin to search, filter :" + filter);

            if (nameEnu == null) {
                // User Not Found
                System.console().printf("[step error] DirContext.search() return null. filter : " + filter);
                return createResult(104, 2001, "User Not Found");
            } else {
                System.console().printf("[step2 ldap] Begin to print elements");
                String ldapUserPwd = null;

                while (nameEnu.hasMore()) {
                    System.console().printf("[step3 ldap] nameEnu element is not null");
                    Object obj = nameEnu.nextElement();
                    System.console().printf(obj.toString());
                    if (obj instanceof SearchResult) {
                        System.console().printf("[step4 ldap] obj instanceof SearchResult");
                        SearchResult result = (SearchResult) obj;
                        Attributes attrs = result.getAttributes();
                        if (attrs == null) {
                            // Password Error
                            System.console().printf("[step error] SearchResult.getAttrbutes() is null. ");
                            return createResult(102, 4003, "Password Error");
                        } else {
                            System.console().printf("[step5 ldap] begin to get attribute : " + attrName);   // attrName就是属性名
                            Attribute attr = attrs.get(attrName);
                            if (attr != null) {
                                System.console().printf("[step6 ldap] attribute is not null.");
                                ldapUserPwd = (String) attr.get();
                                System.console().printf("[step7 print ldapPwd] the ldap password is : *********");
                                System.console().printf("[step7 print ldapPwd] the request password is : *********");
                                if (password.equalsIgnoreCase(ldapUserPwd)) {
                                    // OK
                                    System.console().printf("[step8 ldap] equals password , success .");
                                    return createResult(0, 0, "OK");
                                } else {
                                    // Password Error
                                    System.console().printf("[step9 ldap] equals password , failure . password is not same .");
                                    return createResult(102, 4003, "Password Error");
                                }
                            }
                        }
                    }
                }
                System.console().printf("[step error] while end . ldapUserPwd is null , can't find password from ldap .");
                return createResult(102, 4003, "Password Error");
            }
        } catch (NamingException e) {
            e.printStackTrace();
            System.console().printf("[ldap link failed] message :" + e.getMessage());
            throw e;
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    private Map<String, Object> createResult(int ret, int code, String msg) {
        Map<String, Object> result = new Hashtable<>(); //WmsvrFactory.createParamsObj();
        result.put("ret", ret);
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }
}
