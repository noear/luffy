package org.noear.luffy.event.http;

import org.noear.luffy.dso.*;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.ExceptionUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 应用文件的代理，静态文件或动态文件（数据库安全）
 * */
public class AppHandler implements Handler {

    private static final String _lock = "";
    private static AppHandler _g = null;

    public static AppHandler g() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new AppHandler();
                }
            }
        }
        return _g;
    }


    @Override
    public void handle(Context ctx) throws Exception {
        do_handle(ctx.path(), ctx);
    }

    private void do_handle(String path, Context ctx) throws Exception {
        String path2 = AFileUtil.path2(path);
        String name = null;

        AFileModel file = null;

        //::先用路由工具做检测，防止数据库被恶意刷暴
        if (RouteHelper.has(path)) {
            file = AFileUtil.get(path);
            name = path.replace("/", "__");
        } else if (RouteHelper.has(path2)) {
            file = AFileUtil.get(path2);
            name = path2.replace("/", "__");
        }

        //文件不存在，则404
        if (file == null || file.file_id == 0) {
            ctx.status(404);
            return;
        }

        if (file.is_disabled) {
            ctx.status(403);
            return;
        }

        if (file.content_type != null && file.content_type.startsWith("code/")) {
            ctx.status(403);
            return;
        }

        //如果有跳转，则跳转
        if (TextUtils.isEmpty(file.link_to) == false) {
            if (file.link_to.startsWith("@")) {
                do_handle(file.link_to.substring(1), ctx);
            } else {
                ctx.redirect(file.link_to);
            }
            return;
        }

        //如果是静态
        if (file.is_staticize) {
            if (file.content == null) {
                ctx.status(404);
            } else {
                AFileStaticHandler.handle(ctx, path, file);
            }
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
}
