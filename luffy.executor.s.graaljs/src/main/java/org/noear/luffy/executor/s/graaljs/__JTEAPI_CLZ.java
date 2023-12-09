package org.noear.luffy.executor.s.graaljs;

import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;

import java.nio.file.Paths;
import java.util.Map;

public class __JTEAPI_CLZ {
    public String getResolvedPath(String path) {
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

    public String require(String path) throws Exception {
        path = getResolvedPath(path);

        String name = path.replace("/", "__");
        String name2 = name.replace(".", "_") + "__lib";

        AFileModel file = ExecutorFactory.fileGet(path);

        GraaljsJtExecutor.singleton().preLoad(name2, file);

        return name2;
    }

    public Object modelAndView(String path, Map<String, Object> model) throws Exception {
        String path2 = path;//AFileUtil.path2(path);//不用转为*
        String name = path2.replace("/", "__");

        AFileModel file = ExecutorFactory.fileGet(path2);

        if (file.file_id > 0) {
            return ExecutorFactory.call(name,file, Context.current(),model,true);
        } else {
            return "";
        }
    }
}
