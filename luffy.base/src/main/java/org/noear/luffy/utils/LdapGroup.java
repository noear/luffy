package org.noear.luffy.utils;

import lombok.Getter;
import lombok.Setter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ldap 分组
 *
 * @author noear
 * */
@Setter
@Getter
public class LdapGroup implements LdapNode {
    private String cn;
    private List<String> memberUid;

    @Override
    public void bind(Attributes attrs) throws NamingException {
        NamingEnumeration<String> keys = attrs.getIDs();

        while (keys.hasMore()) {
            String key = keys.next();
            Attribute val = attrs.get(key);

            if ("cn".equals(key)) {
                setCn(val.get().toString());
            } else if ("memberUid".equals(key)) {
                NamingEnumeration<String> vals = (NamingEnumeration<String>) val.getAll();

                setMemberUid(Collections.list(vals));
            }
        }

        if (memberUid == null) {
            memberUid = new ArrayList<>();
        }
    }

    public boolean hasMember(String member) {
        return memberUid.contains(member);
    }
}
