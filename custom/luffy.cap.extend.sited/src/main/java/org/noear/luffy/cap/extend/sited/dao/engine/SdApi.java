package org.noear.luffy.cap.extend.sited.dao.engine;


import java.io.File;

/**
 * Created by yuety on 15/12/19.
 */
public class SdApi {

    protected static SdAdapter _adapter = new SdAdapter();

    public static void tryInit(SdAdapter adapter) {
        _adapter = adapter;
    }

    //-------------------------------
    //

    protected static void log(SdSource source, SdNode node, String url, String json, int tag) {
        log(source, node.name, "tag=" + tag);

        if (url == null)
            log(source, node.name, "url=null");
        else
            log(source, node.name, url);

        if (json == null)
            log(source, node.name, "json=null");
        else
            log(source, node.name, json);
    }

    protected static void log(SdSource source, String tag, String msg) {
        if (msg == null) {
            msg = "null";
        }

        try {
            _adapter.log(source, tag, msg, null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    protected static void log(SdSource source, String tag, Throwable tr) {
        if (tr == null) {
            return;
        }

        try {
            String msg = tr.getMessage();
            if (msg == null) {
                msg = "null";
            }


            _adapter.log(source, tag, msg, tr);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static void set(SdSource source, String key, String val) {

        _adapter.set(source, key, val);
    }

    protected static String get(SdSource source, String key) {
        String temp = _adapter.get(source, key);

        return temp;
    }

    //-------------
    //

    protected static File cacheRoot(){
        return _adapter.cacheRoot();
    }

    //-------------
    //
    protected static SdNode createNode(SdSource source,String tagName) {
        return _adapter.createNode(source, tagName);
    }

    protected static SdNodeSet createNodeSet(SdSource source,String tagName) {
        return _adapter.createNodeSet(source, tagName);
    }

    public static void buildHttpHeader(SdNode cfg, String url, HttpHeaderHandler header) {
        _adapter.buildHttpHeader(cfg, url, header);
    }
}
