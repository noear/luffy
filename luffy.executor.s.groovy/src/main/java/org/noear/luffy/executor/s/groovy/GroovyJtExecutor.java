package org.noear.luffy.executor.s.groovy;

import org.noear.snack.ONode;
import org.noear.solon.Solon;;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.Datetime;
import org.noear.luffy.utils.ThreadData;
import org.noear.luffy.utils.Timecount;
import org.noear.luffy.utils.Timespan;

import javax.script.*;
import java.util.*;

public class GroovyJtExecutor implements IJtExecutor {
    private static final ThreadData<StringBuilder> _tlBuilder = new ThreadData(()->new StringBuilder(1024 * 5));
    private static final String _lock = "";
    private static GroovyJtExecutor _g;

    public static GroovyJtExecutor singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new GroovyJtExecutor();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Invocable _eng_call;
    private final Set<String> _loaded_names;
    private final Bindings     _bindings;

    private GroovyJtExecutor() {
        _loaded_names = Collections.synchronizedSet(new HashSet<>());

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("groovy");
        _eng_call = (Invocable) _eng;
        _bindings = _eng.getBindings(ScriptContext.ENGINE_SCOPE);

        Solon.global().shared().forEach((k, v) -> {
            sharedSet(k, v);
        });

        Solon.global().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });

        sharedSet("__JTEAPI", new __JTEAPI_CLZ());

        sharedSet("Context", Context.class);
        sharedSet("ONode", ONode.class);

        sharedSet("Datetime", Datetime.class);
        sharedSet("Timecount", Timecount.class);
        sharedSet("Timespan", Timespan.class);

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("__global=['lib':[:],'lib_new':[:]];\n");

            sb.append("def modelAndView(tml,mod){return __JTEAPI.modelAndView(tml,mod);};\n");

            sb.append("def requireX(path){" +
                    "  if(path.startsWith('$')){" +
                    "       path=path.substring(1);" +
                    "       __JTEAPI.require(path);" +
                    "       return __global.lib_new[path].NEW1()}" +
                    "  else{" +
                    "       __JTEAPI.require(path);" +
                    "       return __global.lib[path]}" +
                    "};\n");

            //下面两个将不再支持
            //sb.append("def require(path){__JTEAPI.require(path);return __global.lib[path]};\n");
            //sb.append("def requireNew(path){__JTEAPI.require(path);return __global.lib_new[path].NEW1()};\n");

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
        return "groovy";
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
        String name2 = name.replace(".", "_").replace("*", "_");
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
        String name2 = name.replace(".", "_").replace("*", "_");

        preLoad(name2, file);

        String fun_name = "API_" + name2;
        Object tmp = _eng_call.invokeFunction(fun_name, ctx);


        if (tmp == null) {
            return null;
        } else {
            if (outString) {
                if (tmp instanceof Number || tmp instanceof String || tmp instanceof Boolean) {
                    return tmp.toString();
                } else {
                    return ONode.loadObj(tmp).toJson();
                }
            } else {
                return tmp;
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

        if (name.endsWith("__lib")) {
            sb.append("class API_").append(name).append("{");
            sb.append("\n");
            sb.append(file.content);
            sb.append("\n};");

            sb.append("\n");

            sb.append("class API_").append(name).append("_NEW{");
            sb.append("\n");
            sb.append("  def NEW1(){");
            sb.append("\n");
            sb.append("    return new API_").append(name).append("();");
            sb.append("\n");
            sb.append("  };");
            sb.append("\n};");


            sb.append("\n");

            sb.append("__global.lib.put('")
                    .append(file.path)
                    .append("',")
                    .append("new API_")
                    .append(name)
                    .append("());\n");

            sb.append("__global.lib_new.put('")
                    .append(file.path)
                    .append("',")
                    .append("new API_")
                    .append(name)
                    .append("_NEW());\n");

        } else {
            sb.append("def API_").append(name).append("(ctx){");
            sb.append("\n");
            sb.append(file.content);
            sb.append("\n};");
        }


        return sb.toString();
    }

}
