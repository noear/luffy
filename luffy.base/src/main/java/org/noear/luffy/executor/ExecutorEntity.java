package org.noear.luffy.executor;

import org.noear.solon.core.XContext;
import org.noear.luffy.model.AFileModel;

import java.util.Map;

public class ExecutorEntity implements IJtExecutor{
    public IJtExecutor executor;
    public int priority;
    public long lastModified;
    private String _language;

    public ExecutorEntity set(String language,IJtExecutor executor, int priority ) {
        _language = language;
        this.executor = executor;
        this.priority = priority;
        this.lastModified = System.currentTimeMillis();
        return this;
    }

    @Override
    public String language() {
        return _language;
    }

    @Override
    public Object exec(String name, AFileModel file, XContext ctx, Map<String, Object> model, boolean outString) throws Exception {
        try {
            return executor.exec(name, file, ctx, model, outString);
        }catch (NoSuchMethodException ex){
            del(name);
            throw ex;
        }
    }

    @Override
    public Object exec(String code, Map<String, Object> model) throws Exception {
        return executor.exec(code, model);
    }

    @Override
    public void del(String name) {
        executor.del(name);
    }

    @Override
    public void delAll() {
        executor.delAll();
    }

    @Override
    public boolean isLoaded(String name) {
        return executor.isLoaded(name);
    }

    @Override
    public boolean preLoad(String name, AFileModel file) throws Exception {
        return executor.preLoad(name,file);
    }
}
