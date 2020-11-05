package org.noear.luffy.dso;

import org.noear.snack.core.exts.ThData;
import org.noear.luffy.utils.ConfigUtils;
import org.noear.luffy.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CfgUtil {
    static ThData<Map<String,Object>> th_map= new ThData<>(()->new HashMap<>());

    //获取配置值
    public static String cfgGetValue(String name) throws Exception {
        return JtBridge.cfgGet(name);
    }

    public static Properties cfgGetProp(String name) throws Exception{
        String tmp = cfgGetValue(name);

        if(TextUtils.isEmpty(tmp)){
            return null;
        }

        return ConfigUtils.getProp(tmp);
    }
}
