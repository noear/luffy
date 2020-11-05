package org.noear.luffy.dso;


import org.noear.luffy.model.AImageModel;
import org.noear.luffy.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * AImage获取工具，会处理缓存
 * */
public class AImageUtil {
    private static String _lock = "";
    private static Map<String, AImageModel> _files = new HashMap<>();

    public static AImageModel get(String path) throws Exception {
        if(TextUtils.isEmpty(path)){
            return null;
        }

        if(_files.containsKey(path)==false){
            synchronized (_lock){
                if(_files.containsKey(path)==false){
                    AImageModel tml =  DbApi.imgGet(path);
                    _files.put(path,tml);
                }
            }
        }

        return _files.get(path);
    }

    public static void removeAll(){
        _files.clear();
    }

    public static void remove(String path){
        _files.remove(path);
    }

}
