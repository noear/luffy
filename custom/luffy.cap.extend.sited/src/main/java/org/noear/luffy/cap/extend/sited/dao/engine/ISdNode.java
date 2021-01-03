package org.noear.luffy.cap.extend.sited.dao.engine;

/**
 * Created by yuety on 15/8/21.
 */
public interface ISdNode {
    String nodeName();
    int nodeType();
    boolean isEmpty();
    SdNode nodeMatch(String url);
}
