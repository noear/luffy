package org.noear.luffy.event.schedule;

import org.noear.solon.Solon;;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.event.schedule.controller.ScheduleTask;
import org.noear.weed.DbContext;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.sharedGet("db", (DbContext db)->{
            Config.db = db;
        });

        TaskFactory.register(new ScheduleTask());
    }
}
