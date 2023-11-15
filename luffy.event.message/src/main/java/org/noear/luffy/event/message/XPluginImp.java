package org.noear.luffy.event.message;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.dso.JtFun;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.event.message.controller.MessageTask;
import org.noear.luffy.event.message.dso.DbMsgApi;
import org.noear.wood.DbContext;
import org.noear.wood.cache.ICacheServiceEx;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        JtFun.g.set("xbus_publish","发布消息#topic,content,delay?",1, DbMsgApi::msgPublish);
        JtFun.g.set("xbus_forward","转发消息，多级主题层层递进#topic,content,topic_source,delay?",1,DbMsgApi::msgRorward);

        Solon.app().sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        Solon.app().sharedGet("cache", (ICacheServiceEx cache)->{
            Config.cache = cache;
        });

        TaskFactory.register(new MessageTask());
    }
}
