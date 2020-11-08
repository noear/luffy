package org.noear.luffy.trick.extend.sited.utils;

import java.security.MessageDigest;

public class EncryptUtil {

    public static String sha1(String cleanData) {
        return hashEncode("SHA-1", cleanData);
    }
    /** 生成md5码 */
    public static String md5(String cleanData) {
        return hashEncode("MD5", cleanData);
    }

    private static String hashEncode(String algorithm, String cleanData) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] btInput = cleanData.getBytes("UTF-16LE");
            MessageDigest mdInst = MessageDigest.getInstance(algorithm);
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for(int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var11) {
            var11.printStackTrace();
            return null;
        }
    }
}
