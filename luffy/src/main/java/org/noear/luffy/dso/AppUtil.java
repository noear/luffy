package org.noear.luffy.dso;

import org.noear.snack.ONode;
import org.noear.luffy.Config;
import org.noear.luffy.SolonJT;
import org.noear.luffy.event.http.AppHandler;
import org.noear.luffy.event.http.FrmInterceptor;
import org.noear.luffy.event.http.ImgHandler;
import org.noear.luffy.event.http.SufHandler;
import org.noear.luffy.task.TaskFactory;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.IOUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.XApp;
import org.noear.solon.core.XHandler;
import org.noear.solon.core.XHandlerLink;
import org.noear.solon.core.XMethod;
import org.noear.weed.WeedConfig;

import java.net.URL;

/** 应用协助控制工具 */
public class AppUtil {
    /**
     * 初始化数据库和内核
     * */
    public static void init(XApp app, boolean initDb){
        if(initDb) {
            DbUtil.setDefDb(app.prop().getXmap(Config.code_db));
        }

        InitUtil.tryInitDb();
        InitUtil.tryInitCore(app);
        InitUtil.tryInitNode(app);
    }


    public static void runAsInit(XApp app, String extend) {
        URL temp = org.noear.solon.XUtil.getResource("setup.htm");
        String html = null;
        try {
            html = IOUtils.toString(temp.openStream(), "utf-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        final String node2 = app.prop().argx().get("node");
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

                app.prop().argx().putAll(ctx.paramMap());

                AppUtil.init(app, false);

                String _usr = JtUtil.g.cfgGet("_frm_admin_user");
                String _pwd = JtUtil.g.cfgGet("_frm_admin_pwd");
                String _token  = JtUtil.g.sha1(_usr+'#'+_pwd, "UTF-16LE").toUpperCase();

                ONode rst = new ONode();
                rst.set("code",1);
                rst.set("token",_token);
                rst.set("home",XApp.cfg().argx().get("home"));

                ctx.outputAsJson(rst.toJson());

                //new Thread(() -> {
                    XApp.global().router().clear();
                    AppUtil.runAsWork(XApp.global());
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
    public static void runAsWork(XApp app) {
        String sss = app.prop().argx().get("sss");

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
        do_weedTrack();

        //CallUtil.callLabel(null, "hook.start", false, null);

        //5.加载完成事件
        SolonJT.onLoad();

        //6.执行完后，运行勾子
        CallUtil.callLabel(null, "hook.start", false, null);
    }

    private static void do_runWeb(XApp app) {
        //拦截代理
        app.before("**", XMethod.HTTP, FrmInterceptor.g());

        //资源代理(/img/**)
        app.get(Config.frm_root_img + "**", new ImgHandler());

        //文件代理
        app.http("**", AppHandler.g());

        //后缀代理（置于所有代理的前面）
        XHandler h1 = app.handlerGet();
        XHandlerLink hx = new XHandlerLink();
        hx.node = SufHandler.g();
        hx.nextNode = h1;

        app.handlerSet(hx);
    }

    private static void do_runSev(XApp app){
        TaskFactory.run(TaskRunner.g);
    }

    private static void do_weedTrack(){
        WeedConfig.onException((cmd, ex) -> {
            if (cmd.text.indexOf("a_log") < 0 && cmd.isLog >= 0) {
                System.out.println(cmd.text);
                LogUtil.log("weed", "err_log",LogLevel.ERROR, "出错", cmd.text + "<br/><br/>" + ExceptionUtils.getString(ex));
            }
        });

        WeedConfig.onExecuteAft((cmd)->{
            if(cmd.isLog<0){
                return;
            }

            if(cmd.timespan()>1000){
                LogUtil.log("weed", "slow_log",LogLevel.WARN, cmd.timespan()+"ms", cmd.text);
            }
        });
    }
}
