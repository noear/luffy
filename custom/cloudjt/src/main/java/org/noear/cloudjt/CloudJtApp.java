package org.noear.cloudjt;

import org.noear.snack.core.Constants;
import org.noear.snack.core.Feature;
import org.noear.solon.XApp;
import org.noear.luffy.LuffyJT;
import org.noear.luffy.dso.JtUtilEx;
import org.noear.luffy.dso.PluginUtil;
import org.noear.luffy.utils.TextUtils;

public class CloudJtApp {
    public static void main(String[] args) {
        Constants.features_serialize = Feature.of(
                Feature.OrderedField,
                Feature.BrowserCompatible,
                Feature.WriteClassName,
                Feature.QuoteFieldNames);

//        WeedConfig.onException((cmd, err) -> {
//            err.printStackTrace();
//        });

        LuffyJT.start(CloudJtApp.class, args, () -> {
            String add = XApp.cfg().argx().get("add");
            String home = XApp.cfg().argx().get("home");
            String title = XApp.cfg().argx().get("title");

            String init = XApp.cfg().argx().get("init");

            //::0.安装插件
            PluginUtil.add(add);

            //::1.初始化调用
            PluginUtil.initCall(init);

            //::2.初始化配置
            if (TextUtils.isEmpty(home) == false) {
                //JtUtilEx.g2.rootSet(home);
                //PluginUtil.initCfg("upassport_jump_def", home);
            }

            if (TextUtils.isEmpty(title) == false) {
                PluginUtil.initCfg("_frm_admin_title", title + " of luffy");
                PluginUtil.initCfg("ucenter__title", title);
            }

            //::3.重启数据
            JtUtilEx.g2.restart();
        });
    }
}
