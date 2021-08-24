package org.noear.luffy.utils;


import lombok.Getter;
import lombok.Setter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * Ldap 人员
 *
 * @author noear
 * */
@Setter
@Getter
public class LdapPerson implements LdapNode {
    public String uid;
    public String cn;
    public String sn;
    public String userPassword;
    public String displayName;
    public String mail;
    public String description;
    public String uidNumber;
    public String gidNumber;

    @Override
    public void bind(Attributes attrs) throws NamingException {
        NamingEnumeration<String> keys = attrs.getIDs();

        while (keys.hasMore()) {
            String key = keys.next();
            Attribute val = attrs.get(key);

            if ("uid".equals(key)) {
                setUid(val.get().toString());
            } else if ("cn".equals(key)) {
                setCn(val.get().toString());
            } else if ("sn".equals(key)) {
                setSn(val.get().toString());
            } else if ("userPassword".equals(key)) {
                setUserPassword(new String((byte[]) val.get()));
            } else if ("displayName".equals(key)) {
                setDisplayName(val.get().toString());
            } else if ("mail".equals(key)) {
                setMail(val.get().toString());
            } else if ("description".equals(key)) {
                setDescription(val.get().toString());
            }
        }
    }

    @Override
    public String toString() {
        return "LdapUser{" +
                "uid='" + uid + '\'' +
                ", cn='" + cn + '\'' +
                ", sn='" + sn + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", displayName='" + displayName + '\'' +
                ", mail='" + mail + '\'' +
                ", description='" + description + '\'' +
                ", uidNumber='" + uidNumber + '\'' +
                ", gidNumber='" + gidNumber + '\'' +
                '}';
    }
}
