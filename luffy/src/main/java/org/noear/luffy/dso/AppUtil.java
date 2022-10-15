package org.noear.luffy.dso;

import org.noear.snack.ONode;
import org.noear.luffy.Config;
import org.noear.luffy.Luffy;
import org.noear.luffy.event.http.AppHandler;
import org.noear.luffy.event.http.FrmInterceptor;
import org.noear.luffy.event.http.ImgHandler;
import org.noear.luffy.event.http.SufHandler;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.IOUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.HandlerPipeline;
import org.noear.solon.core.handle.MethodType;
import org.noear.wood.WoodConfig;

import java.net.URL;

/** 应用协助控制工具 */
public class AppUtil {
    /**
     * 初始化数据库和内核
     * */
    public static void init(SolonApp app, boolean initDb){
        if(initDb) {
            DbUtil.setDefDb(app.cfg().getXmap(Config.code_db));
        }

        InitUtil.tryInitDb();
        InitUtil.tryInitCore(app);
        InitUtil.tryInitNode(app);
    }


    public static void runAsInit(SolonApp app, String extend) {
        URL temp = Utils.getResource("setup.htm");
        String html = null;
        try {
            html = IOUtils.toString(temp.openStream(), "utf-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        final String node2 = app.cfg().argx().get("node");
        final String html2 = html.trim();

        app.post("/setup.jsx", (ctx) -> {
            if (node2 != null && node2.length() > 30) {
                ctx.paramMap().put("node", node2);
            } else {
                ctx.paramMap().put("node", JtUtil.g.guid());
            }

            DbUtil.setDefDb(ctx.paramMap());
            try {
                DbUtil.db().sql("SHOW TABLES").execute();

                InitUtil.trySaveConfig(extend, ctx.paramMap());

                app.cfg().argx().putAll(ctx.paramMap());

                AppUtil.init(app, false);

                String _usr = JtUtil.g.cfgGet("_frm_admin_user");
                String _pwd = JtUtil.g.cfgGet("_frm_admin_pwd");
                String _token  = JtUtil.g.sha1(_usr+'#'+_pwd, "UTF-16LE").toUpperCase();

                ONode rst = new ONode();
                rst.set("code",1);
                rst.set("token",_token);
                rst.set("home",Solon.cfg().argx().get("home"));

                ctx.outputAsJson(rst.toJson());

                //new Thread(() -> {
                    Solon.global().router().clear();
                    AppUtil.runAsWork(Solon.global());
                //}).start();
            } catch (Throwable ex) {
                ctx.outputAsJson(new ONode()
                        .set("code",0)
                        .set("msg", ExceptionUtils.getString(ex)).toJson()
                );
            }

        });

        app.get("/", (ctx) -> {
            ctx.outputAsHtml(html2);
        });

        app.get("/**", (ctx) -> {
            ctx.redirect("/");
        });
    }

    /**
     * 运行应用
     * */
    public static void runAsWork(SolonApp app) {
        String sss = app.cfg().argx().get("sss");

        /*
         * 注入共享对传（会传导到javascript 和 freemarker 引擎）
         * */
        app.sharedAdd("db", DbUtil.db());
        app.sharedAdd("cache", DbUtil.cache);
        app.sharedAdd("localCache", DbUtil.cache);

        //2.尝试运行WEB应用
        if (TextUtils.isEmpty(sss) || sss.indexOf("web") >= 0) {
            RouteHelper.reset();

            do_runWeb(app);
        }

        //3.尝试运行SEV应用（即定时任务）
        if (TextUtils.isEmpty(sss) || sss.indexOf("sev") >= 0) {
            do_runSev(app);
        }

        //4.踪跟WEED异常
        do_woodTrack();

        //CallUtil.callLabel(null, "hook.start", false, null);

        //5.加载完成事件
        Luffy.onLoad();

        //6.执行完后，运行勾子
        CallUtil.callLabel(null, "hook.start", false, null);
    }

    private static void do_runWeb(SolonApp app) {
        //拦截代理
        app.before("**", MethodType.HTTP, FrmInterceptor.g());

        //资源代理(/img/**)
        app.get(Config.frm_root_img + "**", new ImgHandler());

        //文件代理
        app.http("**", AppHandler.g());

        //后缀代理（置于所有代理的前面）
        HandlerPipeline hx = new HandlerPipeline()
                .next(SufHandler.g())
                .next(app.handlerGet());

        app.handlerSet(hx);
    }

    private static void do_runSev(SolonApp app){
        TaskFactory.run(TaskRunner.g);
    }

    private static void do_woodTrack(){
        WoodConfig.onException((cmd, ex) -> {
            if (cmd.text.indexOf("a_log") < 0 && cmd.isLog >= 0) {
                System.out.println(cmd.text);
                LogUtil.log("wood", "err_log",LogLevel.ERROR, "出错", cmd.text + "<br/><br/>" + ExceptionUtils.getString(ex));
            }
        });

        WoodConfig.onExecuteAft((cmd)->{
            if(cmd.isLog<0){
                return;
            }

            if(cmd.timespan()>1000){
                LogUtil.log("wood", "slow_log",LogLevel.WARN, cmd.timespan()+"ms", cmd.text);
            }
        });
    }
}
