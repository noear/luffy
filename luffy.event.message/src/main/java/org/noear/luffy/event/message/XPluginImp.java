package org.noear.luffy.event.message;

import org.noear.solon.Solon;;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.luffy.dso.JtFun;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.event.message.controller.MessageTask;
import org.noear.luffy.event.message.dso.DbMsgApi;
import org.noear.weed.DbContext;
import org.noear.weed.cache.ICacheServiceEx;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        JtFun.g.set("xbus_publish","发布消息#topic,content,delay?",1, DbMsgApi::msgPublish);
        JtFun.g.set("xbus_forward","转发消息，多级主题层层递进#topic,content,topic_source,delay?",1,DbMsgApi::msgRorward);

        app.sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        app.sharedGet("cache", (ICacheServiceEx cache)->{
            Config.cache = cache;
        });

        TaskFactory.register(new MessageTask());
    }
}
