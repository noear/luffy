package org.noear.cloudjt;

import org.noear.snack.core.Options;
import org.noear.snack.core.Feature;
import org.noear.solon.Solon;;
import org.noear.luffy.Luffy;
import org.noear.luffy.dso.JtUtilEx;
import org.noear.luffy.dso.PluginUtil;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.Utils;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ExtendStaticRepository;

public class CloudJtApp {
    public static void main(String[] args) {
        Options.features_serialize = Feature.of(
                Feature.OrderedField,
                Feature.BrowserCompatible,
                Feature.WriteClassName,
                Feature.QuoteFieldNames);

//        WoodConfig.onException((cmd, err) -> {
//            err.printStackTrace();
//        });

        Luffy.start(CloudJtApp.class, args, () -> {
            Solon.cfg().loadEnv("luffy.");

            String home = getArg("home");
            String title = getArg("title");


            //::安装插件
            PluginUtil.add(getArg("add"));
            //更新插件
            PluginUtil.udp(getArg("upd"));
            //移徐插件
            PluginUtil.rem(getArg("rem"));

            //::1.初始化调用
            PluginUtil.initCall(getArg("init"));

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

            //添加扩展静态目录支持
            StaticMappings.add("/", new ExtendStaticRepository());
        });
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
