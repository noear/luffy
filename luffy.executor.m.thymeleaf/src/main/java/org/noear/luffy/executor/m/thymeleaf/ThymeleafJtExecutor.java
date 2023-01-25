package org.noear.luffy.executor.m.thymeleaf;

import org.noear.solon.Solon;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.TextUtils;
import org.noear.solon.core.handle.Context;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class ThymeleafJtExecutor implements IJtExecutor {
    private static final String _lock ="";
    private static ThymeleafJtExecutor _g;
    public static ThymeleafJtExecutor singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new ThymeleafJtExecutor();
                }
            }
        }

        return _g;
    }

    private TemplateEngine _engine = new TemplateEngine();
    private MapTemplateResolver _loader = new MapTemplateResolver();
    private Map<String,Object> _sharedVariable = new HashMap<>();

    private ThymeleafJtExecutor() {


        _loader.setTemplateMode(TemplateMode.HTML);
        _loader.setCacheable(true);
        _loader.setCacheTTLMs(Long.valueOf(3600000L));

        _engine.setTemplateResolver(_loader);

        try {
            Solon.app().shared().forEach((k,v)->{
                sharedSet(k,v);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Solon.app().onSharedAdd((k,v)->{
            sharedSet(k,v);
        });
    }

    public void sharedSet(String name,Object obj){
        try {
            _sharedVariable.put(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void tagReg(String name,Object obj){
        try {
            _sharedVariable.put(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean put(String name, AFileModel file) {
        if (TextUtils.isEmpty(file.content)) {
            return false;
        } else {
            _loader.put(name, file.content);
            return true;
        }
    }

    public ITemplateResource get(String name) throws Exception {
        return _loader.get(name);
    }


    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "thymeleaf";
    }


    @Override
    public boolean isLoaded(String name){
        return _loader.containsKey(name);
    }

    @Override
    public boolean preLoad(String name, AFileModel file) throws Exception{
        if (isLoaded(name)) {
            return true;
        }

        return put(name, file);
    }

    @Override
    public void del(String name){
        _loader.remove(name);
        if(_engine.getConfiguration().getCacheManager() != null) {
            _engine.getConfiguration().getCacheManager().getTemplateCache().clear();
        }
    }

    @Override
    public void delAll(){
        _loader.clear();
        if(_engine.getConfiguration().getCacheManager() != null) {
            _engine.getConfiguration().getCacheManager().getTemplateCache().clear();
        }
    }

    @Override
    public Object exec(String name, AFileModel file, Context ctx, Map<String,Object> model, boolean outString) throws Exception {
        if(preLoad(name,file)){
            if(model == null){
                model = new HashMap<>();
            }

            if(ctx == null){
                model.put("ctx", Context.current());
            }else{
                model.put("ctx", ctx);
            }

            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariables(_sharedVariable);
            context.setVariables(model);



            StringWriter writer = new StringWriter();

            _engine.process(name,context,writer);


            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
