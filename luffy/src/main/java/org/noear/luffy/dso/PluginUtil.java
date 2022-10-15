package org.noear.luffy.dso;

import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.GzipUtils;
import org.noear.snack.ONode;
import org.noear.solon.Solon;;
import org.noear.luffy.Config;
import org.noear.luffy.utils.Base64Utils;
import org.noear.luffy.utils.HttpUtils;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.noear.wood.DataItem;
import org.noear.wood.DataList;
import org.noear.wood.DbContext;
import org.noear.wood.wrap.DbType;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public class PluginUtil {
    private static DbContext db(){
        return DbUtil.db();
    }

    /**
     * 初始化根页
     * */
    public static void initRoot(String path) {
        try {
            db().table("a_file").set("link_to", path).whereEq("path", "/").update();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 初始化配置
     * */
    public static void initCfg(String key, String val){
        try {
            JtUtil.g.cfgSet(key, val);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 初始化调用
     * */
    public static void initCall(String path) {
        try {
            //运行本地库的初始化
            if (TextUtils.isNotEmpty(path)) {
                CallUtil.callFile(path, JtUtil.g.empMap());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            //运行扩展目录下的初始化
            File location = ExtendUtil.directory();

            if (location != null) {
                File initFile = new File(location, "_init.js");

                if (initFile.exists()) {
                    try (FileInputStream ins = new FileInputStream(initFile)) {
                        String initCode = Utils.transferToString(ins, "utf-8");
                        System.out.println("load _init.js: " + initFile.getPath());

                        AFileModel file = new AFileModel();

                        file.content = initCode;
                        file.path = "/__jt/" + initFile.getName();
                        file.tag = "luffy";

                        file.edit_mode = "javascript";

                        ExecutorFactory.execOnly(file, new ContextEmpty());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 安装插件
     * */
    public static boolean install(String packageTag) {
        try {
            return do_install(packageTag, true);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 重装插件
     * */
    public static  boolean reinstall(String packageTag) {
        try {
            return do_install(packageTag, false);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void uninstall(String packageTag){
        if(TextUtils.isEmpty(packageTag)){
            return;
        }

        String tag = packageTag.split("\\.")[0];

        try {

            //1.删除引擎相关
            db().exe("DELETE FROM a_config WHERE tag=?", tag);
            db().exe("DELETE FROM a_file WHERE tag=?", tag);
            db().exe("DELETE FROM a_image WHERE tag=?", tag);

            db().exe("UPDATE a_plugin SET is_installed=0 WHERE plugin_tag=? ", packageTag);
            db().exe("DELETE FROM a_plugin WHERE plugin_tag=? AND url='' ", packageTag);

            List<String> dtmp = db().sql("SHOW TABLES").getArray(0);

            //2.删除业务相关
            for(String stb: dtmp){
                if(stb==tag || stb.startsWith(tag+'_')){
                    db().exe("DROP TABLE "+stb);
                }
            }

            //3.重启(清空所有缓存)
            JtUtilEx.g2.restart();

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 添加插件
     * */
    public static void add(String adds){
        if (TextUtils.isEmpty(adds) == false) {
            String[] ss = adds.split(",");
            for (String packageTag : ss) {
                PluginUtil.install(packageTag);
            }
        }
    }

    /**
     * 添加插件
     * */
    public static void udp(String adds){
        if (TextUtils.isEmpty(adds) == false) {
            String[] ss = adds.split(",");
            for (String packageTag : ss) {
                PluginUtil.reinstall(packageTag);
            }
        }
    }

    /**
     * 删除插件
     * */
    public static void rem(String rems){
        if (TextUtils.isEmpty(rems) == false) {
            String[] ss = rems.split(",");
            for (String packageTag : ss) {
                PluginUtil.uninstall(packageTag);
            }
        }
    }

    private static boolean do_install(String packageTag, boolean onlyInstall) throws Exception{
        if(TextUtils.isEmpty(packageTag)){
            return false;
        }

        if(onlyInstall) {
            if (db().getMetaData().getTable("a_plugin") != null) {
                if (db().table("a_plugin").whereEq("plugin_tag", packageTag).selectExists()) {
                    return false;
                }
            }
        }

        boolean is_ok = _installDo(packageTag);

        if(is_ok) {
            String tag = packageTag.split("\\.")[0];

            List<String> depList = dependencyGetDo(tag);
            for (String dep : depList) {
                do_install_dependency(dep);
            }

            JtUtilEx.g2.restart();
        }

        return is_ok;
    }

    private static void do_install_dependency(String packageTag) throws Exception{
        String tag = packageTag.split("\\.")[0];

        if(db().table("a_plugin").where("tag=? AND is_installed=1",tag).selectExists()){
            return;
        }

        _installDo(packageTag);
    }

    private static List<String> dependencyGetDo(String tag) throws Exception{
        return db().table("a_config")
                .where("tag=? AND label=?",tag,"dep.plugin")
                .select("value")
                .getDataList()
                .toArray(0);
    }

    private static boolean _installDo(String packageTag) throws Exception{


        String center = Solon.cfg().get(Config.code_center);
        if (TextUtils.isEmpty(center)) {
            center = Solon.cfg().argx().get("center");
        }

        if (TextUtils.isEmpty(center)) {
            return false;
        }


        String url = null;

        if(center.indexOf("://")>0){
            url =  center + "/.plugin/pull.jsx?_fapk=1&_gzip=1&plugin_tag=" + packageTag;
        }else {
            url = "http://" + center + "/.plugin/pull.jsx?_fapk=1&_gzip=1&plugin_tag=" + packageTag;
        }


        String json =  HttpUtils.http(url).asLongHttp().get();

        //支持zip传递
        if(json.startsWith("{") == false){
            json = GzipUtils.unGZip(json);
        }

        ONode data = ONode.load(json);
        if (data.get("code").getInt() != 1) {
            return false;
        }


        ONode body = data.get("data").get("body");
        ONode meta = data.get("data").get("meta");

        String plugin_tag = meta.get("plugin_tag").getString();
        String tag = plugin_tag.split("\\.")[0];


        String p_config = body.get("config").getString();
        String p_menu = body.get("menu").getString();
        String p_file = body.get("file").getString();
        String p_img = body.get("image").getString();

        ONode p_table = body.get("dbtable");


        //2.1.配置表
        if(TextUtils.isEmpty(p_config) ==false) {
            //获取需要排除的配置
            List<String> pcec = db().table("a_config").where("tag=? AND is_exclude=1",tag).select("name").getArray(0);

            //删掉不需要排除的配置
            db().table("a_config").where("tag=? AND is_exclude=0",tag).delete();

            String pcfg = Base64Utils.decode(p_config);
            List<Map<String, Object>> pcfg_d = ONode.deserialize(pcfg, List.class);

            for (Map<String, Object> m : pcfg_d) {
                String name = (String) m.get("name");
                Boolean is_modified = (Boolean) m.get("is_modified");

                if(pcec.contains(name) == false){
                    //插入不存在的
                    m.remove("cfg_id");
                    db().table("a_config").setMap(m).insert();
                }else if(is_modified == true) {
                    //
                    //如果用户可改的，同步编辑类型和提示
                    //
                    if (m.get("edit_type") == null) {
                        m.put("edit_type", "");
                    }

                    if (m.get("edit_placeholder") == null) {
                        m.put("edit_placeholder", "");
                    }

                    if (m.get("note") == null) {
                        m.put("note", "");
                    }

                    db().table("a_config").set("edit_type", m.get("edit_type"))
                            .set("edit_placeholder", m.get("edit_placeholder"))
                            .set("note", m.get("note"))
                            .whereEq("name", name)
                            .update();
                }

            }

            if (db().getType() == DbType.H2) {
                db().table("a_config")
                        .set("value", "LocalJt")
                        .whereEq("name", "_frm_admin_title")
                        .update();
            }
        }

        //2.2.菜单表
        if(TextUtils.isEmpty(p_menu) == false) {
            List<String> pmec = db().table("a_menu").where("tag=? AND is_exclude=1", tag).select("concat(tag,label,url) name").getArray(0);

            db().table("a_menu").where("tag=? AND is_exclude=0", tag).delete();

            String pmenu = Base64Utils.decode(p_menu);
            List<Map<String, Object>> pmenu_d = ONode.deserialize(pmenu, List.class);

            if (pmenu_d != null) {
                for (Map<String, Object> m : pmenu_d) {
                    String pm_name = (m.get("tag") + "" + m.get("label") + m.get("url"));

                    if(pmec.contains(pm_name) == false) {
                        m.remove("menu_id");
                        db().table("a_menu").setMap(m).insert();
                    }
                }
            }
        }


        //2.3.文件表
        if(TextUtils.isEmpty(p_file) == false){

            List<String> pfec = db().table("a_file").where("tag=? AND is_exclude=1",tag).select("path").getArray(0);

            db().table("a_file").where("tag=? AND is_exclude=0",tag).delete();

            String pfile = Base64Utils.decode(p_file);
            List<Map<String, Object>> pfile_d = ONode.deserialize(pfile, List.class);

            if(pfile_d != null) {
                for (Map<String, Object> m : pfile_d) {
                    String path = (String) m.get("path");

                    if(pfec.contains(path) == false) {
                        m.remove("file_id");
                        if (m.get("content") != null) {
                            String c2 = Base64Utils.decode(m.get("content").toString());
                            m.put("content", c2);
                        }
                        db().table("a_file").setMap(m).insert();
                    }
                }
            }
        }


        //2.4.资源表
        if(TextUtils.isEmpty(p_img) == false){
            db().table("a_image").whereEq("tag",tag).delete();

            String pimg = Base64Utils.decode(p_img);
            List<Map<String, Object>> pimg_d = ONode.deserialize(pimg, List.class);

            if (pimg_d != null) {
                for (Map<String, Object> m : pimg_d) {
                    m.remove("img_id");
                    db().table("a_image").setMap(m).insert();
                }
            }
        }

        //3.业务表结构
        if(p_table.isObject()) {
            for (ONode n : p_table.obj().values()) {
                String v = n.getString();
                v = "CREATE TABLE IF NOT EXISTS " + Base64Utils.decode(v).substring(12);
                db().exe(v);
            }
        }

        //4.安装完成
        {
            db().table("a_plugin").set("is_installed", 0).whereEq("tag", tag).update();
            db().table("a_plugin")
                    .set("plugin_tag", meta.get("plugin_tag").getString())
                    .set("tag", tag)
                    .set("name", meta.get("name").getString())
                    .set("author", meta.get("author").getString())
                    .set("contacts", meta.get("contacts").getString())
                    .set("ver_name", meta.get("ver_name").getString())
                    .set("ver_code", meta.get("ver_code").getString())
                    .set("description", meta.get("description").getString())
                    .set("thumbnail", meta.get("thumbnail").getString())
                    .set("category", meta.get("category").getString())
                    .set("is_installed", 1)
                    .upsertBy("plugin_tag");
        }

        //5.下载a_image表的jar包
        {
            int jar_num = 0;
            DataList pjar_d = db().table("a_image")
                    .whereEq("tag",tag)
                    .andEq("label","dep.jar")
                    .andEq("content_type","application/java-archive")
                    .select("path,data,data_md5,note")
                    .getDataList();

            for(DataItem m : pjar_d){
                String m_path = m.getString("path");
                String m_data = m.getString("data");
                String m_data_md5 = m.getString("data_md5");
                String m_note = m.getString("note");

                if(JtUtilEx.g2.loadJar(m_path, m_data, m_data_md5, m_note)){
                    jar_num++;
                }
            }
        }

        //重启(清空所有缓存)//不然勾子，可能会有缓存
        JtUtilEx.g2.restart();

        return true;
    }
}
