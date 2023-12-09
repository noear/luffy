package org.noear.luffy.dso;

import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.StringUtils;
import org.noear.luffy.utils.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class CallUtil {
    private static Logger log = LoggerFactory.getLogger(CallUtil.class);

    private static String getResolvedPath(String path) {
        if (path.startsWith("$")) {
            path = path.substring(1);
        }

        if (path.startsWith("/") == false) {
            //使用了相对路径
            String base = null;
            Context ctx = Context.current();
            if (ctx != null) {
                base = ctx.pathNew();
            }

            if (base != null) {
                path = Paths.get(base).resolveSibling(path).toString();
            }
        }

        return path;
    }

    private static Object callDo(String path, boolean outString) throws Exception {
        String path2 = path;//不用转换*
        String name = path2.replace("/", "__");

        AFileModel file = JtBridge.fileGet(path2);

        if (TextUtils.isNotEmpty(file.content)) {
            return ExecutorFactory.call(name, file, Context.current(), null, outString);
        } else {
            return "";
        }
    }

    /**
     * 调用文件，返回对象
     */
    public static Object callFile(String path, Map<String, Object> attrs) throws Exception {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        path = getResolvedPath(path);

        if (attrs != null && Context.current() != null) {
            Context.current().attrSet(attrs);
        }

        return callDo(path, false);
    }

    /**
     * 调用勾子，返回字符串。勾子调用不能出错，以免影响主业务
     */
    public static String callLabel(String tag, String label, boolean useCache, Map<String, Object> attrs) {
        if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(label)) {
            return "";
        }

        if (attrs != null && Context.current() != null) {
            Context.current().attrSet(attrs);
        }

        StringBuilder sb = StringUtils.borrowBuilder();

        try {
            List<AFileModel> list = JtBridge.fileFind(tag, label, useCache);

            for (AFileModel f : list) {
                try {
                    Object tmp = callDo(f.path, true);
                    if (tmp != null) {
                        sb.append(tmp).append("\r\n");
                    }
                } catch (Exception ex) {
                    log.warn("CallLabel exec error, file={}", f.path, ex);
                }
            }
        } catch (Exception ex) {
            log.warn("CallLabel exec error!", ex);
        }

        return StringUtils.releaseBuilder(sb);
    }
}
