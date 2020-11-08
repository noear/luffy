package org.noear.luffy.trick.extend.sited.dao.engine;

import java.io.File;

/**
 * Created by yuety on 2017/3/19.
 */

public class SdAdapter {
    public SdNode createNode(SdSource source,String tagName) {
        return new SdNode(source);
    }

    public SdNodeSet createNodeSet(SdSource source,String tagName) {
        return new SdNodeSet(source);
    }

    public File cacheRoot(){
        return null;
    }

    public void log(SdSource source, String tag, String msg, Throwable tr){

    }

    public void set(SdSource source, String key, String val){

    }

    public String get(SdSource source, String key){
        return "";
    }


    protected void doBuildCookie(SdNode cfg, String url, HttpHeaderHandler header){
        String cookies = cfg.buildCookies(url);
        if (cookies != null) {
            header.set("Cookie", cookies);
        }
    }

    protected void doBuildRererer(SdNode cfg, String url, HttpHeaderHandler header){
        header.set("Referer", cfg.getReferer(url));
    }

    public void buildHttpHeader(SdNode cfg, String url, HttpHeaderHandler header) {
        if(cfg == null)
            return;

        if (cfg.isInCookie()) {
            doBuildCookie(cfg,url,header);
        }

        if (cfg.isInReferer()) {
            doBuildRererer(cfg,url,header);
        }

        if (cfg.isEmptyHeader() == false) {
            for (String kv : cfg.getHeader(url).split("\\$\\$")) {
                int idx = kv.indexOf(":");
                if (idx > 0) {
                    String k = kv.substring(0, idx).trim();
                    String v = kv.substring(idx + 1).trim();

                    header.set(k, v);
                }else{
                    if(kv.equals("cookie")){
                        doBuildCookie(cfg,url,header);
                    }

                    if(kv.equals("referer")){
                        doBuildRererer(cfg,url,header);
                    }
                }
            }
        }
    }
}
