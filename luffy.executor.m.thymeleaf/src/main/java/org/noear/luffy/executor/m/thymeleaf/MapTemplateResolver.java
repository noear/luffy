package org.noear.luffy.executor.m.thymeleaf;

import org.noear.luffy.executor.ExecutorFactory;
import org.noear.luffy.model.AFileModel;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import java.util.HashMap;
import java.util.Map;

public class MapTemplateResolver extends AbstractConfigurableTemplateResolver {
    private Map<String,StringTemplateResource> _map = new HashMap<>();

    public StringTemplateResource get(String name){
        return _map.get(name);
    }

    public void put(String name, String tml){
        _map.put(name,new StringTemplateResource(tml));
    }

    public boolean containsKey(String name){
        return _map.containsKey(name);
    }


    public void remove(String name){
        _map.remove(name);
    }

    public void clear(){
        _map.clear();
    }


    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes)  {
        return  do_getSource(template);
    }

    public ITemplateResource do_getSource(String name){
        /**
         * name 完全自定义，不会自动去掉或加上/
         * */

        if(name.startsWith("/")){
            String path = name;
            String path2 = path; //AFileUtil.path2(path);//不用转为*
            name = path2.replace("/", "__");

            ITemplateResource tml = get(name);

            if (tml != null) {
                return tml;
            }

            try {
                AFileModel file = ExecutorFactory.fileGet(path2);

                if (ThymeleafJtExecutor.singleton().put(name, file)) {
                    return get(name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        return get(name);
    }


}
