package luffy;

import org.junit.Test;
import org.noear.luffy.utils.LdapLoginUtils;
import org.noear.luffy.utils.LdapPerson;
import org.noear.solon.Utils;

import java.util.Properties;

/**
 * @author noear 2021/8/24 created
 */
public class LdapTest {
    @Test
    public void test() throws Exception{
        String cfg = "url=ldap://10.88.44.63:389\n" +
                "baseDn=DC=company,DC=com\n" +
                "groupCn=vpnuser\n" +
                "username=cn=admin,dc=company,dc=com\n" +
                "paasword=sKSaupjEChi72YGb";

        Properties props = Utils.buildProperties(cfg);

        LdapPerson user = LdapLoginUtils.ldapLogin(props,"xidong","rd81310.");

        System.out.println(user);

        assert user != null;
    }

    @Test
    public void test2() throws Exception{
        String cfg = "url=ldap://10.88.44.63:389\n" +
                "baseDn=DC=company,DC=com\n" +
                "username=cn=admin,dc=company,dc=com\n" +
                "paasword=sKSaupjEChi72YGb";

        Properties props = Utils.buildProperties(cfg);

        LdapPerson user = LdapLoginUtils.ldapLogin(props,"xidong","rd81310.");

        System.out.println(user);

        assert user != null;
    }

    @Test
    public void test3() throws Exception{
        String cfg = "url=ldap://10.88.44.63:389\n" +
                "baseDn=DC=company,DC=com\n" +
                "groupCn=manager\n" +
                "username=cn=admin,dc=company,dc=com\n" +
                "paasword=sKSaupjEChi72YGb";

        Properties props = Utils.buildProperties(cfg);

        LdapPerson user = LdapLoginUtils.ldapLogin(props,"xidong","rd81310.");

        System.out.println(user);

        assert user == null;
    }
}
