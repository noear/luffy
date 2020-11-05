package org.noear.luffy.dso;

public interface IJtLock {
    boolean tryLock(String group, String key, int inSeconds, String inMaster);
    boolean tryLock(String group, String key, int inSeconds);
    boolean tryLock(String group, String key);
    boolean isLocked(String group, String key);
    void unLock(String group, String key);
}
