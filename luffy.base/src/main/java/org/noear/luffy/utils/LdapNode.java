package org.noear.luffy.utils;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * Ldap 节点
 *
 * @author noear
 */
public interface LdapNode {
    String getCn();

    void bind(Attributes attrs) throws NamingException;
}
