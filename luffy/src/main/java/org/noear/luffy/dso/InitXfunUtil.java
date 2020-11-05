package org.noear.luffy.dso;

public class InitXfunUtil {
    public static void init(){

        JtFun.g.set("log","记录日志#tag,tag1?,tag2?,tag3?,tag4?,level?,summary?,content?,from?",
                DbApi::log);
        JtFun.g.set("cfg_get","获取配置#name#{}",1,
                DbApi::cfgGetMap);

        JtFun.g.set("afile_get","获取文件#path#AfileModel",1,
                (map)-> AFileUtil.get((String) map.get("path")));
        JtFun.g.set("afile_get_paths","获取文件路径#tag,label,useCache#AfileModel",1,
                (map)-> DbApi.fileGetPaths((String) map.get("tag"), (String) map.get("label"),(Boolean) map.get("useCache")));
    }
}
