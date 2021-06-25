package org.noear.luffy.event.schedule.controller;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.luffy.dso.JtLock;
import org.noear.luffy.dso.LogLevel;
import org.noear.luffy.dso.LogUtil;
import org.noear.luffy.event.schedule.dso.DbApi;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.task.JtTaskBase;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.Timecount;

import java.util.Date;
import java.util.List;

/**
 * 定时任务处理器（数据库安全）
 * */
public class ScheduleTask extends JtTaskBase {
    public ScheduleTask() {
        super("_schedule", 1000);
    }

    private boolean _init = false;

    @Override
    public void exec() throws Exception {
        if (node_current_can_run() == false) {
            return;
        }

        //如果可运行，恢复为备份的时间间隔
        _interval = _interval_bak;


        if (_init == false) {
            _init = true;

            DbApi.taskResetState();
        }

        List<AFileModel> list = DbApi.taskGetList();

        for (AFileModel task : list) {
            if (JtLock.g.tryLock("luffy.event", getName() + "_" + task.file_id) == false) {
                continue;
            }

            poolExecute(() -> {
                doExec(task);
            });
        }
    }

    private void doExec(AFileModel task) {
        try {
            runTask(task);
        } catch (Throwable ex) {
            ex.printStackTrace();

            try {
                String err = ExceptionUtils.getString(ex);
                LogUtil.log(getName(), task.tag, task.path,  LogLevel.ERROR, "", err);
            } catch (Throwable ee) {
                ee.printStackTrace();
            }

            try {
                DbApi.taskSetState(task, 8);
            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }
    }

    private void runTask(AFileModel task) throws Exception {
        if (task == null || task.file_id == 0) {
            return;
        }

        //1.1.检查次数
        if (task.plan_max > 0 && task.plan_count >= task.plan_max) {
            return;
        }

        //1.2.检查重复间隔
        if (task.plan_interval == null || task.plan_interval.length() < 2) {
            return;
        }

        //1.3.检查是否在处理中
        if (task.plan_state == 2) {
            return;
        }

        //1.4.检查时间
        Date baseTime = task.plan_last_time;
        if (baseTime == null) {
            baseTime = task.plan_begin_time;
        }

        if (baseTime == null) {
            return;
        }

        //1.5.检查执行时间是否到了
        Date nextTime = null;
        if (task.plan_interval.length() > 7 && task.plan_interval.contains(" ")) {
            //说明是： cron
            nextTime = ScheduleHelper.getNextTimeByCron(task, baseTime);
        }else {
            //说明是：1s,1m,1h,1d,1M
            nextTime = ScheduleHelper.getNextTimeBySimple(task, baseTime);
        }

        //1.5.1.如果未到执行时间则反回
        if (System.currentTimeMillis() < nextTime.getTime()) {
            return;
        }

        //////////////////////////////////////////

        //2.执行
        do_runTask(task);
    }

    private void do_runTask(AFileModel task) throws Exception {
        //计时开始
        Timecount timecount = new Timecount().start();

        //开始执行::
        task.plan_last_time = new Date();
        DbApi.taskSetState(task, 2);

        Context ctx = ContextEmpty.create();
        ContextUtil.currentSet(ctx);

        //2.2.执行
        ExecutorFactory.execOnly(task, ctx);

        ContextUtil.currentRemove();


        //3.更新状态
        task.plan_count = task.plan_count + 1;

        //计时结束
        task.plan_last_timespan = timecount.stop().milliseconds();

        DbApi.taskSetState(task, 9);

        LogUtil.log(getName(), task.tag, task.path, task.file_id + "", LogLevel.TRACE, "", "执行成功");
    }
}
