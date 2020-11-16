package org.noear.luffy.dso;

import org.noear.solon.annotation.Note;
import org.noear.luffy.utils.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 引擎扩展函数管理器 */
public class JtFun {
    public static final JtFun g  = new JtFun();

    private Map<String, JtFunEntity> _xfunMap = new HashMap<>();


    public void openList(List<Map<String, Object>> list) {
        Map<String, Object> v1 = new HashMap<>();
        List<Map<String, Object>> methods = new ArrayList<>();

        v1.put("name", "XFun.call(name,args)");
        v1.put("type", "Object");
        v1.put("methods", methods);

        StringBuilder sb = new StringBuilder();

        _xfunMap.forEach((k, ent) -> {
            if(TextUtils.isEmpty(ent.note)){
                return;
            }

            Map<String, Object> m1 = new HashMap<>();
            String[] ss = ent.note.split("#");

            //注解
            m1.put("note", "/** " + ss[0] + " */");

            //代码
            sb.setLength(0);
            sb.append("XFun.call('").append(k).append("',");
            sb.append("{");
            if (ss.length > 1) {
                sb.append(ss[1]);
            } else {
                sb.append("..");
            }
            sb.append("})");
            sb.append("->");

            //此处别再动了
            if (ss.length > 2) {
                sb.append(ss[2]);
            }else{
                sb.append("Object");
            }

            m1.put("code", sb.toString());

            methods.add(m1);
        });

        list.add(v1);
    }

    @Note("函数设置")
    public void set(String name, JtFunHandler fun){
        set(name,null,0,fun);
    }
    @Note("函数设置（带注释）")
    public void set(String name, String note, JtFunHandler fun){
        set(name,note,0,fun);
    }
    @Note("函数设置（带注释、优先级）")
    public void set(String name, String note, int priority, JtFunHandler fun) {
        JtFunEntity ent = _xfunMap.get(name);
        if (ent != null && ent.priority > priority) {
            return;
        }

        if(ent == null) {
            ent = new JtFunEntity();
            ent.set(fun, priority, note);
            _xfunMap.put(name, ent);
        }else{
            ent.set(fun, priority, note);
        }
    }

    @Note("函数获取")
    public JtFunHandler find(String name) { //不能用get；不然，模板可以: XFun.xxx.call({}); //不用于统一
        return _xfunMap.get(name);
    }

    @Note("函数检查")
    public boolean contains(String name){
        return _xfunMap.containsKey(name);
    }

    @Note("函数调用")
    public Object tryCall(String name, Map<String,Object> args) {
        JtFunHandler fun = _xfunMap.get(name);

        Object tmp = null;
        if (fun != null) {
            try {
                tmp = fun.call(args);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return tmp;
    }

    @Note("函数调用")
    public Object call(String name, Map<String,Object> args) throws Exception{ //留着 Exception
        return callT(name,args);
    }

    @Note("函数调用")
    public <T> T callT(String name, Map<String,Object> args) throws Exception{ //留着 Exception
        JtFunHandler fun = _xfunMap.get(name);

        Object tmp = null;
        if (fun != null) {
            tmp = fun.call(args);
        }

        return (T)tmp;
    }

    @Note("调用一个文件")
    public Object callFile(String path) throws Exception {
        return CallUtil.callFile(path ,null);
    }

    @Note("调用一个文件")
    public Object callFile(String path, Map<String,Object> attrs) throws Exception {
        return CallUtil.callFile(path, attrs);
    }

    @Note("调用一组勾子")
    public String callLabel(String tag, String label, boolean useCache) throws Exception{
        return CallUtil.callLabel(tag, label, useCache, null);
    }

    @Note("调用一组勾子")
    public String callLabel(String tag,String label, boolean useCache, Map<String,Object> attrs) throws Exception{
        return CallUtil.callLabel(tag, label, useCache, attrs);
    }

}

