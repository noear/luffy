package org.noear.luffy.executor.s.lua;

import org.luaj.vm2.LuaTable;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;

import java.util.Map;

public  class __JTEAPI_CLZ {
    public String require(String path) throws Exception {
        String name = path.replace("/", "__");
        String name2 = name.replace(".", "_") + "__lib";

        AFileModel file = ExecutorFactory.fileGet(path);

        LuaJtExecutor.singleton().preLoad(name2, file);

        return name2;
    }

    public Object modelAndView(String path, LuaTable tb) throws Exception {
        String path2 = path;//AFileUtil.path2(path);//不用转为*
        String name = path2.replace("/", "__");

        AFileModel file = ExecutorFactory.fileGet(path2);

        if (file.file_id > 0) {
            Map<String, Object> model = (Map<String, Object>) LuaUtil.tableToObj(tb);
            return ExecutorFactory.call(name, file, Context.current(), model, true);
        } else {
            return "";
        }
    }
}