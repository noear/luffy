package org.noear.usopp.extend.sited.utils;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class Base64Util {
    private static final Charset _coder = StandardCharsets.UTF_8;

    public static String encode(String text){
        return encodeByte(text.getBytes(_coder));
    }

    public static String decode(String code){
        return new String(decodeByte(code), _coder);
    }

    public static String encodeByte(byte[] data){
        return new String( Base64.getEncoder().encode(data), _coder);
    }

    public static byte[] decodeByte(String code){
        return Base64.getDecoder().decode(code.getBytes(_coder));
    }
}