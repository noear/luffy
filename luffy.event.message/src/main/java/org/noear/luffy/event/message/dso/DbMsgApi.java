package org.noear.luffy.event.message.dso;

import org.noear.luffy.dso.LogLevel;
import org.noear.luffy.dso.LogUtil;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.event.message.Config;
import org.noear.luffy.utils.Datetime;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.wood.DataItem;
import org.noear.wood.DataList;
import org.noear.wood.DbContext;
import org.noear.wood.DbTableQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DbMsgApi {
    private static DbContext db() {
        return Config.db;
    }

    public static AFileModel fileGet(String path) throws Exception {
        return db().table("a_file")
                .where("path=?", path)
                .select("*")
                .getItem(AFileModel.class);
    }

    public static List<AFileModel> msgGetSubs(String topic) throws Exception {
        return db().table("a_file")
                .where("label=? AND is_disabled=0", topic)
                .select("file_id,tag,label,path,is_disabled")
                .getList(AFileModel.class);
    }

    public static AMessageModel msgGet(long msg_id) throws Exception {
        AMessageModel m = db().table("a_message")
                .where("msg_id=? AND state=0", msg_id)
                .select("*")
                .getItem(AMessageModel.class);

        if (m.state != 0) {
            return null;
        } else {
            return m;
        }
    }

    public static List<Long> msgGetList(int rows, int ntime) throws SQLException {
        return
                db().table("a_message")
                        .where("state=0 AND dist_ntime<?", ntime)
                        .orderBy("msg_id ASC")
                        .limit(rows)
                        .select("msg_id")
                        .getArray("msg_id");
    }


    public static boolean msgSetState(long msg_id, int state) {
        return msgSetState(msg_id, state, 0);
    }

    public static boolean msgSetState(long msg_id, int state, int nexttime) {
        try {
            db().table("a_message")
                    .set("state", state)
                    .build(tb -> {
                        if (state == 0) {
                            int ntime = DisttimeUtil.nextTime(1);
                            tb.set("dist_ntime", ntime);
                            //可以检查处理中时间是否过长了？可手动恢复状态
                        }

                        if (nexttime > 0) {
                            tb.set("dist_ntime", nexttime);
                        }
                    })
                    .where("msg_id=? AND (state=0 OR state=1)", msg_id)
                    .update();

            return true;
        } catch (Exception ex) {
            //ex.printStackTrace();

            LogUtil.log("msg", "setMessageState", msg_id + "", LogLevel.ERROR, "", ExceptionUtils.getString(ex));

            return false;
        }
    }

    //设置消息重试状态（过几秒后再派发）
    public static boolean msgSetRepet(AMessageModel msg, int state) {
        try {
            msg.dist_count += 1;

            int ntime = DisttimeUtil.nextTime(msg.dist_count);

            db().table("a_message").usingExpr(true)
                    .set("state", state)
                    .set("dist_ntime", ntime)
                    .set("dist_count", "$dist_count+1")
                    .where("msg_id=? AND (state=0 OR state=1)", msg.msg_id)
                    .update();

            return true;
        } catch (SQLException ex) {
            //ex.printStackTrace();

            LogUtil.log("msg", "setMessageRepet", msg.msg_id + "", LogLevel.ERROR, "", ExceptionUtils.getString(ex));

            return false;
        }
    }

    public static void msgAddDistribution(long msg_id, AFileModel subs) throws SQLException {


        boolean isExists = db().table("a_message_distribution")
                .where("msg_id=?", msg_id).and("file_id=?", subs.file_id)
                .selectExists();

        if (isExists == false) {
            db().table("a_message_distribution").usingExpr(true)
                    .set("msg_id", msg_id)
                    .set("file_id", subs.file_id)
                    .set("receive_url", subs.path)
                    .set("receive_way", 0)
                    .set("log_date", Datetime.Now().getDate())
                    .set("log_fulltime", "$NOW()")
                    .insert();
        }
    }

    public static List<AMessageDistributionModel> msgGetDistributionList(long msg_id) throws Exception {
        return db().table("a_message_distribution")
                .where("msg_id=? AND (state=0 OR state=1)", msg_id)
                .select("*")
                .getList(AMessageDistributionModel.class);
    }

    //设置派发状态（成功与否）
    public static boolean msgSetDistributionState(long msg_id, AMessageDistributionModel dist, int state) {
        try {
            db().table("a_message_distribution")
                    .set("state", state)
                    .set("duration", dist._duration)
                    .where("msg_id=? and file_id=? and state<>2", msg_id, dist.file_id)
                    .update();

            return true;
        } catch (Exception ex) {
            //ex.printStackTrace();

            LogUtil.log("msg", "setDistributionState", msg_id + "", LogLevel.ERROR, "", ExceptionUtils.getString(ex));

            return false;
        }
    }

    //发布消息
    public static Object msgPublish(Map<String, Object> data) throws Exception {
        return msgAppend(data.get("topic"), data.get("content"), null, data.get("delay"));
    }

    //转发消息（会层层递进）
    public static Object msgRorward(Map<String, Object> data) throws Exception {
        if (data.containsKey("topic_source") == false) {
            return 0;
        }

        String topic = data.get("topic").toString();
        String content = data.get("content").toString();
        String topic_source = data.get("topic_source").toString();


        DataList list = db().table("a_file")
                .where("label LIKE ? AND is_disabled=0", topic_source + "-%").orderBy("rank ASC")
                .select("label")
                .caching(Config.cache).cacheTag("msg_topic_" + topic_source)
                .getDataList();

        if (list.getRowCount() == 0) {
            return 0;
        }

        if (topic.equals(topic_source)) {//如果主题与源相同，说明是第一次发出
            DataItem item = list.getRow(0);
            String ntopic = item.getString("label"); //下个可传递的主题

            msgAppend(ntopic, content, topic_source, data.get("delay"));
        } else {
            boolean is_do = false;
            for (DataItem item : list) {
                String ntopic = item.getString("label"); //下个可传递的主题

                if (is_do) {
                    if (topic.equals(ntopic) == false) { //找到下一个不同的主题
                        msgAppend(ntopic, content, topic_source, data.get("delay"));
                        break;
                    }
                } else {
                    if (topic.equals(ntopic)) {
                        is_do = true;
                    }
                }
            }
        }

        return 0;
    }

    private static Object msgAppend(Object topic, Object content, String topic_source, Object delay) throws Exception {
        DbTableQuery qr = db().table("a_message")
                .set("topic", topic)
                .set("content", content)
                .set("log_date", Datetime.Now().getDate())
                .set("log_fulltime", "$NOW()");

        if (topic_source != null) {
            qr.set("topic_source", topic_source);
        }

        if (delay != null) {
            int delay2 = Integer.parseInt(delay.toString());

            if (delay2 > 0) {
                int ntime2 = DisttimeUtil.nextTime(delay2);
                qr.set("dist_ntime", ntime2);
            }
        }

        return qr.insert();
    }

}
