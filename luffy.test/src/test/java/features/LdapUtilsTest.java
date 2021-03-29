package features;

import org.junit.Test;
import org.noear.luffy.utils.LdapUser;
import org.noear.luffy.utils.LdapUtils;
import org.noear.snack.ONode;

import javax.naming.ldap.LdapContext;

/**
 * @author noear 2021/3/18 created
 */
public class LdapUtilsTest {
    @Test
    public void test() throws Exception {
        String url = "ldap://127.0.0.1:389";
        String basedn = "DC=company,DC=com";  // basedn
        String root = "cn=admin,dc=company,dc=com";  // 用户
        String pwd = "sKSaupjEChi72YGb";  // pwd

        LdapContext ctx = LdapUtils.ldapConnect(url, root, pwd);//集团 ldap认证

        LdapUser user = LdapUtils.ldapAuth(ctx, basedn, "uid=noear", "1234");//获取集团ldap中用户信息

        if(user == null){
            System.out.println("验证失败");
        }else{
            System.out.println(ONode.stringify(user));
        }

        if (ctx != null)
            ctx.close();
    }
}
