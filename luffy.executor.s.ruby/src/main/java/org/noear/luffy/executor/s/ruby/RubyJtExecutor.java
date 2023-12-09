package org.noear.luffy.executor.s.ruby;

import org.noear.luffy.utils.*;
import org.noear.snack.ONode;
import org.noear.solon.Solon;;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;

import javax.script.*;
import java.util.*;

public class RubyJtExecutor implements IJtExecutor {
    private static final ThreadData<StringBuilder> _tlBuilder = new ThreadData(()->new StringBuilder(1024 * 5));
    private static final String _lock = "";
    private static RubyJtExecutor _g;

    public static RubyJtExecutor singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new RubyJtExecutor();
                }
            }
        }

        return _g;
    }


    private final ScriptEngine _eng;
    private final Invocable    _eng_call;
    private final Set<String>  _loaded_names;
    private final Bindings     _bindings;

    private RubyJtExecutor() {
        _loaded_names = Collections.synchronizedSet(new HashSet<>());

        //System.setProperty("org.jruby.embed.localvariable.behavior", "global");

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        _eng = scriptEngineManager.getEngineByName("ruby");
        _eng_call = (Invocable) _eng;
        _bindings = _eng.getBindings(ScriptContext.ENGINE_SCOPE);

        Solon.app().sharedAdd("Context",Context.class);
        Solon.app().sharedAdd("ONode", ONode.class);
        Solon.app().sharedAdd("Datetime", Datetime.class);
        Solon.app().sharedAdd("Timecount", Timecount.class);
        Solon.app().sharedAdd("Timespan", Timespan.class);

        Solon.app().shared().forEach((k, v) -> {
            if(v instanceof Class == false) {
                sharedSet(k, v);
            }
        });

        Solon.app().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });

        //sharedSet("__JTEAPI", new __JTEAPI());

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("require 'java'\n\n");

            sb.append("java_import org.noear.luffy.executor.s.ruby.__JTEAPI_CLZ\n");


            Solon.app().shared().forEach((k, v)->{
                if(v instanceof Class) {
                    Class v2 = (Class) v;
                    sb.append("java_import ");
                    sb.append(v2.getName());
                    sb.append("\n");
                }
            });


            sb.append("\n\n");

            sb.append("$__JTEAPI=__JTEAPI_CLZ.new\n\n");

            sb.append("$__global={'lib'=>{},'lib_new'=>{}}\n\n");

            sb.append("def modelAndView(tml,mod)\n" +
                    "    return $__JTEAPI.modelAndView(tml,mod)\n" +
                    "end\n\n");

            sb.append("def callX(path,attrs)\n" +
                    "    return $__JTEAPI.call(path,attrs)\n" +
                    "end\n\n");

            sb.append("def requireX(path)\n" +
                    "    if path[0] == '$' then\n" +
                    "        path=$__JTEAPI.getResolvedPath(path)\n" +
                    "        $__JTEAPI.require(path)\n" +
                    "        return $__global['lib_new'][path].NEW1()\n" +
                    "    else\n" +
                    "        path=$__JTEAPI.getResolvedPath(path)\n" +
                    "        $__JTEAPI.require(path)\n" +
                    "        return $__global['lib'][path]\n" +
                    "    end\n" +
                    "end\n\n");


            //下面两个将不再支持
//            sb.append("def require(path)\n" +
//                    "    $__JTEAPI.require(path)\n" +
//                    "    return $__global['lib'][path]\n"+
//                    "end\n\n");
//
//            sb.append("def requireNew(path)\n" +
//                    "    $__JTEAPI.require(path)\n" +
//                    "    return $__global['lib_new'][path].NEW1()\n"+
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
        return "ruby";
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


        Object tmp = _eng_call.invokeFunction("API_"+name2, ctx);

        if (tmp == null) {
            return null;
        } else {
            if (outString) {
                if (tmp instanceof Number || tmp instanceof String || tmp instanceof Boolean){
                    return tmp.toString();
                }else{
                    return ONode.loadObj(tmp).toJson();
                }
            }else{
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

        String[] lines = file.content.split("\n");


        if (name.endsWith("__lib")) {
            sb.append("class API_").append(name).append("\n");
            for (int i = 0, len = lines.length; i < len; i++) {
                sb.append("    ").append(lines[i]).append("\n");
            }
            sb.append("end\n\n");

            sb.append("class API_").append(name).append("_NEW\n");
            sb.append("  def NEW1\n");
            sb.append("    return ").append("API_").append(name).append(".new\n");
            sb.append("  end\n");
            sb.append("end\n\n");



            sb.append("$__global['lib']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append(".new\n");

            sb.append("$__global['lib_new']['")
                    .append(file.path)
                    .append("']=")
                    .append("API_")
                    .append(name)
                    .append("_NEW.new\n");

        } else {
            sb.append("def API_").append(name).append("(ctx)\n");
            for (int i = 0, len = lines.length; i < len; i++) {
                sb.append("    ").append(lines[i]).append("\n");
            }
            sb.append("end\n");
        }


        return sb.toString();
    }
}
