package org.noear.luffy.trick.extend.sited.dao.engine;

/**
 * Created by yuety on 15/10/10.
 */
public interface HttpCallback {
    void run(Integer code, HttpMessage msg, String text, String url302);
}
