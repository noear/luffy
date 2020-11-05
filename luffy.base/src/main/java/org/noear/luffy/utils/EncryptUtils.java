package org.noear.luffy.utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class EncryptUtils {
    private static final char[] _hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 生成sha1码
     */
    public static String sha1(String cleanData) {
        return sha1(cleanData, "UTF-8");
    }

    public static String sha1(String cleanData, String chaerset) {
        return hashEncode("SHA-1", cleanData, chaerset);
    }

    /**
     * 生成sha256码
     */
    public static String sha256(String cleanData) {
        return sha256(cleanData, "UTF-8");
    }

    public static String sha256(String cleanData, String chaerset) {
        return hashEncode("SHA-256", cleanData, chaerset);
    }

    /**
     * 生成md5码
     */
    public static String md5(String cleanData) {
        return md5(cleanData, "UTF-8");
    }

    public static String md5(String cleanData, String chaerset) {
        return hashEncode("MD5", cleanData, chaerset);
    }

    public static String md5Bytes(byte[] bytes) {
        try {
            return do_hashEncode("MD5", bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String hashEncode(String algorithm, String cleanData, String chaerset) {

        try {
            byte[] btInput = cleanData.getBytes(chaerset);
            return do_hashEncode(algorithm, btInput);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String do_hashEncode(String algorithm, byte[] btInput) throws Exception {
        MessageDigest mdInst = MessageDigest.getInstance(algorithm);
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;

        for (int i = 0; i < j; ++i) {
            byte byte0 = md[i];
            str[k++] = _hexDigits[byte0 >>> 4 & 15];
            str[k++] = _hexDigits[byte0 & 15];
        }

        return new String(str);
    }

    //
    // aesEncrypt , aesDecrypt
    //
    public static String aesEncrypt(String content, String password) {
        return aesEncrypt(content, password, null);
    }

    public static String aesEncrypt(String content, String password, String algorithm) {
        return aesEncrypt(content, password, algorithm, null);
    }

    public static String aesEncrypt(String content, String password, String algorithm, String offset) {
        return aesEncrypt(content, password, algorithm, offset, null);
    }

    public static String aesEncrypt(String content, String password, String algorithm, String offset, String charset) {
        try {
            if (TextUtils.isEmpty(algorithm)) {
                algorithm = "AES/ECB/PKCS5Padding";
            }

            if (TextUtils.isEmpty(charset)) {
                charset = "UTF-8";
            }

            byte[] pswd = password.getBytes(charset);
            SecretKeySpec secretKey = new SecretKeySpec(pswd, "AES");
            Cipher cipher = Cipher.getInstance(algorithm);
            if (TextUtils.isEmpty(offset)) {
                cipher.init(1, secretKey);
            } else {
                IvParameterSpec iv = new IvParameterSpec(offset.getBytes(charset));
                cipher.init(2, secretKey, iv);
            }

            byte[] encrypted = cipher.doFinal(content.getBytes(charset));
            return Base64Utils.encodeByte(encrypted);//new Base64()).encodeToString(encrypted);
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    public static String aesDecrypt(String content, String password) {
        return aesDecrypt(content, password, null);
    }

    public static String aesDecrypt(String content, String password, String algorithm) {
        return aesDecrypt(content, password, algorithm, null);
    }

    public static String aesDecrypt(String content, String password, String algorithm, String offset) {
        return aesDecrypt(content, password, algorithm, offset, null);
    }

    public static String aesDecrypt(String content, String password, String algorithm, String offset, String charset) {
        try {
            if (TextUtils.isEmpty(algorithm)) {
                algorithm = "AES/ECB/PKCS5Padding";
            }

            if (TextUtils.isEmpty(charset)) {
                charset = "UTF-8";
            }

            byte[] pswd = password.getBytes(charset);
            SecretKey secretKey = new SecretKeySpec(pswd, "AES");

            //密码
            Cipher cipher = Cipher.getInstance(algorithm);
            if (TextUtils.isEmpty(offset)) {
                cipher.init(2, secretKey);
            } else {
                IvParameterSpec iv = new IvParameterSpec(offset.getBytes(charset));
                cipher.init(2, secretKey, iv);
            }

            byte[] encrypted1 = Base64Utils.decodeByte(content); //(new Base64()).decode(content);
            byte[] original = cipher.doFinal(encrypted1);

            return new String(original, charset);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static byte[] hmac(String data, String key, String algorithm, String charset) {
        if(algorithm == null){
            algorithm = "HmacSHA256";
        }

        if(TextUtils.isEmpty(charset)){
            charset = "UTF-8";
        }

        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithm);
            mac.init(keySpec);
            return mac.doFinal(data.getBytes(charset));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toX16(byte[] bytes){
        int j = bytes.length;
        char[] str = new char[j * 2];
        int k = 0;

        for (int i = 0; i < j; ++i) {
            byte byte0 = bytes[i];
            str[k++] = _hexDigits[byte0 >>> 4 & 15];
            str[k++] = _hexDigits[byte0 & 15];
        }

        return new String(str);
    }

    public static String toX64(byte[] bytes){
        return Base64Utils.encodeByte(bytes);
    }
}
