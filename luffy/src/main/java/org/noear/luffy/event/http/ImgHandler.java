package org.noear.luffy.event.http;

import org.noear.luffy.dso.AImageHandler;
import org.noear.luffy.model.AImageModel;
import org.noear.luffy.dso.AImageUtil;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;

/**
 * 图片路径的代理（有可能会，数据库会被恶意刷暴了）
 * */
public class ImgHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        String path = ctx.path();

        AImageModel file = AImageUtil.get(path);

        //文件不存在，则404
        if (file == null || file.img_id == 0) {
            ctx.status(404);
            return;
        }

        //如果是静态
        if (file.data == null) {
            ctx.status(404);
        } else {
            AImageHandler.handle(ctx, file);
        }
    }
}
