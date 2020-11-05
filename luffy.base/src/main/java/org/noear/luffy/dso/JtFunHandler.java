package org.noear.luffy.dso;

import java.util.Map;

public interface JtFunHandler {
    Object call(Map<String, Object> arg) throws Exception;
}
