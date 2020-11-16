package org.noear.luffy.trick.extend.sited;

import org.noear.luffy.trick.extend.sited.dao.DbSiteDApi;
import org.noear.luffy.trick.extend.sited.dao.DbUtil;
import org.noear.luffy.trick.extend.sited.dao.Utils;
import org.noear.luffy.trick.extend.sited.dao.custom.DdApi;
import org.noear.luffy.trick.extend.sited.dao.custom.DdSource;
import org.noear.luffy.trick.extend.sited.utils.*;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.weed.DbContext;

import org.noear.weed.cache.ICacheServiceEx;


public class eSiteD {

    @Note("url转为data形式")
    public String urlData(String uri, String host) {
        return Base64Util.encode(Utils.addinUrl(uri, host));
    }

    @Note("DB上下文对象")
    public DbContext db() {
        return DbUtil.db();
    }

    @Note("缓存对象")
    public ICacheServiceEx cache() {
        return DbUtil.cache();
    }

    @Note("插件源码解密")
    public String codeDecode(String xml) {
        if (xml.startsWith("sited::")) {
            int start = xml.indexOf("::") + 2;
            int end = xml.lastIndexOf("::");
            String txt = xml.substring(start, end);
            String key = xml.substring(end + 2);
            xml = DdApi.unsuan(txt, key);
        }

        return xml;
    }

    @Note("插件源码加密")
    public String codeEncode(String xml) throws Exception {
        if (xml.startsWith("sited::")) {
            return xml;
        } else {
            String key2 = "TIME" + Datetime.Now().toString("yyMMddHHMMsss");

            String txt = Utils.addinEncode(xml, key2);
            String txt2 = "sited::" + txt + "::" + key2;

            return txt2;
        }
    }

    @Note("设置插件源码，返回guid")
    public String sourceSet(String puid, UploadedFile file, String path) throws Exception {
        String xml = IOUtil.stream2String(file.content);
        return sourceSet(puid, path, xml, 1);
    }

    @Note("设置插件源码，返回guid")
    public String sourceSet(String puid, String path, String content, int is_ok) throws Exception {
        DdSource sd = null;

        try {
            sd = new DdSource(content);
        } catch (Exception ex) {
            throw new Exception("插件格式解析出错::" + ex.toString());
        }

        if (sd.engine == 0) {
            throw new Exception("engine引擎版本号错误");
        }

        if (TextUtils.isEmpty(sd.author)) {
            throw new Exception("author格式错误");
        }

        if (TextUtils.isEmpty(sd.intro)) {
            throw new Exception("intro内容太少");
        }

        int puid2 = Integer.parseInt(puid);

        sd.guid = puid + "_" + EncryptUtil.md5(sd.url);

        if(sd.isPrivate()){
            is_ok = 0;
        }

        DbSiteDApi.saveSiteD(puid2, sd, path, is_ok);

        return sd.guid;
    }
}
