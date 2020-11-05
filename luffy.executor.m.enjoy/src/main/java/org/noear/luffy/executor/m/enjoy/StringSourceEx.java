package org.noear.luffy.executor.m.enjoy;

import com.jfinal.template.source.StringSource;

public class StringSourceEx extends StringSource {
    private String _name;

    public String getName(){
        return _name;
    }

    public StringSourceEx(String name,String content, boolean cache) {
        super(content, cache);
        _name = name;
    }
}
