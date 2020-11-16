package org.noear.luffy.dso;

import okhttp3.Response;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.ExtendLoader;
import org.noear.solon.core.Plugin;
import org.noear.luffy.utils.*;

import java.io.File;
import java.io.FileOutputStream;

public class JarUtil {
    public static boolean loadJar(String path, String data64 ,String data_md5 ,String xPlugin) throws Exception {
        String extend = Solon.global().prop().argx().get("extend");

        //构建文件名
        int idx = path.lastIndexOf('/');
        String filename = extend + path.substring(idx);

        if(path.indexOf("://") < 0){
            byte[] data = Base64Utils.decodeByte(data64);

            if(TextUtils.isEmpty(data_md5)){
                data_md5 = EncryptUtils.md5Bytes(data);
            }

            if(doCehckMd5(filename, data_md5) == false){
                return false;
            }

            return doLoadJar(filename, data, data_md5, xPlugin);
        }else{
            if(TextUtils.isEmpty(data_md5)){
                return false;
            }

            if(doCehckMd5(filename, data_md5) == false){
                return false;
            }

            try(Response res = HttpUtils.http(path).exec("GET")){

                if(res.isSuccessful() == false){
                    return false;
                }

                byte[] data = res.body().bytes();
                return doLoadJar(filename, data, data_md5, xPlugin);
            }
        }
    }

    private static boolean doCehckMd5(String filename, String data_md5) throws Exception{
        File file = new File(filename);
        if (file.exists()) {
            //检测md5
            byte[] data2 = IOUtils.getFileBytes(file);
            String md2 = EncryptUtils.md5Bytes(data2);

            //如果一样，则不需要更新
            if (data_md5 != null && data_md5.equalsIgnoreCase(md2)) {
                return false;
            }
        }

        return true;
    }

    private static boolean doLoadJar(String filename, byte[] data, String data_md5, String xPlugin) throws Exception{
        File file = new File(filename);
        //如果存在先删掉
        if (file.exists()) {
            file.delete();
        }

        //创建jar文件并写入
        file.createNewFile();

        FileOutputStream fw = new FileOutputStream(file);
        fw.write(data);
        fw.close();

        //加载jar包
        try {
            ExtendLoader.unloadJar(file);
            ExtendLoader.loadJar(file);

            //尝试加载插件类
            if (TextUtils.isEmpty(xPlugin) == false) {
                Plugin p1 = newInstance(xPlugin);
                if (p1 != null) {
                    Solon.global().plug(p1);
                    return false;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return true;
    }

    public static <T> T newInstance(String className) {
        return Utils.newInstance(className);
    }
}
