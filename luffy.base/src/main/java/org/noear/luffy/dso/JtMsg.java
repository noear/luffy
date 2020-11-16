package org.noear.luffy.dso;

import org.noear.snack.core.exts.ThData;
import org.noear.solon.annotation.Note;

import java.util.HashMap;
import java.util.Map;

@Note("消息总线接口")
public class JtMsg {
    public static final JtMsg g  = new JtMsg();

    static ThData<Map<String,Object>> th_map= new ThData<>(()->new HashMap<>());

    @Note("转发消息")
    public boolean forward(String topic, Object content, String topic_source) throws Exception{
        if(topic == null || content == null){
            return false;
        }

        Map<String,Object> data = th_map.get();
        data.clear();

        data.put("topic",topic);
        data.put("content",content.toString());
        data.put("topic_source",topic_source);

        return JtFun.g.call("xbus_forward", data) != null;
    }
    @Note("转发消息")
    public boolean forward(Map<String,Object> data) throws Exception{
        return JtFun.g.call("xbus_forward", data) != null;
    }

    @Note("发布消息")
    public boolean publish(String topic, Object content) throws Exception{
        if(topic == null || content == null){
            return false;
        }

        Map<String,Object> data = th_map.get();
        data.clear();

        data.put("topic",topic);
        data.put("content",content.toString());

        return JtFun.g.call("xbus_publish", data) != null;
    }

    @Note("发布消息")
    public boolean publish(Map<String,Object> data) throws Exception {
        return JtFun.g.call("xbus_publish", data) != null;
    }
}
