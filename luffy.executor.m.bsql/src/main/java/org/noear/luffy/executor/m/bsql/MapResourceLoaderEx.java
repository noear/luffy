package org.noear.luffy.executor.m.bsql;

import org.beetl.core.Resource;
import org.beetl.core.resource.MapResourceLoader;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;

import java.util.HashSet;
import java.util.Set;

public class MapResourceLoaderEx extends MapResourceLoader {
    private Set<Object> _modifiedSet = new HashSet<>();

    @Override
    public Resource getResource(String key) {
        /**
         * key 由自己决定。不会自动去掉或添加/开头
         * */
        if(key.startsWith("/")){
            String path = key;
            String path2 = path; //AFileUtil.path2(path);//不用转为*
            String name = path2.replace("/", "__");

            Resource tml = super.getResource(name);

            if (tml != null) {
                return tml;
            }

            try {
                AFileModel file = ExecutorFactory.fileGet(path2);

                if (BsqlJtExecutor.singleton().put(name, file)) {
                    return super.getResource(name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }
        return super.getResource(key);
    }

    // 通过 _modifiedSet ，添加修改状态控制

    @Override
    public String put(String key, String value) {
        return super.put(key, value);
    }

    @Override
    public String remove(Object key) {
        _modifiedSet.add(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        _modifiedSet.addAll(this.keySet());
        super.clear();
    }

    @Override
    public boolean isModified(Resource res) {
        return _modifiedSet.remove(res.getId());
    }
}
