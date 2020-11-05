package org.luaj.vm2;


import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LuaTableEx  {
    public LuaValue[] array;
    public LuaTable.Slot[] hash;
    public int hashEntries;

    public LuaTableEx(LuaTable tb){
        array = tb.array;
        hash = tb.hash;
        hashEntries = tb.hashEntries;
    }

    public void hashForEach(BiConsumer<String,LuaValue> fun){
        for(int i=0, len = hash.length; i<len; i++) {
            LuaTable.Slot tmp0 = hash[i];
            while (tmp0 != null) {
                LuaTable.StrongSlot tmp = tmp0.first();
                fun.accept(tmp.key().toString(), tmp.value());

                tmp0 = tmp0.rest();
            }
        }
    }

    public void arrayForEach(Consumer<LuaValue> fun){
        for(int i=0,len=array.length; i<len; i++){
            LuaValue tmp = array[i];
            if(tmp!=null) {
                fun.accept(tmp);
            }
        }
    }
}
