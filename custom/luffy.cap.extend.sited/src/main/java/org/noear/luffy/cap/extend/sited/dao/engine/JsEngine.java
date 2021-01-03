package org.noear.luffy.cap.extend.sited.dao.engine;

/**
 * Created by yuety on 15/8/2.
 */
class JsEngine {
    private SdSource source = null;

    protected synchronized void release() {

    }

    protected JsEngine( SdSource sd) {
        this.source = sd;
    }

    public synchronized JsEngine loadJs(String code) {


        return this;
    }

    public synchronized String callJs(String fun, SdAttributeList atts){
        if(source.schema>=2)
            return callJs2(fun,atts.getJson());
        else
            return callJs1(fun,atts.getValues());
    }

    //调用函数;可能传参数
    private synchronized String callJs1(String fun, String[] args) {
        return null;
    }

    private synchronized String callJs2(String fun, String json) {
        return null;
    }
}
