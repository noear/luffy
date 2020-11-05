package org.noear.luffy.executor.m.velocity;

import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringResourceRepositoryEx implements StringResourceRepository {


    private Map<String,StringResource> templates = new ConcurrentHashMap<>();
    @Override
    public StringResource getStringResource(String name) {
        /**
         *  name 由 / 开头（自动会加上 /） //和 freemarker 不一样；
         * */
        if (name.indexOf("/") == 0) {
            String path = name;
            String path2 = path; //AFileUtil.path2(path);//不用转为*
            name = path2.replace("/", "__");

            StringResource tml = templates.get(name);

            if (tml != null) {
                return tml;
            }

            try {
                AFileModel file = ExecutorFactory.fileGet(path2);

                if (VelocityJtExecutor.singleton().put(name, file)) {
                    return templates.get(name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;

        } else {
            return templates.get(name);
        }
    }

    @Override
    public void putStringResource(String name, String tml) {
        templates.put(name,new StringResource(tml,getEncoding()));
    }

    @Override
    public void putStringResource(String name, String tml,String encoding) {
        templates.put(name,new StringResource(tml,encoding));
    }

    @Override
    public void removeStringResource(String name) {
        templates.remove(name);
    }

    public void removeAll(){
        templates.clear();
    }

    public boolean contains(String name){
        return templates.containsKey(name);
    }

    @Override
    public void setEncoding(String s) {
        _encoding=s;
    }

    @Override
    public String getEncoding() {
        return _encoding;
    }

    private String _encoding="UTF-8";
}
