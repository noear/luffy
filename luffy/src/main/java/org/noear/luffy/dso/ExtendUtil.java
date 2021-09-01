package org.noear.luffy.dso;


import org.noear.luffy.utils.TextUtils;

import java.io.File;
import java.util.*;

/** 扩展文件夹工具 */
public class ExtendUtil {
    private static String _path;
    private static File _file;
    public static void init(String path){
        _path = path;
        _file = new File(_path);
    }

    public static String path(){
        return _path;
    }

    public static File directory(){
        return _file;
    }

    /** 扫描扩展文件夹（如果是目录的话，只处理一级） */
    public static List<Map<String,Object>> scan() {
        List<Map<String,Object>> list = new ArrayList<>();

        if (_file.exists()) {
            if (_file.isDirectory()) {
                File[] tmps = _file.listFiles();
                for (File tmp : tmps) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",tmp.getName());
                    map.put("file_size",tmp.length());
                    map.put("modified",new Date(tmp.lastModified()));

                    list.add(map);
                }
            }
        }

        Collections.sort(list, Comparator.comparing(m -> m.get("name").toString()));
        return list;
    }

    /** 删除扩展文件夹里的文件 */
    public static boolean del(String name){
        if(TextUtils.isEmpty(name)){
            return false;
        }

        if(name.endsWith(".jar") == false){
            return false;
        }

        if (_file.exists()) {
            if (_file.isDirectory()) {
                File[] tmps = _file.listFiles();
                for (File tmp : tmps) {
                    if(name.equals(tmp.getName())){
                        tmp.delete();
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
