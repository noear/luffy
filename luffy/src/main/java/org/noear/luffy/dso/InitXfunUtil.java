package org.noear.luffy.dso;

public class InitXfunUtil {
    public static void init() {

        JtFun.g.set("log", "记录日志#tag,tag1?,tag2?,tag3?,tag4?,level?,summary?,content?,from?",
                (map) -> JtBridge.log(map));
        JtFun.g.set("cfg_get", "获取配置#name#{}", 1,
                (map) -> JtBridge.cfgMap((String) map.get("name")));

        JtFun.g.set("afile_get", "获取文件#path#AfileModel", 1,
                (map) -> JtBridge.fileGet((String) map.get("path")));
        JtFun.g.set("afile_get_paths", "获取文件路径#tag,label,useCache#AfileModel", 1,
                (map) -> JtBridge.fileFind((String) map.get("tag"), (String) map.get("label"), (Boolean) map.get("useCache")));
    }
}
