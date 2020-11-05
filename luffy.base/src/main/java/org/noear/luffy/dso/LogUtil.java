package org.noear.luffy.dso;


import java.util.HashMap;
import java.util.Map;

public class LogUtil {
    public static boolean log(Map<String,Object> data) {
        JtBridge.log(data);
        return true;
    }
    public static boolean log(String tag, LogLevel level,String content){
        return log(tag, null, null, null,null, level, null, content);
    }
    public static boolean log(String tag, LogLevel level,String summary,String content) {
        return log(tag, null, null, null,null, level, summary, content);
    }

    public static boolean log(String tag, String tag1, LogLevel level,String summary,String content) {
        return log(tag, tag1, null, null, null,level, summary, content);
    }

    public static boolean log(String tag, String tag1, String tag2,LogLevel level,String summary,String content) {
        return log(tag, tag1, tag2, null, null,level, summary, content);
    }

    public static boolean log(String tag, String tag1, String tag2, String tag3,LogLevel level,String summary,String content) {
        return log(tag, tag1, tag2, tag3, null,level, summary, content);
    }

    public static boolean log(String tag, String tag1,String tag2, String tag3, String tag4,LogLevel level,String summary,String content) {
        Map<String, Object> map = new HashMap<>();

        map.put("level", level.code);

        if (tag != null) {
            map.put("tag", tag);
        }

        if (tag1 != null) {
            map.put("tag1", tag1);
        }

        if (tag2 != null) {
            map.put("tag2", tag2);
        }

        if (tag3 != null) {
            map.put("tag3", tag3);
        }

        if (tag4 != null) {
            map.put("tag4", tag4);
        }

        if (summary != null) {
            map.put("summary", summary);
        }

        if (content != null) {
            map.put("content", content);
        }
        
        map.put("from", JtBridge.executorAdapter().nodeId());

        JtBridge.log(map);
        return true;
    }
}
