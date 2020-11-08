package org.noear.luffy.trick.extend.sited.dao.custom;

import org.noear.luffy.trick.extend.sited.dao.engine.SdNode;
import org.noear.luffy.trick.extend.sited.dao.engine.SdSource;

/**
 * Created by yuety on 16/2/1.
 */
public class DdNodeAbout extends SdNode {

    public DdSource s(){
        return (DdSource)source;
    }

    public String mail;


    public DdNodeAbout(SdSource source){
        super(source);
    }

    @Override
    public void OnDidInit() {
        mail  = attrs.getString("mail");
    }

    //是否内部WEB运行
    public boolean isWebrun(){
        String run = attrs.getString("run");

        if(run==null)
            return false;

        return run.indexOf("web")>=0;
    }
}
