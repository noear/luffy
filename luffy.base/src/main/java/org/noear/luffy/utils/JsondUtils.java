package org.noear.luffy.utils;

import org.noear.snack.ONode;

import java.io.IOException;

/**
 * @author noear 2021/7/15 created
 */

public class JsondUtils {
    private static final String key = "j$6gxA^KJgBiOgco";

    public static String encode(String table, Object data) throws IOException {
        ONode node = new ONode();
        node.set("table", table);
        node.set("data", data);

        //序列化
        String json = node.toJson();

        //压缩
        String gzip = GzipUtils.gZip(json);

        //加密
        try {
            return EncryptUtils.aesEncrypt(gzip, key);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public static String decode(String jsonD) throws IOException {
        try {
            //解密
            String gzip = EncryptUtils.aesDecrypt(jsonD, key);

            //解压
            String json = GzipUtils.unGZip(gzip);

            //反序列化
            return json;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}
