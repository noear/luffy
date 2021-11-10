package org.noear.cloudjt;

import org.noear.snack.core.Options;
import org.noear.snack.core.Feature;
import org.noear.solon.Solon;;
import org.noear.luffy.Luffy;
import org.noear.luffy.dso.JtUtilEx;
import org.noear.luffy.dso.PluginUtil;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.core.NvMap;

public class CloudJtApp {
    public static void main(String[] args) {
        Options.features_serialize = Feature.of(
                Feature.OrderedField,
                Feature.BrowserCompatible,
                Feature.WriteClassName,
                Feature.QuoteFieldNames);

//        WeedConfig.onException((cmd, err) -> {
//            err.printStackTrace();
//        });

        Luffy.start(CloudJtApp.class, args, () -> {
            NvMap argx = Solon.cfg().argx();

            String home = argx.get("home");
            String title = argx.get("title");

            String init = argx.get("init");

            //::0.安装插件
            PluginUtil.add(argx.get("add"));
            //更新插件
            PluginUtil.udp(argx.get("upd"));
            //移徐插件
            PluginUtil.rem(argx.get("rem"));

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
