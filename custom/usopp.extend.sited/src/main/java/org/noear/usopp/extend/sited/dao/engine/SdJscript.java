package org.noear.usopp.extend.sited.dao.engine;


import org.w3c.dom.Element;

/**
 * Created by yuety on 15/8/26.
 */
public class SdJscript {
    public final SdNode require;
    public final String code;
    public final SdSource s;


    protected SdJscript(SdSource source, Element node) {
        s = source;

        if (node == null) {
            code = "";
            require = SdApi.createNode(source, null).buildForNode(null);
        } else {
            code = Util.getElement(node, "code").getTextContent();
            require = SdApi.createNode(source, node.getTagName()).buildForNode(Util.getElement(node, "require"));
        }
    }

    public void loadJs( JsEngine js) {

    }
}