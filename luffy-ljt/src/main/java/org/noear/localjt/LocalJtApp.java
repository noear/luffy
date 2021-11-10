package org.noear.localjt;

import org.noear.localjt.dso.WebShell;
import org.noear.snack.core.Options;
import org.noear.snack.core.Feature;
import org.noear.solon.Solon;
import org.noear.luffy.Luffy;
import org.noear.luffy.dso.JtUtilEx;
import org.noear.luffy.dso.PluginUtil;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;
import org.noear.weed.WeedConfig;

public class LocalJtApp{

    public static String home;
    public static String title;
    public static String plugin_add;

    public static void main(String[] args) {
        Options.features_serialize = Feature.of(
                Feature.OrderedField,
                Feature.BrowserCompatible,
                Feature.WriteClassName,
                Feature.QuoteFieldNames);

        WeedConfig.onException((cmd, err) -> {
            System.out.println(cmd.toSqlString());
            err.printStackTrace();
        });

        SolonApp app = Luffy.start(LocalJtApp.class, args,()->{
            Solon.cfg().loadEnv("luffy.");

            if("2".equals(getArg("model")) == false) {
                //
                //server: 0,个人app；1,个人网站；2,多人网站
                //
                Solon.global().sharedAdd("__luffy_standalone_model", 1);
            }

            //::添加插件
            plugin_add = getArg("add");
            PluginUtil.add(plugin_add);
            //更新插件
            PluginUtil.udp(getArg("upd"));
            //移徐插件
            PluginUtil.rem(getArg("rem"));

            //::1.初始化调用
            PluginUtil.initCall(getArg("init"));

            //::2.重启数据
            JtUtilEx.g2.restart();
        });

        app.onError((err) -> {
            err.printStackTrace();
        });

        NvMap argx = app.cfg().argx();


        //主页
        home = argx.get("home");

        if (TextUtils.isEmpty(home)) {
            home = "http://localhost:" + app.port() + "/.admin/?_L0n5=1CE24B1CF36B0C5B94AACE6263DBD947FFA53531";
        } else {
            home = "http://localhost:" + app.port() + home;
        }

        //::2.标题
        title = argx.get("title");
        if (TextUtils.isEmpty(title)) {
            title = "LocalJt";
        }

        if(argx.getInt("model") == 0) {
            new Thread(() -> {
                WebShell.start(args);
            }).start();
        }else{
            //尝试浏览器打开
            if (java.awt.Desktop.isDesktopSupported()) {
                try {
                    // 创建一个URI实例
                    java.net.URI uri = java.net.URI.create(home);
                    // 获取当前系统桌面扩展
                    java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                    // 判断系统桌面是否支持要执行的功能
                    if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                        // 获取系统默认浏览器打开链接
                        dp.browse(uri);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取启动参数
     *
     * @param name 参数名
     */
    private static String getArg(String name) {
        //尝试去启动参数取
        String tmp = Solon.cfg().argx().get(name);
        if (Utils.isEmpty(tmp)) {
            //如果为空，尝试从属性配置取
            tmp = Solon.cfg().get("luffy." + name);
        }

        return tmp;
    }
}
