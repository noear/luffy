package org.noear.luffy.dso;

import net.coobird.thumbnailator.Thumbnails;
import org.noear.luffy.Config;
import org.noear.luffy.event.http.FrmInterceptor;
import org.noear.luffy.event.http.SufHandler;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AImageModel;
import org.noear.luffy.utils.*;
import org.noear.solon.annotation.Note;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 引擎扩展工具，提供一些基础的操作支持
 * */
public class JtUtilEx extends JtUtil {
    public static final JtUtilEx g2 = new JtUtilEx();

    @Note("设置根路径链接")
    public void rootSet(String path) {
        PluginUtil.initRoot(path);
    }

    @Note("获取菜单")
    public List<Map<String, Object>> menuGet(String label, int pid) throws Exception {
        return DbApi.menuGet(label, pid);
    }

    @Note("获取菜单")
    public List<Map<String, Object>> menuGet(String label) throws Exception {
        return DbApi.menuGet(label, -1);
    }

    /**
     * 保存图片
     */
    @Note("保存图片")
    public String imgSet(UploadedFile file) throws Exception {
        return imgSet(file, file.extension);
    }

    @Note("保存图片")
    public String imgSet(UploadedFile file, String tag, String dir, int name_mod) throws Exception {
        String extension = file.extension;
        byte[] data_byte = IOUtils.toBytes(file.content);
        String data = Base64Utils.encodeByte(data_byte);
        StringBuilder path = StringUtils.borrowBuilder();

        if (name_mod == 0) {
            //自动
            tag = null;
            path.append("/img/");
            path.append(guid());
            path.append(".");
            path.append(extension);
        } else {
            //保持原名
            path.append("/img/");

            if (TextUtils.isEmpty(tag) == false) {
                path.append(tag).append("/");
            }

            if (TextUtils.isEmpty(dir) == false) {
                path.append(dir).append("/");
            }

            path.append(file.name);
        }

        String path2 = StringUtils.releaseBuilder(path).replace("//", "/");

        DbApi.imgSet(tag, path2, file.contentType, data, "");

        return path2;
    }

    /**
     * 保存图片（后缀名可自定义）
     */
    @Note("保存图片（后缀名可自定义）")
    public String imgSet(UploadedFile file, String extension) throws Exception {
        byte[] data_byte = IOUtils.toBytes(file.content);
        String data = Base64Utils.encodeByte(data_byte);
        String path = "/img/" + guid() + "." + extension;

        DbApi.imgSet(null, path, file.contentType, data, "", file.name);

        return path;
    }

    /**
     * 保存图片（内容，类型，后缀名可自定义）
     */
    @Note("保存图片（内容，类型，后缀名可自定义）")
    public String imgSet(String content, String contentType, String extension) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + guid() + "." + extension;

        DbApi.imgSet(null, path, contentType, data, "");

        return path;
    }

    @Note("保存图片（内容，类型，名字，后缀名可自定义）")
    public String imgSet(String content, String contentType, String extension, String name) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + name + "." + extension;

        DbApi.imgSet(null, path, contentType, data, "");

        return path;
    }

    @Note("保存图片（内容，类型，名字，后缀名，标签可自定义）")
    public String imgSet(String content, String contentType, String extension, String name, String label) throws Exception {
        String data = Base64Utils.encode(content);
        String path = "/img/" + name + "." + extension;

        DbApi.imgSet(null, path, contentType, data, label);

        return path;
    }


    @Note("设定图片输出名称")
    public String imgOutName(Context ctx, String filename) throws Exception {
        String filename2 = URLEncoder.encode(filename, "utf-8");

        ctx.headerSet("Content-Disposition", "attachment; filename=\"" + filename2 + "\"");
        return filename;
    }

    /**
     * 修改图片
     */
    @Note("修改图片")
    public String imgUpd(String path, String content) throws Exception {
        String data = Base64Utils.encode(content);

        DbApi.imgUpd(path, data);

        return path;
    }

    /**
     * 获取图片内容
     */
    @Note("获取图片内容(string)")
    public String imgGet(String path) throws Exception {
        AImageModel img = DbApi.imgGet(path);
        return img2String(img.data);
    }

    @Note("获取图片内容(byte[])")
    public byte[] imgGetBytes(String path) throws Exception {
        AImageModel img = DbApi.imgGet(path);

        if (TextUtils.isEmpty(img.data)) {
            return null;
        } else {
            return Base64Utils.decodeByte(img.data);
        }

    }


    @Note("图片内容转为字符串")
    public String img2String(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        } else {
            return Base64Utils.decode(data);
        }
    }

    @Note("获取文件内容")
    public String fileGet(String path) throws Exception{
        return DbApi.fileGet(path).content;
    }

    /**
     * 新建文件
     */
    @Note("文件新建")
    public boolean fileNew(int fid, Context ctx) throws Exception {
        return DbApi.fileNew(fid, ctx);
    }

    /**
     * 文件设置内容
     */
    @Note("文件设置内容")
    public boolean fileSet(int fid, String fcontent) throws Exception {
        return DbApi.fileSet(fid, fcontent);
    }

    @Note("文件刷新缓存")
    public boolean fileFlush(String path, boolean is_del) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String name = path.replace("/", "__");

        if (is_del) {
            RouteHelper.del(path);
        } else {
            RouteHelper.add(path);
        }

        AFileUtil.remove(path);
        ExecutorFactory.del(name);
        return true;
    }

    @Note("文件刷新缓存")
    public boolean fileFlush(String path, boolean is_del, String label, String note) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String path2 = path;
        String name = path2.replace("/", "__");

        AFileUtil.remove(path2);
        ExecutorFactory.del(name);

        //应用路由
        if (is_del) {
            RouteHelper.del(path);
        } else {
            RouteHelper.add(path);
        }

        //后缀拦截器
        if (Config.filter_file.equals(label)) {
            if (is_del) {
                SufHandler.g().del(note);//文件后缀，只能有一个代理；所以用suf
            } else {
                SufHandler.g().add(path, note);
            }
        }

        //路径拦截器
        if (Config.filter_path.equals(label)) {
            if (is_del) {
                FrmInterceptor.g().del(path);//文件路径，可以有多个；所以用path
            } else {
                FrmInterceptor.g().add(path, note);
            }
        }

        return true;
    }

    @Note("重启缓存")
    public boolean restart() {
        AFileUtil.removeAll();
        AImageUtil.removeAll();

        ExecutorFactory.delAll();

        DbUtil.cache.clear();

        SufHandler.g().reset();
        FrmInterceptor.g().reset();

        RouteHelper.reset();
        return true;
    }


    @Note("获取扩展目录下的文件")
    public List<Map<String, Object>> extendList() {
        return ExtendUtil.scan();
    }

    @Note("删除扩展目录下的文件")
    public boolean extendDel(String name) {
        return ExtendUtil.del(name);
    }


    @Note("创建缩略图工具")
    public Object thumbnailOf(InputStream stream) {
        return Thumbnails.of(stream);
    }


    @Note("加载插件里的jar包")
    public boolean loadJar(String path, String data64, String data_md5, String plugin) throws Exception {
        return JarUtil.loadJar(path, data64, data_md5, plugin);
    }

    @Override
    protected Map<String, Object> ridGet() {
        _ridS.put("XUtil.thumbnailOf(stream)", Thumbnails.Builder.class);
        return _ridS;
    }
}
