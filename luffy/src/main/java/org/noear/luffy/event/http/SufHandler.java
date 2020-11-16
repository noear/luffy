package org.noear.luffy.event.http;

import org.noear.luffy.Config;
import org.noear.luffy.dso.*;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;

import java.util.HashMap;
import java.util.List;

/**
 * 文件后缀拦截器的代理（数据库安全）
 * */
public class SufHandler implements Handler {
    private static final String _lock = "";
    private static  SufHandler _g = null;
    public static SufHandler g(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new SufHandler();
                }
            }
        }
        return  _g;
    }



    private SufHandler(){
        reset();
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String path = ctx.path();
        for (String suf : _cacheMap.keySet()) {
            if (path.endsWith(suf)) {
                ctx.setHandled(true);

                exec(ctx, _cacheMap.get(suf));
                return;
            }
        }
    }

    private void exec(Context ctx, String path) throws Exception {
        String path2 = path;//AFileUtil.path2(path);//不需要转为*
        String name = path2.replace("/", "__");

        AFileModel file = AFileUtil.get(path2);

        //文件不存在，则404
        if (file.file_id == 0) {
            ctx.status(404);
            return;
        }

        //不支持后缀代理，跳过
        if (Config.filter_file.equals(file.label) == false) {
            return;
        }

        try {
            ExecutorFactory.exec(name, file, ctx);
        } catch (Throwable ex) {
            String error = ExceptionUtils.getString(ex);
            LogUtil.log("_file", file.tag, file.path, LogLevel.ERROR, "", error);
            ctx.status(500);
            ctx.output(error);
        }
    }



    private HashMap<String,String> _cacheMap = new HashMap<>();
    public void del(String note) {
        if (TextUtils.isEmpty(note)) {
            return;
        }

        String suf = note.split("#")[0];
        if (suf.length()>0) {
            if (suf.startsWith(".")) {
                _cacheMap.remove(suf);
            } else {
                _cacheMap.remove("." + suf);
            }
        }
    }

    public void add(String path, String note){
        if(TextUtils.isEmpty(note)){
            return;
        }

        String suf = note.split("#")[0];

        if (suf.length()>0) {
            if (suf.startsWith(".")) {
                _cacheMap.put(suf, path);
            } else {
                _cacheMap.put("." + suf, path);
            }
        }
    }

    public void reset() {
        if (DbUtil.db() == null) {
            return;
        }

        try {
            _cacheMap.clear();

            List<AFileModel> list = DbApi.fileFilters();
            for (AFileModel c : list) {
                if(TextUtils.isEmpty(c.note)){
                    continue;
                }

                String suf = c.note.split("#")[0];

                if (suf.length()>0) {
                    if (suf.startsWith(".")) {
                        _cacheMap.put(suf, c.path);
                    } else {
                        _cacheMap.put("." + suf, c.path);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
