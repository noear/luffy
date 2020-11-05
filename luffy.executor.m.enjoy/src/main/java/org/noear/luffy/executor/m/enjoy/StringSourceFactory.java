package org.noear.luffy.executor.m.enjoy;

import com.jfinal.template.source.ISource;
import com.jfinal.template.source.ISourceFactory;
import com.jfinal.template.source.StringSource;
import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class StringSourceFactory implements  ISourceFactory {
    private Set<Object> _modifiedSet = new HashSet<>();

    private HashMap<String,StringSourceEx> _map = new HashMap<>();

    public void put(String name, String tml){
        _map.put(name,new StringSourceEx(name ,tml, true){
            @Override
            public boolean isModified() {
                return StringSourceFactory.this.isModified(this);
            }
        });
    }

    public StringSource get(String name){
        return _map.get(name);
    }

    public boolean containsKey(String name){
        return _map.containsKey(name);
    }

    public void remove(String name){
        _modifiedSet.add(name);
        _map.remove(name);
    }

    public void clear(){
        _modifiedSet.addAll(_map.keySet());
        _map.clear();
    }

    public ISource getSource(String baseTemplatePath, String fileName, String encoding) {
        return do_getSource(fileName);
    }


    private ISource do_getSource(String fileName){
        /**
         * fileName 会自动加上/。需要处理
         * */

        String name = fileName.substring(1);

        if(name.indexOf("/") >= 0){
            String path = fileName;
            String path2 = path; //AFileUtil.path2(path);//不用转为*
             name = path2.replace("/", "__");

            ISource tml = get(name);

            if (tml != null) {
                return tml;
            }

            try {
                AFileModel file = ExecutorFactory.fileGet(path2);

                if (EnjoyJtExecutor.singleton().put(name, file)) {
                    return get(name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        return get(name);
    }

    public boolean isModified(StringSourceEx res){
        return _modifiedSet.remove(res.getName());
    }
}
