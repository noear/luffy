package org.noear.luffy.executor.s.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaTableEx;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

import java.util.*;

public class LuaUtil {

    public static Object luaToObj(Object obj) {
        if(!(obj  instanceof  LuaValue)){
            return obj;
        }

        LuaValue val = (LuaValue)obj;

        if(val.isuserdata()){
            return ((LuaUserdata)val).m_instance;
        }

        if(val.istable()){
            return tableToObj((LuaTable) val);
        }

        if(val.isboolean()){
            return val.toboolean();
        }

        if(val.isstring()){
            return val.toString();
        }

        if(val.islong()){
            return val.tolong();
        }

        if(val.isint()){
            return val.toint();
        }

        if(val.isnumber()){
            return val.tofloat();
        }

        return null;
    }

    public static Object tableToObj(LuaTable tb) {
        LuaTableEx tbx = new LuaTableEx(tb);
        if (tbx.hashEntries > 0) {
            Map<String, Object> map = new HashMap<>();

            tbx.hashForEach((k, v) -> {
                map.put(k, luaToObj(v));
            });

            return map;
        } else if (tbx.array.length > 0) {
            List<Object> ary = new ArrayList<>();
            tbx.arrayForEach((v) -> {
                ary.add(luaToObj(v));
            });

            return ary;
        } else {
            return null;
        }
    }
}
