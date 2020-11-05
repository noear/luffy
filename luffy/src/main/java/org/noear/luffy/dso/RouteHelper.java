package org.noear.luffy.dso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 路由助手，提供路由检测作用；（防止数据库被恶意刷暴）
 * */
public class RouteHelper {
    private static final Set<String> _set = new HashSet<>();

    public static void add(String path){
        _set.add(path);
    }

    public static void del(String path){
        _set.remove(path);
    }

    public static boolean has(String path){
        return _set.contains(path);
    }

    public static void reset() {
        List<String> all_paths = null;
        try {
            all_paths = DbApi.fileGetPathAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (all_paths != null) {
            _set.clear();

            all_paths.forEach(path -> {
                add(path);
            });
        }
    }
}
