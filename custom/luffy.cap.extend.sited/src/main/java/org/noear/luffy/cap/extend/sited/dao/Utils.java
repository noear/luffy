package org.noear.luffy.cap.extend.sited.dao;

import org.noear.luffy.cap.extend.sited.utils.Base64Util;
import org.noear.luffy.cap.extend.sited.utils.TextUtils;
import org.noear.solon.core.handle.Context;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String condition(Context ctx) {

        int isVip = ctx.paramAsInt("u_vip", 0);;

        if (isVip > 0)
            return "is_ok = 1";
        else
            return "is_ok = 1 AND is_app = 1";
    }

    public static String addinUrl(String uri, String root) {
        if(uri.indexOf("://")>0){
            return uri;
        }else{
            return root + uri;
        }
    }

    //str:明文（加密前的内容）
    //key:私钥
    //return:密文
    public static String addinEncode(String str, String key)  throws Exception{
        str = Base64Util.encode(str);
        key = key + "ro4w78Jx";

        Charset coder = Charset.forName("utf-8");

        byte[] data = str.getBytes(coder);
        byte[] keyData = key.getBytes(coder);
        int keyIndex = 0;

        for (int x = 0; x < data.length; x++) {
            data[x] = (byte)(data[x] ^ keyData[keyIndex]);
            if (++keyIndex == keyData.length) {
                keyIndex = 0;
            }
        }

        String temp = new String(data,coder);
        temp = Base64Util.encode(temp);

        StringBuilder sb = new StringBuilder();
        int j = 0;

        for (int i = 0, len = temp.length(); i < len; i++) {
            sb.append(temp.charAt(i));
            sb.append(key.charAt(j));
            if (++j == key.length()) {
                j = 0;
            }
        }

        return sb.toString();
    }

    public static boolean isMatch(String url, String expr){
        if(TextUtils.isEmpty(expr)==false){
            Pattern pattern = Pattern.compile(expr);
            Matcher m = pattern.matcher(url);

            return m.find();
        }else {
            return false;
        }
    }
}
