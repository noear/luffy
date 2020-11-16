package org.noear.luffy.dso;

import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.StringUtils;
import org.noear.luffy.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallUtil {

    private static Object callDo(String path, boolean outString) throws Exception {
        String path2 = path;//不用转换*
        String name = path2.replace("/", "__");

        Map<String,Object> map  =new HashMap<>();
        map.put("path",path2);

        AFileModel file = (AFileModel) JtFun.g.call("afile_get",map);

        if (file.file_id > 0 && TextUtils.isEmpty(file.content) == false) {
            return ExecutorFactory.call(name, file, Context.current(), null, outString);
        } else {
            return "";
        }
    }

    /**
     * 调用文件，返回对象
     * */
    public static Object callFile(String path, Map<String,Object> attrs) throws Exception {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        if (attrs != null && Context.current() != null) {
            Context.current().attrSet(attrs);
        }

        return callDo(path, false);
    }

    /**
     * 调用勾子，返回字符串。勾子调用不能出错，以免影响主业务
     */
    public static String callLabel(String tag, String label, boolean useCache, Map<String,Object> attrs) {
        if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(label)) {
            return "";
        }

        if (attrs != null && Context.current() != null) {
            Context.current().attrSet(attrs);
        }

        StringBuilder sb = StringUtils.borrowBuilder();

        try {
            Map<String,Object> map = new HashMap<>();
            map.put("tag",tag);
            map.put("label",label);
            map.put("useCache",useCache);

            List<AFileModel> list = JtFun.g.callT("afile_get_paths",map);

            list.forEach((f) -> {
                        try {
                            Object tmp = callDo(f.path, true);
                            if (tmp != null) {
                                sb.append(tmp.toString()).append("\r\n");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return StringUtils.releaseBuilder(sb);
    }
}
