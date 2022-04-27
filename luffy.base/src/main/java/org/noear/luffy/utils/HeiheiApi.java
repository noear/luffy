package org.noear.luffy.utils;


import org.noear.snack.ONode;
import org.noear.luffy.dso.LogLevel;
import org.noear.luffy.dso.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeiheiApi {

    private static final String apiUrl = "https://api.jpush.cn/v3/push";
    private static final String masterSecret = "4a8cd168ca71dabcca306cac";
    private static final String appKey = "af9a9da3c73d23aa30ea4af1";


    public static String push(List<String> alias, String text) {
        if (alias.size() == 0) {
            return null;
        }

        ONode data = new ONode().build((d) -> {
            d.getOrNew("platform").val("all");

            d.getOrNew("audience").getOrNew("alias").addAll(alias);

            d.getOrNew("options")
                    .set("apns_production", true);

            d.getOrNew("notification").build(n -> {
                n.getOrNew("android")
                        .set("alert", text);

                n.getOrNew("ios")
                        .set("alert", text)
                        .set("badge", 0)
                        .set("sound", "happy");
            });

            d.getOrNew("message").build(n -> {
                n.set("msg_content", text);
            });
        });


        String message = data.toJson();
        String author = Base64Utils.encode(appKey + ":" + masterSecret);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Basic " + author);

        String rest = null;

        try {
            rest = HttpUtils.http(apiUrl).timeout(10).headers(headers).bodyTxt(message, "text/plain").post();
        } catch (Exception ex) {
            //ex.printStackTrace();
            LogUtil.log("heihei", LogLevel.ERROR, ExceptionUtils.getString(ex));
        }

        if (text.startsWith("报警") == false && text.startsWith("恢复") == false) {
            LogUtil.log("heihei", LogLevel.ERROR, text);
        }

        return rest;
    }
}