package org.noear.luffy.event.schedule.dso;

import org.noear.luffy.model.AFileModel;;
import org.noear.luffy.event.schedule.Config;
import org.noear.luffy.utils.Datetime;;
import org.noear.weed.DbContext;

import java.sql.SQLException;
import java.util.List;

/**
 * 引擎基础的数据库处理接口
 * */
public class DbApi {
    private static DbContext db(){
        return Config.db;
    }



    public static void taskResetState() throws SQLException {
        db().table("a_file")
                .set("plan_state", 9)
                .where("plan_state=?", 2)
                .update();
    }

    public static List<AFileModel> taskGetList() throws Exception {
        return db().table("a_file")
                        .where("label=? AND is_disabled=0","task.plan")
                        .select("*")
                        .getList(AFileModel.class);
    }

    public static void taskSetState(AFileModel task, int state) throws SQLException {

        //对以天为进阶的任务，做同时间处理
        if (task._is_day_task && state == 9) {
            String s_d = new Datetime(task.plan_last_time).toString("yyyy-MM-dd");
            String s_t = new Datetime(task.plan_begin_time).toString("HH:mm:ss");

            try {
                Datetime temp = Datetime.parse(s_d + " " + s_t, "yyyy-MM-dd HH:mm:ss");
                task.plan_last_time = temp.getFulltime();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        db().table("a_file")
                .set("plan_state", state)
                .set("plan_count", task.plan_count)
                .set("plan_last_time", task.plan_last_time)
                .build((tb) -> {
                    if (task.plan_last_timespan > 0) {
                        tb.set("plan_last_timespan", task.plan_last_timespan);
                    }
                })
                .where("file_id=?", task.file_id)
                .update();
    }

}
