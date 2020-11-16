package org.noear.luffy.executor.m.bsql;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.noear.solon.Solon;;
import org.noear.solon.core.handler.Context;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.TextUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class BsqlJtExecutor implements IJtExecutor {
    private static final String _lock ="";
    private static BsqlJtExecutor _g;
    public static BsqlJtExecutor singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new BsqlJtExecutor();
                }
            }
        }

        return _g;
    }

    //引擎或配置
    GroupTemplate _engine;
    //加载器
    MapResourceLoaderEx _loader;

    private BsqlJtExecutor() {
        Configuration cfg = null;

        try {
            cfg = Configuration.defaultConfiguration();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

        cfg.setIgnoreClientIOError(true);

        cfg.setStatementStart("--");
        cfg.setStatementEnd(null);
        cfg.setStatementStart2("#");
        cfg.setStatementEnd2(null);
        cfg.setPlaceholderStart("${");
        cfg.setPlaceholderEnd("}");

        _loader  = new MapResourceLoaderEx();

        _engine = new GroupTemplate(_loader,cfg);

        try {
            Solon.global().shared().forEach((k,v)->{
                sharedSet(k,v);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Solon.global().onSharedAdd((k,v)->{
            sharedSet(k,v);
        });
    }

    public void sharedSet(String name,Object obj){
        try {
            //_engine : GroupTemplate
            _engine.getSharedVars().put(name, obj);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public boolean put(String name, AFileModel file){
        if(TextUtils.isEmpty(file.content)){
            return false;
        }else {
            _loader.put(name, file.content);
            return true;
        }
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "bsql";
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
    }

    @Override
    public void delAll(){
        _loader.clear();
    }

    @Override
    public Object exec(String name, AFileModel file, Context ctx, Map<String,Object> model, boolean outString) throws Exception {
        if(preLoad(name,file)){
            if(model == null){
                model = new HashMap<>();
            }

            if(ctx == null){
                ctx = Context.current();
            }

            model.put("ctx", ctx);

            model.put("p",ctx.paramMap());
            model.put("h",ctx.headerMap());
            model.put("c",ctx.cookieMap());


            Template template = _engine.getTemplate(name);
            template.binding(model);

            StringWriter writer = new StringWriter();


            template.renderTo(writer);

            return writer.toString().trim();
        }else{
            return "";
        }
    }
}
