package org.noear.luffy.event.schedule;

import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.event.schedule.controller.ScheduleTask;
import org.noear.weed.DbContext;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        app.sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        TaskFactory.register(new ScheduleTask());
    }
}
