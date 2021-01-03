package org.noear.luffy.cap.extend.sited.dao.custom;


import org.noear.luffy.cap.extend.sited.dao.engine.SdAdapter;
import org.noear.luffy.cap.extend.sited.dao.engine.SdNodeSet;
import org.noear.luffy.cap.extend.sited.dao.engine.SdNode;
import org.noear.luffy.cap.extend.sited.dao.engine.SdSource;


/**
 * Created by yuety on 2017/3/20.
 */

public class DdAdapter extends SdAdapter {
    public static final DdAdapter g = new DdAdapter();

    @Override
    public SdNode createNode(SdSource source, String tagName) {
        if("login".equals(tagName)){
            return new DdNodeLogin(source);
        }
        else if("reward".equals(tagName) || "about".equals(tagName)) {
            return new DdNodeAbout(source);
        }
        else {
            return new DdNode(source);
        }
    }

    @Override
    public SdNodeSet createNodeSet(SdSource source, String tagName) {
        return new DdNodeSet(source);
    }

    //--------------------------------
    //
}
