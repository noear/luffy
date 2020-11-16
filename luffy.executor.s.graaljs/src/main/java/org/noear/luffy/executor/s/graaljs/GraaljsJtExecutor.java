package org.noear.luffy.executor.s.graaljs;

import org.noear.solon.Solon;;
import org.noear.solon.core.handler.Context;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.ThreadData;

import javax.script.*;
import java.util.*;

/**
 * javascript 代码运行工具
 * */
public class GraaljsJtExecutor implements IJtExecutor {
    private static final ThreadData<StringBuilder> _tlBuilder = new ThreadData(()->new StringBuilder(1024*5));
    private static final String _lock ="";
    private static GraaljsJtExecutor _g;
    public static GraaljsJtExecutor singleton(){
        if(_g == null){
            synchronized (_lock){
                if(_g == null){
                    _g = new GraaljsJtExecutor();
                }
            }
        }

        return _g;
    }

    /**
     * Nashorn features available by default:
     *
     * Java.type, Java.typeName
     * Java.from, Java.to
     * Java.extend, Java.super
     * Java package globals: Packages, java, javafx, javax, com, org, edu
     * */

    private final ScriptEngine _eng;
    private final Invocable    _eng_call;
    private final Set<String>  _loaded_names;
    private final Bindings     _bindings;

    private GraaljsJtExecutor(){
        _loaded_names = Collections.synchronizedSet(new HashSet<>());

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("graal.js");
        _eng_call = (Invocable)_eng;
        //添加配置，支持本地java对接
        _bindings = _eng.getBindings(ScriptContext.ENGINE_SCOPE);
        _bindings.put("polyglot.js.allowAllAccess",true);
        //_bindings.put("polyglot.js.allowHostAccess", true);
        //_bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);


        Solon.global().shared().forEach((k, v)->{
            sharedSet(k, v);
        });

        Solon.global().onSharedAdd((k,v)->{
            sharedSet(k, v);
        });

        sharedSet("__JTEAPI", new __JTEAPI_CLZ());


        try {
            StringBuilder sb = new StringBuilder();

            sb.append("var __global={lib:{},lib_new:{}};\n");

            sb.append("Date.prototype.toJSON =function(){ return this.getTime()};\n");

            sb.append("const Context = Java.type('org.noear.solon.core.handler.Context;');\n");
            sb.append("const ONode = Java.type('org.noear.snack.ONode');\n");

            sb.append("const Datetime  = Java.type('org.noear.luffy.utils.Datetime');\n");
            sb.append("const Timecount = Java.type('org.noear.luffy.utils.Timecount');\n");
            sb.append("const Timespan  = Java.type('org.noear.luffy.utils.Timespan');\n");

            sb.append("function modelAndView(tml,mod){return __JTEAPI.modelAndView(tml,mod);};\n");

            sb.append("function requireX(path){" +
                    "  if(path.startsWith('$')){" +
                    "       path=path.substr(1);" +
                    "       __JTEAPI.require(path);" +
                    "       return __global.lib_new[path]()}" +
                    "  else{" +
                    "       __JTEAPI.require(path);" +
                    "       return __global.lib[path]}" +
                    "};\n");

            //下面两个将不再支持
            //sb.append("function require(path){__JTEAPI.require(path);return __global.lib[path]};\n");
            //sb.append("function requireNew(path){__JTEAPI.require(path);return __global.lib_new[path]()};\n");

            //为JSON.stringify 添加java的对象处理

            sb.append("function stringify_java(k,v){if(v){if(v.getTicks){return v.getTicks()}if(v.getTime){return v.getTime()}if(v.putAll){var obj={};v.forEach(function(k2,v2){obj[k2]=v2});return obj}if(v.addAll){var ary=[];v.forEach(function(v2){ary.push(v2)});return ary}}return v};\n");

            sb.append("function API_RUN(api){var rst=api(Context.current());if(rst){if(typeof(rst)=='object'){return JSON.stringify(rst,stringify_java)}else{return rst}}else{return null}};\n");

            _eng.eval(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }

    public void sharedSet(String name,Object val){
        _eng.put(name, val);
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "graaljs";
    }

    @Override
    public boolean isLoaded(String name2) {
        return _loaded_names.contains(name2);
    }

    @Override
    public boolean preLoad(String name2, AFileModel file) throws Exception {
        if (isLoaded(name2) == false) {
            synchronized (name2.intern()) {
                if (isLoaded(name2) == false) {
                    _eng.eval(compilerAsFun(name2, file));
                    _loaded_names.add(name2);
                }
            }
        }

        return true;
    }

    @Override
    public  void del(String name) {
        String name2 = name.replace(".", "_").replace("*","_");
        synchronized (name2.intern()) {
            _loaded_names.remove(name2);
            _loaded_names.remove(name2 + "__lib");
        }
    }

    @Override
    public  void delAll() {
        _loaded_names.clear();
    }

    @Override
    public  Object exec(String name, AFileModel file, Context ctx, Map<String,Object> model, boolean outString) throws Exception {
        String name2 = name.replace(".","_").replace("*","_");

        preLoad(name2, file);

        if(outString){
            Object api = _eng.get("API_"+name2);
            if(api == null){
                del(name);
                throw new RuntimeException("接口未找到或初始化失败");
            }

            Object tmp = _eng_call.invokeFunction("API_RUN",api);

            if (tmp == null) {
                return null;
            } else {
                return tmp.toString();
            }
        }else{
            return _eng_call.invokeFunction("API_"+name2, ctx);
        }
    }

    @Override
    public Object exec(String code, Map<String, Object> model) throws Exception {
        if(model != null){
            Bindings bindings = _eng.createBindings();
            bindings.putAll(_bindings);
            bindings.putAll(model);

            return _eng.eval(code, bindings);
        }else{
            return _eng.eval(code);
        }
    }

    //////////////////////////////////////////////////////////////////


    /**
     * 编译为函数代码
     * */
    public  String compilerAsFun(String name, AFileModel file) {
        StringBuilder sb = _tlBuilder.get();
        sb.setLength(0);

        sb.append("this.API_").append(name).append("=function(ctx){");
        sb.append("\r\n\r\n");
        sb.append(file.content);
        sb.append("\r\n\r\n};");

        if (name.endsWith("__lib")) {
            sb.append("__global.lib['")
                    .append(file.path)
                    .append("']=")
                    .append("new API_")
                    .append(name)
                    .append("();");

            sb.append("__global.lib_new['")
                    .append(file.path)
                    .append("']=function(){")
                    .append("return new API_")
                    .append(name)
                    .append("();};");
        }

        return sb.toString();
    }
}
