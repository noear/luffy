package org.noear.luffy.executor.m.enjoy;

import com.jfinal.template.Directive;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.source.ISource;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.luffy.executor.IJtExecutor;
import org.noear.luffy.model.AFileModel;
import org.noear.luffy.utils.TextUtils;

import java.io.StringWriter;
import java.util.*;

public class EnjoyJtExecutor implements IJtExecutor {
    private static final String _lock = "";
    private static EnjoyJtExecutor _g;

    public static EnjoyJtExecutor singleton() {
        if (_g == null) {
            synchronized (_lock) {
                if (_g == null) {
                    _g = new EnjoyJtExecutor();
                }
            }
        }

        return _g;
    }

    //引擎或配置
    Engine _engine = Engine.use();
    //加载器
    StringSourceFactory _loader = new StringSourceFactory();

    private EnjoyJtExecutor() {
        _engine.setDevMode(true);
        _engine.setSourceFactory(_loader);

        try {
            Solon.app().shared().forEach((k, v) -> {
                sharedSet(k, v);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Solon.app().onSharedAdd((k, v) -> {
            sharedSet(k, v);
        });
    }

    public void sharedSet(String name, Object obj) {
        try {
            _engine.addSharedObject(name, obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tagReg(String name, Class<? extends Directive> clz) {
        try {
            _engine.addDirective(name, clz);
        } catch (Exception ex) {
            EventBus.push(ex);
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

    public ISource get(String name) throws Exception {
        return _loader.get(name);
    }

    //
    // IJtEngine 接口
    //
    @Override
    public String language() {
        return "enjoy";
    }


    @Override
    public boolean isLoaded(String name) {
        return _loader.containsKey(name);
    }

    @Override
    public boolean preLoad(String name, AFileModel file) throws Exception {
        if (isLoaded(name)) {
            return true;
        }

        return put(name, file);
    }

    @Override
    public void del(String name) {
        _loader.remove(name);
    }

    @Override
    public void delAll() {
        _loader.clear();
    }

    @Override
    public Object exec(String name, AFileModel file, Context ctx, Map<String, Object> model, boolean outString) throws Exception {
        if (preLoad(name, file)) {
            if (model == null) {
                model = new HashMap<>();
            }

            if (ctx == null) {
                model.put("ctx", Context.current());
            } else {
                model.put("ctx", ctx);
            }

            Template template = _engine.getTemplate(name);

            StringWriter writer = new StringWriter();

            template.render(model, writer);

            return writer.toString().trim();
        } else {
            return "";
        }
    }
}
