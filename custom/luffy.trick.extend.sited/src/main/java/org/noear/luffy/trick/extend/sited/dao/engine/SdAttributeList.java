package org.noear.luffy.trick.extend.sited.dao.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuety on 16/2/1.
 */
public class SdAttributeList {
    private Map<String,String> _items;
    private List<String> _values;
    public SdAttributeList(){
        _items  =new HashMap<>();
        _values = new ArrayList<>();
    }

    public String[] getValues(){
        return _values.toArray(new String[]{});
    }

    public String getJson(){
        return Util.toJson(_items);
    }

    public int count(){
        return _items.size();
    }

    public void clear(){
        _items.clear();
    }

    public boolean contains(String key){
        return _items.containsKey(key);
    }

    public void set(String key, String val){
        _items.put(key,val);
        _values.add(val);
    }


    public SdValue getValue(String key) {
        return getValue(key, null);
    }

    public SdValue getValue(String key, String def) {
        String val = getString(key);
        return new SdValue(val, def);
    }

    public String getString2(String key,String key2) {
        if(contains(key)) {
            return getString(key, null);
        }else{
            return getString(key2, null);
        }
    }

    public String getString(String key) {
        return getString(key,null);
    }

    public String getString(String key, String def){
        if(contains(key))
            return _items.get(key);
        else
            return def;
    }

    public int getInt(String key) {
        return getInt(key,0);
    }

    public int getInt(String key, int def) {
        if (contains(key))
            return Integer.parseInt(_items.get(key));
        else
            return def;
    }

    public long getLong(String key) {
       return getLong(key,0);
    }

    public long getLong(String key, long def) {
        if (contains(key))
            return Long.parseLong(_items.get(key));
        else
            return def;
    }

    public void addAll(SdAttributeList attrs){
        _items.putAll(attrs._items);
    }
}
