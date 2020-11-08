package org.noear.luffy.trick.extend.sited.dao.engine;


import org.noear.luffy.trick.extend.sited.utils.TextUtils;

/**
 * Created by yuety on 2017/7/16.
 */

public class SdValue {
    public String value; //静态值
    public String build; //动态构建函数

    public SdValue(String value){
        this(value,null);
    }

    public SdValue(String value, String def) {
        this.value = value;

        if (value != null && value.startsWith("js:")) {
            this.build = value.substring(3);
            this.value = def;
        }
    }

    public boolean isEmpty(){
        return TextUtils.isEmpty(value) && TextUtils.isEmpty(build);
    }


    public boolean isEmptyValue(){
        return TextUtils.isEmpty(value);
    }

    public boolean isEmptyBuild(){
        return TextUtils.isEmpty(build);
    }

    //============================================================
    //
    //

    public String getValue(String def){
        if(this.value == null)
            return def;
        else
            return this.value;
    }

    public int indexOf(String str){
        if(value == null)
            return -1;
        else
            return value.indexOf(str);
    }

    //============================================================
    //
    //

    public String run(SdSource sd, SdAttributeList args) {
        return run(sd, args, value);
    }

    public String run(SdSource sd, SdAttributeList args, String defValue){
        if (TextUtils.isEmpty(build))
            return defValue;
        else {
            return sd.js.callJs(build, args);
        }
    }
}
