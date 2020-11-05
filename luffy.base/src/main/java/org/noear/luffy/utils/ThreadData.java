package org.noear.luffy.utils;

import org.noear.luffy.utils.ext.Fun0;

public class ThreadData<T> extends ThreadLocal<T> {
    private Fun0<T> _def;

    public ThreadData(Fun0<T> def) {
        _def = def;
    }

    @Override
    protected T initialValue() {
        if (_def == null) {
            return null;
        } else {
            return _def.run();
        }
    }
}

