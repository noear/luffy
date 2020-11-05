package org.noear.luffy.dso;

import java.util.Map;

class JtFunEntity implements JtFunHandler {
    public JtFunHandler handler;
    public String note;
    public int priority;
    public long lastModified;

    public JtFunEntity set(JtFunHandler handler, int priority , String note) {
        this.handler = handler;
        this.priority = priority;
        this.note = note;
        this.lastModified = System.currentTimeMillis();
        return this;
    }


    @Override
    public Object call(Map<String, Object> arg) throws Exception {
        return handler.call(arg);
    }
}
