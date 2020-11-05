package org.noear.usopp.extend.sited.dao.custom;

import org.noear.usopp.extend.sited.utils.TextUtils;
import org.noear.usopp.extend.sited.dao.engine.SdAttributeList;
import org.noear.usopp.extend.sited.dao.engine.SdNode;
import org.noear.usopp.extend.sited.dao.engine.SdSource;

/**
 * Created by yuety on 16/2/1.
 */
public class DdNodeLogin extends SdNode {

    public DdSource s(){
        return (DdSource)source;
    }


    //只应用于login节点
    protected String  onCheck;
    protected boolean isAutoCheck = true;

    public DdNodeLogin(SdSource source){
        super(source);
    }

    @Override
    public void OnDidInit() {
        onCheck   = attrs.getString2("onCheck","check"); //控制外部浏览器的打开
        isAutoCheck = attrs.getInt("auto") > 0;
    }

    //是否内部WEB运行
    public boolean isWebrun(){
        String run = attrs.getString("run");

        if(run==null)
            return false;

        return run.indexOf("web")>=0;
    }

    public boolean doCheck(String url, String cookies, Boolean isFromAuto){
        if (TextUtils.isEmpty(this.onCheck)) {
            return true;
        } else {
            if (url == null || cookies == null)
                return false;

            SdAttributeList attrs = new SdAttributeList();
            attrs.set("url", url);
            attrs.set("cookies", (cookies == null ? "" : cookies));

            if (isFromAuto) {
                if (this.isAutoCheck) {
                    String temp = source.callJs(onCheck, attrs);
                    return temp.equals("1");
                } else {
                    return true;//如果不支持自动,则总是返回ok
                }
            } else {
                String temp = source.callJs(onCheck, attrs);
                return "1".equals(temp);
            }
        }
    }
}
