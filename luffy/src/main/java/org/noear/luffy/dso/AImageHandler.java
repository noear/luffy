package org.noear.luffy.dso;

import org.noear.luffy.model.AImageModel;
import org.noear.luffy.utils.Base64Utils;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;

import java.net.URLEncoder;
import java.util.Date;

/**
 * 资源文件代理（如：图片，视频，音频等资源）
 * */
public class AImageHandler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";


    public static void handle(Context context, AImageModel file) throws Exception {
        String path = context.path();

        context.setHandled(true);

        String modified_since = context.header("If-Modified-Since");
        String modified_now = app_runtime.toString();

        if (modified_since != null) {
            if (modified_since.equals(modified_now)) {
                context.headerSet(CACHE_CONTROL, "max-age=6000");
                context.headerSet(LAST_MODIFIED, modified_now);
                context.contentType(file.content_type);
                context.charset("utf-8");
                context.status(304);
                return;
            }
        }


        byte[] data = Base64Utils.decodeByte(file.data);

        int idx = path.lastIndexOf(".");
        if (idx > 0) {
            String mime = file.content_type;

            if (Utils.isNotEmpty(mime)) {
                context.headerSet(CACHE_CONTROL, "max-age=6000");
                context.headerSet(LAST_MODIFIED, app_runtime.toString());
                context.contentType(file.content_type);
                context.charset("utf-8");


                if (Utils.isNotEmpty(file.note)) {
                    String fileName = URLEncoder.encode(file.note, Solon.encoding());
                    context.headerSet("Content-Disposition", "filename=\"" + fileName + "\"");
                }
            }

            DownloadedFile downloadedFile = new DownloadedFile(mime, data, null);
            context.outputAsFile(downloadedFile);
        } else {
            context.status(200);
            context.output(data);
        }
    }

    private static final Date app_runtime = new Date();
}