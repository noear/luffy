package org.noear.luffy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JtConfig {
    public static ExecutorService pools = Executors.newCachedThreadPool();
}
