package org.noear.luffy.event.schedule;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.event.schedule.controller.ScheduleTask;
import org.noear.wood.DbContext;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        Solon.app().sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        TaskFactory.register(new ScheduleTask());
    }
}
