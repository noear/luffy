package org.noear.luffy.executor.s.lua;

import org.noear.snack.ONode;
import org.noear.solon.Solon;;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.Datetime;
import org.noear.luffy.utils.ThreadData;
import org.noear.luffy.utils.Timecount;
import org.noear.luffy.utils.Timespan;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

public class LuaJtExecutor implements IJtExecutor {
    private static final ThreadData<StringBuilder> _tlBuilder = new ThreadData(()->new StringBuilder(1024 * 5));
    private static final String _lock = "";
    private static LuaJtExecutor _g;

    public static LuaJtExecutor singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new LuaJtExecutor();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Set<String>  _loaded_names;
    private final Bindings     _bindings;

    private LuaJtExecutor() {
        _loaded_names = Collections.synchronizedSet(new HashSet<>());


        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("luaj");
        _bindings = _eng.getBindings(ScriptContext.ENGINE_SCOPE);


        Solon.app().shared().forEach((k, v) -> {
            sharedSet(k, v);
        });

        Solon.app().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });

        sharedSet("__JTEAPI", new __JTEAPI_CLZ());
        sharedSet("Context",Context.class);
        sharedSet("ONode", ONode.class);

        sharedSet("Datetime", Datetime.class);
        sharedSet("Timecount", Timecount.class);
        sharedSet("Timespan", Timespan.class);

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("require 'org.luaj.vm2.lib.DebugLib'\n\n");

//            sb.append("Context = luajava.bindClass('org.noear.solon.core.handle.Context')\n");
//            sb.append("ONode = luajava.bindClass('org.noear.snack.ONode')\n");

            sb.append("\n");
            sb.append("__global = {lib={},lib_new={}}\n\n");

            sb.append("function modelAndView(tml,mod)\n" +
                      "    return __JTEAPI:modelAndView(tml,mod)\n" +
                      "end\n\n");

            sb.append("function callX(path,attrs)\n" +
                    "    return __JTEAPI:call(path,attrs)\n" +
                    "end\n\n");

            sb.append("function requireX(path)\n" +
                    "    if string.sub(path,1,1) == '$' then\n"+
                    "        path=__JTEAPI:getResolvedPath(path)\n" +
                    "        __JTEAPI:require(path)\n" +
                    "        return __global['lib_new'][path]()\n"+
                    "    else\n"+
                    "        path=__JTEAPI:getResolvedPath(path)\n" +
                    "        __JTEAPI:require(path)\n" +
                    "        return __global['lib'][path]\n"+
                    "    end\n"+
                    "end\n\n");

            sb.append("function funX(f)\n" +
                    "    return luajava.createProxy('org.noear.luffy.dso.JtFunHandler', {call=f})\n" +
                    "end\n\n");


            //下面两个将不再支持
//            sb.append("function require(path)\n" +
//                    "    __JTEAPI:require(path)\n" +
//                    "    return __global['lib'][path]\n"+
//                    "end\n\n");
//
//            sb.append("function requireNew(path)\n" +
//                    "    __JTEAPI:require(path)\n" +
//                    "    return __global['lib_new'][path]()\n"+
//                    "end\n\n");

            _eng.eval(sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void sharedSet(String name, Object val) {
        _eng.put(name, val);
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "lua";
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
    public void del(String name) {
        String name2 = name.replace(".", "_").replace("*","_");
        synchronized (name2.intern()) {
            _loaded_names.remove(name2);
            _loaded_names.remove(name2 + "__lib");
        }
    }

    @Override
    public void delAll() {
        _loaded_names.clear();
    }

    @Override
    public Object exec(String name, AFileModel file, Context ctx, Map<String, Object> model, boolean outString) throws Exception {
        String name2 = name.replace(".", "_").replace("*","_");

        preLoad(name2, file);
//
        Object tmp = _eng.eval("return API_"+name2+"(Context:current())");

        if (tmp == null) {
            return null;
        } else {
            if (outString) {
                if (tmp instanceof Number || tmp instanceof String || tmp instanceof Boolean){
                    return tmp.toString();
                }else{
                    Object obj = LuaUtil.luaToObj(tmp);
                    return ONode.loadObj(obj).toJson();
                }
            }else{
                return LuaUtil.luaToObj(tmp);
            }
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
     */
    public String compilerAsFun(String name, AFileModel file) {
        StringBuilder sb = _tlBuilder.get();
        sb.setLength(0);

        String[] lines = file.content.split("\n");


        sb.append("function API_").append(name).append("(ctx)\n");
        for (int i = 0, len = lines.length; i < len; i++) {
            sb.append("    ").append(lines[i]).append("\n");
        }
        sb.append("end\n");

        if (name.endsWith("__lib")) {
            sb.append("__global['lib']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append("();");

            sb.append("\n");

            sb.append("__global['lib_new']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append(";");
        }

        return sb.toString();
    }
}
