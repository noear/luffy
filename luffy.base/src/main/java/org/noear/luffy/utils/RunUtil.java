package org.noear.luffy.utils;

import org.noear.luffy.utils.ext.Act0Ex;
import org.noear.luffy.utils.ext.Fun0Ex;

public class RunUtil {
    public static void runActEx(Act0Ex act0Ex) {
        try {
            act0Ex.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <R> R runFunEx(Fun0Ex<R> fun0Ex) {
        try {
            return fun0Ex.run();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
